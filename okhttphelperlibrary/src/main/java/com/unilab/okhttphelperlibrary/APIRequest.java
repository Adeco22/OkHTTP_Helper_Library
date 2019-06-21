package com.unilab.okhttphelperlibrary;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <p>
 * OkHttp Helper Class designed for removing boilerplate and redundant code
 * </p>
 *
 * @author Anthony Deco
 * @version 0.8.0 (alpha)
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
    public void setRequestType(int requestType) {
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
     * Executes the API request then informs the result to the activity via the interface,
     * see {@link onAPIRequestListener}
     */
    public void executeRequest(Context context) {
        handler = new Handler(Looper.getMainLooper());
        checkRequestIfNull();

        // Initiates the pre request listener method for loading screens, progress bars, etc.
        listener.onPreRequest();

        // Checks if device has internet connection, else, throws to onRequestFailure
        if (isConnectedToInternet(context)) {
            // Builds the URL to be requested
            HttpUrl.Builder urlBuilder;
            HttpUrl p = HttpUrl.parse(request_URL);

            // Checks if the URL is valid, if not, will trigger onRequestFailure
            if (p != null) {
                urlBuilder = p.newBuilder();
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

            // Adds URL parameters if the request type is GET
            if (requestType == GET_REQUEST) {
                for (Parameter parameter : request_parameters) {
                    urlBuilder.addQueryParameter(parameter.getParameter_key(), parameter.getParameter_value());
                }
            }

            // Prints out url in logs if in debug mode
            String url = urlBuilder.build().toString();
            Log.d(TAG + " => executeRequest", url);

            // Adds the form body parameters if the request type is POST
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if (requestType == POST_REQUEST) {
                for (Parameter parameter : request_parameters) {
                    formBodyBuilder.add(parameter.getParameter_key(), parameter.getParameter_value());
                }
            }
            FormBody body = formBodyBuilder.build();

            // Adds the request headers and the POST form body if available
            Request.Builder requestBuilder = new Request.Builder();
            for (Header header : request_headers) {
                requestBuilder.addHeader(header.getHeader_key(), header.getHeader_value());
                if (requestType == POST_REQUEST) requestBuilder.post(body);
            }
            Request request = requestBuilder.url(url).build();

            // Calls the API request then transmits the data through the listener interface
            OkHttpClient client = clientBuilder
                    .connectTimeout(REQUEST_TIMEOUT, REQUEST_TIMEOUT_TIME_UNIT)
                    .build();

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
        if (requestType != GET_REQUEST &&
                requestType != POST_REQUEST &&
                requestType != PUT_REQUEST &&
                requestType != PATCH_REQUEST &&
                requestType != DELETE_REQUEST) requestType = GET_REQUEST;
    }

    /**
     * Checks the current active networks of the device then individually checks them for internet access
     *
     * @param context {@link Context} where the activity is held
     * @return boolean true if there is an active internet connection with the list of networks,
     * else will return false
     */
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            Network[] networks = connectivity.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivity.getNetworkInfo(mNetwork);
                if (NetworkInfo.State.CONNECTED.equals(networkInfo.getState())) {
                    return true;
                }
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
        void onRequestFailure(String error, int errorCode);

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
}
