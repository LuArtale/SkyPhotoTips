package com.ea2soa.skyphototips.services;

import com.ea2soa.skyphototips.dto.RequestLogin;
import com.ea2soa.skyphototips.dto.RequestRegisterEvent;
import com.ea2soa.skyphototips.dto.RequestRegisterUser;
import com.ea2soa.skyphototips.dto.ResponseLogin;
import com.ea2soa.skyphototips.dto.ResponseRefreshToken;
import com.ea2soa.skyphototips.dto.ResponseRegisterEvent;
import com.ea2soa.skyphototips.dto.ResponseRegisterUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ServiceSoa {

    @POST("api/register")
    Call<ResponseRegisterUser> respRegisterUser(@Body RequestRegisterUser reqRegisterUser);

    @POST("api/login")
    Call<ResponseLogin> respLogin(@Body RequestLogin reqLogin);

    @PUT("api/refresh")
    Call<ResponseRefreshToken> respRefreshToken(@Header("Authorization") String token);

    @POST("api/event")
    Call<ResponseRegisterEvent> respRegisterEvent(@Header("Authorization") String token, @Body RequestRegisterEvent reqRegisterEvent);

}
