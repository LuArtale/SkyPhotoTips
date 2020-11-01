package com.ea2soa.skyphototips;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class SensorsCheckActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private TextView inputTextAcelerometro;
    private TextView inputTextGiroscopo;
    private TextView inputTextMagnetico;
    private TextView inputTextLuminosidad;

    private Button buttonContinue;

    DecimalFormat dosdecimales = new DecimalFormat("##.##");

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

        buttonContinue.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("LOG_MAIN","Continue to Main");

                Intent continueIntent;
                continueIntent=new Intent(SensorsCheckActivity.this, MainActivity.class);

                startActivity(continueIntent);
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
            Log.d("sensor", event.sensor.getName());

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



}
