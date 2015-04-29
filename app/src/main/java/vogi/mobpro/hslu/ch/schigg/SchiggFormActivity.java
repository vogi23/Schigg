package vogi.mobpro.hslu.ch.schigg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SPECIAL_CHAR_REQUEST_CODE){
            String specialChar = data.getStringExtra(SpecialCharActivity.SPECIAL_CHAR_EXTRA_KEY);
            TextView textWort = (TextView) findViewById(R.id.newschiggWortInput);
            textWort.setText(textWort.getText()+specialChar);
        }
    }

    public void btn_save(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
