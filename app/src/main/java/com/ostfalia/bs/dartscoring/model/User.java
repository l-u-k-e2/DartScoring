package com.ostfalia.bs.dartscoring.model;

/**
 * Created by Lukas on 15.04.2016.
 */
public class User {

    private long id;
    private String vorname;
    private String nachname;
    private String alias;

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
