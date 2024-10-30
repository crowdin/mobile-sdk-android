package com.crowdin.platform.data.remote

import android.util.Log
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.DataManager.Companion.DISTRIBUTION_DATA
import com.crowdin.platform.data.DistributionInfoCallback
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class DistributionInfoManager(
    private val crowdinApi: CrowdinApi,
    private val dataManager: DataManager,
    private val distributionHash: String,
) {
    fun getDistributionInfo(callback: DistributionInfoCallback) {
        crowdinApi
            .getInfo(distributionHash)
            .enqueue(
                object : Callback<DistributionInfoResponse> {
                    override fun onResponse(
                        call: Call<DistributionInfoResponse>,
                        response: Response<DistributionInfoResponse>,
                    ) {
                        val distributionInfo = response.body()
                        if (distributionInfo == null) {
                            Log.w(
                                DistributionInfoManager::class.java.simpleName,
                                "Distribution info not loaded. Response code: ${response.code()}",
                            )
                        } else {
                            dataManager.saveData(DISTRIBUTION_DATA, distributionInfo.data)
                        }

                        callback.onResponse()
                    }

                    override fun onFailure(
                        call: Call<DistributionInfoResponse>,
                        throwable: Throwable,
                    ) {
                        callback.onError(throwable)
                    }
                },
            )
    }
}
