package com.unilab.okhttphelpersample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unilab.okhttphelperlibrary.APIRequest;
import com.unilab.okhttphelperlibrary.Parameter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private TextView tv_output;
    private Button btn_execute;
    private Button btn_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                executeRequestWithParameters();
            }
        });

        btn_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ComparisonActivity.class));
                finish();
            }
        });
    }

    private void executeNewRequest() {
        APIRequest apiRequest1 = new APIRequest("https://nba.com", null, null);
        apiRequest1.setOnAPIRequestFinishedListener(new APIRequest.onAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String error, int errorCode) {
                tv_output.setText("onRequestError");
            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.setText(result + "\nStatus Code: " + statusCode);
            }

            @Override
            public void onResponseFailure(String error, Call call) {
                tv_output.setText("onResponseError");

            }
        });

        apiRequest1.setOnAPIRequestFinishedListener(new APIRequest.onAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String error, int errorCode) {

            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {

            }

            @Override
            public void onResponseFailure(String error, Call call) {

            }
        });
        apiRequest1.executeRequest(this);
    }

    private void executeNormalRequest() {
        APIRequest request = new APIRequest("http://dummy.restapiexample.com/api/v1/employees");

        // Add callback on response
        request.setOnAPIRequestFinishedListener(new APIRequest.onAPIRequestListener() {
            @Override
            public void onPreRequest() {
                tv_output.setText("Requesting Data...");
            }

            @Override
            public void onRequestFailure(String error, int errorCode) {
                tv_output.setText(error);
            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                try {
                    JSONArray array = new JSONArray(result);
                    tv_output.setText(array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailure(String error, Call call) {
                tv_output.setText(error);
            }
        });

        // Execute the request at any time
        request.executeRequest(context);
    }

    //"https://reqres.in/api/users?delay=5"
    private void executeRequestWithHeaders() {
        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("v", "1.0"));
        parameters.add(new Parameter("user", "vogella"));
        APIRequest request = new APIRequest("http://dummy.restapiexample.com/api/v1/employees",
                null, parameters, APIRequest.GET_REQUEST);

        // Add callback on response
        request.setOnAPIRequestFinishedListener(new APIRequest.onAPIRequestListener() {
            @Override
            public void onPreRequest() {
                tv_output.setText("Requesting Data...");
            }

            @Override
            public void onRequestFailure(String error, int errorCode) {
                tv_output.setText(error);
            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                try {
                    JSONObject array = new JSONObject(result);
                    tv_output.setText(array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailure(String error, Call call) {
                tv_output.setText(error);
            }
        });

        // Execute the request at any time
        request.executeRequest(context);
    }


    private void executeRequestWithParameters() {
        ArrayList<Parameter> formData = new ArrayList<>();
        formData.add(new Parameter("package_name", "com.unilab.demoapplication"));
        formData.add(new Parameter("token", "x9owdte8caaojrc59r2w4"));
        formData.add(new Parameter("device_id", ""));
        APIRequest request = new APIRequest("https://www.nba.com/",
                null, formData, APIRequest.POST_REQUEST);

        // Add callback on response
        request.setOnAPIRequestFinishedListener(new APIRequest.onAPIRequestListener() {
            @Override
            public void onPreRequest() {
                tv_output.setText("Requesting Data...");
            }

            @Override
            public void onRequestFailure(String error, int errorCode) {
                tv_output.setText(error);
            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                try {
                    JSONObject array = new JSONObject(result);
                    tv_output.setText(array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailure(String error, Call call) {
                tv_output.setText(error);
            }
        });

        request.build();

        Log.d("OKHTTPTESTING", request.toString());

        request.setURL("http://172.29.70.126/scms/content_management/config_api/check_config/");
        request.rebuild();

        Log.d("OKHTTPTESTING", request.toString());
        // Execute the request at any time
        request.executeRequest(context);
    }
}
