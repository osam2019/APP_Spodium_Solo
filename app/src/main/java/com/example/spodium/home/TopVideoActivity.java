package com.example.spodium.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.spodium.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TopVideoActivity extends AppCompatActivity {

    VideoView videoView;

    int top_idx;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_video);

        videoView = (VideoView)findViewById(R.id.top_video);

        Intent intent = getIntent();
        top_idx = intent.getIntExtra("top_idx", 0);

        //Log.d("test111", top_idx + "");

        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);

        GetDataThread thread = new GetDataThread();
        thread.start();

        videoView.setMediaController(controller);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }

    class GetDataThread extends Thread{
        @Override
        public void run() {
            super.run();
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder = builder.url("http://192.168.122.30:8080/SpodiumServer/get_video_data.jsp");

            FormBody.Builder bodybuilder = new FormBody.Builder();
            bodybuilder.add("top_idx", top_idx + "");

            FormBody body = bodybuilder.build();

            builder = builder.post(body);

            Request request1 = builder.build();

            Callback1 callback1 = new Callback1();
            Call call = client.newCall(request1);
            call.enqueue(callback1);
        }
    }

    class  Callback1 implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) {
            try{
                String result = response.body().string();

                //Log.d("test100", result);

                JSONObject obj = new JSONObject(result);

                String top_video = obj.getString("top_video");

                uri = Uri.parse("http://192.168.122.30:8080/SpodiumServer/video/" + top_video);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
/*
    class VideoNetworkThread extends Thread{
        String fileName;

        VideoNetworkThread(String fileName){
            this.fileName = fileName;
        }

        @Override
        public void run() {
            super.run();
            try {
                controller.setAnchorView(videoView);

                Uri uri = Uri.parse("http://192.168.122.90:8080/SpodiumServer/video/" + fileName);

                videoView.setMediaController(controller);
                videoView.setVideoURI(uri);
                videoView.requestFocus();
                videoView.start();

            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }
 */

}
