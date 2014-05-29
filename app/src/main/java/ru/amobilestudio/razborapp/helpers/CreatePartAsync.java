package ru.amobilestudio.razborapp.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.JsonReader;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ru.amobilestudio.razborapp.app.AddPartActivity;
import ru.amobilestudio.razborapp.app.MainActivity;
import ru.amobilestudio.razborapp.app.R;

/**
 * Created by vetal on 24.05.14.
 */
public class CreatePartAsync extends AsyncTask<Void, Void, Void> {

    private ArrayList<String> _errors;
    private Number _id;

    private Context _context;

    private ProgressBar _loader;

    public CreatePartAsync(Context context) {
        _context = context;
        _errors = new ArrayList<String>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        AddPartActivity activity = (AddPartActivity) _context;
        _loader = (ProgressBar) activity.findViewById(R.id.loader);

        _loader.setVisibility(ImageView.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String url = MainActivity.HOST + "api/createPart";
        try {
            HttpClient httpclient = new DefaultHttpClient(); // for port 80 requests!
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();

            JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

            reader.beginObject();

            while (reader.hasNext()){
                String name = reader.nextName();
                if (name.equals("errors")) {
                    reader.beginArray();
                    while(reader.hasNext()){
                        _errors.add(reader.nextString());
                    }
                    reader.endArray();
                }else if (name.equals("data")) {
                    reader.beginObject();
                    while (reader.hasNext()){
                        String idName = reader.nextName();
                        //get Part Id
                        if(idName.equals("id")){
                            _id = reader.nextInt();
                        }
                    }
                    reader.endObject();
                }else{
                    reader.skipValue();
                }
            }
            reader.endObject();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        _loader.setVisibility(ImageView.GONE);

        if(_errors.isEmpty() && _id != null){
            AddPartActivity activity = (AddPartActivity) _context;
            TextView tv = (TextView) activity.findViewById(R.id.article_part);

            String text = tv.getText().toString();
            tv.setText(text + " " + _id.intValue());

            SharedPreferences settings = _context.getSharedPreferences(DataFieldsAsync.DB_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            editor.putInt("part_id", _id.intValue());
            editor.commit();
        }else{
            new AlertDialog.Builder(_context)
                    .setTitle("Error")
                    .setMessage(TextUtils.join("\n", _errors))
                    .setPositiveButton(R.string.confirm_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {}
                    })
                    .show();
        }
    }
}
