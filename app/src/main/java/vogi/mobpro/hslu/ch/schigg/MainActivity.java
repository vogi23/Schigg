package vogi.mobpro.hslu.ch.schigg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import vogi.mobpro.hslu.ch.schigg.business.ISchigg;
import vogi.mobpro.hslu.ch.schigg.business.Schigg;
import vogi.mobpro.hslu.ch.schigg.vogi.mobpro.hslu.ch.schigg.dataaccess.LoadSchigg;


public class MainActivity extends Activity implements LoadedSchiggHandler {

    private final String PREF_KEY_ACTUAL_SCHIGG_INDEX = "actual_schigg_index"; // Persist position of Scroller while MainAcitvity is destroyed.

    private SchiggLinkedList schiggList;
    private boolean isLoading = false;
    private boolean loadNewer;

    /**
     * onclick Callback of btn id/btn_add_wort in main_activity layout.
     *
     * @param v
     */
    public void btn_newWort(View v) {
        Intent intent = new Intent(this, SchiggFormActivity.class);
        startActivity(intent);
    }

    /**
     * onclick Callback of btn id/btn_rotateRight in main_activity layout.
     *
     * @param v
     */
    public void btn_rotateRight(View v) {
        this.rotateRight();
    }

    /**
     * onclick Callback of btn id/btn_rotateLeft in main_activity layout.
     *
     * @param v
     */
    public void btn_rotateLeft(View v) {
        this.rotateLeft();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleSchiggs(List<ISchigg> loadedSchigg) {
        if (loadNewer) {
            schiggList.addAllLeft(loadedSchigg);
        } else {
            schiggList.addAllRight(loadedSchigg);
        }

        LocalSchiggCache cache = LocalSchiggCache.getInstance();
        cache.setCachedList(schiggList);
        isLoading = false;
        syncScrollerToList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get cached list for scroller
        LocalSchiggCache cache = LocalSchiggCache.getInstance();
        this.schiggList = cache.getCachedList();
        if (this.schiggList == null) {
            // If no cached list, Load initial list from webservice
            Intent intent = new Intent(this, LoadingActivity.class);
            startActivity(intent);
            return;
        }


        // Retrive last position of Scroller
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        int actualSchiggIndex = prefs.getInt(PREF_KEY_ACTUAL_SCHIGG_INDEX, 0);

        // Swipe Listener for Scroller
        RelativeLayout view = (RelativeLayout) findViewById(R.id.main_container);
        view.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                MainActivity.this.rotateLeft();
            }

            @Override
            public void onSwipeRight() {
                MainActivity.this.rotateRight();
            }
        });

        this.schiggList.get(actualSchiggIndex);
        syncScrollerToList();
    }

    /**
     * Rotate scroller to the right - display next NEWER Schigg.
     */
    private void rotateRight() {
        ISchigg schigg = schiggList.previous();
        if (schigg != null) {
            syncScrollerToList();
            if (!schiggList.hasPrevious() && !this.isLoading) {
                loadMoreSchiggs(R.id.btn_rotateRight, R.id.progress_rotateRight, true);
            }
        }
    }

    /**
     * Rotate scroller to the left - display next OLDER Schigg.
     */
    private void rotateLeft() {
        ISchigg schigg = schiggList.next();
        if (schigg != null) {
            syncScrollerToList();
            if (!schiggList.hasNext() && !this.isLoading) {
                loadMoreSchiggs(R.id.btn_rotateLeft, R.id.progress_rotateLeft, false);
            }
        }
    }

    private void loadMoreSchiggs(Integer buttonId, Integer barId, boolean fromTop) {
        this.isLoading = true;
        loadNewer = fromTop;
        Button buttonRight = (Button) findViewById(buttonId);
        ProgressBar barRight = (ProgressBar) findViewById(barId);
        buttonRight.setVisibility(View.GONE);
        barRight.setVisibility(View.VISIBLE);
        LoadSchigg task = new LoadSchigg(this);
        URL url = null;
        try {
            url = new URL(createUrl(fromTop, schiggList.current().getID(), 5));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        task.execute(url);
    }

    private String createUrl(boolean newer, Integer id, Integer numof) {
        String[] urls = {getResources().getString(R.string.url) + "?$orderby=id asc&$top=" + numof + "&$filter=id gt " + id,
                getResources().getString(R.string.url) + "?$orderby=id desc&$top=" + numof + "&$filter=id lt " + id};

        Uri.Builder uriBuilder = Uri.parse(getResources().getString(R.string.url)).buildUpon();
        String direction = newer ? "Id asc" : "Id desc";
        String filter = newer ? "Id gt " : "Id lt ";
        filter = filter + id.toString();

        String uri = uriBuilder.appendQueryParameter("$orderby", direction)
                .appendQueryParameter("$filter", filter)
                .appendQueryParameter("$top", numof.toString()).build().toString();

        return uri;
    }

    /**
     * Displays Schigg at current position of SchiggLinkedList and updates scrolling-buttons-visibility.
     */
    private void syncScrollerToList() {
        ISchigg schigg = this.schiggList.current();
        TextView wortText = (TextView) findViewById(R.id.schiggScrollerWort);
        wortText.setText(schigg.getWort());
        TextView beschribigText = (TextView) findViewById(R.id.schiggScrollerBeschribig);
        beschribigText.setText(schigg.getBeschribig());
        TextView plzText = (TextView) findViewById(R.id.schiggScrollerPLZ);
        plzText.setText(schigg.getPLZ());

        // Buttons
        Button btnRight = (Button) findViewById(R.id.btn_rotateRight);
        if (!this.schiggList.hasPrevious()) {
            btnRight.setVisibility(View.INVISIBLE);
        } else {
            btnRight.setVisibility(View.VISIBLE);
        }
        Button btnLeft = (Button) findViewById(R.id.btn_rotateLeft);
        if (!this.schiggList.hasNext()) {
            btnLeft.setVisibility(View.INVISIBLE);
        } else {
            btnLeft.setVisibility(View.VISIBLE);
        }

        ProgressBar barLeft = (ProgressBar) findViewById(R.id.progress_rotateLeft);
        ProgressBar barRight = (ProgressBar) findViewById(R.id.progress_rotateRight);
        barLeft.setVisibility(View.GONE);
        barRight.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_KEY_ACTUAL_SCHIGG_INDEX, this.schiggList.currentIndex());
        editor.apply();
    }
}
