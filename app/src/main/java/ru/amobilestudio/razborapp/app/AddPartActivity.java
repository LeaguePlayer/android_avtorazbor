package ru.amobilestudio.razborapp.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.amobilestudio.razborapp.helpers.Connection;
import ru.amobilestudio.razborapp.helpers.CreatePartAsync;
import ru.amobilestudio.razborapp.helpers.DataFieldsAsync;
import ru.amobilestudio.razborapp.helpers.DictionariesSQLiteHelper;
import ru.amobilestudio.razborapp.helpers.Part;
import ru.amobilestudio.razborapp.helpers.SendImageAsync;
import ru.amobilestudio.razborapp.helpers.SendPartAsync;


public class AddPartActivity extends ActionBarActivity implements View.OnClickListener {

    public static EditText _partsPriceSell;
    public static EditText _partsPriceBuy;
    public static EditText _partsComment;

    static final int REQUEST_IMAGE_CAPTURE = 1;

//    public static MyAutoComplete _partsCategoryId;
    public static Spinner _partsCategoryId;
    public static Spinner _partsCarModelId;
    public static Spinner _partsLocationId;
    public static Spinner _partsSupplierId;
    public static Spinner _partsBuId;

    //for POST send
    public static int _categoryId;
    public static int _carModelId;
    public static int _locationId;
    public static int _supplierId;
    public static int _buId;

    private static Button _sendButton;
    private static Button _takePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_part);

        //init
        _partsPriceSell = (EditText) findViewById(R.id.parts_price_sell);
        _partsPriceBuy = (EditText) findViewById(R.id.parts_price_buy);
        _partsComment = (EditText) findViewById(R.id.parts_comment);

        _partsCategoryId = (Spinner) findViewById(R.id.parts_category_id);
        _partsCarModelId = (Spinner) findViewById(R.id.parts_car_model_id);
        _partsLocationId = (Spinner) findViewById(R.id.parts_location_id);
        _partsSupplierId = (Spinner) findViewById(R.id.parts_supplier_id);
        _partsBuId = (Spinner) findViewById(R.id.parts_bu_id);

        _sendButton = (Button) findViewById(R.id.save_part_button);
        _sendButton.setOnClickListener(this);

        _takePhoto = (Button) findViewById(R.id.take_photo);
        _takePhoto.setOnClickListener(this);

        setSelectsField(this);

        //when click on item ListView
        Bundle extras = getIntent().getExtras();

        if(extras == null){
            //check connection
            if(Connection.checkNetworkConnection(this)){
                CreatePartAsync createPartAsync = new CreatePartAsync(this);
                createPartAsync.execute();
            }
        }else{
            Part part = (Part) getIntent().getSerializableExtra("Part");

            SharedPreferences part_info = getSharedPreferences(DataFieldsAsync.DB_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = part_info.edit();

            editor.putInt("part_id", part.get_id());
            editor.commit();

            setPartForm(part);
        }
    }

    private void setPartForm(Part part){
        TextView tv = (TextView) findViewById(R.id.article_part);

        String text = tv.getText().toString();
        tv.setText(text + " " + part.get_id());

        ProgressBar loader = (ProgressBar) findViewById(R.id.loader);
        loader.setVisibility(View.GONE);

        //format for price
        DecimalFormat df = new DecimalFormat("#");

        String value = (part != null) ? df.format(part.getPrice_sell()) : "";
        _partsPriceSell.setText(value);

        value = (part != null) ? df.format(part.getPrice_buy()) : "";
        _partsPriceBuy.setText(value);

        _partsComment.setText(part.getComment());

        //selects
        final DictionariesSQLiteHelper dictionariesSQLiteHelper = new DictionariesSQLiteHelper(this);
        int position = dictionariesSQLiteHelper.getPositionById(part.getCategory_id(), DictionariesSQLiteHelper.TABLE_NAME_CATEGORIES);
        _partsCategoryId.setSelection(position);

        position = dictionariesSQLiteHelper.getPositionById(part.getCar_model_id(), DictionariesSQLiteHelper.TABLE_NAME_CAR_MODELS);
        _partsCarModelId.setSelection(position);

        position = dictionariesSQLiteHelper.getPositionById(part.getLocation_id(), DictionariesSQLiteHelper.TABLE_NAME_LOCATIONS);
        _partsLocationId.setSelection(position + 1);

        position = dictionariesSQLiteHelper.getPositionById(part.getSupplier_id(), DictionariesSQLiteHelper.TABLE_NAME_SUPPLIERS);
        _partsSupplierId.setSelection(position + 1);

        position = dictionariesSQLiteHelper.getPositionById(part.getUsed_car_id(), DictionariesSQLiteHelper.TABLE_NAME_BU_CARS);
        _partsBuId.setSelection(position + 1);
    }

    public static void setSelectsField(final Context context){
        DictionariesSQLiteHelper dsh = new DictionariesSQLiteHelper(context);

        AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DictionariesSQLiteHelper.Item item = (DictionariesSQLiteHelper.Item) adapterView.getItemAtPosition(i);
                switch (adapterView.getId()){
                    case R.id.parts_category_id:
                        _categoryId = item.getId();
                        break;
                    case R.id.parts_car_model_id:
                        _carModelId = item.getId();
                        break;
                    case R.id.parts_location_id:
                        _locationId = item.getId();
                        break;
                    case R.id.parts_supplier_id:
                        _supplierId = item.getId();
                        break;
                    case R.id.parts_bu_id:
                        _buId = item.getId();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };

        //categories
        ArrayList<DictionariesSQLiteHelper.Item> list = dsh.getAll(DictionariesSQLiteHelper.TABLE_NAME_CATEGORIES, false);
        ItemAdapter adapter = new ItemAdapter(context, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _partsCategoryId.setAdapter(adapter);
        _partsCategoryId.setOnItemSelectedListener(selectedListener);

        //car models
        list = dsh.getAll(DictionariesSQLiteHelper.TABLE_NAME_CAR_MODELS, false);
        adapter = new ItemAdapter(context, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _partsCarModelId.setAdapter(adapter);
        _partsCarModelId.setOnItemSelectedListener(selectedListener);

        //locations
        list = dsh.getAll(DictionariesSQLiteHelper.TABLE_NAME_LOCATIONS, true);
        adapter = new ItemAdapter(context, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _partsLocationId.setAdapter(adapter);
        _partsLocationId.setOnItemSelectedListener(selectedListener);

        //suppliers
        list = dsh.getAll(DictionariesSQLiteHelper.TABLE_NAME_SUPPLIERS, true);
        adapter = new ItemAdapter(context, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _partsSupplierId.setAdapter(adapter);
        _partsSupplierId.setOnItemSelectedListener(selectedListener);

        //bu
        list = dsh.getAll(DictionariesSQLiteHelper.TABLE_NAME_BU_CARS, true);
        adapter = new ItemAdapter(context, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _partsBuId.setAdapter(adapter);
        _partsBuId.setOnItemSelectedListener(selectedListener);

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
            case R.id.take_photo:
                takePhoto();
                break;
        }

    }

    private void sendPart(){
        //get ID part
        SharedPreferences part_info = getSharedPreferences(DataFieldsAsync.DB_PREFS, Context.MODE_PRIVATE);
        int id = part_info.getInt("part_id", 0);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            Part part = (Part) getIntent().getSerializableExtra("Part");
            id = part.get_id();
        }

        if(id != 0 && Connection.checkNetworkConnection(this)){
            SendPartAsync sendTask = new SendPartAsync(this);
            sendTask.execute(id);
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void takePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {}
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File file = new File(mCurrentPhotoPath);

            if(file.exists()){
                SendImageAsync sendImageAsync = new SendImageAsync(this);
                sendImageAsync.execute(file);
            }

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

    }
}
