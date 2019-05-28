package com.crowdin.platform.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

internal class Connectivity private constructor() {

    companion object {

        fun isOnline(context: Context): Boolean {
            val info = getNetworkInfo(context)
            return info != null && info.isConnected
        }

        fun isNetworkAllowed(context: Context, networkType: NetworkType): Boolean {
            val currentNetworkType = getCurrentNetworkType(context)
            when {
                networkType == NetworkType.ALL &&
                        (currentNetworkType == NetworkType.WIFI ||
                                currentNetworkType == NetworkType.CELLULAR) -> {
                    return true
                }
                networkType == NetworkType.WIFI && currentNetworkType == NetworkType.WIFI -> {
                    return true
                }
                networkType == NetworkType.CELLULAR && currentNetworkType == NetworkType.CELLULAR -> {
                    return true
                }
            }

            return false
        }

        private fun getNetworkInfo(context: Context): NetworkInfo? {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo
        }

        private fun getCurrentNetworkType(context: Context): NetworkType {
            val info = getNetworkInfo(context)

            var type = NetworkType.UNKNOWN
            if (info != null && info.isConnected) {
                type = NetworkType.fromConnectivityType(info.type)
            }

            return type
        }
    }
}

enum class NetworkType {
    UNKNOWN,
    ALL,
    CELLULAR,
    WIFI;

    companion object {

        fun fromConnectivityType(connectivityType: Int): NetworkType {
            when (connectivityType) {
                ConnectivityManager.TYPE_ETHERNET, ConnectivityManager.TYPE_WIFI -> return WIFI
                ConnectivityManager.TYPE_MOBILE -> return CELLULAR
            }
            return UNKNOWN
        }
    }
}
