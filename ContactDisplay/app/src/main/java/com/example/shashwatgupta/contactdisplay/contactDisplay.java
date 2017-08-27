package com.example.shashwatgupta.contactdisplay;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;


public class contactDisplay extends AppCompatActivity {

    ProgressDialog mProgressDialog;
    private static String file_url = "http://www.cs.columbia.edu/~coms6998-8/assignments/homework2/contacts/contacts.txt";
    HandlerClass handlerClass;
    public LayoutInflater layoutInflater;
    public LinearLayout myRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_display);

        mProgressDialog = new ProgressDialog(getApplicationContext());
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        final DownloadTask downloadTask = new DownloadTask(getApplicationContext());
        downloadTask.execute(file_url);

        handlerClass=new HandlerClass();
        layoutInflater= (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myRoot=(LinearLayout)findViewById(R.id.linearLayoutContact);
        GetContactDetails();
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                String outputFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                output = new FileOutputStream(outputFolder + "/contactFile.txt");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }

    public void GetContactDetails(){
        final List<Contact> htmlResponces =new ArrayList<>();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url="http://www.cs.columbia.edu/~coms6998-8/assignments/homework2/contacts/contacts.txt";
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(url);
                    HttpResponse response = client.execute(request);

                    InputStream in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder str = new StringBuilder();
                    String line = null;
                    while((line = reader.readLine()) != null)
                    {
                        String[] data=line.split(" ");
                        Contact contact=new Contact(data[0],data[1],data[2], data[3]);
                        handlerClass.obtainMessage(1,contact).sendToTarget();
                        htmlResponces.add(contact);
                    }
                    in.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    class HandlerClass extends Handler {
        HandlerClass() {
        }

        public void handleMessage(Message msg) {
            Contact contact=(Contact ) msg.obj;
            switch (msg.what) {
                case 1:
                    View v = layoutInflater.inflate(R.layout.list_item, null);
                    TextView textViewName =(TextView)v.findViewById(R.id.name);
                    TextView textViewEmail =(TextView)v.findViewById(R.id.email);
                    TextView textViewMobile =(TextView)v.findViewById(R.id.mobile);
                    TextView textViewHome =(TextView)v.findViewById(R.id.home);

                    textViewName.setText(contact.name);
                    textViewEmail.setText(contact.email);
                    textViewMobile.setText(contact.mobile);
                    textViewHome.setText(contact.home);
                    myRoot.addView(v);
                    return;
                default:
                    return;
            }
        }
    }

    public void OnClick(View view){
        TextView textViewName=(TextView)view.findViewById(R.id.name);
        TextView textViewEmail=(TextView)view.findViewById(R.id.email);
        TextView textViewMobileLat=(TextView)view.findViewById(R.id.mobile);
        TextView textViewHomeLong=(TextView)view.findViewById(R.id.home);
        Intent intent=new Intent(contactDisplay.this, Maps.class);
        intent.putExtra("name",textViewName.getText().toString());
        intent.putExtra("email",textViewEmail.getText().toString());
        intent.putExtra("location",textViewMobileLat.getText().toString() + " " + textViewHomeLong.getText().toString());
        startActivity(intent);
    }
}
