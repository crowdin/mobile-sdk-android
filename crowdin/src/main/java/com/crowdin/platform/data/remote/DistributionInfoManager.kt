package com.crowdin.platform.data.remote

import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.DataManager.Companion.AUTH_INFO
import com.crowdin.platform.data.DataManager.Companion.DISTRIBUTION_DATA
import com.crowdin.platform.data.DistributionInfoCallback
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class DistributionInfoManager(private val crowdinApi: CrowdinApi,
                                       private val dataManager: DataManager,
                                       private val distributionHash: String) {

    fun getDistributionInfo(callback: DistributionInfoCallback) {
        val authInfo = dataManager.getData(AUTH_INFO, AuthInfo::class.java) as AuthInfo?
        authInfo ?: return
        val bearer = "Bearer ${authInfo.accessToken}"
        crowdinApi.getInfo(bearer, distributionHash)
                .enqueue(object : Callback<DistributionInfoResponse> {
                    override fun onResponse(call: Call<DistributionInfoResponse>, response: Response<DistributionInfoResponse>) {
                        val distributionInfo = response.body()
                        distributionInfo?.let {
                            dataManager.saveData(DISTRIBUTION_DATA, it.data)
                            callback.onSuccess()
                        }
                    }

                    override fun onFailure(call: Call<DistributionInfoResponse>, throwable: Throwable) {
                        callback.onError(throwable)
                    }
                })
    }
}