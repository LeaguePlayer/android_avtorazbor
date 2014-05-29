package ru.amobilestudio.razborapp.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ru.amobilestudio.razborapp.helpers.Connection;
import ru.amobilestudio.razborapp.helpers.DataFieldsAsync;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText loginField;
    private EditText passField;
    private Button sendButton;

    public static final String LOGIN_PREFS = "LoginPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check connection
        if(Connection.checkNetworkConnection(this)){

            //create database with data for select inputs
            SharedPreferences db_info = getSharedPreferences(DataFieldsAsync.DB_PREFS, Context.MODE_PRIVATE);
            if(!db_info.getBoolean("isDb", false)){
                DataFieldsAsync dataFieldsAsync = new DataFieldsAsync(this);
                dataFieldsAsync.execute();
            }
        }

        if(LoginActivity.isLogin(this)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            return;
        }

        setContentView(R.layout.activity_login);

        loginField = (EditText) findViewById(R.id.login_name);
        passField = (EditText) findViewById(R.id.login_pass);
        sendButton = (Button) findViewById(R.id.login_send);

        sendButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(LoginActivity.isLogin(this)){
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add_part) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.login_send:
                checkLoginForm();
                break;
        }
    }

    private void checkLoginForm(){
        boolean valid = true;
        ArrayList<String> errors = new ArrayList<String>();

        String login = loginField.getText().toString();
        String pass = passField.getText().toString();

        if(login.equals("")){
            errors.add(getString(R.string.login_name_empty));
            valid = valid && false;
        }

        if(pass.equals("")){
            errors.add(getString(R.string.login_pass_empty));
            valid = valid && false;
        }

        if(valid){
            if(Connection.checkNetworkConnection(this)){
                SendPostAsyncTask task = new SendPostAsyncTask(this);
                task.execute(login, pass);
            }
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(TextUtils.join("\n", errors))
                    .setPositiveButton(R.string.confirm_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
    }

    public static boolean isLogin(Context context){
        SharedPreferences user_info = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);

        return user_info.getBoolean("isLogin", false);
    }

    class SendPostAsyncTask extends AsyncTask<String, Void, Void> {
        private ArrayList<String> errors;
        private Context context;

        private Integer user_id = null;
        private String user_fio;

        private ProgressDialog _progress;

        public SendPostAsyncTask(Context context){
            this.context = context;
            this.errors = new ArrayList<String>();

            _progress = new ProgressDialog(context);
            _progress.setTitle(context.getString(R.string.wait_title));
            _progress.setMessage(context.getString(R.string.login_title));
            _progress.setCancelable(true);
            _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progress.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            String login = strings[0];
            String pass = strings[1];

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MainActivity.HOST + "api/auth");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", login));
                nameValuePairs.add(new BasicNameValuePair("pass", pass));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                InputStream inputStream = response.getEntity().getContent();
                //this.errors = JSON.getErrorsFromJSON(inputStream);

                JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

                reader.beginObject();

                while (reader.hasNext()){
                    String name = reader.nextName();
                    if (name.equals("errors")) {
                        reader.beginArray();
                        while(reader.hasNext()){
                            this.errors.add(reader.nextString());
                        }
                        reader.endArray();
                    }else if (name.equals("data")) {
                        reader.beginObject();
                        while (reader.hasNext()){
                            String dataName = reader.nextName();
                            //get User Id
                            if(dataName.equals("user")){
                                reader.beginObject();

                                while (reader.hasNext()){
                                    String nUser = reader.nextName();

                                    if(nUser.equals("id")) user_id = reader.nextInt();
                                    else if(nUser.equals("fio")) user_fio = reader.nextString();
                                    else reader.skipValue();
                                }

                                reader.endObject();
                            }
                        }
                        reader.endObject();
                    }else{
                        reader.skipValue();
                    }
                }
                reader.endObject();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            _progress.dismiss();

            if(this.errors.isEmpty()){

                //TODO: save date login User (Session)
                SharedPreferences settings = getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();

                editor.putInt("user_id", user_id);
                editor.putString("user_fio", user_fio);
                editor.putBoolean("isLogin", true);

                editor.commit();

                /*//Create date login
                Date login_date = new Date();
                editor.putLong("login_date", login_date.getTime());*/

                Intent intent = new Intent(this.context, MainActivity.class);
                startActivity(intent);
            }else{
                new AlertDialog.Builder(this.context)
                        .setTitle("Error")
                        .setMessage(TextUtils.join("\n", errors))
                        .setPositiveButton(R.string.confirm_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        })
                        .show();
            }
        }
    }
}
