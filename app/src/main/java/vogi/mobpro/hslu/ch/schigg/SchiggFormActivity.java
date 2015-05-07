package vogi.mobpro.hslu.ch.schigg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import vogi.mobpro.hslu.ch.schigg.business.ISchigg;
import vogi.mobpro.hslu.ch.schigg.business.Schigg;


public class SchiggFormActivity extends Activity {

    private static final int SPECIAL_CHAR_REQUEST_CODE = 232323;

    private static final String NEW_WORT_PREF_KEY = "newwortprefkey";
    private static final String NEW_BESCHRIBIG_PREF_KEY = "newbeschribigprefkey";
    private static final String NEW_PLZ_PREF_KEY = "newplztprefkey";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schigg_form);

        // Load last strings of EditText-Views
        prefs = getPreferences(MODE_PRIVATE);
        EditText wort = (EditText) findViewById(R.id.newschiggWortInput);
        EditText beschribig = (EditText) findViewById(R.id.newschiggBeschribigInput);
        EditText plz = (EditText) findViewById(R.id.newschiggPLZInput);
        wort.setText(prefs.getString(NEW_WORT_PREF_KEY, ""));
        beschribig.setText(prefs.getString(NEW_BESCHRIBIG_PREF_KEY, ""));
        plz.setText(prefs.getString(NEW_PLZ_PREF_KEY, ""));
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(NEW_WORT_PREF_KEY, (((EditText) findViewById(R.id.newschiggWortInput))).getText().toString());
        editor.putString(NEW_BESCHRIBIG_PREF_KEY, ((EditText) findViewById(R.id.newschiggBeschribigInput)).getText().toString());
        editor.putString(NEW_PLZ_PREF_KEY, ((EditText) findViewById(R.id.newschiggPLZInput)).getText().toString());
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schigg_form, menu);
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

    public void btn_specialChar(View v){
        Intent intent = new Intent(this, SpecialCharActivity.class);
        startActivityForResult(intent, SPECIAL_CHAR_REQUEST_CODE);
    }

    /**
     * Callback from SpecialCharActivity - retrieves SpecialChar as String and append it to Wort.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SPECIAL_CHAR_REQUEST_CODE){
            String specialChar = data.getStringExtra(SpecialCharActivity.SPECIAL_CHAR_EXTRA_KEY);
            TextView textWort = (TextView) findViewById(R.id.newschiggWortInput);
            textWort.setText(textWort.getText()+specialChar);
        }
    }

    /**
     * onclick Callback of btn id/btn_save_schigg in activity_schigg_form layout.
     * @param v
     */
    public void btn_save(View v){
        EditText wortText = (EditText) findViewById(R.id.newschiggWortInput);
        EditText beschribigText = (EditText) findViewById(R.id.newschiggBeschribigInput);
        EditText plzText = (EditText) findViewById(R.id.newschiggPLZInput);
        ISchigg schigg = new Schigg();
        schigg.setWort(wortText.getText().toString());
        schigg.setWort(beschribigText.getText().toString());
        schigg.setWort(plzText.getText().toString());

        Toast toast = Toast.makeText(getApplicationContext(), "Beitrag wird hochgeladen...", Toast.LENGTH_SHORT);
        toast.show();

        UploadSchiggAsyncTask task = new UploadSchiggAsyncTask();
        task.execute(schigg);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class UploadSchiggAsyncTask extends AsyncTask<ISchigg, Void, Void>{

        private boolean saved = false;

        @Override
        protected Void doInBackground(ISchigg... schiggs) {

            ISchigg schigg;
            if(schiggs.length > 0){
                schigg = schiggs[0];
            }else{
                return null;
            }

            JSONObject jsonO = new JSONObject();
            try {
                jsonO.put("Wort", schigg.getWort());
                jsonO.put("Beschribig", schigg.getBeschribig());
                jsonO.put("PoschtLeitZau", schigg.getPLZ());
            } catch (JSONException e) {
                return null;
            }

            byte[] postData = jsonO.toString().getBytes();
            int    postDataLength = postData.length;

            URL urlx = null;
            HttpURLConnection httpURLConnection = null;
            InputStream in;
            try {
                urlx = new URL("http://vgbau.ch/json.htm");
                httpURLConnection = (HttpURLConnection) urlx.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setInstanceFollowRedirects(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("charset", "utf-8");
                httpURLConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                httpURLConnection.setUseCaches(false);
                DataOutputStream wr = new DataOutputStream( httpURLConnection.getOutputStream());
                wr.write( postData );
                int responseCode = httpURLConnection.getResponseCode();
                this.saved = httpURLConnection.getResponseCode() == 201;
            } catch (IOException e) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String s = this.saved ? "Danke, dein Beitrag wurde hochgeladen" : "faiiiiiiiil";
            Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
