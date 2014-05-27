package ru.amobilestudio.razborapp.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import ru.amobilestudio.razborapp.custom.MyAutoComplete;
import ru.amobilestudio.razborapp.helpers.Connection;
import ru.amobilestudio.razborapp.helpers.CreatePartAsync;
import ru.amobilestudio.razborapp.helpers.DataFieldsAsync;
import ru.amobilestudio.razborapp.helpers.DictionariesSQLiteHelper;
import ru.amobilestudio.razborapp.helpers.SendPartAsync;


public class AddPartActivity extends ActionBarActivity implements View.OnClickListener {

    public static EditText _partsPriceSell;
    public static EditText _partsPriceBuy;
    public static EditText _partsComment;

    public static MyAutoComplete _partsCategoryId;
    public static MyAutoComplete _partsCarModelId;
    public static MyAutoComplete _partsLocationId;
    public static MyAutoComplete _partsSupplierId;
    public static MyAutoComplete _partsBuId;

    //for POST send
    public static int _categoryId;
    public static int _carModelId;
    public static int _locationId;
    public static int _supplierId;
    public static int _buId;

    private static Button _sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_part);

        //init
        _partsPriceSell = (EditText) findViewById(R.id.parts_price_sell);
        _partsPriceBuy = (EditText) findViewById(R.id.parts_price_buy);
        _partsComment = (EditText) findViewById(R.id.parts_comment);

        _partsCategoryId = (MyAutoComplete) findViewById(R.id.parts_category_id);
        _partsCarModelId = (MyAutoComplete) findViewById(R.id.parts_car_model_id);
        _partsLocationId = (MyAutoComplete) findViewById(R.id.parts_location_id);
        _partsSupplierId = (MyAutoComplete) findViewById(R.id.parts_supplier_id);
        _partsBuId = (MyAutoComplete) findViewById(R.id.parts_bu_id);

        _sendButton = (Button) findViewById(R.id.save_part_button);
        _sendButton.setOnClickListener(this);



        //check connection
        if(Connection.checkNetworkConnection(this)){

            //when click on item ListView
            Bundle extras = getIntent().getExtras();

            if(extras == null){
                //create part
                CreatePartAsync createPartAsync = new CreatePartAsync(this);
                createPartAsync.execute();
            }else{
                int clicked_part_id = extras.getInt("part_id");
            }

            setSelectsField(this);
        }
    }

    public static void setSelectsField(final Context context){
        DictionariesSQLiteHelper dsh = new DictionariesSQLiteHelper(context);

        //categories
        ArrayList<DictionariesSQLiteHelper.Item> list = dsh.getAll(DictionariesSQLiteHelper.TABLE_NAME_CATEGORIES);
        ItemAdapter adapter = new ItemAdapter(context, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _partsCategoryId.setAdapter(adapter);
        _partsCategoryId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DictionariesSQLiteHelper.Item item = (DictionariesSQLiteHelper.Item) adapterView.getItemAtPosition(i);
                _categoryId = item.getId();
            }
        });

        //car models
        list = dsh.getAll(DictionariesSQLiteHelper.TABLE_NAME_CAR_MODELS);
        adapter = new ItemAdapter(context, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _partsCarModelId.setAdapter(adapter);
        _partsCarModelId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DictionariesSQLiteHelper.Item item = (DictionariesSQLiteHelper.Item) adapterView.getItemAtPosition(i);
                _carModelId = item.getId();
            }
        });

        //locations
        list = dsh.getAll(DictionariesSQLiteHelper.TABLE_NAME_LOCATIONS);
        adapter = new ItemAdapter(context, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _partsLocationId.setAdapter(adapter);
        _partsLocationId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DictionariesSQLiteHelper.Item item = (DictionariesSQLiteHelper.Item) adapterView.getItemAtPosition(i);
                _locationId = item.getId();
            }
        });

        //suppliers
        list = dsh.getAll(DictionariesSQLiteHelper.TABLE_NAME_SUPPLIERS);
        adapter = new ItemAdapter(context, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _partsSupplierId.setAdapter(adapter);
        _partsSupplierId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DictionariesSQLiteHelper.Item item = (DictionariesSQLiteHelper.Item) adapterView.getItemAtPosition(i);
                _supplierId = item.getId();
            }
        });

        //bu
        list = dsh.getAll(DictionariesSQLiteHelper.TABLE_NAME_BU_CARS);
        adapter = new ItemAdapter(context, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _partsBuId.setAdapter(adapter);
        _partsBuId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DictionariesSQLiteHelper.Item item = (DictionariesSQLiteHelper.Item) adapterView.getItemAtPosition(i);
                _buId = item.getId();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_part, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_part_button:
                sendPart();
                break;
        }
    }

    private void sendPart(){
        //get ID part
        SharedPreferences part_info = getSharedPreferences(DataFieldsAsync.DB_PREFS, Context.MODE_PRIVATE);
        int id = part_info.getInt("part_id", 0);

        if(id != 0 && Connection.checkNetworkConnection(this)){
            SendPartAsync sendTask = new SendPartAsync(this);
            sendTask.execute(id);
        }
    }

    public static class ItemAdapter extends ArrayAdapter<DictionariesSQLiteHelper.Item>{

        private ArrayList<DictionariesSQLiteHelper.Item> _items;
        private Context _context;

        public ItemAdapter(Context context, int resource, ArrayList<DictionariesSQLiteHelper.Item> items) {
            super(context, resource, items);
            _items = items;
            _context = context;
        }

        @Override
        public int getCount() {
            return _items.size();
        }

        @Override
        public DictionariesSQLiteHelper.Item getItem(int position) {
            return _items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return _items.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(_context);
            label.setText(_items.get(position).getValue());

            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(_context);
            label.setText(_items.get(position).getValue());

            return label;
        }
    }
}
