package com.heycode.connectiondemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ImageView mImageView;
    TextView mTextView;
    ConnectivityManager mConnectivityManager;
    EditText giveUrl;

    Boolean flag = false;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        mTextView = findViewById(R.id.tv);
        mImageView = findViewById(R.id.iv);
        giveUrl = findViewById(R.id.url_here);

    }

    public void checkNetworkStatus(View view) {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(this, networkInfo.getTypeName() + "\nConnected with Wifi Network", Toast.LENGTH_SHORT).show();

//                String textUrl = "https://drive.google.com/uc?export=download&id=1ivvH3bgEqR0JCFBNRyHNcas0dLIXgtO3";
                String textUrl = "https://gist.githubusercontent.com/kmike/2e4ed9f45c162481bf39c4d36e9e9920/raw/790aaa78eaf9c3158da2054899f7708a3dec4f5a/log.txt";

//                String textUrl = "http://volker.top.geek.nz/Jokes/txt/bankletter.txt";
                new MyTextFile().execute(textUrl);//calling Text url

//                String imageUrl ="https://c4.wallpaperflare.com/wallpaper/928/275/718/movies-bollywood-movies-wallpaper-preview.jpg";
                String imageUrl = giveUrl.getText().toString();
                new MyImageFile().execute(imageUrl);
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(this, networkInfo.getTypeName() + "\nConnected with Mobile Network", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No Active Network Found!!!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkWritePermission();
    }

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


    class MyTextFile extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return downloadText(strings[0]);
        }

        String downloadText(String path) {
            String s = null;
            try {
                URL myurl = new URL(path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) myurl.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                int code = httpURLConnection.getResponseCode();
                if (code == httpURLConnection.HTTP_OK) {
                    InputStream stream = httpURLConnection.getInputStream();
                    if (stream != null) {
                        BufferedReader reader = new BufferedReader((new InputStreamReader(stream)));
                        String line = "";
                        String text = "";
                        while ((line = reader.readLine()) != null) {
                            text = text + line + "\n";
                        }
                        s = text;
                        if (flag) {
                            String state = Environment.getExternalStorageState();
                            if (state.equals(Environment.MEDIA_MOUNTED)) {
                                File root = Environment.getExternalStorageDirectory();
                                File f = new File(root, "datatoread.txt");
                                FileOutputStream ff = new FileOutputStream(f);
                                byte[] b = s.getBytes();
                                ff.write(b);
                                ff.close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                mTextView.setText(s);
            }
        }
    }


    ////Download the image file
    class MyImageFile extends AsyncTask<String, Void, Bitmap> {

        Bitmap mBitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setIcon(R.drawable.ic_launcher_foreground);
            mProgressDialog.setMessage("Downloading....");
            mProgressDialog.create();
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return downloadImage(strings[0]);
        }

        Bitmap downloadImage(String path) {
            String s = null;
            try {
                URL myurl = new URL(path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) myurl.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                Bitmap mIcon11 = null;
                InputStream inputStream = myurl.openStream();
                mIcon11 = BitmapFactory.decodeStream(inputStream);
                mBitmap = mIcon11;


            } catch (IOException e) {
                e.printStackTrace();
            }
            return mBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            if (s != null) {
                mProgressDialog.dismiss();
                mImageView.setVisibility(View.VISIBLE);
                mImageView.setImageBitmap(mBitmap);
            }
        }
    }
}



