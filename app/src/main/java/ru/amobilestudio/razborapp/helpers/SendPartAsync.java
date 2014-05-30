package ru.amobilestudio.razborapp.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import ru.amobilestudio.razborapp.app.AddPartActivity;
import ru.amobilestudio.razborapp.app.MainActivity;
import ru.amobilestudio.razborapp.app.R;

/**
 * Created by vetal on 26.05.14.
 */
public class SendPartAsync extends AsyncTask<Number, Void, Number> {

    private Context _context;
    private ProgressDialog _progress;

    public SendPartAsync(Context context) {
        _context = context;
        _progress = new ProgressDialog(context);
        _progress.setTitle(context.getString(R.string.send_message));
        _progress.setMessage(context.getString(R.string.wait_title));
        _progress.setCancelable(true);
        _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        _progress.show();
    }

    @Override
    protected Number doInBackground(Number... numbers) {

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MainActivity.HOST + "api/savePart/id/" + numbers[0]);

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

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);
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
    }
}
