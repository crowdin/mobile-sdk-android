package com.crowdin.platform.api;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 *
 */
public interface CrowdinApi {

    @POST("/api/auth")
    ResponseBody authorization(@Body AuthRequestBody body);

    @GET("")
    ResponseBody getValue();

}
