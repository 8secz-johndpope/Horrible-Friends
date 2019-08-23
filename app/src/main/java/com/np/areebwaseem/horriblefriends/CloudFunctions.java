package com.np.areebwaseem.horriblefriends;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by areebwaseem on 1/3/18.
 */


public interface CloudFunctions {

    @GET("getCustomToken")
    Call<ResponseBody> getCustomToken(@Query("access_token") String accessToken);
}