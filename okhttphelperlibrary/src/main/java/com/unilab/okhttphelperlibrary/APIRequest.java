package com.unilab.okhttphelperlibrary;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.text.SpannableStringBuilder;
import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okio.BufferedSink;

/**
 * <p>
 * OkHttp Helper Class designed for removing boilerplate and redundant code
 * </p>
 *
 * @author Anthony Deco
 * @version 0.12.1 (beta)
 * @since 05/02/2019, 4:13 PM
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class APIRequest {
    private static final String TAG = APIRequest.class.getSimpleName();
    private String request_URL;
    private ArrayList<Header> request_headers;
    private ArrayList<Parameter> request_parameters;
    private onAPIRequestListener listener;
    private Handler handler;
    private int requestType;
    private OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
    private OkHttpClient client;
    private Request request;
    private SpannableStringBuilder credentialUsername;
    private SpannableStringBuilder credentialPassword;

    /**
     * Base URL used by the library
     */
    private String base_url = "";

    /**
     * Boolean checker if client has been built
     */
    private boolean clientHasBuilt = false;

    /**
     * TimeUnit associated with the request timeout with {@link TimeUnit#SECONDS} as default
     * , see {@link TimeUnit}
     */
    private TimeUnit REQUEST_TIMEOUT_TIME_UNIT = TimeUnit.SECONDS;

    /**
     * Value associated with the request timeout, the default is 30
     */
    private int REQUEST_TIMEOUT = 30;

    // API Helper class request types
    /**
     * Get request type
     */
    public static final int GET_REQUEST = 822;
    /**
     * Post request type
     */
    public static final int POST_REQUEST = 253;
    /**
     * Put request type
     */
    public static final int PUT_REQUEST = 704;
    /**
     * Patch request type
     */
    public static final int PATCH_REQUEST = 880;
    /**
     * Delete request type
     */
    public static final int DELETE_REQUEST = 254;

    // API Helper Class error codes
    /**
     * Generic client error code
     */
    public static final int ERROR_ON_REQUEST = 600;

    /**
     * Error code when URL provided is malformed or invalid
     */
    public static final int ERROR_MALFORMED_URL = 601;

    /**
     * Error message when URL provided is malformed or invalid
     */
    private static final String ERROR_MALFORMED_URL_MESSAGE = "URL provided is malformed," +
            " this may be caused by typos in the URL path";

    /**
     * Error code if device has no internet connection
     */
    public static final int ERROR_NO_CONNECTION = 602;

    /**
     * Error message if device has no internet connection
     */
    private static final String ERROR_NO_CONNECTION_MESSAGE = "No internet connection," +
            " ensure that you have internet access";

    /**
     * Default constructor of the class
     *
     * @param request_URL        {@link String} base URL to be requested from
     * @param request_headers    ArrayList<Pair<String, String>> key-value pair
     *                           used for the API request headers
     * @param request_parameters ArrayList<Pair<String, String>> key-value pair
     *                           used for the API request body
     * @param listener           {@link onAPIRequestListener} interface of the API request
     * @param requestType        int type of Request, default value is GET,
     *                           see {@link #GET_REQUEST}
     */
    public APIRequest(String request_URL, ArrayList<Header> request_headers,
                      ArrayList<Parameter> request_parameters,
                      onAPIRequestListener listener, int requestType) {
        this.request_URL = request_URL;
        this.request_headers = request_headers;
        this.request_parameters = request_parameters;
        this.listener = listener;
        this.requestType = requestType;
    }

    /**
     * Constructor of the class without request type
     *
     * @param request_URL        {@link String} base URL to be requested from
     * @param request_headers    ArrayList<Pair<String, String>> key-value pair
     *                           used for the API request headers
     * @param request_parameters ArrayList<Pair<String, String>> key-value pair
     *                           used for the API request body
     * @param listener           {@link onAPIRequestListener} interface of the API request
     */
    public APIRequest(String request_URL, ArrayList<Header> request_headers,
                      ArrayList<Parameter> request_parameters,
                      onAPIRequestListener listener) {
        this(request_URL, request_headers, request_parameters, listener, GET_REQUEST);
    }

    /**
     * Constructor of the class without interface connection and request type
     *
     * @param request_URL        {@link String} base URL to be requested from
     * @param request_headers    ArrayList<Pair<String, String>> key-value pair
     *                           used for the API request headers
     * @param request_parameters ArrayList<Pair<String, String>> key-value pair
     *                           used for the API request body
     * @param requestType        int type of Request, default value is GET,
     *                           see {@link #GET_REQUEST}
     */
    public APIRequest(String request_URL, ArrayList<Header> request_headers,
                      ArrayList<Parameter> request_parameters, int requestType) {
        this(request_URL, request_headers, request_parameters, null, requestType);
    }

    /**
     * Constructor of the class without interface connection and request type
     *
     * @param request_URL        {@link String} base URL to be requested from
     * @param request_headers    ArrayList<Header> key-value pair
     *                           used for the API request headers
     * @param request_parameters ArrayList<Parameter> key-value pair
     *                           used for the API request body
     */
    public APIRequest(String request_URL, ArrayList<Header> request_headers,
                      ArrayList<Parameter> request_parameters) {
        this(request_URL, request_headers, request_parameters, null);
    }

    /**
     * Constructor of the class without headers, parameters, interface connection, and request type
     *
     * @param request_URL {@link String} base URL to be requested from
     */
    public APIRequest(String request_URL) {
        this(request_URL, null, null);
    }

    /**
     * Sets the listener interface
     *
     * @param listener {@link onAPIRequestListener} interface of the API request
     */
    public void setOnAPIRequestFinishedListener(onAPIRequestListener listener) {
        this.listener = listener;
    }

    /**
     * Checks if the listener interface is present
     */
    public boolean hasAPIRequestFinishedListeners() {
        return listener != null;
    }

    /**
     * Sets the connection timeout of the request
     *
     * @param timeoutInSecs int timeout in seconds
     */
    public void setRequestTimeoutInSecs(int timeoutInSecs) {
        this.REQUEST_TIMEOUT = timeoutInSecs;
    }

    /**
     * Sets the connection timeout of the request with corresponding time unit
     *
     * @param timeout  int timeout value
     * @param timeUnit {@link TimeUnit} of the request
     */
    public void setRequestTimeout(int timeout, TimeUnit timeUnit) {
        this.REQUEST_TIMEOUT = timeout;
        this.REQUEST_TIMEOUT_TIME_UNIT = timeUnit;
    }

    /**
     * Sets a new base url
     *
     * @param url {@link String} new base url
     */
    public void setURL(String url) {
        this.request_URL = url;
    }

    /**
     * Sets new headers of the request
     *
     * @param headers ArrayList<Pair<String, String>> new request headers
     */
    public void setHeaders(ArrayList<Header> headers) {
        this.request_headers = headers;
    }

    /**
     * Sets new request body parameters
     *
     * @param parameters ArrayList<Pair<String, String>> new body parameters
     */
    public void setParameters(ArrayList<Parameter> parameters) {
        this.request_parameters = parameters;
    }

    /**
     * Sets a new request type
     *
     * @param requestType constant int request type, see {@link #GET_REQUEST}
     *                    , {@link #POST_REQUEST}, {@link #PUT_REQUEST}, {@link #PATCH_REQUEST} and
     *                    {@link #DELETE_REQUEST}
     */
    public void setRequestType(@RequestType int requestType) {
        this.requestType = requestType;
    }

    /**
     * Sets a custom OkHttp3 client builder for user override
     *
     * @param clientBuilder {@link OkHttpClient.Builder} builder of the client
     */
    public void setCustomClientBuilder(OkHttpClient.Builder clientBuilder) {
        this.clientBuilder = clientBuilder;
    }

    /**
     * Gets the OkHttpClient Builder of the library, see {@link okhttp3.OkHttpClient.Builder}
     *
     * @return {@link #clientBuilder}
     */
    public OkHttpClient.Builder getClientBuilder() {
        if (!clientHasBuilt) {
            throw new RuntimeException("Builder is null, you must build the APIRequest first.");
        }
        return clientBuilder;
    }

    /**
     * Gets the OkHttpClient Request of the library, see {@link okhttp3.OkHttpClient.Builder}
     *
     * @return {@link #request}
     */
    public Request getRequest() {
        if (!clientHasBuilt) {
            throw new RuntimeException("Request is null, you must build the APIRequest first.");
        }
        return request;
    }

    /**
     * Gets the OkHttpClient of the library, see {@link OkHttpClient}
     *
     * @return {@link #client}
     */
    public OkHttpClient getClient() {
        if (!clientHasBuilt) {
            throw new RuntimeException("Client is null, you must build the APIRequest first.");
        }
        return client;
    }

    /**
     * Rebuilds the whole client, this is to prevent multiple and confusing calls for {@link #build()}
     */
    public void rebuild() {
        clientHasBuilt = false;
        build();
    }

    /**
     * Preemptively builds the client, enabling getters {@link #getClient()},
     * {@link #getClientBuilder()} and {@link #getRequest()}.
     * Must call {@link #rebuild()} to build again.
     */
    public void build() {
        if (clientHasBuilt) {
            throw new RuntimeException(client.toString()
                    + " has already been built, do you mean to call rebuild() ?");
        }

        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }

        // Checks if request is null before building
        checkRequestIfNull();

        // Builds the URL to be requested
        HttpUrl.Builder urlBuilder;
        HttpUrl httpUrl = HttpUrl.parse(request_URL);

        // Checks if the URL is valid, if not, will trigger onRequestFailure
        if (httpUrl != null) {
            urlBuilder = httpUrl.newBuilder();
        } else {
            Log.e(TAG + " => executeRequest", ERROR_MALFORMED_URL_MESSAGE);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onRequestFailure(ERROR_MALFORMED_URL_MESSAGE, ERROR_MALFORMED_URL);
                }
            });
            return;
        }

        // Create the multi-body builder and request body object
        RequestBody body = new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(@NonNull BufferedSink sink) {

            }
        };

        if(request_parameters.size() > 0){
            // Adds URL parameters if the request type is GET
            if (requestType == GET_REQUEST) {
                for (Parameter parameter : request_parameters) {
                    urlBuilder.addQueryParameter(parameter.getParameter_key(), parameter.getParameter_value());
                }
            }

            // Adds the form body parameters if the request type is POST
            else if (requestType == POST_REQUEST) {
                // If the parameter is a single file
                if (request_parameters.size() == 1 && request_parameters.get(0).isFileParameter()) {
                    for (Parameter parameter : request_parameters) {
                        if(parameter.isFileValue()){
                            MultipartBody.Builder builder = new MultipartBody.Builder();
                            RequestBody requestBody = RequestBody.create(MediaType.parse(parameter.getMIME_type()), parameter.getParameter_file());
                            builder.addFormDataPart(parameter.getParameter_key(), parameter.getParameter_file().getName(), requestBody);
                            body = builder.build();
                        } else {
                            body = RequestBody.create(MediaType.parse(parameter.getMIME_type()), parameter.getParameter_value());
                        }
                    }
                }
                // If there are multiple parameters
                else {
                    boolean hasFile = false;
                    // Check if parameter has file
                    for (Parameter parameter : request_parameters) {
                        if (parameter.isFileParameter()) {
                            hasFile = true;
                            break;
                        }
                    }
                    // If parameters has file, initializes MultipartBody
                    if (hasFile) {
                        MultipartBody.Builder builder = new MultipartBody.Builder();
                        for (Parameter parameter : request_parameters) {
                            // If parameter is a file, will upload a separate request body
                            if (parameter.isFileParameter()) {
                                RequestBody requestBody;
                                if(parameter.isFileValue()){
                                    requestBody = RequestBody.create(MediaType.parse(
                                            parameter.getMIME_type()),
                                            parameter.getParameter_file());
                                } else {
                                    requestBody = RequestBody.create(MediaType.parse(
                                            parameter.getMIME_type()),
                                            parameter.getParameter_value());
                                }

                                builder.addFormDataPart(parameter.getParameter_key(), parameter.getParameter_key(), requestBody);
                            }
                            // Else will add as text parameter
                            else {
                                builder.addFormDataPart(parameter.getParameter_key(), parameter.getParameter_value());
                            }
                        }
                        body = builder.build();
                    }
                    // If parameters has no file, initializes FormBody
                    else {
                        FormBody.Builder builder = new FormBody.Builder();
                        for (Parameter parameter : request_parameters) {
                            builder.add(parameter.getParameter_key(), parameter.getParameter_value());
                        }
                        body = builder.build();
                    }
                }
            }
        }

        // Adds the request headers
        Request.Builder requestBuilder = new Request.Builder();
        for (Header header : request_headers) {
            requestBuilder.addHeader(header.getHeader_key(), header.getHeader_value());
        }

        // Specifies the request type of the OkHttpClient
        switch (requestType) {
            case GET_REQUEST: requestBuilder.get(); break;
            case POST_REQUEST: requestBuilder.post(body); break;
            case PUT_REQUEST: requestBuilder.put(body); break;
            case PATCH_REQUEST: requestBuilder.patch(body); break;
            case DELETE_REQUEST: requestBuilder.delete(body); break;
        }

        // Prints out url in logs if in debug mode
        String url = urlBuilder.build().toString();
        Log.d(TAG + " => executeRequest", url);

        request = requestBuilder.url(url).build();

        // TODO: add credentials builder
        clientBuilder.connectTimeout(REQUEST_TIMEOUT, REQUEST_TIMEOUT_TIME_UNIT);
        if (false) {
            clientBuilder.authenticator(new Authenticator() {
                @Override
                public Request authenticate(@Nullable Route route, @NonNull Response response) {
                    String credential = Credentials.basic("scott", "tiger");
                    return response.request().newBuilder().header("Authorization", credential).build();
                }
            });

        }
        client = clientBuilder.build();

        clientHasBuilt = true;
    }

    /**
     * Executes the API request then informs the result to the activity via the interface,
     * see {@link onAPIRequestListener}
     */
    public void executeRequest(Context context) {
        if (!clientHasBuilt) {
            build();
        }

        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }

        // Initiates the pre request listener method for loading screens, progress bars, etc.
        listener.onPreRequest();

        // Checks if device has internet connection, else, throws to onRequestFailure
        if (isConnectedToInternet(context)) {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    final String errorMessage = "Connection failed due to " + e.getMessage();
                    e.printStackTrace();
                    Log.e(TAG + " => client onFailure", errorMessage);
                    final Call outCall = call;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResponseFailure(errorMessage, outCall);
                        }
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    final String responseString;
                    if (response.body() != null) {
                        responseString = response.body().string();
                    } else {
                        responseString = "";
                    }
                    final int requestCode = response.code();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResponseSuccess(responseString, requestCode);
                        }
                    });
                }
            });
        } else {
            Log.e(TAG + " => client onFailure", ERROR_NO_CONNECTION_MESSAGE);
            listener.onRequestFailure(ERROR_NO_CONNECTION_MESSAGE, ERROR_NO_CONNECTION);
        }
    }

    /**
     * Checks the request variables if it is null then initializes it with empty or default value
     */
    private void checkRequestIfNull() {
        if (request_headers == null) request_headers = new ArrayList<>();
        if (request_parameters == null) request_parameters = new ArrayList<>();
        if (request_URL == null) request_URL = "";
        if (clientBuilder == null) clientBuilder = new OkHttpClient.Builder();
    }

    /**
     * Checks the current active network of the device then checks for internet access
     *
     * @param context {@link Context} where the activity is held
     * @return boolean true if there is an active internet connection with the list of networks,
     * else will return false
     */
    public boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * Interface in communicating with the Activity, this is to prohibit any
     * context-based and UI interactions within the class
     */
    public interface onAPIRequestListener {

        /**
         * Interface method before calling the API
         */
        void onPreRequest();

        /**
         * Interface method if encountered error before calling the API
         *
         * @param error {@link String} error message
         */
        void onRequestFailure(String error, @ErrorCode int errorCode);

        /**
         * Interface method for retrieving the response and its response code
         *
         * @param result     {@link String} response of the API call
         * @param statusCode int response code of the api ranging from 200 to 500
         */
        void onResponseSuccess(String result, int statusCode);

        /**
         * Interface method if the API call has failed
         *
         * @param error {@link String} error message
         * @param call  {@link Call} of the API
         */
        void onResponseFailure(String error, Call call);
    }

    @IntDef({GET_REQUEST, POST_REQUEST, PUT_REQUEST, PATCH_REQUEST, DELETE_REQUEST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequestType {
    }

    @IntDef({ERROR_ON_REQUEST, ERROR_MALFORMED_URL, ERROR_NO_CONNECTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorCode {
    }

    @StringDef({ ERROR_MALFORMED_URL_MESSAGE, ERROR_NO_CONNECTION_MESSAGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorMessage {
    }
}
