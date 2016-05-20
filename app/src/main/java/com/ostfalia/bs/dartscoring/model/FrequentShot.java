package com.ostfalia.bs.dartscoring.model;

/**
 * Created by lukas on 19.05.2016.
 */
public class FrequentShot {

    private Integer punkte;
    private Integer count;

    public FrequentShot(Integer punkte, Integer count) {
        this.punkte = punkte;
        this.count = count;
    }

    public Integer getPunkte() {
        return punkte;
    }

    public void setPunkte(Integer punkte) {
        this.punkte = punkte;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
