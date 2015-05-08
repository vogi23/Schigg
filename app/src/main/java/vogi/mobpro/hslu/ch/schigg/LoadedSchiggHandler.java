package vogi.mobpro.hslu.ch.schigg;

import java.util.List;

import vogi.mobpro.hslu.ch.schigg.business.ISchigg;

/**
 * Created by lts on 08.05.2015.
 */
public interface LoadedSchiggHandler {
    void handleSchiggs(List<ISchigg> loadedSchigg);
}
