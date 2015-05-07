package vogi.mobpro.hslu.ch.schigg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vogi.mobpro.hslu.ch.schigg.business.ISchigg;
import vogi.mobpro.hslu.ch.schigg.business.Schigg;


public class MainActivity extends Activity{

    private final String PREF_KEY_ACTUAL_SCHIGG_INDEX = "actual_schigg_index"; // Persist position of Scroller while MainAcitvity is destroyed.

    private SchiggLinkedList schiggList;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get cached list for scroller
        LocalSchiggCache cache = LocalSchiggCache.getInstance();
        this.schiggList = cache.getCachedList();
        if(this.schiggList == null) {
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
    private void rotateRight(){
        ISchigg schigg = schiggList.previous();
        if(schigg != null){
            syncScrollerToList();
        }else if(this.isLoading == false){
            this.isLoading = true;
            Button buttonRight = (Button) findViewById(R.id.btn_rotateRight);
            ProgressBar barRight = (ProgressBar) findViewById(R.id.progress_rotateRight);
            buttonRight.setVisibility(View.GONE);
            barRight.setVisibility(View.VISIBLE);
            LoadMoreSchiggsAsyncTask task = new LoadMoreSchiggsAsyncTask();
            task.execute(1,5);
        }
    }

    /**
     * Rotate scroller to the left - display next OLDER Schigg.
     */
    private void rotateLeft(){
        ISchigg schigg = schiggList.next();
        if(schigg != null){
            syncScrollerToList();
        }else if(this.isLoading == false){
            this.isLoading = true;
            Button buttonLeft = (Button) findViewById(R.id.btn_rotateLeft);
            ProgressBar barLeft = (ProgressBar) findViewById(R.id.progress_rotateLeft);
            buttonLeft.setVisibility(View.GONE);
            barLeft.setVisibility(View.VISIBLE);
            LoadMoreSchiggsAsyncTask task = new LoadMoreSchiggsAsyncTask();
            task.execute(2, 5);
        }
    }

    private class LoadMoreSchiggsAsyncTask extends AsyncTask<Integer, Void, Void> {

        private boolean newer;

        @Override
        protected Void doInBackground(Integer... integers) {
            this.newer = integers.length > 0 && integers[0] == 1;
            int id = integers.length > 1 ? integers[1] : 1;
            int numof = integers.length > 2 ? integers[2] : 5;

            //String[] urls = {   "localhost/schigg?$orderby=id asc&$top=5&$filter=id gt '44'",
            //                    "localhost/schigg?$orderby=id desc&$top=5&$filter=id lt '44'"};
            String[] urls = {   "http://vgbau.ch/json.htm","http://vgbau.ch/json.htm"};

            String url = newer ? urls[0] : urls[1];

            List<ISchigg> newschiggs = new ArrayList<>();

            URL urlx = null;
            HttpURLConnection httpURLConnection = null;
            InputStream in;
            try {
                urlx = new URL(url);
                httpURLConnection = (HttpURLConnection) urlx.openConnection();
                httpURLConnection.setAllowUserInteraction(false);
                httpURLConnection.setInstanceFollowRedirects(true);
                httpURLConnection.setRequestMethod("GET");
                in = httpURLConnection.getInputStream();
            } catch (MalformedURLException e) {
                return null;
            } catch (ProtocolException e) {
                return null;
            } catch (IOException e) {
                return null;
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String text = reader.readLine();
                in.close();

                JSONArray json = null;
                JSONObject mainO = new JSONObject(text);
                JSONArray schiggArray = mainO.getJSONArray("value");
                for (int i = 0 ; i < schiggArray.length(); i++) {
                    JSONObject schiggO = schiggArray.getJSONObject(i);
                    int schiggId = schiggO.getInt("Id");
                    String wort = schiggO.getString("Wort");
                    String beschribig = schiggO.getString("Beschribig");
                    String plz = schiggO.getString("PoschtLeitZau");
                    ISchigg schigg = new Schigg(schiggId);
                    schigg.setWort(wort);
                    schigg.setBeschribig(beschribig);
                    schigg.setPLZ(plz);
                    newschiggs.add(schigg);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(newer){
                MainActivity.this.schiggList.addAllLeft(newschiggs);
            }else{
                MainActivity.this.schiggList.addAllRight(newschiggs);
            }

            LocalSchiggCache cache = LocalSchiggCache.getInstance();
            cache.setCachedList(MainActivity.this.schiggList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(this.newer) {
                MainActivity.this.schiggList.previous();
            }else{
                MainActivity.this.schiggList.next();
            }
            syncScrollerToList();
            MainActivity.this.isLoading = false;
        }
    }



    private void appendLeft(List<ISchigg> list){
        schiggList.addAllLeft(list);
    }
    private void appendRight(List<ISchigg> list){
        schiggList.addAllRight(list);
    }

    /**
     * Displays Schigg at current position of SchiggLinkedList and updates scrolling-buttons-visibility.
     */
    private void syncScrollerToList(){
        ISchigg schigg = this.schiggList.current();
        TextView wortText = (TextView) findViewById(R.id.schiggScrollerWort);
        wortText.setText(schigg.getWort());
        TextView beschribigText = (TextView) findViewById(R.id.schiggScrollerBeschribig);
        beschribigText.setText(schigg.getBeschribig());
        TextView plzText = (TextView) findViewById(R.id.schiggScrollerPLZ);
        plzText.setText(schigg.getPLZ());

        // Buttons
        Button btnRight = (Button) findViewById(R.id.btn_rotateRight);
        if(!this.schiggList.hasPrevious()){
            btnRight.setVisibility(View.INVISIBLE);
        }else{
            btnRight.setVisibility(View.VISIBLE);
        }
        Button btnLeft = (Button) findViewById(R.id.btn_rotateLeft);
        if(!this.schiggList.hasNext()){
            btnLeft.setVisibility(View.INVISIBLE);
        }else{
            btnLeft.setVisibility(View.VISIBLE);
        }

        ProgressBar barLeft = (ProgressBar) findViewById(R.id.progress_rotateLeft);
        ProgressBar barRight = (ProgressBar) findViewById(R.id.progress_rotateRight);
        barLeft.setVisibility(View.GONE);
        barRight.setVisibility(View.GONE);
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

    /**
     * onclick Callback of btn id/btn_add_wort in main_activity layout.
     * @param v
     */
    public void btn_newWort(View v){
        Intent intent = new Intent(this, SchiggFormActivity.class);
        startActivity(intent);
    }

    /**
     * onclick Callback of btn id/btn_rotateRight in main_activity layout.
     * @param v
     */
    public void btn_rotateRight(View v){
        this.rotateRight();
    }
    /**
     * onclick Callback of btn id/btn_rotateLeft in main_activity layout.
     * @param v
     */
    public void btn_rotateLeft(View v){
        this.rotateLeft();
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
