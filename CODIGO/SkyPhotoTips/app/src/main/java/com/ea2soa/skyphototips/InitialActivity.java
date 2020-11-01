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

public class InitialActivity extends AppCompatActivity {

    private EditText inputTextUser;
    private EditText inputTextPass;
    private Button buttonLogin;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        Log.i("LOG_MAIN:", "Ejecuto onCreate");

        inputTextUser=(EditText)findViewById(R.id.inputTextUser);
        inputTextPass=(EditText)findViewById(R.id.inputTextPass);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(botonesListeners);
        buttonRegister.setOnClickListener(botonesListeners);
    }

    @Override
    protected void onStart() {
        Log.i("LOG_MAIN:", "Ejecuto OnStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i("LOG_MAIN:", "Ejecuto OnResume");
        super.onResume();

        if(!internetConection())
            Toast.makeText(getApplicationContext(),"Se necesita conexion a internet...",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        Log.i("LOG_MAIN:", "Ejecuto OnPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("LOG_MAIN:", "Ejecuto OnStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("LOG_MAIN:", "Ejecuto OnDestroy");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.i("LOG_MAIN:", "Ejecuto OnRestart");
        super.onRestart();
    }


    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {

        public void onClick(View v)
        {
            Intent intent;
            switch (v.getId())
            {
                case R.id.buttonLogin:

                    if(internetConection()) {
                        intent=new Intent(InitialActivity.this,LoginActivity.class);
                        intent.putExtra("user",inputTextUser.getText().toString());
                        intent.putExtra("pass",inputTextPass.getText().toString());

                        Log.i("LOG_MAIN:", "Pressed Login");

                        startActivity(intent);
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Se necesita conexion a internet...",Toast.LENGTH_LONG).show();

                    break;

                case R.id.buttonRegister:
                    if(internetConection()) {
                        intent=new Intent(InitialActivity.this,RegisterActivity.class);

                        Log.i("LOG_MAIN:", "Pressed Register");

                        startActivity(intent);
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Se necesita conexion a internet...",Toast.LENGTH_LONG).show();

                    break;

                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones",Toast.LENGTH_LONG).show();
            }


        }
    };

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



}
