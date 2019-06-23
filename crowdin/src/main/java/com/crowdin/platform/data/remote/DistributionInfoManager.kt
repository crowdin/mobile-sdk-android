package com.crowdin.platform.data.remote

import com.crowdin.platform.data.DistributionInfoCallback
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.DataManager.Companion.DISTRIBUTION_DATA
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class DistributionInfoManager(private val crowdinApi: CrowdinApi,
                                       private val dataManager: DataManager,
                                       private val distributionKey: String?) {

    fun getDistributionInfo(userAgent: String,
                            cookies: String,
                            xCsrfToken: String,
                            callback: DistributionInfoCallback) {
        crowdinApi.getInfo(userAgent, cookies, xCsrfToken, distributionKey)
                .enqueue(object : Callback<DistributionInfoResponse> {

                    override fun onResponse(call: Call<DistributionInfoResponse>, response: Response<DistributionInfoResponse>) {
                        val distributionInfo = response.body()
                        distributionInfo?.let {
                            if (it.success) {
                                dataManager.saveData(DISTRIBUTION_DATA, it.data)
                                callback.onSuccess()
                            }
                        }
                    }

                    override fun onFailure(call: Call<DistributionInfoResponse>, throwable: Throwable) {
                        callback.onError(throwable)
                    }
                })
    }
}