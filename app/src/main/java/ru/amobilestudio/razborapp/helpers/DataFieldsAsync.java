package ru.amobilestudio.razborapp.helpers;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.amobilestudio.razborapp.app.AddPartActivity;
import ru.amobilestudio.razborapp.app.MainActivity;

/**
 * Created by vetal on 20.05.14.
 */
public class DataFieldsAsync extends AsyncTask<Void, Integer, Void> {

    private Context _context;
    private ProgressDialog _progress;

    public static final String DB_PREFS = "DbPrefsFile";

    public DataFieldsAsync(Context context) {
        _context = context;
        _progress = new ProgressDialog(context);
        _progress.setTitle("Загружаем данные.");
        _progress.setMessage("Подождите.");
        _progress.setCancelable(true);
        _progress.setProgress(0);
        _progress.setMax(100);
        _progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        /*_progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);*/
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        _progress.show();

        Log.d(MainActivity.TAG, "onPreExecute");
    }

    @Override
    protected Void doInBackground(Void... voids) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(MainActivity.HOST + "api/fieldsData");
        DictionariesSQLiteHelper dictionarySql = new DictionariesSQLiteHelper(_context);
        SQLiteDatabase db = dictionarySql.getWritableDatabase();

        try {
            HttpResponse response = httpclient.execute(httppost);
            InputStream inputStream = response.getEntity().getContent();

            JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

            reader.beginObject();

            publishProgress(0);

            while (reader.hasNext()){
                String name = reader.nextName();
                if (name.equals("data")) {
                    reader.beginObject();
                    while(reader.hasNext()){
                        String type = reader.nextName();

                        if(type.equals("categories")) {
                            saveDataField(reader, db, DictionariesSQLiteHelper.TABLE_NAME_CATEGORIES);
                            publishProgress((int) ((1 / (float) 5) * 100));
                        }else if(type.equals("car_models")) {
                            saveDataField(reader, db, DictionariesSQLiteHelper.TABLE_NAME_CAR_MODELS);
                            publishProgress((int) ((2 / (float) 5) * 100));
                        }else if(type.equals("locations")){
                            saveDataField(reader, db, DictionariesSQLiteHelper.TABLE_NAME_LOCATIONS);
                            publishProgress((int) ((3 / (float) 5) * 100));
                        }else if(type.equals("suppliers")) {
                            saveDataField(reader, db, DictionariesSQLiteHelper.TABLE_NAME_SUPPLIERS);
                            publishProgress((int) ((4 / (float) 5) * 100));
                        }else if(type.equals("bu_cars")) {
                            saveDataField(reader, db, DictionariesSQLiteHelper.TABLE_NAME_BU_CARS);
                            publishProgress((int) ((5 / (float) 5) * 100));
                        }else reader.skipValue();
                    }
                    reader.endObject();
                }else
                    reader.skipValue();
            }

            reader.endObject();

        } catch (IOException e) {
            e.printStackTrace();
        }


        //this.errors = JSON.getErrorsFromJSON(inputStream);

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        _progress.setProgress(values[0]);
        Log.d(MainActivity.TAG, "% - " + values[0]);
        Log.d(MainActivity.TAG, "2 % - " + _progress.getProgress());
    }

    public void saveDataField(JsonReader reader, SQLiteDatabase db, String tableName) throws IOException {
        reader.beginArray();

        while (reader.hasNext()){
            reader.beginObject();
            ContentValues values = new ContentValues();

            while (reader.hasNext()){
                String fieldName = reader.nextName();

                if(fieldName.equals("id")){
                    values.put(DictionariesSQLiteHelper.COLUMN_ID_VALUE, reader.nextString());
                }
                else if(fieldName.equals("name")){
                    values.put(DictionariesSQLiteHelper.COLUMN_NAME_VALUE, reader.nextString());
                }else
                    reader.skipValue();
            }
            db.insert(tableName, null, values);
            reader.endObject();
        }

        reader.endArray();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        _progress.dismiss();

        //TODO: save date login User (Session)
        SharedPreferences settings = _context.getSharedPreferences(DB_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("isDb", true);
        editor.commit();

        AddPartActivity.setSelectsField(_context);
    }
}
