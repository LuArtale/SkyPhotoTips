package com.ea2soa.skyphototips.dto;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ea2soa.skyphototips.MainActivity;
import com.ea2soa.skyphototips.R;
import com.ea2soa.skyphototips.services.ServiceSoa;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenManager {

    SharedPreferences sharedPref;
    private static final String TOKEN = "saved_token";
    private static final String TOKEN_REFRESH = "saved_token_refresh";
    private static final String SOA_API_URL = "http://so-unlam.net.ar/api/";




    public TokenManager(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }



    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public String readToken() {
        return sharedPref.getString(TOKEN, "Sin Datos");
    }


    public void saveTokenRefresh(String tokenRefresh) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(TOKEN_REFRESH, tokenRefresh);
        editor.apply();
    }

    public String readTokenRefresh() {
        return sharedPref.getString(TOKEN_REFRESH, "Sin Datos");
    }


    public void executeRegisterEvent(String env, String type_events, String desc) {

        Log.i("LOG_TOKEN_MANAGER","Registering " + type_events);
        Log.i("LOG_TOKEN_MANAGER","Token: " + readToken());
        Log.i("LOG_TOKEN_MANAGER","Token_Refresh: " + readTokenRefresh());

        RequestRegisterEvent requestRegisterEvent = new RequestRegisterEvent();
        requestRegisterEvent.setEnv(env);
        requestRegisterEvent.setType_events(type_events);
        requestRegisterEvent.setDescription(desc);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SOA_API_URL)
                .build();

        ServiceSoa serviceSoa = retrofit.create(ServiceSoa.class);

        Call<ResponseRegisterEvent> call = serviceSoa.respRegisterEvent("Bearer " + readToken(), requestRegisterEvent);
        if(readToken().equals("Sin Datos"))
            Log.e("LOG_TOKEN_MANAGER","Register Event: NO SE ENCONTRARON DATOS DEL TOKEN");
        call.enqueue(new Callback<ResponseRegisterEvent>() {
            @Override
            public void onResponse(Call<ResponseRegisterEvent> call, Response<ResponseRegisterEvent> response) {

                if(response.isSuccessful()){
                    String envResp = response.body().getEnv();
                    Event eventResp = response.body().getEvent();

                    Log.i("LOG_TOKEN_MANAGER","Register Event - enviroment: " + envResp);
                    Log.i("LOG_TOKEN_MANAGER","Register Event - event: " + eventResp.toString());
                }
                else {
                    Log.e("LOG_TOKEN_MANAGER",response.errorBody().toString());
                    Log.e("LOG_TOKEN_MANAGER","Error de datos en Registro " + type_events + ", con http response code = " + response.code());
                }
                Log.i("LOG_TOKEN_MANAGER","Fin Mensaje");
            }

            @Override
            public void onFailure(Call<ResponseRegisterEvent> call, Throwable t) {
                Log.e("LOG_TOKEN_MANAGER",t.getMessage());
                Log.e("LOG_TOKEN_MANAGER","Error al registrar el evento " + type_events);
            }
        });

    }
}
