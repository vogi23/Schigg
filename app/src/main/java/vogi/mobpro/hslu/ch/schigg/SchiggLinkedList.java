package vogi.mobpro.hslu.ch.schigg;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

import vogi.mobpro.hslu.ch.schigg.business.ISchigg;

/**
 * Sortierte Liste mit Schiggs durch welche im Scroller auf der MainActivity gescrollt wird.
 *
 * Created by tgdvoch5 on 18.04.2015.
 */
public class SchiggLinkedList {
    private LinkedList<ISchigg> list;
    private ListIterator<ISchigg> iterator;
    private ISchigg current;

    public SchiggLinkedList(Collection<ISchigg> schiggs){
        this.list = new LinkedList<>();
        iterator = list.listIterator();
        this.addAllLeft(schiggs);
        if(iterator.hasNext()){current = iterator.next();}
    }

    public ISchigg get(int index){
        ISchigg schigg = list.get(index);
        if(schigg != null){
            this.iterator = list.listIterator(index);
            current = schigg;
        }
        return schigg;
    }

    /**
     * Returns next Schigg after current in List.
     *
     * @return
     */
    public ISchigg next(){
        if(!iterator.hasNext()){
            return null;
        }else{
            ISchigg next = iterator.next();
            if(next == current){
                return next();
            }
            current = next;
            return current;
        }
    }

    /**
     * Returns previous Schigg before current in List.
     *
     * @return
     */
    public ISchigg previous(){
        if(!iterator.hasPrevious()){
            return null;
        }else{
            ISchigg prev = iterator.previous();
            if(prev == current){
                return previous();
            }
            current = prev;
            return current;
        }
    }

    public ISchigg current(){
        return current;
    }
    public int currentIndex(){
        return this.list.indexOf(current);
    }

    public boolean hasNext(){
        return this.iterator.hasNext();
    }
    public boolean hasPrevious(){
        return this.iterator.hasPrevious();
    }

    /**
     * Adds Schiggs to to left end (newer) of the list.
     *
     * ListIterator is updated, so that next() + previous() will still stay up to date.
     *
     * @param newSchiggs
     */
    public void addAllLeft(Collection<ISchigg> newSchiggs){
        int pos = iterator.nextIndex();
        list.addAll(0, newSchiggs);
        iterator = list.listIterator(pos+newSchiggs.size());
    }

    /**
     * Adds Schiggs to to right end (older) of the list.
     *
     * ListIterator is updated, so that next() + previous() will still stay up to date.
     *
     * @param newSchiggs
     */
    public void addAllRight(Collection<ISchigg> newSchiggs){
        int pos = iterator.nextIndex();
        list.addAll(newSchiggs);
        iterator = list.listIterator(pos);
    }

    public void clear(){
        list.clear();
    }
}
