package ru.amobilestudio.razborapp.app;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.amobilestudio.razborapp.custom.MyAutoComplete;
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
    private static Button _publishButton;
    private static Button _takePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();

        setContentView(R.layout.activity_add_part);

        //init
        _partsPriceSell = (EditText) findViewById(R.id.parts_price_sell);
        _partsPriceBuy = (EditText) findViewById(R.id.parts_price_buy);
        _partsComment = (EditText) findViewById(R.id.parts_comment);

        /*------------Selects INIT--------------*/

        final DictionariesSQLiteHelper dictionariesSQLiteHelper = new DictionariesSQLiteHelper(this);
        //categories
        ArrayList<DictionariesSQLiteHelper.Item> list = dictionariesSQLiteHelper.getAll(DictionariesSQLiteHelper.TABLE_NAME_CATEGORIES, false);
        ArrayAdapter<DictionariesSQLiteHelper.Item> adapter = new ArrayAdapter<DictionariesSQLiteHelper.Item>(this, android.R.layout.simple_dropdown_item_1line, list);
        _partsCategoryId = (MyAutoComplete) findViewById(R.id.parts_category_id);
        _partsCategoryId.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i2, int i3) {}
            @Override
            public void onTextChanged(CharSequence s, int i, int i2, int i3) {
                _categoryId = dictionariesSQLiteHelper.getIdByValue(s.toString(), DictionariesSQLiteHelper.TABLE_NAME_CATEGORIES);
            }
        });
        _partsCategoryId.setAdapter(adapter);

        //car models
        list = dictionariesSQLiteHelper.getAll(DictionariesSQLiteHelper.TABLE_NAME_CAR_MODELS, false);
        adapter = new ArrayAdapter<DictionariesSQLiteHelper.Item>(this, android.R.layout.simple_dropdown_item_1line, list);
        _partsCarModelId = (MyAutoComplete) findViewById(R.id.parts_car_model_id);
        _partsCarModelId.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i2, int i3) {}
            @Override
            public void onTextChanged(CharSequence s, int i, int i2, int i3) {
                _carModelId = dictionariesSQLiteHelper.getIdByValue(s.toString(), DictionariesSQLiteHelper.TABLE_NAME_CAR_MODELS);
            }
        });
        _partsCarModelId.setAdapter(adapter);

        //locations
        list = dictionariesSQLiteHelper.getAll(DictionariesSQLiteHelper.TABLE_NAME_LOCATIONS, false);
        adapter = new ArrayAdapter<DictionariesSQLiteHelper.Item>(this, android.R.layout.simple_dropdown_item_1line, list);
        _partsLocationId = (MyAutoComplete) findViewById(R.id.parts_location_id);
        _partsLocationId.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i2, int i3) {}
            @Override
            public void onTextChanged(CharSequence s, int i, int i2, int i3) {
                _locationId = dictionariesSQLiteHelper.getIdByValue(s.toString(), DictionariesSQLiteHelper.TABLE_NAME_LOCATIONS);
            }
        });
        _partsLocationId.setAdapter(adapter);

        //suppliers
        list = dictionariesSQLiteHelper.getAll(DictionariesSQLiteHelper.TABLE_NAME_SUPPLIERS, false);
        adapter = new ArrayAdapter<DictionariesSQLiteHelper.Item>(this, android.R.layout.simple_dropdown_item_1line, list);
        _partsSupplierId = (MyAutoComplete) findViewById(R.id.parts_supplier_id);
        _partsSupplierId.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i2, int i3) {}
            @Override
            public void onTextChanged(CharSequence s, int i, int i2, int i3) {
                _supplierId = dictionariesSQLiteHelper.getIdByValue(s.toString(), DictionariesSQLiteHelper.TABLE_NAME_SUPPLIERS);
            }
        });
        _partsSupplierId.setAdapter(adapter);

        //bu
        list = dictionariesSQLiteHelper.getAll(DictionariesSQLiteHelper.TABLE_NAME_BU_CARS, false);
        adapter = new ArrayAdapter<DictionariesSQLiteHelper.Item>(this, android.R.layout.simple_dropdown_item_1line, list);
        _partsBuId = (MyAutoComplete) findViewById(R.id.parts_bu_id);
        _partsBuId.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i2, int i3) {}
            @Override
            public void onTextChanged(CharSequence s, int i, int i2, int i3) {
                _buId = dictionariesSQLiteHelper.getIdByValue(s.toString(), DictionariesSQLiteHelper.TABLE_NAME_BU_CARS);
            }
        });
        _partsBuId.setAdapter(adapter);

        /*------------Selects end--------------*/

        _sendButton = (Button) findViewById(R.id.save_part_button);
        _sendButton.setOnClickListener(this);

        _publishButton = (Button) findViewById(R.id.publish_part_button);
        _publishButton.setOnClickListener(this);

        _takePhoto = (Button) findViewById(R.id.take_photo);
        _takePhoto.setOnClickListener(this);

        //setSelectsField(this);

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

        String name = dictionariesSQLiteHelper.getValueById(part.getCategory_id(), DictionariesSQLiteHelper.TABLE_NAME_CATEGORIES);
        _partsCategoryId.setText(name);

        name = dictionariesSQLiteHelper.getValueById(part.getCar_model_id(), DictionariesSQLiteHelper.TABLE_NAME_CAR_MODELS);
        _partsCarModelId.setText(name);

        name = dictionariesSQLiteHelper.getValueById(part.getLocation_id(), DictionariesSQLiteHelper.TABLE_NAME_LOCATIONS);
        _partsLocationId.setText(name);

        name = dictionariesSQLiteHelper.getValueById(part.getSupplier_id(), DictionariesSQLiteHelper.TABLE_NAME_SUPPLIERS);
        _partsSupplierId.setText(name);

        name = dictionariesSQLiteHelper.getValueById(part.getUsed_car_id(), DictionariesSQLiteHelper.TABLE_NAME_BU_CARS);
        _partsBuId.setText(name);
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
                sendPart(false);
                break;
            case R.id.publish_part_button:
                sendPart(true);
                break;
            case R.id.take_photo:
                takePhoto();
                break;
        }

    }

    /* hide ActionBar */
    public void hideActionBar(){
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else{
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }
    }

    private void sendPart(boolean publish){

        if(Connection.checkNetworkConnection(this)){
            SendPartAsync sendTask = new SendPartAsync(this);
            sendTask.execute(publish);
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

            if(file.exists() && Connection.checkNetworkConnection(this)){
                SendImageAsync sendImageAsync = new SendImageAsync(this);
                sendImageAsync.execute(file);
            }

        }
    }
}
