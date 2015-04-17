package vogi.mobpro.hslu.ch.schigg.business;

/**
 * Created by tgdvoch5 on 17.04.2015.
 */
public interface ISchigg {
    int getID();
    String getWort();
    void setWort(String wort);
    String getBeschribig();
    void setBeschribig(String beschribig);
    String getPLZ();
    void setPLZ(String plz);
}