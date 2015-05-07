package vogi.mobpro.hslu.ch.schigg;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

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


public class LoadingActivity extends Activity {

    private static final int NUM_OF_INITIALLY_LOADED_SCHIGGS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Randomly show one of the welcome images
        ImageView image = (ImageView) findViewById(R.id.welcome_image);
        double rand = Math.random();
        int res_id;
        if (rand < 0.33) {
            res_id = R.drawable.bern1;
        } else if (rand < 0.66) {
            res_id = R.drawable.bern2;
        }else{
            res_id = R.drawable.bern3;
        }
        image.setImageResource(res_id);

        loadInitalSchiggs();
    }


    public void loadInitalSchiggs(){
        LoadInitalListAsyncTask task = new LoadInitalListAsyncTask();
        task.execute(NUM_OF_INITIALLY_LOADED_SCHIGGS);
    }

    private class LoadInitalListAsyncTask extends AsyncTask<Integer, Void, Void>{

        @Override
        protected Void doInBackground(Integer... integers) {
            int numof = integers.length > 0 ? integers[0] : 5;

            List<ISchigg> newschiggs = new ArrayList<>();

            URL urlx = null;
            HttpURLConnection httpURLConnection = null;
            InputStream in;
            try {
                urlx = new URL("http://vgbau.ch/json.htm");
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

            LocalSchiggCache cache = LocalSchiggCache.getInstance();
            cache.setCachedList(new SchiggLinkedList(newschiggs));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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
}
