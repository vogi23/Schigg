package vogi.mobpro.hslu.ch.schigg.vogi.mobpro.hslu.ch.schigg.dataaccess;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vogi.mobpro.hslu.ch.schigg.LoadedSchiggHandler;
import vogi.mobpro.hslu.ch.schigg.business.ISchigg;
import vogi.mobpro.hslu.ch.schigg.business.Schigg;

/**
 * Created by lts on 08.05.2015.
 */
public class LoadSchigg extends AsyncTask<URL, Void, List<ISchigg>> {

    private LoadedSchiggHandler schiggHandler;

    public LoadSchigg(LoadedSchiggHandler schiggHandler) {
        this.schiggHandler = schiggHandler;
    }

    @Override
    protected List<ISchigg> doInBackground(URL... urls) {
        URL url = urls[0];
        InputStream inputStream = getInputStream(url);
        List<ISchigg> loadedSchiggs = getSchiggs(inputStream);
        return loadedSchiggs;
    }

    @Override
    protected void onPostExecute(List<ISchigg> loadedSchigg) {
        super.onPostExecute(loadedSchigg);
        this.schiggHandler.handleSchiggs(loadedSchigg);
    }

    private List<ISchigg> getSchiggs(InputStream inputStream) {
        List<ISchigg> loadedSchiggs = new ArrayList<ISchigg>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String jsonString = "";
        try {
            String text = reader.readLine();
            while (text != null) {
                jsonString += text;
                text = reader.readLine();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            JSONArray schiggArray = jsonObject.getJSONArray("value");
            for (int i = 0; i < schiggArray.length(); i++) {
                JSONObject jsonSchigg = schiggArray.getJSONObject(i);
                ISchigg schigg = new Schigg(jsonSchigg.getInt("Id"));
                schigg.setPLZ(jsonSchigg.getString("PoschtLeitZau"));
                schigg.setBeschribig(jsonSchigg.getString("Beschribig"));
                schigg.setWort(jsonSchigg.getString("Wort"));
                loadedSchiggs.add(schigg);
            }
            return loadedSchiggs;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private InputStream getInputStream(URL url) {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        httpURLConnection.setAllowUserInteraction(false);
        httpURLConnection.setInstanceFollowRedirects(false);

        try {
            httpURLConnection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        }
        try {
            inputStream = httpURLConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}
