package vogi.mobpro.hslu.ch.schigg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vogi.mobpro.hslu.ch.schigg.business.ISchigg;
import vogi.mobpro.hslu.ch.schigg.business.Schigg;
import vogi.mobpro.hslu.ch.schigg.http.OnSwipeTouchListener;


public class MainActivity extends Activity{

    private final String PREF_KEY_ACTUAL_SCHIGG_ID = "actual_schigg_id";
    private int actualSchiggIndex;
    private SchiggLinkedList schiggList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        this.actualSchiggIndex = prefs.getInt(PREF_KEY_ACTUAL_SCHIGG_ID, 0);

        LocalSchiggCache cache = LocalSchiggCache.getInstance();
        this.schiggList = cache.getCachedList();
        if(this.schiggList == null){
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            return;
        }

        this.schiggList.get(this.actualSchiggIndex);
        syncScrollerToList();

        // Swipe Listener
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
    }



    private void rotateRight(){
        ISchigg schigg = schiggList.previous();
        if(schigg != null){
            syncScrollerToList();
        }else{
            Button buttonRight = (Button) findViewById(R.id.btn_rotateRight);
            ProgressBar barRight = (ProgressBar) findViewById(R.id.progress_rotateRight);
            buttonRight.setVisibility(View.GONE);
            barRight.setVisibility(View.VISIBLE);
            this.appendLeft(SchiggGenerator.generateSchiggs(3));
            buttonRight.setVisibility(View.VISIBLE);
            barRight.setVisibility(View.GONE);
            schiggList.previous();
            syncScrollerToList();
        }
    }

    private void rotateLeft(){
        ISchigg schigg = schiggList.next();
        if(schigg != null){
            syncScrollerToList();
        }else{
            Button buttonLeft = (Button) findViewById(R.id.btn_rotateLeft);
            ProgressBar barLeft = (ProgressBar) findViewById(R.id.progress_rotateLeft);
            buttonLeft.setVisibility(View.GONE);
            barLeft.setVisibility(View.VISIBLE);
            this.appendRight(SchiggGenerator.generateSchiggs(3));
            buttonLeft.setVisibility(View.VISIBLE);
            barLeft.setVisibility(View.GONE);
            schiggList.next();
            syncScrollerToList();
        }
    }

    private void appendLeft(List<ISchigg> list){
        schiggList.addAllLeft(list);
    }
    private void appendRight(List<ISchigg> list){
        schiggList.addAllRight(list);
    }


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

    public void btn_newWort(View v){
        Intent intent = new Intent(this, SchiggFormActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_KEY_ACTUAL_SCHIGG_ID, this.actualSchiggIndex);
        editor.apply();
    }

    public void btn_rotateRight(View v){
        this.rotateRight();
    }
    public void btn_rotateLeft(View v){
        this.rotateLeft();
    }
}
