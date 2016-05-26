package com.ostfalia.bs.dartscoring;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ostfalia.bs.dartscoring.database.UserDbHelper;
import com.ostfalia.bs.dartscoring.fragment.ScoringFragment;
import com.ostfalia.bs.dartscoring.fragment.UserchoiceFragment;
import com.ostfalia.bs.dartscoring.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Lukas on 29.04.2016.
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private UserDbHelper userDbHelper;
    private static android.app.Dialog dialog;
    private UserchoiceFragment userchoiceFragment;
    private ScoringFragment scoringFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //Zugriff auf DB
        userDbHelper = new UserDbHelper(getApplicationContext());
        //Initialisierung der Fragmente
        userchoiceFragment = new UserchoiceFragment();
        scoringFragment = new ScoringFragment();
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Links oben in Toolbar
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menue);
        ab.setDisplayHomeAsUpEnabled(true);
        //Layoutcontainer mit navigationview
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //NavigagtionView (Menü von links)
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        //ViewPager (Tabs oben) bzw. ermöglicht scrollen zwischen Fragment
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            /**
             * Wenn eine neue Seite im ViewPager geöffnet wird, wird eine Neue Tabelle im scoringFragment aufgebaut
             * --> ein neues Spiel beginnt
             * @param position
             */
            @Override
            public void onPageSelected(int position) {
                //Update UserChoiceFragment
                scoringFragment.updateUserTable(userchoiceFragment.getCheckedPlayers());
                scoringFragment.clearUsers();
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        if (viewPager != null) {
            //Erstellt den ViewPager
            setupViewPager(viewPager);
        }
        //Anzeige der Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog().show();
            }
        });
    }

    /**
     * Erstellt den Dialog zum Erstellen eines Users
     * @return
     */
    public Dialog createDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        b.setTitle(getResources().getString(R.string.create_user));
        b.setView(inflater.inflate(R.layout.user_create, null));
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Dialog f = (Dialog) dialog;
                String vorname, nachname, alias;
                vorname = ((EditText) f.findViewById(R.id.vorname)).getText().toString();
                nachname = ((EditText) f.findViewById(R.id.nachname)).getText().toString();
                alias = ((EditText) f.findViewById(R.id.alias)).getText().toString();
                if(!vorname.isEmpty()) {
                    userDbHelper.createUser(new User(vorname, nachname, alias));
                    userchoiceFragment.updateList(userDbHelper.getAllUser());
                    userchoiceFragment.clearCheckedPlayers();
                } else {
                    Snackbar.make(findViewById(R.id.main_content), getResources().getString(R.string.mandator_field), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.dialog.cancel();
            }
        });
        return b.create();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        scoringFragment.setSpielmodus(menuItem.getTitle().toString());
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    /**
     * ViewPager konfigurieren
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(userchoiceFragment, getResources().getString(R.string.userchoice));
        adapter.addFragment(scoringFragment, getResources().getString(R.string.scoring));
        viewPager.setAdapter(adapter);
    }

    /**
     * Für ViewPager
     */
    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}
