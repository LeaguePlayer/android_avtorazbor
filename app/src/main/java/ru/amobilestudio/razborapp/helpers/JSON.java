package ru.amobilestudio.razborapp.helpers;

import android.util.JsonReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by vetal on 30.04.14.
 */
public class JSON {

    private InputStream inputStream;

    private ArrayList<String> errors;

    public JSON(String url) {
        errors = new ArrayList<String>();

        // HTTP
        try {
            HttpClient httpclient = new DefaultHttpClient(); // for port 80 requests!
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
        } catch(Exception e) {
            inputStream = null;
        }
    }

    public Number getIdNewPart() throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        Number id = null;

        reader.beginObject();

        while (reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("errors")) {
                reader.beginArray();
                while(reader.hasNext()){
                    errors.add(reader.nextString());
                }
                reader.endArray();
            }else if (name.equals("data")) {
                reader.beginObject();
                while (reader.hasNext()){
                    String idName = reader.nextName();
                    //get Part Id
                    if(idName.equals("id")){
                        id = reader.nextInt();
                    }
                }
                reader.endObject();
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();

        return id;
    }

    /**
     * Check JSON on errors
     * @return
     * @throws IOException
     */
    public static ArrayList<String> getErrorsFromJSON(InputStream is) throws IOException{
        JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
        ArrayList<String> errors = new ArrayList<String>();

        reader.beginObject();

        while(reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("errors")) {
                reader.beginArray();
                while(reader.hasNext()){
                    errors.add(reader.nextString());
                }
                reader.endArray();
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();

        return errors;
    }

    /**
     * Return JsonReader array from json string (data: [...])
     * @param reader
     * @return
     * @throws IOException
     */
    private void getData(JsonReader reader) throws IOException{

    }
}
