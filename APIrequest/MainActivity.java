package com.example.apitest;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HERE api test";
    public static final int LOAD_SUCCESS = 101;

    private String SEARCH_URL = "https://pos.api.here.com/positioning/v1/locate?";
    private String APP_ID_TAG = "app_id=";
    private String APP_ID = "g9cu0MS7SE5blxTfApTn";
    private String APP_CODE_TAG = "&app_code=";
    private String APP_CODE = "FUYirG2FANQ0q9h2b1vtwg";
    private String REQUEST_URL = SEARCH_URL + APP_ID_TAG + APP_ID  + APP_CODE_TAG+ APP_CODE;

    private ProgressDialog progressDialog;
    private TextView textviewJSONText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonRequestJSON = (Button)findViewById(R.id.button_main_requestjson);
        textviewJSONText = (TextView)findViewById(R.id.textview_main_jsontext);
        textviewJSONText.setMovementMethod(new ScrollingMovementMethod());

        buttonRequestJSON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog( MainActivity.this );
                progressDialog.setMessage("Please wait.....");
                progressDialog.show();

                getJSON();
            }
        });
    }

    private final MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> weakReference;

        public MyHandler(MainActivity mainactivity) {
            weakReference = new WeakReference<MainActivity>(mainactivity);
        }

        @Override
        public void handleMessage(Message msg) {

            MainActivity mainactivity = weakReference.get();

            if (mainactivity != null) {
                switch (msg.what) {

                    case LOAD_SUCCESS:
                        mainactivity.progressDialog.dismiss();
                        String jsonString = (String)msg.obj;
                        mainactivity.textviewJSONText.setText(jsonString);
                        break;
                }
            }
        }
    }




    public void  getJSON() {

        Thread thread = new Thread(new Runnable() {

            public void run() {

                String result;

                try {

                    Log.d(TAG, REQUEST_URL);
                    URL url = new URL(REQUEST_URL);
                    Log.i("url",REQUEST_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    /** body 설정 구간 **/

                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);
                    conn.setRequestMethod("POST");

                    conn.setRequestProperty("Content-Type","application/json");
                    conn.setRequestProperty("Accept", "application/json");

                    JSONObject param1 = new JSONObject();
                    param1.put("mac","0a:ae:d6:0d:b8:67");
                    param1.put("powrx",-37);

                    JSONObject param2 = new JSONObject();
                    param2.put("mac","0a:ae:d6:0d:b8:41");
                    param2.put("powrx",-45);

                    JSONObject param3 = new JSONObject();
                    param3.put("mac","70:5d:cc:09:80:b6");
                    param3.put("powrx",-70);

                    JSONObject param4 = new JSONObject();
                    param4.put("mac","88:36:6c:9f:aa:d4");
                    param4.put("powrx",-77);

                    JSONObject param5 = new JSONObject();
                    param5.put("mac","00:62:ec:e6:26:4f");
                    param5.put("powrx",-40);

                    JSONObject param6 = new JSONObject();
                    param6.put("mac","00:62:ec:e6:26:4d");
                    param6.put("powrx",-46);

                    JSONArray jsonArray1 = new JSONArray();
                    jsonArray1.put(param1);
                    jsonArray1.put(param2);
                    jsonArray1.put(param3);
                    jsonArray1.put(param4);
                    jsonArray1.put(param5);
                    jsonArray1.put(param6);

                    JSONObject obj =  new JSONObject();
                    obj.put("wlan",jsonArray1);

                    String str = obj.toString();

                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    Log.i("strrr222r",str);


                    OutputStream os = conn.getOutputStream();
                    os.write(str.getBytes("utf-8"));
                    os.flush();

                    int responseStatusCode = conn.getResponseCode();

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = conn.getInputStream();
                    } else {
                        inputStream = conn.getErrorStream();
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    conn.disconnect();

                    result = sb.toString().trim();

                }

                catch (Exception e) {
                    result = e.toString();
                }

                Message message = mHandler.obtainMessage(LOAD_SUCCESS, result);
                mHandler.sendMessage(message);
            }

        });
        thread.start();
    }

}
