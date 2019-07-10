package com.example.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GithubService {
    @GET("/v2/{user}")
    Call<GithubUserBean> getUserString(@Path("user") String user);
}
