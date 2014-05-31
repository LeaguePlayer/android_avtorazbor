package ru.amobilestudio.razborapp.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
 * Created by vetal on 27.05.14.
 */
public class AllPartsAsync extends AsyncTask<Void, Void, Void> {

    private ArrayList<String> _errors;

    private Context _context;
    private ProgressDialog _progress;

    //all parts
    private ArrayList<Part> _parts;

    public AllPartsAsync(Context context) {
        _context = context;
        _progress = new ProgressDialog(context);
        _progress.setTitle(context.getString(R.string.load_message));
        _progress.setMessage(context.getString(R.string.wait_title));
        _progress.setCancelable(true);
        _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        _parts = new ArrayList<Part>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        _progress.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        String url = MainActivity.HOST + "api/getAllParts";
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
                        String arr_name = reader.nextName();

                        if(arr_name.equals("parts")){
                            reader.beginArray();

                            while(reader.hasNext()){
                                reader.beginObject();

                                Part part = new Part();

                                while(reader.hasNext()){
                                    String part_field = reader.nextName();

                                    if(part_field.equals("id")){
                                        part.set_id(reader.nextInt());
                                    }else if(part_field.equals("name")){
                                        part.set_name(reader.nextString());
                                    }else if(part_field.equals("date")){
                                        part.set_date(reader.nextString());
                                    }else if(part_field.equals("price_sell") && reader.peek() != JsonToken.NULL){
                                        part.setPrice_sell(reader.nextDouble());
                                    }else if(part_field.equals("price_buy")  && reader.peek() != JsonToken.NULL){
                                        part.setPrice_buy(reader.nextDouble());
                                    }else if(part_field.equals("comment") && reader.peek() != JsonToken.NULL){
                                        part.setComment(reader.nextString());
                                    }else if(part_field.equals("category_id") && reader.peek() != JsonToken.NULL){
                                        part.setCategory_id(reader.nextInt());
                                    }else if(part_field.equals("car_model_id") && reader.peek() != JsonToken.NULL){
                                        part.setCar_model_id(reader.nextInt());
                                    }else if(part_field.equals("location_id") && reader.peek() != JsonToken.NULL){
                                        part.setLocation_id(reader.nextInt());
                                    }else if(part_field.equals("supplier_id") && reader.peek() != JsonToken.NULL){
                                        part.setSupplier_id(reader.nextInt());
                                    }else if(part_field.equals("used_car_id") && reader.peek() != JsonToken.NULL){
                                        part.setUsed_car_id(reader.nextInt());
                                    }else{
                                        reader.skipValue();
                                    }
                                }
                                _parts.add(part);
                                reader.endObject();
                            }
                            reader.endArray();
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
    protected void onPostExecute(Void Void) {
        super.onPostExecute(Void);

        _progress.dismiss();

        MainActivity activity = (MainActivity) _context;
        ListView listView = (ListView) activity.findViewById(android.R.id.list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(_context, AddPartActivity.class);
                intent.putExtra("Part", _parts.get(i));

                _context.startActivity(intent);
            }
        });

        ListAdapter listAdapter = new ListAdapter(_context, R.layout.activity_main, _parts);

        TextView emptyText = (TextView) activity.findViewById(R.id.empty_text);
        listView.setEmptyView(emptyText);

        listView.setAdapter(listAdapter);
    }

    static class ViewHolder {
        public TextView nameView;
        public TextView dateView;
    }

    //custom adapter
    private class ListAdapter extends ArrayAdapter<Part>{

        public ListAdapter(Context context, int resource, ArrayList<Part> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) _context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.part_item, null, true);
                holder = new ViewHolder();
                holder.nameView = (TextView) rowView.findViewById(R.id.part_name);
                holder.dateView = (TextView) rowView.findViewById(R.id.part_date);
                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            Part p = _parts.get(position);
            holder.nameView.setText(p.toString());
            holder.dateView.setText(p.get_date());

            return rowView;
        }

        @Override
        public int getCount() {
            return _parts.size();
        }

        @Override
        public Part getItem(int position) {
            return _parts.get(position);
        }
    }
}
