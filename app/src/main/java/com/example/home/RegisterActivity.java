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
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    String url="http://172.20.10.6:5000/register";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    public void register(View v) {
        EditText usernameView = findViewById(R.id.email);
        EditText passwordView = findViewById(R.id.pwd);
        EditText passwordView1 = findViewById(R.id.pwd1);
        EditText nameView = findViewById(R.id.name);
        EditText telView = findViewById(R.id.tel);
        EditText addressView = findViewById(R.id.address);

        String email_register = usernameView.getText().toString().trim();
        String pwd_register = passwordView.getText().toString().trim();
        String pwd_register1 = passwordView1.getText().toString().trim();
        String name_register = nameView.getText().toString().trim();
        String tel_register = telView.getText().toString().trim();
        String address_register = addressView.getText().toString().trim();

        if (name_register.length() == 0 || email_register.length() == 0 || pwd_register.length() == 0 || pwd_register1.length() == 0 || tel_register.length() == 0 || address_register.length() == 0) {
            Toast.makeText(getApplicationContext(), "Something is wrong. Please check your inputs.", Toast.LENGTH_LONG).show();
        }
        else if(!pwd_register.equals(pwd_register1)){
            Toast.makeText(getApplicationContext(), "Something is wrong. Password Not matching.", Toast.LENGTH_LONG).show();
        }
        else {
            JSONObject registrationForm = new JSONObject();
            try {
                registrationForm.put("email_register", email_register);
                registrationForm.put("pwd_register", pwd_register);
                registrationForm.put("name_register", name_register);
                registrationForm.put("tel_register", tel_register);
                registrationForm.put("address_register", address_register);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), registrationForm.toString());

            postRequest(body);
        }
    }
    public void postRequest(RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                //.url("http://10.0.2.2:5000/insert/register")
                .url(url)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                call.cancel();
                Log.d("step connect http", e.getMessage());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(RegisterActivity.this);
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
            public void onResponse(okhttp3.Call call, final Response response) {
                try {
                    final String responseString = response.body().string().trim();
                    Log.d("step insert", "Response from the server : " + responseString);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseString.equals("success")) {
                                Log.d("step insert", "Successful Register");

                                Toast toast = Toast.makeText(getApplicationContext(), "Register Success", Toast.LENGTH_SHORT);
                                toast.show();

                                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                Log.e("step:","intent");
                                //ถ้า username and password ถูกต้อง
                                startActivity(intent);
                                finish();
                            } else if (responseString.equals("failure")) {
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(RegisterActivity.this);

                                dlgAlert.setMessage("Username already exists. Please chose another username.");
                                dlgAlert.setTitle("Error Message...");
                                dlgAlert.setPositiveButton("OK", null);
                                dlgAlert.setCancelable(true);
                                dlgAlert.create().show();

                                dlgAlert.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                            } else {
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(RegisterActivity.this);

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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
