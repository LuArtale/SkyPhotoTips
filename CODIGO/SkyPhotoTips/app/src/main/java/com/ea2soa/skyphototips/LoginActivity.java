package com.ea2soa.skyphototips;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ea2soa.skyphototips.dto.Event;
import com.ea2soa.skyphototips.dto.RequestLogin;
import com.ea2soa.skyphototips.dto.RequestRegisterEvent;
import com.ea2soa.skyphototips.dto.ResponseLogin;
import com.ea2soa.skyphototips.dto.ResponseRegisterEvent;
import com.ea2soa.skyphototips.dto.TokenManager;
import com.ea2soa.skyphototips.services.ServiceSoa;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends Activity {

    private SharedPreferences sharedPref;
    private TokenManager tokenManager;

    private String token;
    private String tokenRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        tokenManager = new TokenManager(sharedPref);

        Intent loginIntent=getIntent();
        Bundle extras=loginIntent.getExtras();

        executeLogin(extras);
    }

    public void goBack() {
        Intent goBackIntent;
        goBackIntent=new Intent(LoginActivity.this, InitialActivity.class);
        startActivity(goBackIntent);
        finish();
    }

    @Override
    protected void onPause() {
        Log.i("LOG_LOGIN","Ejecuto onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("LOG_LOGIN","Ejecuto onStop");
        super.onStop();
    }


    public void executeLogin(Bundle extras) {

        Log.i("LOG_LOGIN","Starting Login");

        //LLAMAR A API PARA LOGUEO
        RequestLogin requestLogin = new RequestLogin();
        requestLogin.setEmail(extras.getString("user"));
        requestLogin.setPassword(extras.getString("pass"));

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.retrofit_service))
                .build();

        ServiceSoa serviceSoa = retrofit.create(ServiceSoa.class);

        Call<ResponseLogin> call = serviceSoa.respLogin(requestLogin);
        call.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {

                if(response.isSuccessful()){
                    token = response.body().getToken();
                    tokenManager.saveToken(response.body().getToken());
                    tokenRefresh = response.body().getToken_refresh();
                    tokenManager.saveTokenRefresh(response.body().getToken_refresh());

                    Log.i("LOG_LOGIN","Token: " + token);
                    Log.i("LOG_LOGIN","Token_Refresh: " + tokenRefresh);

                    Intent continueIntent;
                    continueIntent=new Intent(LoginActivity.this, SensorsCheckActivity.class);
                    continueIntent.putExtra("user",requestLogin.getEmail());
                    continueIntent.putExtra("pass",requestLogin.getPassword());
                    continueIntent.putExtra("tokenRefresh",tokenRefresh);
                    continueIntent.putExtra("from", "login");

                    //REGISTRAR EVENTO
                    tokenManager.executeRegisterEvent(getString(R.string.enviroment), "login", "El usuario " + requestLogin.getEmail() + " inicio sesion.");

                    Toast.makeText(getApplicationContext(),"Bienvenido!",Toast.LENGTH_LONG).show();

                    startActivity(continueIntent);

                    finish();
                }
                else {
                    Log.e("LOG_LOGIN",response.errorBody().toString());

                    Toast.makeText(getApplicationContext(),"Datos Invalidos",Toast.LENGTH_LONG).show();

                    goBack();
                }

                Log.i("LOG_LOGIN","Fin Mensaje");
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Log.e("LOG_LOGIN",t.getMessage());
                Toast.makeText(getApplicationContext(),"Error al iniciar sesion",Toast.LENGTH_LONG).show();

                goBack();
            }
        });

    }


}
