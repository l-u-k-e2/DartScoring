package com.ostfalia.bs.dartscoring;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.ostfalia.bs.dartscoring.database.UserDbHelper;
import com.ostfalia.bs.dartscoring.model.FrequentShot;
import com.ostfalia.bs.dartscoring.model.Shot;
import com.ostfalia.bs.dartscoring.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 18.05.2016.
 */
public class UserStatisticActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "spieler_name";
    public static final String USER_ID = "spieler_id";
    public static final String DRAWABLE_ID = "drawable_id";
    private UserDbHelper userDbHelper;
    private String username;
    private long id;
    private int drawableId;
    private TextView statisticTwenty;
    private TextView statisticFourty;
    private TextView statisticSixty;
    private BarChart barChart;
    private static android.app.Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic_detail);
        //Zugriff auf Daten, die beim Click dem Intent übergeben wurden
        Intent intent = getIntent();
        username = intent.getStringExtra(EXTRA_NAME);
        id = intent.getLongExtra(USER_ID, 0l);
        drawableId = intent.getIntExtra(DRAWABLE_ID, 0);
        //ToolbarLayout
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(username);
        loadBackdrop();
        //Zugriff auf DB
        userDbHelper = new UserDbHelper(getApplicationContext());
        //FAB für editieren des Users
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_detail);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = createDialog();
                dialog.show();
            }
        });
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(User.getDartProPanoramaDrawable(drawableId)).centerCrop().into(imageView);
    }

    /**
     * Nachdem die Aktivität erstellt wurde, werden die Statistiken des Users berechnet und die Anzeige befüllt
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //StatistikInfo
        updateStatisticInfo();
        //Statistik
        statisticTwenty = (TextView)findViewById(R.id.text_statistic_20);
        statisticFourty = (TextView)findViewById(R.id.text_statistic_40);
        statisticSixty = (TextView)findViewById(R.id.text_statistic_60);
        //Statistik berechnen
        fillStatistic(userDbHelper.getUser(id));
        //BarChart
        barChart = (BarChart)findViewById(R.id.chart);
        createBarChart();
    }

    /**
     * setzt Vorname, Nachname und Alias
     */
    private void updateStatisticInfo() {

        TextView tVvorname = (TextView) findViewById(R.id.textView10);
        tVvorname.setText(userDbHelper.getUser(id).getVorname());
        TextView tVnachname = (TextView) findViewById(R.id.textView11);
        tVnachname.setText(userDbHelper.getUser(id).getNachname());
        TextView tValias = (TextView) findViewById(R.id.textView12);
        tValias.setText(userDbHelper.getUser(id).getAlias());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    /**
     * Berechnet die Statistik des Users für:
     * Trefferquote 20
     * Trefferquote 40
     * Trefferquote 60
     * Und setzt die Informationen in der View
     * @param user
     */
    private void fillStatistic(User user){
        List<Shot> shots = userDbHelper.getShots(user);
        Double countOfAllShots = Double.valueOf(userDbHelper.getShots(user).size());
        Double countOfTwenties = 0d;
        Double countOfForties = 0d;
        Double countOFSixties = 0d;
        for (int i = 0; i < shots.size(); i++) {
            switch (shots.get(i).getPunkte()){
                case 20:
                    countOfTwenties++;
                    break;
                case 40:
                    countOfForties++;
                    break;
                case 60:
                    countOFSixties++;
                    break;
            }
        }
        String percentageTwenties = String.valueOf(Math.round(countOfTwenties/countOfAllShots * 100)) + " %";
        String percentageFourties = String.valueOf(Math.round(countOfForties/countOfAllShots * 100)) + " %";
        String percentageSixties = String.valueOf(Math.round(countOFSixties/countOfAllShots * 100)) + " %";
        statisticTwenty.setText(percentageTwenties);
        statisticFourty.setText(percentageFourties);
        statisticSixty.setText(percentageSixties);
    }

    /**
     * Holt sich Informationen des Users über mostFrequentShots
     * Zeigt diese Informationen in einem BarChart an
     */
    private void createBarChart() {

        List<FrequentShot> mostFrequentShots = userDbHelper.getMostFrequentShotsOfUser(userDbHelper.getUser(id));

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < mostFrequentShots.size(); i++) {
            entries.add(new BarEntry(mostFrequentShots.get(i).getCount(),i));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Anzahl der Treffer");

        //X-Axis Label
        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < mostFrequentShots.size(); i++) {
            labels.add(mostFrequentShots.get(i).getPunkte().toString());
        }

        BarData data = new BarData(labels, dataSet);
        barChart.setData(data);
    }

    /**
     * Erstellen des Dialogs zum editieren des Users
     * @return dialog
     */
    public Dialog createDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        b.setTitle(getResources().getString(R.string.update_user));
        final View myView = inflater.inflate(R.layout.user_create, null);
        b.setView(myView);
        User currentUser = userDbHelper.getUser(id);
        ((EditText) myView.findViewById(R.id.vorname)).setText(currentUser.getVorname());
        ((EditText) myView.findViewById(R.id.nachname)).setText(currentUser.getNachname());
        ((EditText) myView.findViewById(R.id.alias)).setText(currentUser.getAlias());
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText vorname, nachname, alias;
                vorname = ((EditText) myView.findViewById(R.id.vorname));
                nachname = ((EditText) myView.findViewById(R.id.nachname));
                alias = ((EditText) myView.findViewById(R.id.alias));
                if (!vorname.getText().toString().isEmpty()) {
                    userDbHelper.updateUser(new User(id, vorname.getText().toString(), nachname.getText().toString(), alias.getText().toString()));
                    updateStatisticInfo();
                } else {
                    Snackbar.make(findViewById(R.id.main_content), getResources().getString(R.string.mandator_field_update), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return b.create();
    }

}
