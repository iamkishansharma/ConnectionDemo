package com.heycode.connectiondemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class MusicActivity extends AppCompatActivity {

    ConnectivityManager mConnectivityManager;
    ProgressDialog mProgressDialog;
    boolean flag = false;
    MediaPlayer mMediaPlayer;
    String filePath;
    Button play,stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        play = findViewById(R.id.play);

        mMediaPlayer = new MediaPlayer();

        mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNetworkStatus2();
            }
        });
        stop = findViewById(R.id.stopMusic);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkWritePermission();
    }

    ///heck Network Status
    public void checkNetworkStatus2() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(this, networkInfo.getTypeName() + "\nConnected with Wifi Network", Toast.LENGTH_SHORT).show();

                String musicUrl = "https://drive.google.com/uc?export=download&id=1SIIOas1JY34xi1xwvKLK4_XG4no8OMXV";
                new MusicDownload().execute(musicUrl);

            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(this, networkInfo.getTypeName() + "\nConnected with Mobile Network", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No Active Network Found!!!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //////////////////Check Writ Permission
    public void checkWritePermission() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Permission Already Granted..", Toast.LENGTH_SHORT).show();
            flag = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkWritePermission();
            } else {
                Toast.makeText(this, "Write permission denied by user ", Toast.LENGTH_LONG).show();
            }
        }
    }

    class MusicDownload extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MusicActivity.this);
            mProgressDialog.setIcon(R.drawable.ic_launcher_foreground);
            mProgressDialog.setMessage("Downloading....");
            mProgressDialog.create();
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return fetchMusicFile(strings[0]);
        }

        String fetchMusicFile(String path) {
            String s= null;
            try {
                URL myurl = new URL(path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) myurl.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                int code = httpURLConnection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    if(inputStream!=null){
                        if(flag){
                            String state = Environment.getExternalStorageState();
                            if(state.equals(Environment.MEDIA_MOUNTED)){

                                File root = Environment.getExternalStorageDirectory();
                                File file1 = new File(root, "mymusic.mp3");

                                filePath = file1.getAbsolutePath();

                                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                                int i=0;
                                while ((i = inputStream.read())  != -1){
                                    fileOutputStream.write(i);
                                }
                                fileOutputStream.close();
                                s = "DONE";
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();

            if(s!=null){
                try{
                    mMediaPlayer.setDataSource(filePath);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}

