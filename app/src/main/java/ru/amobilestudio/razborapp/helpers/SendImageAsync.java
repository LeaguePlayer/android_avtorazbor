package ru.amobilestudio.razborapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import ru.amobilestudio.razborapp.app.AddPartActivity;
import ru.amobilestudio.razborapp.app.MainActivity;
import ru.amobilestudio.razborapp.app.R;

/**
 * Created by vetal on 28.05.14.
 */
public class SendImageAsync extends AsyncTask<File, Void, Void> {

    private Context _context;
    private int _part_id;

    private View _child;

    public SendImageAsync(Context context) {
        _context = context;
    }


    @Override
    protected Void doInBackground(File... files) {

        if(_part_id > 0){
            try{

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(MainActivity.HOST + "api/addImage/id/" + _part_id);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                FileBody fb = new FileBody(files[0]);
                builder.addPart("Image", fb);

                final HttpEntity entity = builder.build();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);

            }catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        SharedPreferences part_info = _context.getSharedPreferences(DataFieldsAsync.DB_PREFS,
                Context.MODE_PRIVATE);
        _part_id = part_info.getInt("part_id", 0);

        AddPartActivity activity = (AddPartActivity) _context;

        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.photos);
        _child = LayoutInflater.from(activity).inflate(R.layout.photo_row, null, false);
        TextView tv = (TextView) _child.findViewById(R.id.photo_message);
        tv.setText(R.string.photo_load);
        layout.addView(_child);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        TextView tv = (TextView) _child.findViewById(R.id.photo_message);
        tv.setText(R.string.photo_success);

        ProgressBar progressBar = (ProgressBar) _child.findViewById(R.id.photo_progress);
        progressBar.setVisibility(View.GONE);
    }
}
