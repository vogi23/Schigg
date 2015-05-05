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

        @Override
        protected Void doInBackground(ISchigg... schiggs) {
            // TODO call webservice and save Schigg

            // TODO remove sleep - only for simulating net-delay
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast toast = Toast.makeText(getApplicationContext(), "Danke, dein Beitrag wurde hochgeladen", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
