package com.heycode.connectiondemo;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoActivity extends AppCompatActivity {

    VideoView mVideoView;
    Button download;
    Uri videoUrl;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        download = findViewById(R.id.playVideo);
        mVideoView = findViewById(R.id.videoView);

        videoUrl = Uri.parse("https://developers.google.com/training/images/tacoma_narrows.mp4");
        mVideoView.setVideoURI(videoUrl);
        mVideoView.start();

        MediaController mediaController = new MediaController(this);
        mVideoView.setMediaController(mediaController);

        mediaController.show();

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadVideo().execute("https://developers.google.com/training/images/tacoma_narrows.mp4");
            }
        });


    }

    class DownloadVideo extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(VideoActivity.this);
            mProgressDialog.setIcon(R.drawable.ic_launcher_background);
            mProgressDialog.setMessage("Downloading....");
            mProgressDialog.create();
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            fetchVideo(strings[0]);
            return null;
        }

        void fetchVideo(String path) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yymmdd");
            String date = simpleDateFormat.format(new Date());
            String name = "Video" + date + ".mp4";
            try {
                String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "MyDire";
                File file = new File(root);
                file.mkdir();

                URL myUrl = new URL(path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) myUrl.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                FileOutputStream fos = new FileOutputStream(new File(root, name));
                InputStream in = httpURLConnection.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();

            } catch (Exception e) {

            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
        }
    }
}