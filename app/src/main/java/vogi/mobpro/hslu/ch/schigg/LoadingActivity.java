package vogi.mobpro.hslu.ch.schigg;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


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
        task.execute();
    }

    private class LoadInitalListAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            // TODO call webservice instead of SchiggGenerator
            LocalSchiggCache cache = LocalSchiggCache.getInstance();
            cache.setCachedList(new SchiggLinkedList(SchiggGenerator.generateSchiggs(NUM_OF_INITIALLY_LOADED_SCHIGGS)));

            // TODO remove sleep - only for simulating net-delay
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
