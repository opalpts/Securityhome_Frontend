package com.example.home;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class AddActivity extends AppCompatActivity {
    String url="http://172.20.10.6:5000/camera";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
    }
    public void addSubmit(View view){
        EditText nameView = findViewById(R.id.name);
        String name = nameView.getText().toString().trim();

        JSONObject loginForm = new JSONObject();
        try {
            loginForm.put("name", name);
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
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(AddActivity.this);

                        dlgAlert.setMessage("มองที่กล้อง");
                        dlgAlert.setTitle("success");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();

                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent1=new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent1);
                                        overridePendingTransition(0,0);
                                        finish();
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

                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(AddActivity.this);

                                dlgAlert.setMessage("มองที่กล้อง");
                                dlgAlert.setTitle("success");
                                dlgAlert.setCancelable(true);
                                dlgAlert.create().show();

                                dlgAlert.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent1=new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(intent1);
                                                overridePendingTransition(0,0);
                                                finish();
                                            }
                                        });

                            } else if (loginResponseString.equals("failure")) {
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(AddActivity.this);

                                dlgAlert.setMessage("มองที่กล้อง");
                                dlgAlert.setTitle("success");
                                dlgAlert.setPositiveButton("OK", null);
                                dlgAlert.setCancelable(true);
                                dlgAlert.create().show();

                                dlgAlert.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent1=new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(intent1);
                                                overridePendingTransition(0,0);
                                                finish();
                                            }
                                        });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(AddActivity.this);

                            dlgAlert.setMessage("Something ");
                            dlgAlert.setTitle("Error Message...");
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();

                            dlgAlert.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent1=new Intent(getApplicationContext(),MainActivity.class);
                                            startActivity(intent1);
                                            overridePendingTransition(0,0);
                                            finish();
                                        }
                                    });
                        }
                    }
                });
            }
        });
    }
}
