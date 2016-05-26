package com.ostfalia.bs.dartscoring.fragment;


import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ostfalia.bs.dartscoring.R;
import com.ostfalia.bs.dartscoring.database.UserDbHelper;
import com.ostfalia.bs.dartscoring.model.FrequentShot;
import com.ostfalia.bs.dartscoring.model.User;
import com.ostfalia.bs.dartscoring.ui.ScoringButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lukas on 29.04.2016.
 */
public class ScoringFragment extends Fragment implements View.OnClickListener {

    private UserDbHelper userDbHelper;
    private List<User> users = new ArrayList<>();
    private TableLayout table;
    private GridLayout grid;
    private int posOfAktuellerSpieler = 0;
    private List<Integer> currentThrowList = new ArrayList<>();
    private Button currentThrowTextButton;
    private int throwScore = 0;
    private String spielmodus = "501";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Zugriff auf DB
        userDbHelper = new UserDbHelper(getActivity().getApplicationContext());
        return inflater.inflate(R.layout.new_scoring, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //UserTable
        table = (TableLayout)getActivity().findViewById(R.id.tableLayout);
        //OKButton
        Button ok = (Button)getActivity().findViewById(R.id.score_ok_button);
        ok.setOnClickListener(this);
        //CancelButton
        Button cancel = (Button)getActivity().findViewById(R.id.cancel_score);
        cancel.setOnClickListener(this);
        //CurrentScoreButton -> Anzeigefeld der aktuell eingegebenen Score
        currentThrowTextButton = (Button) getActivity().findViewById(R.id.current_score_button);
        super.onActivityCreated(savedInstanceState);
        //ScoringGrid aufbauen
        prepareStatisticalGrid();
    }

    /**
     * Wird beim schwenken auf dieses Fragment ausgeführt
     * Erstellt eine Tabelle mit Spielern die vorher gecheckt wurden
     * @param checkedUserIds
     */
    public void updateUserTable(List<Long> checkedUserIds){
        if(table != null){
            table.removeAllViews();
            if (checkedUserIds.size()>0){
                for (int i = 0; i < checkedUserIds.size() ; i++) {
                    //User holen
                    User user = userDbHelper.getUser(checkedUserIds.get(i));
                    users.add(user);
                    //Row erstellen + Layout
                    TableRow tableRow = new TableRow(getActivity());
                    TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0,0,0,20);
                    tableRow.setLayoutParams(layoutParams);
                    //Vorname der Row hinzufügen
                    TextView playerName = new TextView(getActivity());
                    playerName.setText(userDbHelper.getUser(checkedUserIds.get(i)).getVorname());
                    playerName.setTextSize(30);
                    playerName.setTag(user);
                    tableRow.addView(playerName);
                    //Punktzahl der Row hinzufügen
                    TextView score = new TextView(getActivity());
                    score.setText(spielmodus);
                    score.setTextSize(30);
                    tableRow.addView(score);
                    //Row der Table hinzufügen
                    table.addView(tableRow);
                    //ersten Spieler markieren
                    startGame();
                }
            }
        }
    }

    /**
     * startet ein neues Spiel
     */
    public void startGame(){
            this.posOfAktuellerSpieler = 0;
            markCurrentUser();
    }

    /**
     * Speichert eingegeben Score bzw. cancelt die Eingabe
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.score_ok_button){
            reduceScore(currentThrowList);
            setNextPlayer();
            deleteScores();
        }else if (view.getId() == R.id.cancel_score){
            deleteScores();
        }
    }

    /**
     * löscht zuvor eingegebene Scores
     */
    private void deleteScores() {
        throwScore = 0;
        currentThrowList.clear();
        currentThrowTextButton.setText(String.valueOf(throwScore));
        for (int i = 0; i < grid.getChildCount(); i++) {
            ((ScoringButton)grid.getChildAt(i)).setEnabled(true);
        }
    }

    /**
     * Setzt den nächsten Spieler als aktuellen Spieler
     */
    private void setNextPlayer(){
        demarkCurrentUser();
        if (posOfAktuellerSpieler >= users.size()-1){
            posOfAktuellerSpieler = 0;
        }else {
            posOfAktuellerSpieler++;
        }
        markCurrentUser();
    }

    /**
     * markiert den User der aktuell am Zug ist
     */
    private void markCurrentUser(){
        TableRow tr = getCurrentUserRow();
        if(tr != null) {
            TextView username = (TextView) tr.getChildAt(0);
            username.setPaintFlags(username.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    /**
     * löscht die Markierung des aktuellen Users
     */
    private void demarkCurrentUser(){
        if(getCurrentUserRow() != null){
            TextView username = (TextView)getCurrentUserRow().getChildAt(0);
            username.setPaintFlags(0);
        }
    }

    /**
     * Reduziert die aktuelle Score des aktuellen Users
     * Speichert Scores in der DB für aktuellen User
     * @param throwScoreList
     */
    private void reduceScore(List<Integer> throwScoreList){
        if(getCurrentUserRow() != null){
            TextView scoreTextView = (TextView)getCurrentUserRow().getChildAt(1);
            Integer currentScore = Integer.valueOf(((TextView)getCurrentUserRow().getChildAt(1)).getText().toString());
            if ((currentScore - throwScore)>0){
                scoreTextView.setText(String.valueOf(currentScore - throwScore));
                //save Aufnahme in DB
                if (currentScore > 100){
                    userDbHelper.saveScore(throwScoreList,users.get(posOfAktuellerSpieler).getId());
                }
            }else if((currentScore - throwScore) == 0){
                scoreTextView.setText(String.valueOf(currentScore - throwScore));
                //save Aufnahme in DB
                userDbHelper.saveScore(throwScoreList,users.get(posOfAktuellerSpieler).getId());
                showWinner();
            }
        }
    }

    /**
     * addiert übergebene score der currentThrowList
     * @param score
     */
    public void addScore(int score){
        currentThrowList.add(score);
        throwScore+=score;
        currentThrowTextButton.setText(String.valueOf(throwScore));
        if (currentThrowList.size() == 3){
            for (int i = 0; i < grid.getChildCount(); i++) {
                ((ScoringButton)grid.getChildAt(i)).setEnabled(false);
            }
        }
    }

    /**
     * Öffnet Dialog mit dem Gewinner des Spiels
     */
    private void showWinner(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(users.get(posOfAktuellerSpieler).getVorname() + " hat gewonnen");
        builder.setTitle("Winner");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Baut das Grid für die Eingabe der Scores auf
     * Färbt 9 am häufigsten geworfenen Felder rot ein
     */
    private void prepareStatisticalGrid() {
        List<FrequentShot> mostFrequentShots = userDbHelper.getMostFrequentShots();
        grid = (GridLayout)getActivity().findViewById(R.id.gridLayout);
        for (int i = 0; i < mostFrequentShots.size() ; i++) {
            ScoringButton scoringButton = new ScoringButton(this.getContext(),mostFrequentShots.get(i).getPunkte().toString(),this);
            scoringButton.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.shape_drawable));
            grid.addView(scoringButton);
            scoringButton.setColumnSpec();
        }
        boolean found = false;
        for (int i = 0; i < 61 ; i++) {
            for (int j = 0; j < mostFrequentShots.size(); j++) {
                if (i == mostFrequentShots.get(j).getPunkte()){
                    found = true;
                }
            }
            if (found == false){
                ScoringButton scoringButton = new ScoringButton(this.getContext(),String.valueOf(i),this);
                grid.addView(scoringButton);
                scoringButton.setColumnSpec();
            }else {
                found = false;
            }
        }
    }

    /**
     * Hilfsmethode:
     * Liefert die Tabellenreihe des aktuellen Spielers
     * @return row
     */
    private TableRow getCurrentUserRow(){
        for (int i=0; i < table.getChildCount(); i++){
            TableRow row = (TableRow) table.getChildAt(i);
            TextView tv = (TextView)row.getChildAt(0);
            User user = (User)tv.getTag();
            if (user.getId() == users.get(posOfAktuellerSpieler).getId()){
                return row;
            }
        }
        return null;
    }

    public void setSpielmodus(String spielmodus) {
        this.spielmodus = spielmodus;
    }
}
