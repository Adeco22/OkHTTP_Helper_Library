package com.unilab.okhttphelpersample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.unilab.okhttphelperlibrary.APIRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
                executeNewRequest();
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
        /*APIRequest apiRequest = new APIRequest("'http://dummy.restapiexample.com/api/v1/employees");
        apiRequest.setOnAPIRequestFinishedListener(new APIRequest.onAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String error, int errorCode) {
                tv_output.setText(error);
            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.setText(result);
            }

            @Override
            public void onResponseFailure(String error, Call call) {

            }
        });

        apiRequest.executeRequest(context);*/

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
        ArrayList<Pair<String, String>> parameters = new ArrayList<>();
        parameters.add(new Pair<>("v", "1.0"));
        parameters.add(new Pair<>("user", "vogella"));
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
        ArrayList<Pair<String, String>> formData = new ArrayList<>();
        formData.add(new Pair<>("v", "1.0"));
        formData.add(new Pair<>("user", "vogella"));
        APIRequest request = new APIRequest("http://dummy.restapiexample.com/api/v1/employees",
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

        // Execute the request at any time
        request.executeRequest(context);
    }
}
