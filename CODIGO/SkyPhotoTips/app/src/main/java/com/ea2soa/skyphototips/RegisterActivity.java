package com.ea2soa.skyphototips;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ea2soa.skyphototips.dto.RequestRegisterUser;
import com.ea2soa.skyphototips.dto.ResponseRegisterUser;
import com.ea2soa.skyphototips.services.ServiceSoa;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends Activity {

    private EditText inputTextName;
    private EditText inputTextLastname;
    private EditText inputTextDni;
    private EditText inputTextEmailR;
    private EditText inputTextPassR;
    private EditText inputTextCommission;

    private Button buttonRegisterR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.i("LOG_REGISTER","OnCreate");

        inputTextName = (EditText)findViewById(R.id.inputTextName);
        inputTextLastname = (EditText)findViewById(R.id.inputTextLastname);
        inputTextDni = (EditText)findViewById(R.id.inputTextDni);
        inputTextEmailR = (EditText)findViewById(R.id.inputTextEmailR);
        inputTextPassR = (EditText)findViewById(R.id.inputTextPassR);
        inputTextCommission = (EditText)findViewById(R.id.inputTextCommission);

        buttonRegisterR = (Button) findViewById(R.id.buttonRegisterR);


        buttonRegisterR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(internetConection()) {

                    Log.i("LOG_REGISTER", "Starting Register User Request");

                    if(!checkFields()){
                        Toast.makeText(getApplicationContext(), "Completar todos los campos", Toast.LENGTH_LONG).show();
                        return;
                    }

                    RequestRegisterUser requestRegisterUser = new RequestRegisterUser();
                    requestRegisterUser.setEnv(getString(R.string.enviroment)); //puede ser TEST o PROD
                    requestRegisterUser.setName(inputTextName.getText().toString());
                    requestRegisterUser.setLastname(inputTextLastname.getText().toString());
                    requestRegisterUser.setDni(Long.parseLong(inputTextDni.getText().toString()));
                    requestRegisterUser.setEmail(inputTextEmailR.getText().toString());
                    if(inputTextPassR.getText().toString().length() < 8) {
                        Toast.makeText(getApplicationContext(), "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else
                        requestRegisterUser.setPassword(inputTextPassR.getText().toString());
                    requestRegisterUser.setCommission(Long.parseLong(inputTextCommission.getText().toString()));

                    Retrofit retrofit = new Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl(getString(R.string.retrofit_service))
                            .build();

                    ServiceSoa serviceSoa = retrofit.create(ServiceSoa.class);

                    Call<ResponseRegisterUser> call = serviceSoa.respRegisterUser(requestRegisterUser);
                    call.enqueue(new Callback<ResponseRegisterUser>() {
                        @Override
                        public void onResponse(Call<ResponseRegisterUser> call, Response<ResponseRegisterUser> response) {

                            if (response.isSuccessful()) {
                                String resp = response.body().getEnv();
                                resp += " - " + response.body().getToken();
                                resp += " - " + response.body().getToken_refresh();

                                Log.i("LOG_REGISTER", "Respuesta: " + resp);

                                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                loginIntent.putExtra("user", inputTextEmailR.getText().toString());
                                loginIntent.putExtra("pass", inputTextPassR.getText().toString());

                                startActivity(loginIntent);

                                finish();
                            } else {
                                Log.e("LOG_REGISTER", response.errorBody().toString());
                                Toast.makeText(getApplicationContext(), "Datos Invalidos", Toast.LENGTH_LONG).show();
                            }

                            Log.i("LOG_REGISTER", "Fin Mensaje");
                        }

                        @Override
                        public void onFailure(Call<ResponseRegisterUser> call, Throwable t) {
                            Log.e("LOG_REGISTER", t.getMessage());
                            Toast.makeText(getApplicationContext(), "Error al registrar", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(),"Se necesita conexion a internet...",Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.i("LOG_REGISTER","Register User Finished");
    }


    private boolean internetConection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        else {
            return false;
        }
    }


    public boolean checkFields() {
        if(!inputTextName.getText().toString().equals("") &&
                !inputTextLastname.getText().toString().equals("") &&
                !inputTextDni.getText().toString().equals("") &&
                !inputTextEmailR.getText().toString().equals("") &&
                !inputTextPassR.getText().toString().equals("") &&
                !inputTextCommission.getText().toString().equals("")){

            return true;
        }
        return false;
    }

}
