package ru.amobilestudio.razborapp.app;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import ru.amobilestudio.razborapp.helpers.AllPartsAsync;
import ru.amobilestudio.razborapp.helpers.Connection;
import ru.amobilestudio.razborapp.helpers.DataFieldsAsync;
import ru.amobilestudio.razborapp.helpers.DictionariesSQLiteHelper;


public class MainActivity extends ListActivity {

    static final public boolean DEBUG_MODE = true;
    static final public String HOST = DEBUG_MODE ? "http://10.0.3.2:2000/" : "http://razbor.amobile2.tmweb.ru/";
    static final public String TAG = "razbor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //logout user if he don't login
        if(!LoginActivity.isLogin(this)) logOut();

        //check connection
        if(Connection.checkNetworkConnection(this)){

            AllPartsAsync allPartsAsync = new AllPartsAsync(this);
            allPartsAsync.execute();

            //create database with data for select inputs
            SharedPreferences db_info = getSharedPreferences(DataFieldsAsync.DB_PREFS, Context.MODE_PRIVATE);
            if(!db_info.getBoolean("isDb", false)){
                DataFieldsAsync dataFieldsAsync = new DataFieldsAsync(this);
                dataFieldsAsync.execute();
            }
        }

        //TODO: delete this lines
        //------------------------------------------------------
        /*deleteDatabase(DictionariesSQLiteHelper.DATABASE_NAME);
        SharedPreferences settings = getSharedPreferences(DataFieldsAsync.DB_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("isDb", false);
        editor.commit();*/
        //------------------------------------------------------

        TextView helloText = (TextView) findViewById(R.id.hello_text);

        SharedPreferences user_info = getSharedPreferences(LoginActivity.LOGIN_PREFS, Context.MODE_PRIVATE);
        helloText.setText(getString(R.string.hello_message) + user_info.getString("user_fio", "") + "\n" + getString(R.string.hello_message2));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_add_part:
                Intent intent = new Intent(this, AddPartActivity.class);
                startActivity(intent);
                break;
            case R.id.action_update_bd:
                updateDb();
                break;
            case R.id.action_logout:
                logOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //logout user if he don't login
        if(!LoginActivity.isLogin(this)) logOut();

        if(Connection.checkNetworkConnection(this)){
            AllPartsAsync allPartsAsync = new AllPartsAsync(this);
            allPartsAsync.execute();
        }
    }

    private void updateDb(){
        deleteDatabase(DictionariesSQLiteHelper.DATABASE_NAME);
        SharedPreferences settings = getSharedPreferences(DataFieldsAsync.DB_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("isDb", false);
        editor.commit();

        if(Connection.checkNetworkConnection(this)){
            DataFieldsAsync dataFieldsAsync = new DataFieldsAsync(this);
            dataFieldsAsync.execute();
        }
    }

    private void logOut(){
        SharedPreferences settings = getSharedPreferences(LoginActivity.LOGIN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt("user_id", 0);
        editor.putString("user_fio", "");
        editor.putBoolean("isLogin", false);

        editor.commit();

        finish();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
