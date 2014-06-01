package ru.amobilestudio.razborapp.helpers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.widget.TextView;

import org.apache.http.HttpEntity;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.amobilestudio.razborapp.app.AddPartActivity;
import ru.amobilestudio.razborapp.app.MainActivity;
import ru.amobilestudio.razborapp.app.R;

/**
 * Created by vetal on 26.05.14.
 */
public class SendPartAsync extends AsyncTask<Boolean, Void, Number> {

    private Context _context;
    private ProgressDialog _progress;

    private HashMap<String, String> _labels;
    private HashMap<String, String> _errors;

    private int _id;

    public SendPartAsync(Context context) {
        _context = context;
        _errors = new HashMap<String, String>();
        _labels = new HashMap<String, String>();

        _progress = new ProgressDialog(context);
        _progress.setTitle(context.getString(R.string.send_message));
        _progress.setMessage(context.getString(R.string.wait_title));
        _progress.setCancelable(true);
        _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        _labels.put("id", "Артикул");
        _labels.put("name", "'Название'");
        _labels.put("price_sell", "Стоимость (на продажу)");
        _labels.put("price_buy", "Стоимость (покупка)");
        _labels.put("comment", "Комментарий");
        _labels.put("category_id", "Категория");
        _labels.put("car_model_id", "Модель автомобиля");
        _labels.put("location_id", "Склад");
        _labels.put("supplier_id", "Поставщик");
        _labels.put("create_time", "Дата создания");
        _labels.put("status", "Статус");
        _labels.put("gallery_id", "Галерея");

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        _progress.show();

        SharedPreferences part_info = _context.getSharedPreferences(DataFieldsAsync.DB_PREFS, Context.MODE_PRIVATE);
        _id = part_info.getInt("part_id", 0);

        AddPartActivity activity = (AddPartActivity) _context;

        Bundle extras = activity.getIntent().getExtras();
        if(extras != null){
            Part part = (Part) activity.getIntent().getSerializableExtra("Part");
            _id = part.get_id();
        }
    }

    @Override
    protected Number doInBackground(Boolean... booleans) {

        Boolean publish = booleans[0];

//        Log.d(MainActivity.TAG, " ID - " + _id);

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MainActivity.HOST + "api/savePart/id/" + _id);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            AddPartActivity activity = (AddPartActivity) _context;

            //init array for sending
            nameValuePairs.add(new BasicNameValuePair("Part[price_sell]", activity._partsPriceSell.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("Part[price_buy]", activity._partsPriceBuy.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("Part[comment]", activity._partsComment.getText().toString()));

            //selects
            nameValuePairs.add(new BasicNameValuePair("Part[category_id]", (activity._categoryId != 0 ? activity._categoryId : "") + ""));
            nameValuePairs.add(new BasicNameValuePair("Part[car_model_id]", (activity._carModelId != 0 ? activity._carModelId : "") + ""));
            nameValuePairs.add(new BasicNameValuePair("Part[location_id]", (activity._locationId != 0 ? activity._locationId : "") + ""));
            nameValuePairs.add(new BasicNameValuePair("Part[supplier_id]", (activity._supplierId != 0 ? activity._supplierId : "") + ""));
            nameValuePairs.add(new BasicNameValuePair("UsedCar", (activity._buId != 0 ? activity._buId : "") + ""));

            if(publish)
                nameValuePairs.add(new BasicNameValuePair("Part[status]", "1"));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            //Log.d(MainActivity.TAG, "response - " + EntityUtils.toString(entity));

            InputStream inputStream = entity.getContent();

            JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

            reader.beginObject();

            while (reader.hasNext()){
                String name = reader.nextName();
                if (name.equals("errors")) {
                    reader.beginArray();
                    while (reader.hasNext()){
                        reader.beginObject();
                        while(reader.hasNext()){
                            String field_name = reader.nextName();
                            if(field_name.equals("name")){
                                reader.beginArray();
                                while (reader.hasNext()){
                                    _errors.put(field_name, reader.nextString());
                                }
                                reader.endArray();
                            }else if(field_name.equals("price_sell")){
                                reader.beginArray();
                                while (reader.hasNext()){
                                    _errors.put(field_name, reader.nextString());
                                }
                                reader.endArray();
                            }else if(field_name.equals("price_buy")){
                                reader.beginArray();
                                while (reader.hasNext()){
                                    _errors.put(field_name, reader.nextString());
                                }
                                reader.endArray();
                            }else{
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    }
                    reader.endArray();
                }else{
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    protected void onPostExecute(Number number) {
        super.onPostExecute(number);

        if(_errors.isEmpty()){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    _progress.dismiss();

                /*delete id*/
                    SharedPreferences part_info = _context.getSharedPreferences(DataFieldsAsync.DB_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = part_info.edit();

                    editor.remove("part_id");
                    editor.commit();

                    AddPartActivity activity = (AddPartActivity) _context;
                    activity.finish();
                }
            }, 1500);

            _progress.setMessage(_context.getString(R.string.success_message));
        }else{
            _progress.dismiss();

            StringBuilder errorsStr = new StringBuilder();

            for (Map.Entry<String, String> entry : _labels.entrySet()) {
                String key = entry.getKey();

                if(_errors.containsKey(key)){
                    if(key.equals("name")){
                        errorsStr.append(_context.getString(R.string.validate_message_categor_and_car) + "\n");
                        continue;
                    }
                    errorsStr.append(_errors.get(key) + "\n");
                }
            }

            AlertDialog dialog = new AlertDialog.Builder(_context)
                    .setTitle("Errors")
                    .setMessage(errorsStr.toString())
                    .setPositiveButton(R.string.confirm_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {}
                    })
                    .show();
            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTextSize(15);
        }


    }
}
