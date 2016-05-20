package com.ostfalia.bs.dartscoring.model;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lukas on 26.04.2016.
 */
public class Shot {

    private long id;
    private Date date;
    private Integer punkte;
    private Integer playerId;

    public Shot() {}

    public Shot(long id, Date date, Integer punkte, Integer playerId) {
        this.id = id;
        this.date = date;
        this.punkte = punkte;
        this.playerId = playerId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String dateString) {
        DateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.date = dateTime.parse(dateString);
        }catch (ParseException e){
            Log.d("Parsing", "Failed");
        }

    }

    public Integer getPunkte() {
        return punkte;
    }

    public void setPunkte(Integer punkte) {
        this.punkte = punkte;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }
}
