package com.unilab.okhttphelperlibrary;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;

import com.unilab.okhttphelperlibrary.models.Header;
import com.unilab.okhttphelperlibrary.models.Parameter;
import com.unilab.okhttphelperlibrary.utilities.InternetConnection;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * <p>
 * OkHttp Helper Class designed for removing boilerplate and redundant code
 * </p>
 *
 * @author Anthony Deco
 * @version 0.13.1 (beta)
 * @since 05/02/2019, 4:13 PM
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class APIRequest {
    private static final String TAG = APIRequest.class.getSimpleName();

    // Current data
    private Handler currentHandler;
    private List<Header> currentRequestHeaders;
    private List<Parameter> currentRequestParameters;
    private OkHttpClient.Builder currentClientBuilder = new OkHttpClient.Builder();
    private OkHttpClient currentClient;
    private OnAPIRequestListener currentAPIRequestListener;
    private Request currentRequest;
    private String currentRequestURL;
    private String currentRequestType;

    /**
     * Base URL used by the library
     * TODO: create baseURL support
     */
    private String currentBaseURL = "";

    /**
     * Boolean flag if client has been built
     */
    private boolean clientHasBuilt = false;

    /**
     * {@link TimeUnit} associated with the request timeout with {@link TimeUnit#SECONDS} as default
     */
    private TimeUnit currentRequestTimeoutUnit = TimeUnit.SECONDS;

    /**
     * Value associated with the request timeout, the default is 30
     */
    private int currentRequestTimeout = 30;

    /**
     * Default constructor of the class
     *
     * @param currentRequestURL         {@link String} base URL to be requested from
     * @param currentRequestHeaders     {@link List<>} of {@link Header}s used for the API request headers
     * @param currentRequestParameters  {@link List<>} of {@link Parameter}s used for the API request body
     * @param currentAPIRequestListener {@link OnAPIRequestListener} callback interface of the API request
     * @param currentRequestType        {@link String} type of Request, default value is {@link #GET_REQUEST}
     */
    public APIRequest(String currentRequestURL,
                      List<Header> currentRequestHeaders,
                      List<Parameter> currentRequestParameters,
                      OnAPIRequestListener currentAPIRequestListener,
                      @RequestType String currentRequestType) {

        this.currentRequestURL = currentRequestURL;
        this.currentRequestHeaders = currentRequestHeaders;
        this.currentRequestParameters = currentRequestParameters;
        this.currentAPIRequestListener = currentAPIRequestListener;
        this.currentRequestType = currentRequestType;
    }

    /**
     * Constructor of the class without request type
     *
     * @param currentRequestURL         {@link String} base URL to be requested from
     * @param currentRequestHeaders     {@link List<>} of {@link Header}s used for the API request headers
     * @param currentRequestParameters  {@link List<>} of {@link Parameter}s used for the API request body
     * @param currentAPIRequestListener {@link OnAPIRequestListener} callback interface of the API request
     */
    public APIRequest(String currentRequestURL,
                      List<Header> currentRequestHeaders,
                      List<Parameter> currentRequestParameters,
                      OnAPIRequestListener currentAPIRequestListener) {
        this(currentRequestURL, currentRequestHeaders, currentRequestParameters, currentAPIRequestListener, GET_REQUEST);
    }

    /**
     * Constructor of the class without interface connection and request type
     *
     * @param currentRequestURL        {@link String} base URL to be requested from
     * @param currentRequestHeaders    {@link List<>} of {@link Header}s used for the API request headers
     * @param currentRequestParameters {@link List<>} of {@link Parameter}s used for the API request body
     * @param currentRequestType       {@link String} type of Request, default value is {@link #GET_REQUEST}
     */
    public APIRequest(String currentRequestURL,
                      List<Header> currentRequestHeaders,
                      List<Parameter> currentRequestParameters,
                      @RequestType String currentRequestType) {
        this(currentRequestURL, currentRequestHeaders, currentRequestParameters, null, currentRequestType);
    }

    /**
     * Constructor of the class without interface connection and request type
     *
     * @param currentRequestURL        {@link String} base URL to be requested from
     * @param currentRequestHeaders    {@link List<>} of {@link Header}s used for the API request headers
     * @param currentRequestParameters {@link List<>} of {@link Parameter}s used for the API request body
     */
    public APIRequest(String currentRequestURL,
                      List<Header> currentRequestHeaders,
                      List<Parameter> currentRequestParameters) {
        this(currentRequestURL, currentRequestHeaders, currentRequestParameters, GET_REQUEST);
    }

    /**
     * Constructor of the class without headers, parameters, interface connection, and request type
     *
     * @param currentRequestURL {@link String} base URL to be requested from
     */
    public APIRequest(String currentRequestURL) {
        this(currentRequestURL, null, null);
    }

    /**
     * Sets the callback interface
     *
     * @param listener {@link OnAPIRequestListener} interface of the API request
     */
    public void setOnAPIRequestFinishedListener(OnAPIRequestListener listener) {
        this.currentAPIRequestListener = listener;
    }

    /**
     * Checks if a callback interface is present
     */
    public boolean hasAPIRequestFinishedListeners() {
        return currentAPIRequestListener != null;
    }

    /**
     * Sets the connection timeout of the request
     *
     * @param timeoutInSecs int timeout in seconds
     */
    public void setRequestTimeoutInSecs(int timeoutInSecs) {
        this.currentRequestTimeout = timeoutInSecs;
    }

    /**
     * Sets the connection timeout of the request with corresponding time unit
     *
     * @param timeout  int timeout value
     * @param timeUnit {@link TimeUnit} of the request
     */
    public void setRequestTimeout(int timeout, TimeUnit timeUnit) {
        this.currentRequestTimeout = timeout;
        this.currentRequestTimeoutUnit = timeUnit;
    }

    /**
     * Sets a new base url
     *
     * @param url {@link String} new base url
     */
    public void setURL(String url) {
        this.currentRequestURL = url;
    }

    /**
     * Sets new headers of the request
     *
     * @param headers ArrayList<Pair<String, String>> new request headers
     */
    public void setHeaders(ArrayList<Header> headers) {
        this.currentRequestHeaders = headers;
    }

    /**
     * Sets new request body parameters
     *
     * @param parameters ArrayList<Pair<String, String>> new body parameters
     */
    public void setParameters(ArrayList<Parameter> parameters) {
        this.currentRequestParameters = parameters;
    }

    /**
     * Sets a new request type
     *
     * @param currentRequestType constant int request type, see {@link #GET_REQUEST}
     *                           , {@link #POST_REQUEST}, {@link #PUT_REQUEST}, {@link #PATCH_REQUEST} and
     *                           {@link #DELETE_REQUEST}
     */
    public void setCurrentRequestType(@RequestType String currentRequestType) {
        this.currentRequestType = currentRequestType;
    }

    /**
     * Sets a custom OkHttp3 client builder for user override
     *
     * @param clientBuilder {@link OkHttpClient.Builder} builder of the client
     */
    public void setCustomClientBuilder(OkHttpClient.Builder clientBuilder) {
        this.currentClientBuilder = clientBuilder;
    }

    /**
     * Gets the OkHttpClient Builder of the library, see {@link okhttp3.OkHttpClient.Builder}
     *
     * @return {@link #currentClientBuilder}
     */
    public OkHttpClient.Builder getCurrentClientBuilder() {
        if (!clientHasBuilt) {
            throw new RuntimeException("Builder is null, you must build the APIRequest first.");
        }
        return currentClientBuilder;
    }

    /**
     * Gets the OkHttpClient Request of the library, see {@link okhttp3.OkHttpClient.Builder}
     *
     * @return {@link #currentRequest}
     */
    public Request getCurrentRequest() {
        if (!clientHasBuilt) {
            throw new RuntimeException("Request is null, you must build the APIRequest first.");
        }
        return currentRequest;
    }

    /**
     * Gets the OkHttpClient of the library, see {@link OkHttpClient}
     *
     * @return {@link #currentClient}
     */
    public OkHttpClient getCurrentClient() {
        if (!clientHasBuilt) {
            throw new RuntimeException("Client is null, you must build the APIRequest first.");
        }
        return currentClient;
    }

    /**
     * Rebuilds the whole client, this is to prevent multiple and confusing calls for {@link #build()}
     */
    public void rebuild() {
        clientHasBuilt = false;
        build();
    }

    /**
     * Preemptively builds the client, enabling getters {@link #getCurrentClient()},
     * {@link #getCurrentClientBuilder()} and {@link #getCurrentRequest()}.
     * Must call {@link #rebuild()} to build again.
     */
    public void build() {
        if (clientHasBuilt) {
            throw new RuntimeException(currentClient.toString()
                    + " has already been built, do you mean to call rebuild() ?");
        }

        if (currentHandler == null) {
            currentHandler = new Handler(Looper.getMainLooper());
        }

        // Checks if request is null before building
        checkRequestIfNull();

        // Builds the URL to be requested
        HttpUrl.Builder urlBuilder;
        HttpUrl httpUrl = HttpUrl.parse(currentRequestURL);

        // Checks if the URL is valid, if not, will trigger onRequestFailure
        if (httpUrl != null) {
            urlBuilder = httpUrl.newBuilder();
        } else {
            Log.e(TAG + " => executeRequest", ERROR_MALFORMED_URL_MESSAGE);
            currentHandler.post(new Runnable() {
                @Override
                public void run() {
                    currentAPIRequestListener.onRequestFailure(ERROR_MALFORMED_URL_MESSAGE, ERROR_MALFORMED_URL);
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

        if (currentRequestParameters.size() > 0) {
            // Adds URL parameters if the request type is GET
            if (GET_REQUEST.equals(currentRequestType) || HEAD_REQUEST.equals(currentRequestType)) {
                for (Parameter parameter : currentRequestParameters) {
                    urlBuilder.addQueryParameter(parameter.getParameter_key(), parameter.getParameter_value());
                }
            }

            // Adds the form body parameters if the request type is POST
            else if (POST_REQUEST.equals(currentRequestType)) {
                // If the parameter is a single file
                if (currentRequestParameters.size() == 1 && currentRequestParameters.get(0).isFileParameter()) {
                    for (Parameter parameter : currentRequestParameters) {
                        if (parameter.isFileValue()) {
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
                    for (Parameter parameter : currentRequestParameters) {
                        if (parameter.isFileParameter()) {
                            hasFile = true;
                            break;
                        }
                    }
                    // If parameters has file, initializes MultipartBody
                    if (hasFile) {
                        MultipartBody.Builder builder = new MultipartBody.Builder();
                        for (Parameter parameter : currentRequestParameters) {
                            // If parameter is a file, will upload a separate request body
                            if (parameter.isFileParameter()) {
                                RequestBody requestBody;
                                if (parameter.isFileValue()) {
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
                        for (Parameter parameter : currentRequestParameters) {
                            builder.add(parameter.getParameter_key(), parameter.getParameter_value());
                        }
                        body = builder.build();
                    }
                }
            }
        }

        // Adds the request headers
        Request.Builder requestBuilder = new Request.Builder();
        for (Header header : currentRequestHeaders) {
            requestBuilder.addHeader(header.getHeader_key(), header.getHeader_value());
        }

        // Specifies the request type of the OkHttpClient
        try {
            switch (currentRequestType) {
                case GET_REQUEST:
                    requestBuilder.get();
                    break;
                case HEAD_REQUEST:
                    requestBuilder.head();
                    break;
                case POST_REQUEST:
                    requestBuilder.post(body);
                    break;
                case PUT_REQUEST:
                    requestBuilder.put(body);
                    break;
                case PATCH_REQUEST:
                    requestBuilder.patch(body);
                    break;
                case DELETE_REQUEST:
                    requestBuilder.delete(body);
                    break;
                default:
                    requestBuilder.method(currentRequestType, body);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            currentAPIRequestListener.onRequestFailure(ERROR_INVALID_REQUEST_TYPE_MESSAGE, ERROR_INVALID_REQUEST_TYPE);
        }


        // Prints out url in logs if in debug mode
        String url = urlBuilder.build().toString();
        Log.d(TAG + " => executeRequest", url);

        currentRequest = requestBuilder.url(url).build();

        // TODO: add credentials builder
        currentClientBuilder.connectTimeout(currentRequestTimeout, currentRequestTimeoutUnit);
        /*if (false) {
            currentClientBuilder.authenticator(new Authenticator() {
                @Override
                public Request authenticate(@Nullable Route route, @NonNull Response response) {
                    String credential = Credentials.basic("scott", "tiger");
                    return response.request().newBuilder().header("Authorization", credential).build();
                }
            });

        }*/
        currentClient = currentClientBuilder.build();

        clientHasBuilt = true;
    }

    /**
     * Executes the API request then informs the result to the activity via the interface,
     * see {@link OnAPIRequestListener}
     */
    public void executeRequest(Context context) {
        if (!clientHasBuilt) {
            build();
        }

        if (currentHandler == null) {
            currentHandler = new Handler(Looper.getMainLooper());
        }

        // Initiates the pre request currentAPIRequestListener method for loading screens, progress bars, etc.
        currentAPIRequestListener.onPreRequest();

        // Checks if device has internet connection, else, throws to onRequestFailure
        if (InternetConnection.hasInternet(context)) {
            currentClient.newCall(currentRequest).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    final Call outCall = call;
                    e.printStackTrace();

                    if (e instanceof UnknownHostException) {
                        currentHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                currentAPIRequestListener.onResponseFailure(
                                        ERROR_COULD_NOT_RESOLVE_HOST_MESSAGE,
                                        ERROR_COULD_NOT_RESOLVE_HOST,
                                        outCall);
                            }
                        });
                    } else if (e instanceof SocketTimeoutException) {
                        currentHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                currentAPIRequestListener.onResponseFailure(
                                        ERROR_REQUEST_TIMEOUT_MESSAGE,
                                        ERROR_REQUEST_TIMEOUT,
                                        outCall);
                            }
                        });
                    } else {
                        currentHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                currentAPIRequestListener.onResponseFailure(
                                        ERROR_ON_RESPONSE_MESSAGE,
                                        ERROR_ON_RESPONSE,
                                        outCall);
                            }
                        });
                    }
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
                    currentHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            currentAPIRequestListener.onResponseSuccess(responseString, requestCode);
                        }
                    });
                }
            });
        } else {
            Log.e(TAG + " => client onFailure", ERROR_NO_CONNECTION_MESSAGE);
            currentAPIRequestListener.onRequestFailure(ERROR_NO_CONNECTION_MESSAGE, ERROR_NO_CONNECTION);
        }
    }

    /**
     * Checks the request variables if it is null then initializes it with empty or default value
     */
    private void checkRequestIfNull() {
        if (currentRequestHeaders == null) currentRequestHeaders = new ArrayList<>();
        if (currentRequestParameters == null) currentRequestParameters = new ArrayList<>();
        if (currentRequestURL == null) currentRequestURL = "";
        if (currentClientBuilder == null) currentClientBuilder = new OkHttpClient.Builder();
    }

    /**
     * Interface in communicating with the Activity, this is to prohibit any context and UI interactions within the class
     */
    public interface OnAPIRequestListener {

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
        void onResponseFailure(String error, @ErrorCode int errorCode, Call call);
    }

    // API Helper class request types
    /**
     * Get request type
     */
    public static final String GET_REQUEST = "GET";

    /**
     * Head request type
     */
    public static final String HEAD_REQUEST = "HEAD";

    /**
     * Post request type
     */
    public static final String POST_REQUEST = "POST";

    /**
     * Delete request type
     */
    public static final String DELETE_REQUEST = "DELETE";

    /**
     * Put request type
     */
    public static final String PUT_REQUEST = "PUT";

    /**
     * Patch request type
     */
    public static final String PATCH_REQUEST = "PATCH";

    // API Helper Class error codes
    /**
     * Generic client error code
     */
    public static final int ERROR_ON_REQUEST = 600;

    /**
     * Generic client error message
     */
    private static final String ERROR_ON_REQUEST_MESSAGE = "Cannot execute request";

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
     * Error code if {@link #currentRequestType} is invalid
     */
    private static final int ERROR_INVALID_REQUEST_TYPE = 603;

    /**
     * Generic client error code
     */
    public static final int ERROR_ON_RESPONSE = 700;

    /**
     * Error message if {@link #currentRequestType} is invalid
     */
    private static final String ERROR_ON_RESPONSE_MESSAGE = "Error connecting to server";

    /**
     * Error message if {@link #currentRequestType} is invalid
     */
    private static final String ERROR_INVALID_REQUEST_TYPE_MESSAGE = "Request type is not supported";

    /**
     * Error message if server does not respond within {@link #currentRequestTimeout} in {@link #currentRequestTimeoutUnit}
     */
    private static final int ERROR_REQUEST_TIMEOUT = 701;

    /**
     * Error message if server does not respond within {@link #currentRequestTimeout} in {@link #currentRequestTimeoutUnit}
     */
    private static final String ERROR_REQUEST_TIMEOUT_MESSAGE = "Connection timeout, could not get response from server";

    /**
     * Error message if server or host cannot be found
     */
    private static final int ERROR_COULD_NOT_RESOLVE_HOST = 702;

    /**
     * Error message if server or host cannot be found
     */
    private static final String ERROR_COULD_NOT_RESOLVE_HOST_MESSAGE = "Could not resolve host";

    @StringDef({
            GET_REQUEST,
            HEAD_REQUEST,
            POST_REQUEST,
            DELETE_REQUEST,
            PUT_REQUEST,
            PATCH_REQUEST,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequestType {
    }

    @IntDef({
            ERROR_ON_REQUEST,
            ERROR_MALFORMED_URL,
            ERROR_NO_CONNECTION,
            ERROR_INVALID_REQUEST_TYPE,
            ERROR_ON_RESPONSE,
            ERROR_REQUEST_TIMEOUT,
            ERROR_COULD_NOT_RESOLVE_HOST,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorCode {
    }

    @StringDef({
            ERROR_ON_REQUEST_MESSAGE,
            ERROR_MALFORMED_URL_MESSAGE,
            ERROR_NO_CONNECTION_MESSAGE,
            ERROR_INVALID_REQUEST_TYPE_MESSAGE,
            ERROR_ON_RESPONSE_MESSAGE,
            ERROR_REQUEST_TIMEOUT_MESSAGE,
            ERROR_COULD_NOT_RESOLVE_HOST_MESSAGE,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorMessage {
    }
}
