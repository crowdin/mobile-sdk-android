package com.crowdin.platform

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri

class CrowdinInitProvider : ContentProvider() {

    override fun attachInfo(context: Context, info: ProviderInfo) {
        checkContentProviderAuthority(info)
        super.attachInfo(context, info)
    }

    override fun onCreate(): Boolean {
        val context = this.context
        if (context != null) {
            Crowdin.init(context)
        }
        return false
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    companion object {

        private const val EMPTY_APPLICATION_ID_PROVIDER_AUTHORITY = "com.crowdin.platform.crowdininitprovider"

        private fun checkContentProviderAuthority(info: ProviderInfo) {
            checkNotNull(info, "CrowdinInitProvider ProviderInfo cannot be null.")
            if (EMPTY_APPLICATION_ID_PROVIDER_AUTHORITY == info.authority) {
                throw IllegalStateException("Incorrect provider authority in manifest. Most likely due to a missing applicationId variable in application's build.gradle.")
            }
        }

        private fun <T> checkNotNull(var0: T?, var1: Any): T {
            return var0 ?: throw NullPointerException(var1.toString())
        }
    }
}
