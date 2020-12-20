package com.example.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Shared shared;
    String url ="http://172.20.10.6:8081";
    String url2="http://172.20.10.6:5000/logout";
    String sound = "http://172.20.10.6:5000/alert";
    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shared = new Shared(this);
        shared.checkLogin();

        TextView email= findViewById(R.id.email);
        HashMap<String,String> user = shared.getUserDetail();
        String mEmail = user.get(shared.EMAIL);
        //email.setText(mEmail);


        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        return true;
//                    case R.id.navigation_notifications:
//                        Intent intent1=new Intent(getApplicationContext(),NotificationsActivity.class);
//                        startActivity(intent1);
//                        overridePendingTransition(0,0);
//                        finish();
//                        return true;
                    case R.id.navigation_settings:
                        Intent intent2=new Intent(getApplicationContext(),SettingsActivity.class);
                        startActivity(intent2);
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.navigation_logout:
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
                        dlgAlert.setMessage("คุณต้องการออกจากระบบหรือไม่");
                        dlgAlert.setTitle("Logout");
                        dlgAlert.setPositiveButton("logout",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        HashMap<String,String> user = shared.getUserDetail();
                                        String mEmail = user.get(shared.EMAIL);
                                        Log.d("step Logout", "mEmail : " + mEmail);
                                        JSONObject loginForm = new JSONObject();
                                        try {
                                            loginForm.put("email", mEmail);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), loginForm.toString());
                                        postRequest(body);
                                    }
                                });
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                        return true;
                }
                return false;
            }
        });
    }
    public void postRequest(RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url2)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("step connect http", e.getMessage());

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);

                        dlgAlert.setMessage("Failed to Connect to Server. Please Try Again.");
                        dlgAlert.setTitle("Error Message...");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();

                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String logoutString = response.body().string().trim();
                            Log.d("step Logout", "Response from the server : " + logoutString);
                            if (logoutString.equals("success")) {
                                Shared shared=new Shared(getApplicationContext());
                                shared.logout();
                                Log.d("step Logout", "Successful Logout");
                                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();

                            } else if (logoutString.equals("failure")) {
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
                                dlgAlert.setMessage("Logout Failed");
                                dlgAlert.setTitle("Error Message...");
                                dlgAlert.setPositiveButton("OK", null);
                                dlgAlert.setCancelable(true);
                                dlgAlert.create().show();

                                dlgAlert.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
                            dlgAlert.setMessage("Something went wrong. Please try again later.");
                            dlgAlert.setTitle("Error Message...");
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();

                            dlgAlert.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                        }
                    }
                });
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                Intent intent1=new Intent(getApplicationContext(),AddActivity.class);
                startActivity(intent1);
                overridePendingTransition(0,0);
                finish();
                return true;
            case R.id.action_sound:
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
                dlgAlert.setMessage("ต้องการส่งเสียงหรือไม่");
                dlgAlert.setTitle("Sound");
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url(sound)
                                        .header("Accept", "application/json")
                                        .header("Content-Type", "application/json")
                                        .build();

                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        // Cancel the post on failure.
                                        call.cancel();
                                        Log.d("step connect http", e.getMessage());

                                        // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);

                                                dlgAlert.setMessage("Failed to Connect to Server. Please Try Again.");
                                                dlgAlert.setTitle("Error Message...");
                                                dlgAlert.setPositiveButton("OK", null);
                                                dlgAlert.setCancelable(true);
                                                dlgAlert.create().show();

                                                dlgAlert.setPositiveButton("Ok",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, final Response response) throws IOException {
                                        // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String logoutString = response.body().string().trim();
                                                    Log.d("step Logout", "Response from the server : " + logoutString);
                                                    if (logoutString.equals("success")) {
                                                        Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();

                                                    } else if (logoutString.equals("failure")) {
                                                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
                                                        dlgAlert.setMessage("Logout Failed");
                                                        dlgAlert.setTitle("Error Message...");
                                                        dlgAlert.setPositiveButton("OK", null);
                                                        dlgAlert.setCancelable(true);
                                                        dlgAlert.create().show();

                                                        dlgAlert.setPositiveButton("Ok",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                    }
                                                                });
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
                                                    dlgAlert.setMessage("Something went wrong. Please try again later.");
                                                    dlgAlert.setTitle("Error Message...");
                                                    dlgAlert.setPositiveButton("OK", null);
                                                    dlgAlert.setCancelable(true);
                                                    dlgAlert.create().show();

                                                    dlgAlert.setPositiveButton("Ok",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            });
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
