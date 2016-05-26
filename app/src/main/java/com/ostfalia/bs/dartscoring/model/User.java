package com.ostfalia.bs.dartscoring.model;

import com.ostfalia.bs.dartscoring.R;

import java.util.Random;

/**
 * Created by Lukas on 15.04.2016.
 */
public class User {

    private long id;
    private String vorname;
    private String nachname;
    private String alias;
    private static final Random RANDOM = new Random();

    public User(){}

    public User(long id, String vorname, String nachname, String alias) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.alias = alias;
        this.id = id;
    }

    public User(String vorname, String nachname, String alias) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.alias = alias;
    }

    public static int getDartProDrawable(int id){
        switch (id){
            default:
            case 0:
                return R.drawable.dart_spieler1;
            case 1:
                return R.drawable.dart_spieler2;
            case 2:
                return R.drawable.dart_spieler3;
            case 3:
                return R.drawable.dart_spieler4;
        }
    }

    public static int getDartProPanoramaDrawable(int id){
        switch (id){
            default:
            case 0:
                return R.drawable.dart_spieler1_panorama;
            case 1:
                return R.drawable.dart_spieler2_panorama;
            case 2:
                return R.drawable.dart_spieler3_panorama;
            case 3:
                return R.drawable.dart_spieler4_panorama;
        }
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
