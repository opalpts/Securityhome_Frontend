package com.example.home;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    Shared shared;
    public static String emails;
    String url="http://172.20.10.6:5000/login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        shared = new Shared(this);
    }
    public void register(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    public void submit(View v) {
        EditText usernameView = findViewById(R.id.email);
        EditText passwordView = findViewById(R.id.pwd);

        String email = usernameView.getText().toString().trim();
        String pwd = passwordView.getText().toString().trim();
        emails = email;
        if (email.length() == 0 || pwd.length() == 0) {
            Toast.makeText(getApplicationContext(), "Something is wrong. Please check your inputs.", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject loginForm = new JSONObject();
        try {
            loginForm.put("email", email);
            loginForm.put("pwd", pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), loginForm.toString());
        postRequest(body);
    }
    public void postRequest(RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
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
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(LoginActivity.this);

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
                            String loginResponseString = response.body().string().trim();
                            Log.d("step LOGIN", "Response from the server : " + loginResponseString);
                            if (loginResponseString.equals("success")) {
                                Log.d("step LOGIN", "Successful Login");

                                shared.createSession(emails);
                                Log.e("step:","Intent");
                                //ถ้า username and password ถูกต้อง
                                Log.e("step:","Intent data"+emails);
                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
//                                intent.putExtra("email",emails);
                                startActivity(intent);
                                finish();//finishing activity and return to the calling activity.

                            } else if (loginResponseString.equals("failure")) {
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(LoginActivity.this);

                                dlgAlert.setMessage("Login Failed. Invalid username or password.");
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
                            else if (loginResponseString.equals("logined")) {
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(LoginActivity.this);
                                dlgAlert.setMessage("You're Logined!!");
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
                            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(LoginActivity.this);

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
}
