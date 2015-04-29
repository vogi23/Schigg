package vogi.mobpro.hslu.ch.schigg;

/**
 * Created by tgdvoch5 on 28.04.2015.
 */
public class LocalSchiggCache {
    private static LocalSchiggCache localSchiggCache;

    private SchiggLinkedList schiggLinkedList;

    private LocalSchiggCache(){

    }

    public static LocalSchiggCache getInstance(){
        if(localSchiggCache == null){
            localSchiggCache = new LocalSchiggCache();
        }
        return localSchiggCache;
    }

    public SchiggLinkedList getCachedList(){
        return schiggLinkedList;
    }

    public void setCachedList(SchiggLinkedList list){
        this.schiggLinkedList = list;
    }
}
