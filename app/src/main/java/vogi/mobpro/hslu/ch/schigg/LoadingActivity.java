package vogi.mobpro.hslu.ch.schigg;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import vogi.mobpro.hslu.ch.schigg.business.ISchigg;
import vogi.mobpro.hslu.ch.schigg.vogi.mobpro.hslu.ch.schigg.dataaccess.LoadSchigg;


public class LoadingActivity extends Activity implements LoadedSchiggHandler {

    private static final Integer NUM_OF_INITIALLY_LOADED_SCHIGGS = 2;

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
        String uri = Uri.parse(getResources().getString(R.string.url))
                .buildUpon()
                .appendQueryParameter("$top", NUM_OF_INITIALLY_LOADED_SCHIGGS.toString())
                .appendQueryParameter("$orderby", "Id desc")
                .build().toString();
        URL urlx = null;
        try {
            urlx = new URL(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        LoadSchigg task = new LoadSchigg(this);
        task.execute(urlx);
    }

    @Override
    public void handleSchiggs(List<ISchigg> loadedSchigg) {
        LocalSchiggCache cache = LocalSchiggCache.getInstance();
        cache.setCachedList(new SchiggLinkedList(loadedSchigg));
        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        startActivity(intent);
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
