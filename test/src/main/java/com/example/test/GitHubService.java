package com.example.test;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;


public interface GitHubService {
    @GET("/users/{username}")
    Call<Users> getUser(@Path("username") String username);
}