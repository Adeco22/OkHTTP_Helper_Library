package com.unilab.okhttphelpersample;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ComparisonActivity extends AppCompatActivity {
    private Handler mHandler;
    private OkHttpClient client;
    private Context context;
    private TextView tv_output;
    private Button btn_execute;
    private Button btn_switch;

    private final static String TAG = ComparisonActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);
        setData();
    }

    private void setData() {
        context = this;
        setLayout();
    }

    private void setLayout() {
        tv_output = findViewById(R.id.tv_output);
        btn_execute = findViewById(R.id.btn_execute);
        btn_switch = findViewById(R.id.btn_switch);

        setListeners();
    }

    private void setListeners() {
        btn_execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeNormalRequest();
            }
        });

        btn_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });
    }

    private void executeNormalRequest() {
        tv_output.setText("Requesting Data...");
        mHandler = new Handler(Looper.getMainLooper());
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl
                .parse("http://dummy.restapiexample.com/api/v1/employees")
                .newBuilder();
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String errorMessage = "Connection failed due to " + e.getMessage();
                e.printStackTrace();
                Log.e(TAG + " => client onFailure", errorMessage);
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String r;
                if (response.body() != null) {
                    r = response.body().string();
                } else {
                    r = "";
                }
                final int requestCode = response.code();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray array = new JSONArray(r);
                            tv_output.setText(array.toString() + "\n status code: "
                                    + requestCode);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void executeRequestWithHeaders() {
        mHandler = new Handler(Looper.getMainLooper());
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl
                .parse("http://dummy.restapiexample.com/api/v1/employees")
                .newBuilder();
        urlBuilder.addQueryParameter("v", "1.0");
        urlBuilder.addQueryParameter("user", "vogella");
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String errorMessage = "Connection failed due to " + e.getMessage();
                e.printStackTrace();
                Log.e(TAG + " => client onFailure", errorMessage);
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String r;
                if (response.body() != null) {
                    r = response.body().string();
                } else {
                    r = "";
                }
                final int requestCode = response.code();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject array = new JSONObject(r);

                            tv_output.setText(array.toString() + "\n status code: "
                                    + requestCode);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void executeRequestWithParameters() {
        mHandler = new Handler(Looper.getMainLooper());
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl
                .parse("http://dummy.restapiexample.com/api/v1/employees")
                .newBuilder();
        String url = urlBuilder.build().toString();
        FormBody formBody = new FormBody.Builder()
                .add("v", "1.0")
                .add("user", "vogella")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String errorMessage = "Connection failed due to " + e.getMessage();
                e.printStackTrace();
                Log.e(TAG + " => client onFailure", errorMessage);
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String r;
                if (response.body() != null) {
                    r = response.body().string();
                } else {
                    r = "";
                }
                final int requestCode = response.code();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject array = new JSONObject(r);

                            tv_output.setText(array.toString() + "\n status code: "
                                    + requestCode);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
