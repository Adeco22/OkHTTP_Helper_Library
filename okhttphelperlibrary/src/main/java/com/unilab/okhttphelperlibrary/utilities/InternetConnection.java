package com.unilab.okhttphelperlibrary.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

/**
 * Utility class for Network checking
 *
 * @author Anthony Deco
 * @since  11:25 AM 8/30/2019
 */
public final class InternetConnection {

    public static boolean hasInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isConnected();
            }
        }
        return false;
    }

    public static boolean isWiFiConnected(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            Network network = connectivity.getActiveNetwork();
            return connectivity.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        }
        return false;
    }

    public static boolean isMobileDataConnected(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            Network network = connectivity.getActiveNetwork();
            return connectivity.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        }
        return false;
    }

    public static boolean isConnectionMetered(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            return connectivity.isActiveNetworkMetered();
        }

        return false;
    }
}
