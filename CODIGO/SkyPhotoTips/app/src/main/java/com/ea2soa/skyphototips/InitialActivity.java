package com.ea2soa.skyphototips;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    private EditText textBattery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        Log.i("LOG_INITIAL:", "Ejecuto onCreate");

        inputTextUser=(EditText)findViewById(R.id.inputTextUser);
        inputTextPass=(EditText)findViewById(R.id.inputTextPass);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        textBattery = (EditText)findViewById(R.id.textBattery);

        buttonLogin.setOnClickListener(botonesListeners);
        buttonRegister.setOnClickListener(botonesListeners);

        this.registerReceiver(this.mBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            textBattery.setText(String.valueOf(level) + "%");
        }
    };

    @Override
    protected void onStart() {
        Log.i("LOG_INITIAL:", "Ejecuto OnStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i("LOG_INITIAL:", "Ejecuto OnResume");
        super.onResume();

        if(!internetConection())
            Toast.makeText(getApplicationContext(),"Se necesita conexion a internet...",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        Log.i("LOG_INITIAL:", "Ejecuto OnPause");
        inputTextPass.setText("");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("LOG_INITIAL:", "Ejecuto OnStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("LOG_INITIAL:", "Ejecuto OnDestroy");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.i("LOG_INITIAL:", "Ejecuto OnRestart");
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

                        if(!checkFields()){
                            Toast.makeText(getApplicationContext(), "Completar todos los campos", Toast.LENGTH_LONG).show();
                            return;
                        }

                        intent=new Intent(InitialActivity.this,LoginActivity.class);
                        intent.putExtra("user",inputTextUser.getText().toString());
                        intent.putExtra("pass",inputTextPass.getText().toString());

                        Log.i("LOG_INITIAL:", "Pressed Login");

                        startActivity(intent);
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Se necesita conexion a internet...",Toast.LENGTH_LONG).show();

                    break;

                case R.id.buttonRegister:
                    if(internetConection()) {
                        intent=new Intent(InitialActivity.this,RegisterActivity.class);

                        Log.i("LOG_INITIAL:", "Pressed Register");

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

    public boolean checkFields() {
        if(!inputTextUser.getText().toString().equals("") && !inputTextPass.getText().toString().equals("")){
            return true;
        }
        return false;
    }

}
