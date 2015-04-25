package vogi.mobpro.hslu.ch.schigg.business;

/**
 * Created by tgdvoch5 on 17.04.2015.
 */
public class SpecialCharVariant {
    private String word;
    private String theChar;

    public SpecialCharVariant(String highlightChar, String replaceWithChar, String[] parts){
        this.word = "";
        this.theChar = replaceWithChar;
        for(String part : parts){
            if(part.equals(highlightChar)){
                this.word += "<b>"+replaceWithChar+"</b>";
            }else {
                this.word += part;
            }
        }
    }

    public String getChar(){
        return this.theChar;
    }

    public String getHighligtedWord(){
        return this.word;
    }

    @Override
    public String toString() {
        return this.getHighligtedWord();
    }
}
