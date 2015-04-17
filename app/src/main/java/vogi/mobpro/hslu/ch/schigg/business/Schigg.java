package vogi.mobpro.hslu.ch.schigg.business;

import vogi.mobpro.hslu.ch.schigg.business.ISchigg;

/**
 * Created by tgdvoch5 on 17.04.2015.
 */
public class Schigg implements ISchigg {
    private int id;
    private String wort;
    private String beschribig;
    private String plz;

    public Schigg(int id){
        this.id = id;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public String getWort() {
        return this.wort;
    }

    @Override
    public void setWort(String wort) {
        this.wort = wort;
    }

    @Override
    public String getBeschribig() {
        return this.beschribig;
    }

    @Override
    public void setBeschribig(String beschribig) {
        this.beschribig = beschribig;
    }

    @Override
    public String getPLZ() {
        return this.plz;
    }

    @Override
    public void setPLZ(String plz) {
        this.plz = plz;
    }
}
