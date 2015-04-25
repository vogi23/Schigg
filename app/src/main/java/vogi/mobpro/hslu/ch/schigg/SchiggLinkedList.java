package vogi.mobpro.hslu.ch.schigg;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

import vogi.mobpro.hslu.ch.schigg.business.ISchigg;

/**
 * Created by tgdvoch5 on 18.04.2015.
 */
public class SchiggLinkedList {
    private LinkedList<ISchigg> list;
    private ListIterator<ISchigg> iterator;
    private ISchigg current;

    public SchiggLinkedList(Collection<ISchigg> schiggs){
        this.list = new LinkedList<>();
        this.addAll(schiggs);
        iterator = list.listIterator();
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

    public boolean hasNext(){
        return this.iterator.hasNext();
    }
    public boolean hasPrevious(){
        return this.iterator.hasPrevious();
    }

    public void addAll(Collection<ISchigg> newSchiggs){
        list.addAll(0, newSchiggs);
    }

    public void clear(){
        list.clear();
    }
}
