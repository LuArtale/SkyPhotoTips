package com.ea2soa.skyphototips;

import java.text.DecimalFormat;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.ea2soa.skyphototips.dto.TokenManager;


public class SensorsCheckActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private TextView inputTextAcelerometro;
    private TextView inputTextGiroscopo;
    private TextView inputTextMagnetico;
    private TextView inputTextLuminosidad;

    private Button buttonContinue;

    DecimalFormat dosdecimales = new DecimalFormat("##.##");

    private SharedPreferences sharedPref;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors_check);

        Button buttonContinue = (Button) findViewById(R.id.buttonContinue);

        inputTextAcelerometro = (TextView) findViewById(R.id.inputTextAcelerometro);
        inputTextGiroscopo = (TextView) findViewById(R.id.inputTextGiroscopo);
        inputTextMagnetico = (TextView) findViewById(R.id.inputTextMagnetico);
        inputTextLuminosidad = (TextView) findViewById(R.id.inputTextLuminosidad);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        tokenManager = new TokenManager(sharedPref);

        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        if(extras != null && extras.get("from").equals("login")){
            showLastSensorsData();
        }

        buttonContinue.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(inputTextAcelerometro.getText() != null && inputTextMagnetico.getText() != null && !extras.get("from").equals("main")) {
                    Log.i("LOG_SENSORS_CHECK","Registrando evento sensores");
                    tokenManager.executeRegisterEvent(getString(R.string.enviroment), "sensors_activity", "Los sensores ACELEROMETRO y MAGNETCO tienen actividad");
                }

                Log.i("LOG_SENSORS_CHECK","Continue to Main");

                Intent continueIntent;
                continueIntent=new Intent(SensorsCheckActivity.this, MainActivity.class);

                startActivity(continueIntent);
                finish();
            }
        });
    }

    protected void Ini_Sensores()
    {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void Parar_Sensores()
    {

        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        String txt = "";

        synchronized (this)
        {
            //Log.d("sensor", event.sensor.getName());

            switch(event.sensor.getType())
            {
                case Sensor.TYPE_ACCELEROMETER :
                    txt += "x: " + dosdecimales.format(event.values[0]) + " m/seg2 \n";
                    txt += "y: " + dosdecimales.format(event.values[1]) + " m/seg2 \n";
                    txt += "z: " + dosdecimales.format(event.values[2]) + " m/seg2 \n";
                    inputTextAcelerometro.setText(txt);

                    if ((event.values[0] > 25) || (event.values[1] > 25) || (event.values[2] > 25))
                    {
                        Log.i("LOG_SENSORS", "Vibracion Detectada");
                    }
                    break;

                case Sensor.TYPE_GYROSCOPE:
                    txt += "x: " + dosdecimales.format(event.values[0]) + " deg/s \n";
                    txt += "y: " + dosdecimales.format(event.values[1]) + " deg/s \n";
                    txt += "z: " + dosdecimales.format(event.values[2]) + " deg/s \n";
                    inputTextGiroscopo.setText(txt);
                    break;

                case Sensor.TYPE_MAGNETIC_FIELD :
                    txt += event.values[0] + " uT" + "\n";

                    inputTextMagnetico.setText(txt);
                    break;

                case Sensor.TYPE_LIGHT :
                    txt += event.values[0] + " Lux \n";

                    inputTextLuminosidad.setText(txt);
                    break;
            }
        }
    }


    @Override
    protected void onStop()
    {
        Parar_Sensores();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Parar_Sensores();
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        Parar_Sensores();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("last_data_acelerometer", inputTextAcelerometro.getText().toString());
        editor.putString("last_data_giroscope", inputTextGiroscopo.getText().toString());
        editor.putString("last_data_magnetic", inputTextMagnetico.getText().toString());
        editor.putString("last_data_light", inputTextLuminosidad.getText().toString());
        editor.apply();

        super.onPause();
    }

    @Override
    protected void onRestart()
    {
        Ini_Sensores();
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Ini_Sensores();
    }

    public void showLastSensorsData() {

        String sensorsData = "Acelerometro: \n" + sharedPref.getString("last_data_acelerometer", "Sin Datos");
        sensorsData += "\nGiroscopo: \n" + sharedPref.getString("last_data_giroscope", "Sin Datos");
        sensorsData += "\nMagnetico: \n" + sharedPref.getString("last_data_magnetic", "Sin Datos");
        sensorsData += "\nLuminosidad: \n" + sharedPref.getString("last_data_light", "Sin Datos");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Datos previos de los sensores");
        builder.setMessage(sensorsData);
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
