package vogi.mobpro.hslu.ch.schigg;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;


public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
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

        startWaitAysnc();
    }


    public void startWaitAysnc(){
        WaitAsyncTask task = new WaitAsyncTask();
        task.execute();
    }

    private class WaitAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LocalSchiggCache cache = LocalSchiggCache.getInstance();
            cache.setCachedList(new SchiggLinkedList(SchiggGenerator.generateSchiggs(5)));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
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
