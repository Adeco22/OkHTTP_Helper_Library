package com.unilab.okhttphelpersample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unilab.okhttphelperlibrary.APIRequest;
import com.unilab.okhttphelperlibrary.models.Header;
import com.unilab.okhttphelperlibrary.models.Parameter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private TextView tv_output;
    private Button btn_execute;
    private Button btn_switch;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ATTACH_FILE_REQUEST_CODE = 670;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 280;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ATTACH_FILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    Uri imageUri = data.getData();
                    saveImageToPrivateStorage(imageUri);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    return;
                }
            }
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
        }
    }

    private void attachFile() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, ATTACH_FILE_REQUEST_CODE);
        }
    }

    private void saveImageToPrivateStorage(Uri imageUri) {
        //create a file to write bitmap data
        if (imageUri.getPath() != null) {
            final String fileName = new File(imageUri.getPath()).getName() + ".png";

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                FileOutputStream out = openFileOutput(fileName, Context.MODE_PRIVATE);

                new AsyncSaveFileToInternalStorage(bitmap, out, new AsyncSaveFileToInternalStorage.OnInternalStorageFileSaveListener() {
                    @Override
                    public void onInternalFileSaved() {
                        executeFileRequest(fileName);
                    }

                    @Override
                    public void onInternalFileSaveFail() {
                    }
                }).execute();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "file was not transferred");
            }
        }
    }

    private void executeFileRequest(String filename) {
        File file = new File(getFilesDir(), filename);
        ArrayList<Header> headerList = new ArrayList<>();
        headerList.add(new Header("Authorization", "Basic " + Base64.encodeToString("m_jppalacpac:password1".getBytes(), Base64.NO_WRAP)));

        ArrayList<Parameter> parameters = new ArrayList<>();
        Parameter parameter = new Parameter();
        parameter.setParameter_key("");
        parameter.setParameter_type(Parameter.TYPE_FILE);
        parameter.setCustomParameter_media_type(Parameter.MEDIA_TYPE_PNG);
        parameter.setParameter_file(file);
        //parameter.setParameter_value(total.toString());
        parameters.add(parameter);

        Log.d(TAG, "filename: " + file.getName());

        APIRequest apiRequest = new APIRequest("http://phsjulchsvr4.unilab.com.ph:1218/api/fileupload");
        apiRequest.setCurrentRequestType(APIRequest.POST_REQUEST);
        apiRequest.setHeaders(headerList);
        apiRequest.setParameters(parameters);
        apiRequest.setOnAPIRequestFinishedListener(new APIRequest.OnAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String errorMessage, int errorCode) {
                tv_output.setText(errorMessage);
            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.setText(result);
            }

            @Override
            public void onResponseFailure(String errorMessage, int errorCode, Call call) {
                tv_output.setText(errorMessage);
            }
        });
        apiRequest.executeRequest(this);
    }

    private void executeRawRequest() {
        ArrayList<Header> headerList = new ArrayList<>();
        headerList.add(new Header("Authorization", "Basic " + Base64.encodeToString("m_jppalacpac:password1".getBytes(), Base64.NO_WRAP)));

        Parameter parameter = new Parameter();
        parameter.setParameter_key("");
        parameter.setParameter_value("{\n" +
                "    \"Year\": 2019,\n" +
                "    \"Month\": 6,\n" +
                "    \"Week\": 0,\n" +
                "    \"Day\": 0,\n" +
                "    \"AccountCoverageStatusId\": 0,\n" +
                "    \"IsAMPUser\": true,\n" +
                "    \"AMPUserId\": 312,\n" +
                "    \"IsDMUser\": false,\n" +
                "    \"DMUserId\": 0,\n" +
                "    \"SubBrandClassificationId\": 5\n" +
                "}");

        parameter.setMIME_type(Parameter.MEDIA_TYPE_JSON);
        parameter.setParameter_type(Parameter.TYPE_FILE);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter);

        APIRequest apiRequest = new APIRequest("http://phsjulchsvr4.unilab.com.ph:1218/api/CoveragePlanList");
        apiRequest.setCurrentRequestType(APIRequest.POST_REQUEST);
        apiRequest.setHeaders(headerList);
        apiRequest.setParameters(parameters);
        apiRequest.setOnAPIRequestFinishedListener(new APIRequest.OnAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String errorMessage, int errorCode) {
                tv_output.setText(errorMessage);
            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.setText(result);
            }

            @Override
            public void onResponseFailure(String errorMessage, int errorCode, Call call) {
                tv_output.setText(errorMessage);
            }
        });
        apiRequest.executeRequest(this);
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
                executeTimeoutRequest();
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
        request.setCurrentRequestType(APIRequest.GET_REQUEST);
        request.setHeaders(null);
        request.setParameters(null);
        // Step 3 - setup request listener
        request.setOnAPIRequestFinishedListener(new APIRequest.OnAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String errorMessage, int errorCode) {

            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.append("\n\n\n");
                tv_output.append(result);
            }

            @Override
            public void onResponseFailure(String errorMessage, int errorCode, Call call) {

            }
        });
        // Optional - build the request
        request.build();

        // Optional - get request build data
        request.getCurrentRequest();
        request.getCurrentClientBuilder();
        request.getCurrentClient();

        // Step 4 - execute the request

        request.executeRequest(this);

        // Optional - create new request requirements then rebuild

        request.setURL("https://reqres.in/api/users/2");

        request.setOnAPIRequestFinishedListener(new APIRequest.OnAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String errorMessage, int errorCode) {

            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.append("\n\n\n");
                tv_output.append(result);
            }

            @Override
            public void onResponseFailure(String errorMessage, int errorCode, Call call) {

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

        request.setOnAPIRequestFinishedListener(new APIRequest.OnAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String errorMessage, int errorCode) {

            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.setText(result);
            }

            @Override
            public void onResponseFailure(String errorMessage, int errorCode, Call call) {

            }
        });

        // Step 4 - execute the request
        request.executeRequest(this);
    }

    private void executeNormalRequest() {
        APIRequest request = new APIRequest("http://dummy.restapiexample.com/api/v1/employees");

        // Add callback on response
        request.setOnAPIRequestFinishedListener(new APIRequest.OnAPIRequestListener() {
            @Override
            public void onPreRequest() {
                tv_output.setText("Requesting Data...");
            }

            @Override
            public void onRequestFailure(String errorMessage, int errorCode) {
                tv_output.setText(errorMessage);
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
            public void onResponseFailure(String errorMessage, int errorCode, Call call) {
                tv_output.setText(errorMessage);
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
        request.setOnAPIRequestFinishedListener(new APIRequest.OnAPIRequestListener() {
            @Override
            public void onPreRequest() {
                tv_output.setText("Requesting Data...");
            }

            @Override
            public void onRequestFailure(String errorMessage, int errorCode) {
                tv_output.setText(errorMessage);
            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.setText(result);
            }

            @Override
            public void onResponseFailure(String errorMessage, int errorCode, Call call) {
                tv_output.setText(errorMessage);
            }
        });

        // Execute the request at any time
        request.executeRequest(context);
    }

    private void executeDaveRequest() {
        ArrayList<Header> headerList = new ArrayList<>();
        headerList.add(new Header("Authorization", "Basic " + Base64.encodeToString("m_jppalacpac:password1".getBytes(), Base64.NO_WRAP)));

        ArrayList<Parameter> parameters = new ArrayList<>();
        Parameter parameter = new Parameter();
        parameter.setParameter_key("");
        parameter.setParameter_type(Parameter.TYPE_FILE);
        parameter.setCustomParameter_media_type(Parameter.MEDIA_TYPE_PNG);
        parameter.setParameter_file(new File(Environment.getDataDirectory(), ""));
        //parameter.setParameter_value(total.toString());
        parameters.add(parameter);

        APIRequest apiRequest = new APIRequest("http://phsjulchsvr4.unilab.com.ph:1218/api/fileupload");
        apiRequest.setCurrentRequestType(APIRequest.POST_REQUEST);
        apiRequest.setHeaders(headerList);
        apiRequest.setParameters(parameters);
        apiRequest.setOnAPIRequestFinishedListener(new APIRequest.OnAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String errorMessage, int errorCode) {
                tv_output.setText(errorMessage);
            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.setText(result);
            }

            @Override
            public void onResponseFailure(String errorMessage, int errorCode, Call call) {
                tv_output.setText(errorMessage);
            }
        });
        apiRequest.executeRequest(this);
    }

    private void executeTimeoutRequest() {

        APIRequest apiRequest = new APIRequest("http://example.com:81");
        apiRequest.setCurrentRequestType(APIRequest.GET_REQUEST);
        apiRequest.setRequestTimeoutInSecs(1);
        apiRequest.setOnAPIRequestFinishedListener(new APIRequest.OnAPIRequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestFailure(String errorMessage, int errorCode) {
                tv_output.setText(errorMessage);
            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.setText(result);
            }

            @Override
            public void onResponseFailure(String errorMessage, int errorCode, Call call) {
                tv_output.setText(errorMessage);
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
        request.setCurrentRequestType(APIRequest.POST_REQUEST);

        // Add callback on response
        request.setOnAPIRequestFinishedListener(new APIRequest.OnAPIRequestListener() {
            @Override
            public void onPreRequest() {
                tv_output.setText("Requesting Data...");
            }

            @Override
            public void onRequestFailure(String errorMessage, int errorCode) {
                tv_output.setText(errorMessage);
            }

            @Override
            public void onResponseSuccess(String result, int statusCode) {
                tv_output.setText(result);
                Log.d(TAG, "result = " + result);
                Log.d(TAG, "status code = " + statusCode);
            }

            @Override
            public void onResponseFailure(String errorMessage, int errorCode, Call call) {
                tv_output.setText(errorMessage);
            }
        });

        // Execute the request at any time
        request.executeRequest(context);
    }
}
