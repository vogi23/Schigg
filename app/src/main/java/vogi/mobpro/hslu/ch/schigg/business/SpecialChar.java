package vogi.mobpro.hslu.ch.schigg.business;

/**
 * Created by tgdvoch5 on 17.04.2015.
 */
public class SpecialChar {
    private String desc;

    public SpecialChar(String desc){
        this.desc = desc;
    }

    public void setString(String desc){
        this.desc = desc;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
