package com.ostfalia.bs.dartscoring;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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
    private UserDbHelper userDbHelper;
    private String username;
    private long id;
    private TextView statisticTwenty;
    private TextView statisticFourty;
    private TextView statisticSixty;
    private BarChart barChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        userDbHelper = new UserDbHelper(getApplicationContext());

        Intent intent = getIntent();
        username = intent.getStringExtra(EXTRA_NAME);
        id = intent.getLongExtra(USER_ID,0l);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(username);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //StatistikInfo
        TextView tVvorname = (TextView) findViewById(R.id.textView10);
        tVvorname.setText(userDbHelper.getUser(id).getVorname());
        TextView tVnachname = (TextView) findViewById(R.id.textView11);
        tVnachname.setText(userDbHelper.getUser(id).getNachname());
        TextView tValias = (TextView) findViewById(R.id.textView12);
        tValias.setText(userDbHelper.getUser(id).getAlias());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

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

}
