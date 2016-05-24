package com.ostfalia.bs.dartscoring.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.ostfalia.bs.dartscoring.fragment.ScoringFragment;

/**
 * Created by lukas on 18.05.2016.
 */
public class ScoringButton extends Button implements View.OnClickListener {

    private ScoringFragment scoringFragment;

    public ScoringButton(Context context, String text, ScoringFragment scoringFragment) {
        super(context);
        this.scoringFragment = scoringFragment;
        this.setGravity(Gravity.CENTER);
        this.setText(text);
        this.setOnClickListener(this);
        this.setHeight(200);
        this.setTextSize(30);
    }

    /**
     * Konfiguration für den GridView in den die Buttons hinzugefügt werden sollen
     */
    public void setColumnSpec(){
        ((GridLayout.LayoutParams)this.getLayoutParams()).columnSpec = GridLayout.spec(GridLayout.UNDEFINED,1f);
        ((GridLayout.LayoutParams)this.getLayoutParams()).setGravity(Gravity.FILL_HORIZONTAL);
    }

    /**
     * Listener der im scoringFragment die addScore-Methode aufruft
     * @param v
     */
    @Override
    public void onClick(View v) {
        scoringFragment.addScore(Integer.valueOf(this.getText().toString()));
    }
}
