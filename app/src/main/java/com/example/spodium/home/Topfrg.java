package com.example.spodium.home;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.spodium.MainActivity;
import com.example.spodium.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Topfrg extends ListFragment {

    ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
    HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();

    TextView top_views;
    int top_idx1;

    public Topfrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_frg, container, false);

        top_views = (TextView)view.findViewById(R.id.top_views);

        MainActivity activity = (MainActivity)getActivity();

        ListAdapter adapter = new ListAdapter();
        setListAdapter(adapter);

        GetDataThread thread = new GetDataThread();
        thread.start();

        return view;
    }

    class ListAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.top_frg_row, null);
            }

            ImageView imageView = (ImageView)convertView.findViewById(R.id.top_image);
            TextView title = (TextView)convertView.findViewById(R.id.top_title);
            TextView rank = (TextView)convertView.findViewById(R.id.top_rank);
            TextView views = (TextView)convertView.findViewById(R.id.top_views);

            HashMap<String, Object> map = listData.get(position);

            int top_idx = (int)map.get("top_idx");
            String top_image = (String)map.get("top_image");
            String top_title = (String)map.get("top_title");
            String top_rank = (String)map.get("top_rank");
            String top_views = (String)map.get("top_views");


            Bitmap bitmap = imageMap.get(top_image);
            if(bitmap == null){
                ImageNetworkThread thread2 = new ImageNetworkThread(top_image);
                thread2.start();
            } else {
                imageView.setImageBitmap(bitmap);
            }



            title.setText(top_title);
            rank.setText(top_rank);
            views.setText(top_views);

            return convertView;
        }
    }

    class GetDataThread extends Thread{
        @Override
        public void run() {
            super.run();
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder = builder.url("http://192.168.122.30:8080/SpodiumServer/get_top_list.jsp");
            Request request = builder.build();

            Callback1 callback1 = new Callback1();
            Call call = client.newCall(request);
            call.enqueue(callback1);

        }
    }

    class Callback1 implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response){
            try{
                String result = response.body().string();

                listData.clear();

                JSONArray root = new JSONArray(result);

                for(int i = 0; i < root.length(); i++){
                    JSONObject obj = root.getJSONObject(i);

                    int top_idx = obj.getInt("top_idx");
                    String top_image = obj.getString("top_image");
                    String top_title = obj.getString("top_title");
                    String top_rank = obj.getString("top_rank");
                    String top_views = obj.getString("top_views");

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("top_idx", top_idx);
                    map.put("top_image", top_image);
                    map.put("top_title", top_title);
                    map.put("top_rank", top_rank);
                    map.put("top_views", top_views);

                    listData.add(map);
                }


                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        ListAdapter adapter = (ListAdapter)getListView().getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                });


            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    class ImageNetworkThread extends Thread{
        String fileName;

        ImageNetworkThread(String fileName){
            this.fileName = fileName;
        }

        @Override
        public void run() {
            super.run();
            try{

                URL url = new URL("http://192.168.122.30:8080/SpodiumServer/image/" + fileName);

                URLConnection connection = url.openConnection();
                InputStream is = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                imageMap.put(fileName, bitmap);


                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        ListAdapter adapter = (ListAdapter)getListView().getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent video_intent = new Intent(getActivity(), TopVideoActivity.class);

        HashMap<String, Object> map = (HashMap<String, Object>)listData.get(position);
        top_idx1 = (Integer)map.get("top_idx");
        video_intent.putExtra("top_idx", top_idx1);

        ViewsThread thread = new ViewsThread();
        thread.start();

        startActivity(video_intent);
    }


    class ViewsThread extends Thread{
        public void run(){
            super.run();

            try{
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                builder = builder.url("http://192.168.122.30:8080/SpodiumServer/upload.jsp");

                String views = top_views.getText().toString();
                int view = Integer.parseInt(views);
                view++;
                String update_views = Integer.toString(view);

                FormBody.Builder bodyBuilder = new FormBody.Builder();

                bodyBuilder.add("top_idx", top_idx1 + "");
                bodyBuilder.add("top20_views", update_views);

                Log.d("test123", update_views);
                Log.d("test123", top_idx1 + "");

                FormBody body = bodyBuilder.build();
                builder = builder.post(body);

                Request request = builder.build();
                Call call = client.newCall(request);
                call.execute();

            }catch(Exception e){
                e.printStackTrace();
            }

        }

    }



}
