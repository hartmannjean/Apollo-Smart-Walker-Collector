package com.example.service_runner;

import static android.Manifest.permission.HIGH_SAMPLING_RATE_SENSORS;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SensorActivity extends AppCompatActivity implements SensorEventListener, LocationTracker.LocationUpdateListener, LocationTracker {

    private String nome, idade, altura, peso, swtAceler, swtGyro, swtMagne, sAceler, sGyro, sMagne;;
    //declara variaveis
    private static final String TAG = "SENSORES";
    private SensorManager sensorManager;
    SensorManager multiSensor;
    Sensor sensores;
    File csvFileacc, csvFilegyr, csvFilemag, csvFilegps;
    OutputStreamWriter outputWriteracc, outputWritergyr, outputWritermag, outputWritergps;
    private boolean isRunning;
    private LocationTracker.LocationUpdateListener listener;
    private LocationClass gps, net;
    private GoogleMap mMap;
    Location lastLoc;
    long lastTime;
    TextView txtacc, txtgyr, txtmag;
    Button btnIniciar, btnFinalizar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);
        //Captura dados que vem da MainActivity
        Intent intent = this.getIntent();
        nome = intent.getStringExtra("nome");
        idade = intent.getStringExtra("idade");
        altura = intent.getStringExtra("altura");
        peso = intent.getStringExtra("peso");
        swtAceler = intent.getStringExtra("swtAceler");
        swtGyro = intent.getStringExtra("swtGyro");
        swtMagne = intent.getStringExtra("swtMagne");
        sAceler = intent.getStringExtra("sAceler");
        sGyro = intent.getStringExtra("sGyro");
        sMagne = intent.getStringExtra("sMagne");

        txtacc = (TextView) findViewById(R.id.txtAcelerometro);
        txtgyr = (TextView) findViewById(R.id.txtGyroscopio);
        txtmag = (TextView) findViewById(R.id.txtMagnetometro);
        txtacc.setEnabled(false);
        txtgyr.setEnabled(false);
        txtmag.setEnabled(false);
        btnIniciar = (Button) findViewById(R.id.buttonIniciar);
        btnFinalizar = (Button) findViewById(R.id.buttonFinalizar);


        //Requisição de permissão para usuários (Escrita, localização e internet)
        if(Permissoes.validarPermissoes(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION, HIGH_SAMPLING_RATE_SENSORS
        }, this, 101)){

        }

        //instancia GPS
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            FallbackLocationTracker(this);
        }
        btnIniciar.setEnabled(true);
        btnFinalizar.setEnabled(false);
    }
    public void iniciaSensor(View view){
        //inicializa sensores
        multiSensor = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor msensor = multiSensor.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor asensor = multiSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gsensor = multiSensor.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //registra o listener do sensor acelerometro
        if(Boolean.valueOf(swtAceler) == true){
            if(sAceler.equals("DELAY_NORMAL")){
                if(multiSensor.registerListener(this, asensor, SensorManager.SENSOR_DELAY_NORMAL)){
                    Toast.makeText(getApplicationContext(), "Acelerometer OK", Toast.LENGTH_SHORT).show();
                }
            }else if(sAceler.equals("DELAY_UI")){
                if(multiSensor.registerListener(this, asensor, SensorManager.SENSOR_DELAY_UI)){
                    Toast.makeText(getApplicationContext(), "Acelerometer OK", Toast.LENGTH_SHORT).show();
                }
            }else if(sAceler.equals("DELAY_GAME")){
                if(multiSensor.registerListener(this, asensor, SensorManager.SENSOR_DELAY_GAME)){
                    Toast.makeText(getApplicationContext(), "Acelerometer OK", Toast.LENGTH_SHORT).show();
                }
            }else if(sAceler.equals("DELAY_FASTEST")){
                if(multiSensor.registerListener(this, asensor, SensorManager.SENSOR_DELAY_FASTEST)){
                    Toast.makeText(getApplicationContext(), "Acelerometer OK", Toast.LENGTH_SHORT).show();
                }
            }
        }
        //registra o listener do sensor Gyroscopio
        if(Boolean.valueOf(swtGyro) == true){
            if(sGyro.equals("DELAY_NORMAL")){
                if(multiSensor.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_NORMAL)){
                    Toast.makeText(getApplicationContext(), "Gyroscopio OK", Toast.LENGTH_SHORT).show();
                }
            }else if(sGyro.equals("DELAY_UI")){
                if(multiSensor.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_UI)){
                    Toast.makeText(getApplicationContext(), "Gyroscopio OK", Toast.LENGTH_SHORT).show();
                }
            }else if(sGyro.equals("DELAY_GAME")){
                if(multiSensor.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_GAME)){
                    Toast.makeText(getApplicationContext(), "Gyroscopio OK", Toast.LENGTH_SHORT).show();
                }
            }else if(sGyro.equals("DELAY_FASTEST")){
                if(multiSensor.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_FASTEST)){
                    Toast.makeText(getApplicationContext(), "Gyroscopio OK", Toast.LENGTH_SHORT).show();
                }
            }
        }
        //registra o listener do sensor Magnetometro
        if(Boolean.valueOf(swtMagne) == true){
            if(sMagne.equals("DELAY_NORMAL")){
                if(multiSensor.registerListener(this, msensor, SensorManager.SENSOR_DELAY_NORMAL)){
                    Toast.makeText(getApplicationContext(), "Magnetometro OK", Toast.LENGTH_SHORT).show();
                }
            }else if(sMagne.equals("DELAY_UI")){
                if(multiSensor.registerListener(this, msensor, SensorManager.SENSOR_DELAY_UI)){
                    Toast.makeText(getApplicationContext(), "Magnetometro OK", Toast.LENGTH_SHORT).show();
                }
            }else if(sMagne.equals("DELAY_GAME")){
                if(multiSensor.registerListener(this, msensor, SensorManager.SENSOR_DELAY_GAME)){
                    Toast.makeText(getApplicationContext(), "Magnetometro OK", Toast.LENGTH_SHORT).show();
                }
            }else if(sMagne.equals("DELAY_FASTEST")){
                if(multiSensor.registerListener(this, msensor, SensorManager.SENSOR_DELAY_FASTEST)){
                    Toast.makeText(getApplicationContext(), "Magnetometro OK", Toast.LENGTH_SHORT).show();
                }
            }
        }

        //cria o arquivo que será usado no metodo onSensorChanged para escrita dos dados
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            //csvFileacc = new File (this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)  + "/acc_"+ timestamp+".csv");
            //csvFilegyr = new File (this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)   + "/gyr_"+ timestamp+".csv");
            //csvFilemag = new File (this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)   + "/mag_"+ timestamp+".csv");
            //csvFilegps = new File (this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)  + "/gps_"+ timestamp+".csv");
            File filesDir = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            assert filesDir != null;
            if (!filesDir.exists()){
                if(filesDir.mkdirs()){
                }
            }
            csvFileacc = new File(filesDir, "/acc_"+ timestamp+".csv");
            csvFilegyr = new File(filesDir, "/gyr_"+ timestamp+".csv");
            csvFilemag = new File(filesDir, "/mag_"+ timestamp+".csv");
            csvFilegps = new File(filesDir, "/gps_"+ timestamp+".csv");

            //csvFileacc = new File (this.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/acc_"+ timestamp+".csv");
            //csvFilegyr = new File (this.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/gyr_"+ timestamp+".csv");
            //csvFilemag = new File (this.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/mag_"+ timestamp+".csv");
            //csvFilegps = new File (this.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/gps_"+ timestamp+".csv");
        } else {
            csvFileacc = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/acc_"+ timestamp+".csv");
            csvFilegyr = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/gyr_"+ timestamp+".csv");
            csvFilemag = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mag_"+ timestamp+".csv");
            csvFilegps = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/gps_"+ timestamp+".csv");
        }




        //csvFile = new File(spqi + "/SPQI_"+timestamp+".csv");
        //csvFile.mkdir();
        //File csvFile = new File(csvPasta, "SPQI_"+timestamp+".csv");
        try {
            if (!csvFileacc.exists()) {
                csvFileacc.createNewFile();
            }else if (!csvFilegyr.exists()) {
                csvFilegyr.createNewFile();
            }else if (!csvFilemag.exists()) {
                csvFilemag.createNewFile();
            }else if (!csvFilegps.exists()) {
                csvFilegps.createNewFile();
            }
            Log.d(TAG, "csvFileacc: " + csvFileacc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream outputStreamacc = null;
        FileOutputStream outputStreamgyr = null;
        FileOutputStream outputStreammag = null;
        FileOutputStream outputStreamgps = null;

        try {
            outputStreamacc = new FileOutputStream(csvFileacc);
            outputStreamgyr = new FileOutputStream(csvFilegyr);
            outputStreammag = new FileOutputStream(csvFilemag);
            outputStreamgps = new FileOutputStream(csvFilegps);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        outputWriteracc = new OutputStreamWriter(outputStreamacc);
        outputWritergyr = new OutputStreamWriter(outputStreamgyr);
        outputWritermag = new OutputStreamWriter(outputStreammag);
        outputWritergps = new OutputStreamWriter(outputStreamgps);

        //inicia gps
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            this.start();
        }
        btnIniciar.setEnabled(false);
        btnFinalizar.setEnabled(true);

    }
    public void paraSensor(View view){
        try{
            multiSensor.unregisterListener(SensorActivity.this);
            multiSensor = null;
            if(multiSensor == null){
                Log.d(TAG, "onCreate: Serviços parados");
                Toast.makeText(getApplicationContext(), "Finalizado", Toast.LENGTH_SHORT).show();
            }
        }catch(NullPointerException e){
            //erro de null pointer, então não está registrado, mostrar mensagem
        }

        try {
            outputWriteracc.close();
            outputWritergyr.close();
            outputWritermag.close();
            outputWritergps.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtacc.setText("Accelerometer (Stopped)");
        txtgyr.setText("Gyroscope (Stopped)");
        txtmag.setText("Magnetometer (Stopped)");
        btnIniciar.setEnabled(true);
        btnFinalizar.setEnabled(false);

    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public synchronized void onSensorChanged(SensorEvent sensorEvent) {
        //TYPE_ACCELEROMETER = 1
        //TYPE_MAGNETIC_FIELD = 2
        //TYPE_GYROSCOPE = 3

            try {
                if (sensorEvent.sensor.getType() == sensores.TYPE_ACCELEROMETER) {
                    txtacc.setText("Accelerometer (Collecting)");
                    outputWriteracc.append(String.valueOf(sensorEvent.timestamp).concat(";")
                            .concat("1;")
                            .concat(String.valueOf(sensorEvent.values[0])).concat(";")
                            .concat(String.valueOf(sensorEvent.values[1])).concat(";")
                            .concat(String.valueOf(sensorEvent.values[2])).concat(";")
                    );
                    outputWriteracc.append("\n");
                    outputWriteracc.flush();
                    Log.d(TAG, "Accelerometer: " + (String.valueOf(sensorEvent.timestamp).concat(";")
                            .concat("1;")
                            .concat(String.valueOf(sensorEvent.values[0])).concat(";")
                            .concat(String.valueOf(sensorEvent.values[1])).concat(";")
                            .concat(String.valueOf(sensorEvent.values[2])).concat(";").concat("\n")));

                } /*else {
                        outputWriteracc.append(
                                "1;"
                                        .concat("nulo").concat(";")
                                        .concat("nulo").concat(";")
                                        .concat("nulo").concat(";")
                        );
                        outputWriteracc.append("\n");
                        outputWriteracc.flush();
                }*/
                if (sensorEvent.sensor.getType() == sensores.TYPE_MAGNETIC_FIELD) {
                    txtmag.setText("Magnetometer (Collecting)");
                    outputWritermag.append(
                            "2;"
                                    .concat(String.valueOf(sensorEvent.values[0])).concat(";")
                                    .concat(String.valueOf(sensorEvent.values[1])).concat(";")
                                    .concat(String.valueOf(sensorEvent.values[2])).concat(";")
                    );
                    outputWritermag.append("\n");
                    outputWritermag.flush();

                } /*else {
                        outputWritermag.append(
                                "2;"
                                    .concat("nulo").concat(";")
                                    .concat("nulo").concat(";")
                                    .concat("nulo").concat(";")
                        );
                        outputWritermag.append("\n");
                        outputWritermag.flush();
                    }*/
                if (sensorEvent.sensor.getType() == sensores.TYPE_GYROSCOPE) {
                    txtgyr.setText("Gyroscope (Collecting)");
                    outputWritergyr.append(
                            "3;"
                                    .concat(String.valueOf(sensorEvent.values[0])).concat(";")
                                    .concat(String.valueOf(sensorEvent.values[1])).concat(";")
                                    .concat(String.valueOf(sensorEvent.values[2])).concat(";")

                    );
                    outputWritergyr.append("\n");
                    outputWritergyr.flush();

                } /*else {
                        outputWritergyr.append(
                                "3;"
                                    .concat("nulo").concat(";")
                                    .concat("nulo").concat(";")
                                    .concat("nulo").concat(";")

                        );
                        outputWritergyr.append("\n");
                        outputWritergyr.flush();

                }
                */
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), "NullPointerException: Por favor reinicie a aplicação", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "NullPointerException: Por favor reinicie a aplicação", Toast.LENGTH_LONG).show();
            }

    }
    @Override
    public void onAccuracyChanged(Sensor sensorEvent, int i) {
        //codar
    }
    public void FallbackLocationTracker(Context context) {
        gps = new LocationClass(context, LocationClass.ProviderType.GPS);
        net = new LocationClass(context, LocationClass.ProviderType.NETWORK);
    }

    public void start(){
        if(isRunning){
            //Already running, do nothing
            return;
        }

        //Start both
        gps.start(this);
        net.start(this);
        isRunning = true;
    }

    public void start(LocationTracker.LocationUpdateListener update) {
        start();
        listener = update;
    }


    public void stop(){
        if(isRunning){
            gps.stop();
            net.stop();
            isRunning = false;
            listener = null;
        }
    }

    public boolean hasLocation(){
        //If either has a location, use it
        return gps.hasLocation() || net.hasLocation();
    }

    public boolean hasPossiblyStaleLocation(){
        //If either has a location, use it
        return gps.hasPossiblyStaleLocation() || net.hasPossiblyStaleLocation();
    }

    public Location getLocation(){
        Location ret = gps.getLocation();
        if(ret == null){
            ret = net.getLocation();
        }
        return ret;
    }

    public Location getPossiblyStaleLocation(){
        Location ret = gps.getPossiblyStaleLocation();
        if(ret == null){
            ret = net.getPossiblyStaleLocation();
        }
        return ret;
    }

    public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
        boolean update = false;
        Log.d(TAG, "onUpdateGPS: ");
        //We should update only if there is no last location, the provider is the same, or the provider is more accurate, or the old location is stale
        if(lastLoc == null){
            update = true;
        }
        else if(lastLoc != null && lastLoc.getProvider().equals(newLoc.getProvider())){
            update = true;
        }
        else if(newLoc.getProvider().equals(LocationManager.GPS_PROVIDER)){
            update = true;
        }
        else if (newTime - lastTime > 5 * 60 * 1000){
            update = true;
        }

        if(update){
            if(listener != null){
                listener.onUpdate(lastLoc, lastTime, newLoc, newTime);
            }
            lastLoc = newLoc;
            lastTime = newTime;
        }

        if(getLocation() != null && outputWritergps != null) {
            try {
                outputWritergps.append(
                        "GPS;"
                                .concat(String.valueOf(getLocation().getLatitude())).concat(";")
                                .concat(String.valueOf(getLocation().getLongitude())).concat(";")
                                .concat(String.valueOf(getLocation().getSpeed())).concat(";")
                                .concat(String.valueOf(getLocation().getTime())).concat(";")
                                .concat( idade).concat(";")
                                .concat( peso)
                                .concat(";").concat(altura)
                );
               //Log.d(TAG, "onUpdateGPS: GRAVOU NO ARQUIVO");
                outputWritergps.append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}