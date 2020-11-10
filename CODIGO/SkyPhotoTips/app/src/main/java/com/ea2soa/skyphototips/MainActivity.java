package com.ea2soa.skyphototips;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ea2soa.skyphototips.dto.DailyWeather;
import com.ea2soa.skyphototips.dto.ResponseGetWeather;
import com.ea2soa.skyphototips.dto.TimeAndWeatherManager;
import com.ea2soa.skyphototips.dto.TokenManager;
import com.ea2soa.skyphototips.services.ServiceOpenWeather;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressLint("SourceLockedOrientationActivity")
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private SharedPreferences sharedPref;
    private TokenManager tokenManager;
    private TimeAndWeatherManager tmManager;


    private ImageView imgFrontNavArrow;
    private Button buttonCheckSensores;

    private float[] mGravity;
    private float[] mGeomagnetic;
    private float rotation[] = new float[9];
    private float i[] = new float[9];
    private float orientation[] = new float[3];
    private float azimut;

    private static final Double limitAcelerometerLowXY = -4.00;
    private static final Double limitAcelerometerHighXY = 4.00;
    private static final Double limitAcelerometerLowZ = 6.00;
    private static final Double limitAcelerometerHighZ = 10.00;

    private Boolean correctPosition;
    private ImageView imgBadPosition;
    private ImageView imgOkPosition;

    private TextView editTextTimezone;
    private TextView editTextCondicion;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("LOG_MAIN:", "Ejecuto onCreate");

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        tokenManager = new TokenManager(sharedPref);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        imgFrontNavArrow = (ImageView) findViewById(R.id.imgFrontNavArrow);
        imgFrontNavArrow.setVisibility(View.VISIBLE);

        Button buttonCheckSensores = (Button) findViewById(R.id.buttonCheckSensores);

        correctPosition = false;
        imgBadPosition = (ImageView) findViewById(R.id.imgBadPosition);
        imgOkPosition = (ImageView) findViewById(R.id.imgOkPosition);
        imgOkPosition.setVisibility(View.INVISIBLE);

        editTextTimezone = (TextView) findViewById((R.id.editTextTimezone));
        editTextCondicion = (TextView) findViewById((R.id.editTextCondicion));

        buttonCheckSensores.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("LOG_MAIN","Go to check sensors");
                Intent checkSensorsIntent=new Intent(MainActivity.this, SensorsCheckActivity.class);
                checkSensorsIntent.putExtra("from","main");
                startActivity(checkSensorsIntent);
                finish();
            }
        });
    }

    public void saveWeather(String weather) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_last_weather), weather);
        editor.apply();
    }

    public String readWeather() {
        return sharedPref.getString(getString(R.string.saved_last_weather), "Sin Datos");
    }

    public void informBadWeather() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alerta de Clima");
        builder.setMessage("Debido a las condiciones climaticas no te recomendamos sacar fotos hoy :(");
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public void analizeWeather() {

        Log.i("LOG_MAIN","Analizando clima");

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.openweather_service))
                .build();

        ServiceOpenWeather openWeatherService = retrofit.create(ServiceOpenWeather.class);

        Call<ResponseGetWeather> call = openWeatherService.getWeatherForecast(
                getString(R.string.openweather_lat),
                getString(R.string.openweather_lon),
                getString(R.string.openweather_exclude),
                getString(R.string.openweather_units),
                getString(R.string.openweather_appid));
        call.enqueue(new Callback<ResponseGetWeather>() {
            @Override
            public void onResponse(Call<ResponseGetWeather> call, Response<ResponseGetWeather> response) {
                if(response.isSuccessful()){

                    tokenManager.executeRegisterEvent(getString(R.string.enviroment), "service_call", "Se ha llamado al service de pronostico climatico exitosamente.");

                    showLocation(response.body().getTimezone());
                    DailyWeather dailyWeatherToday = response.body().getDaily()[0];

                    Log.i("LOG_MAIN","Weather - dailyWeatherToday: " + dailyWeatherToday.toString());

                    if(dailyWeatherToday.getWeather()[0].getId() == 800) {
                        saveWeather("Despejado");
                    }
                    else {
                        if(dailyWeatherToday.getWeather()[0].getId() == 801 || dailyWeatherToday.getClouds() < 20) {
                            saveWeather("Pocas Nubes");
                        }
                        else {
                            saveWeather("Muchas nubes y posible lluvia");
                        }
                    }
                }
                else {
                    saveWeather("Error");
                    Log.e("LOG_MAIN",response.errorBody().toString());
                    Toast.makeText(getApplicationContext(),"Error al analizar el clima",Toast.LENGTH_LONG).show();
                    Log.e("LOG_MAIN","Error de datos en analizeWeather()");
                }

                Log.i("LOG_MAIN","Fin analisis clima");
            }

            @Override
            public void onFailure(Call<ResponseGetWeather> call, Throwable t) {
                Log.e("LOG_LOGIN",t.getMessage());
                Toast.makeText(getApplicationContext(),"Error al analizar el clima",Toast.LENGTH_LONG).show();
                Log.e("LOG_LOGIN","Error al analizar el clima");
            }
        });
    }

    public void showLocation(String location){
        editTextTimezone.setText(location);
    }

    public void showCondition(String message) {
        if(message.equals("Ok")) {
            String condition = readWeather();
            Log.e("LOG_LOGIN","Show condition = " + condition);
            editTextCondicion.setText(condition);
            if(condition.equals("Muchas nubes y posible lluvia")) {
                informBadWeather();
            }
            tokenManager.executeRegisterEvent(getString(R.string.enviroment), "background", "Se ejecuto el analisis de clima en background.");
        }
        else {
            editTextCondicion.setText(message);
            Toast.makeText(getApplicationContext(),"Error en los datos del clima",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this)
        {
            switch(event.sensor.getType())
            {
                case Sensor.TYPE_ACCELEROMETER :

                    mGravity = event.values;

                    if((event.values[0] > limitAcelerometerLowXY) && (event.values[0] < limitAcelerometerHighXY) &&
                            (event.values[1] > limitAcelerometerLowXY) && (event.values[1] < limitAcelerometerHighXY) &&
                            (event.values[2] > limitAcelerometerLowZ) && (event.values[2] < limitAcelerometerHighZ)) {

                        correctPosition = true;
                        imgBadPosition.setVisibility(View.INVISIBLE);
                        imgOkPosition.setVisibility(View.VISIBLE);
                    }
                    else {
                        correctPosition = false;
                        imgBadPosition.setVisibility(View.VISIBLE);
                        imgOkPosition.setVisibility(View.INVISIBLE);
                    }

                    if (mGravity != null && mGeomagnetic != null && correctPosition == true) {

                        if (SensorManager.getRotationMatrix(rotation, i, mGravity, mGeomagnetic)) {

                            // orientation contains azimut, pitch and roll
                            SensorManager.getOrientation(rotation, orientation);

                            azimut = orientation[0];

                            imgFrontNavArrow.setRotation((float) (-azimut*180/3.14159)-90);
                        }
                    }

                    break;


                case Sensor.TYPE_MAGNETIC_FIELD :

                    mGeomagnetic = event.values;

                    if (mGravity != null && mGeomagnetic != null && correctPosition == true) {

                        if (SensorManager.getRotationMatrix(rotation, i, mGravity, mGeomagnetic)) {

                            SensorManager.getOrientation(rotation, orientation);

                            azimut = orientation[0];

                            imgFrontNavArrow.setRotation((float) (-azimut*180/3.14159)-90);
                        }
                    }
                    break;
            }
        }

    }

    protected void Ini_Sensores()
    {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void Parar_Sensores()
    {
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
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



    @Override
    protected void onStart() {
        Log.i("LOG_MAIN:", "Ejecuto OnStart");
        super.onStart();

        if(internetConection())
            new TimeAndWeatherManager(sharedPref, this).execute();
        else
            Toast.makeText(getApplicationContext(),"Se necesita conexion a internet...",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        Log.i("LOG_MAIN:", "Ejecuto OnResume");
        super.onResume();
        Ini_Sensores();
    }

    @Override
    protected void onPause() {
        Log.i("LOG_MAIN:", "Ejecuto OnPause");
        Parar_Sensores();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("LOG_MAIN:", "Ejecuto OnStop");
        Parar_Sensores();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("LOG_MAIN:", "Ejecuto OnDestroy");
        Parar_Sensores();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.i("LOG_MAIN:", "Ejecuto OnRestart");
        Ini_Sensores();
        super.onRestart();
    }


}
