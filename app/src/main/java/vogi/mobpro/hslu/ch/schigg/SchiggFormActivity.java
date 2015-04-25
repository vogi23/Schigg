package vogi.mobpro.hslu.ch.schigg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class SchiggFormActivity extends Activity {

    private static final int SPECIAL_CHAR_REQUEST_CODE = 232323;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schigg_form);
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
