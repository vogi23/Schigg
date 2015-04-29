package vogi.mobpro.hslu.ch.schigg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import vogi.mobpro.hslu.ch.schigg.business.SpecialChar;
import vogi.mobpro.hslu.ch.schigg.business.SpecialCharVariant;


public class SpecialCharActivity extends Activity implements AdapterView.OnItemClickListener{

    public static final String SPECIAL_CHAR_EXTRA_KEY = "specialcharextra";
    private Map<String, SpecialCharVariant[]> charMap;
    private SpecialCharVariant[] actCharArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_char);

        SpecialCharVariant[] variantsA = {
                new SpecialCharVariant("A", "A", new String[]{"A", "dubode"}),
                new SpecialCharVariant("A", "ä", new String[]{"L", "A", "nk"}),
        };
        SpecialCharVariant[] variantsE = {
                new SpecialCharVariant("E", "e", new String[]{"S", "E", "pp"}),
                new SpecialCharVariant("E", "è", new String[]{"Landstriich", "E", "r"}),
        };
        SpecialCharVariant[] variantsI = {
                new SpecialCharVariant("I", "i", new String[]{"B", "I", "iel / B", "I", "enne"}),
                new SpecialCharVariant("I", "î", new String[]{"S", "I", "ngä"}),
                new SpecialCharVariant("I", "ì", new String[]{"I", "Pod"})
        };
        SpecialCharVariant[] variantsO = {
                new SpecialCharVariant("O", "o", new String[]{"M", "O", "nd"}),
                new SpecialCharVariant("O", "Ö", new String[]{"O", "zi"}),
        };
        SpecialCharVariant[] variantsU = {
                new SpecialCharVariant("U", "u", new String[]{"M", "U", "sig"}),
                new SpecialCharVariant("U", "ù", new String[]{"Z", "U", "g"}),
                new SpecialCharVariant("U", "ü", new String[]{"R", "U", "ebli"}),
        };

        this.charMap = new HashMap<>();
        charMap.put("A", variantsA);
        charMap.put("E", variantsE);
        charMap.put("I", variantsI);
        charMap.put("O", variantsO);
        charMap.put("U", variantsU);

    }

    public void btn_selectChar(View v){
        Button b = (Button) v;
        this.setVariants(this.charMap.get(b.getText()));
        this.actCharArray = this.charMap.get(b.getText());
    }

    public void setVariants(SpecialCharVariant[] items){
        ArrayAdapter<SpecialCharVariant> adapter = new ArrayAdapter<SpecialCharVariant>(this, R.layout.specialchar_list_item, items);
        ListView listView = (ListView) findViewById(R.id.specialCharListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_special_char, menu);
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SpecialCharVariant var = this.actCharArray[position];
        Intent intent = new Intent();
        intent.putExtra(SPECIAL_CHAR_EXTRA_KEY, var.getChar());
        setResult(RESULT_OK, intent);
        finish();
    }
}
