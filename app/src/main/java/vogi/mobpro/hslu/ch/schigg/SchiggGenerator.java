package vogi.mobpro.hslu.ch.schigg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import vogi.mobpro.hslu.ch.schigg.business.ISchigg;
import vogi.mobpro.hslu.ch.schigg.business.Schigg;

/**
 * This class is used only as a placeholder for the webservice.
 *
 * Created by tgdvoch5 on 18.04.2015.
 */
public class SchiggGenerator {

    private static String characters = "abcdefghijklmnopqrstuvwxyz";
    private static Random random;
    private static Random getRandom(){
        if(random == null){random = new Random();}
        return random;
    }

    private static String generateString(int length){
        char[] text = new char[length];
        for (int i = 0; i < length; i++){
            text[i] = characters.charAt(getRandom().nextInt(characters.length()));
        }
        return new String(text);
    }

    public static String getWort(int length){
        return generateString(length);
    }

    public static String getBeschribig(int length){
        String s = "";
        while(s.length() < length){
            s += generateString(getRandom().nextInt(10)+3)+" ";
        }
        return s;
    }

    public static String getPLZ(){
        return ""+(getRandom().nextInt(999)+1000);
    }

    public static List<ISchigg> generateSchiggs(int amount){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<ISchigg> list = new ArrayList<>();
        for(int i = 0; i < amount;i++){
            ISchigg s = new Schigg();
            s.setWort(getWort(getRandom().nextInt(10)+3));
            s.setBeschribig(getBeschribig(getRandom().nextInt(40)+10));
            s.setPLZ(getPLZ());
            list.add(s);
        }
        return list;
    }
}
