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
import com.unilab.okhttphelperlibrary.Header;
import com.unilab.okhttphelperlibrary.Parameter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private TextView tv_output;
    private Button btn_execute;
    private Button btn_switch;
    private static final String TAG = MainActivity.class.getSimpleName();

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

    /**
     * link 1 - https://reqres.in/api/users?page=2
     * link 2 - https://reqres.in/api/users/2
     */
    private void executeRequestWithBuild() {
        // Step 1 - create request object
        APIRequest request = new APIRequest("https://reqres.in/api/users?page=2");

        // Step 2 - setup request requirements (params, headers, request type, etc.)
        request.setRequestType(APIRequest.GET_REQUEST);
        request.setHeaders(null);
        request.setParameters(null);
        // Step 3 - setup request listener
        request.setOnAPIRequestFinishedListener(new APIRequest.onAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String error, int errorCode) {

            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.append("\n\n\n");
                tv_output.append(result);
            }

            @Override
            public void onResponseFailure(String error, Call call) {

            }
        });
        // Optional - build the request
        request.build();

        // Optional - get request build data
        request.getRequest();
        request.getClientBuilder();
        request.getClient();

        // Step 4 - execute the request

        request.executeRequest(this);

        // Optional - create new request requirements then rebuild

        request.setURL("https://reqres.in/api/users/2");

        request.setOnAPIRequestFinishedListener(new APIRequest.onAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String error, int errorCode) {

            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.append("\n\n\n");
                tv_output.append(result);
            }

            @Override
            public void onResponseFailure(String error, Call call) {

            }
        });

        request.rebuild();

        request.executeRequest(this);

    }

    /**
     * https://reqres.in/api/users
     * <p>
     * "name": "",
     * "job": ""
     */
    private void executeNewPostRequest() {
        // Step 1 - create request object
        APIRequest request = new APIRequest("https://reqres.in/api/users");

        // Step 2 - setup request requirements (params, headers, request type, etc.)
        ArrayList<Parameter> parameters = new ArrayList<>();

        parameters.add(new Parameter("name", "tonton"));
        parameters.add(new Parameter("job", "taga kulit kay jayson"));

        request.setParameters(parameters);

        // Step 3 - setup request listener

        request.setOnAPIRequestFinishedListener(new APIRequest.onAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String error, int errorCode) {

            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.setText(result);
            }

            @Override
            public void onResponseFailure(String error, Call call) {

            }
        });

        // Step 4 - execute the request
        request.executeRequest(this);
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
                tv_output.setText(result);
            }

            @Override
            public void onResponseFailure(String error, Call call) {
                tv_output.setText(error);
            }
        });

        // Execute the request at any time
        request.executeRequest(context);
    }

    private void executeDaveRequest() {
        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("package_name", "com.rnd.concealtest"));
        parameters.add(new Parameter("token", "k7plzk9cxuyxvq8pj0t6d"));
        parameters.add(new Parameter("device_id", ""));
        APIRequest apiRequest = new APIRequest("http://172.29.70.126/scms/content_management/config_api/check_config");
        apiRequest.setRequestType(APIRequest.POST_REQUEST);
        apiRequest.setParameters(parameters);
        apiRequest.setOnAPIRequestFinishedListener(new APIRequest.onAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String error, int errorCode) {

            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.setText(result);
            }

            @Override
            public void onResponseFailure(String error, Call call) {

            }
        });
        apiRequest.executeRequest(this);
    }


    private void executeRequestWithParameters() {
        ArrayList<Header> headers = new ArrayList<>();
        headers.add(new Header("Authorization", "Basic el9tbm5vbmF0bzpwYXNzd29yZDE="));

        ArrayList<Parameter> parameters = new ArrayList<>();
        Parameter fileParameter = new Parameter();
        fileParameter.setParameter_key("");
        fileParameter.setParameter_value("{'Year':2019,'Month':7,'Week':0,'Day':0,'AccountCoverageStatusId':0,'IsAMPUser':true,'AMPUserId':162,'IsDMUser':false,'DMUserId':0,'SubBrandClassificationId':5}");
        fileParameter.setCustomParameter_media_type("application/x-www-form-urlencoded");
        fileParameter.setParameter_type(Parameter.TYPE_FILE);
        parameters.add(fileParameter);

        APIRequest request = new APIRequest("http://phsjulchsvr4.unilab.com.ph:1218/api/CoveragePlanList");

        request.setHeaders(headers);
        request.setParameters(parameters);
        request.setRequestType(APIRequest.POST_REQUEST);

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
                tv_output.setText(result);
                Log.d(TAG, "result = " + result);
                Log.d(TAG, "status code = " + statusCode);
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
