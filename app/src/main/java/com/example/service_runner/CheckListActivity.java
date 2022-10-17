package com.example.service_runner;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
public class CheckListActivity extends AppCompatActivity {

    private String nome, idade, altura, peso, swtAceler, swtGyro, swtMagne, sAceler, sGyro, sMagne;


    SensorManager sensorManager;
    Switch swtAcelerometro, swtGyroscopio, swtMagnetometro;
    Spinner sAcelerometro, sGyroscopio, sMagnetometro;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_check_list_activity);
        //Captura dados que vem da MainActivity
        Intent intent = this.getIntent();
        nome = intent.getStringExtra("nome");
        idade = intent.getStringExtra("idade");
        altura = intent.getStringExtra("altura");
        peso = intent.getStringExtra("peso");

        swtAcelerometro = findViewById(R.id.switchAcelerometro);
        swtGyroscopio = findViewById(R.id.switchGiroscopio);
        swtMagnetometro = findViewById(R.id.switchMagnetometro);
        sAcelerometro = findViewById(R.id.spinner_acelerometro);
        sGyroscopio = findViewById(R.id.spinner_gyroscopio);
        sMagnetometro = findViewById(R.id.spinner_magnetometro);

        Spinner spinnerAcelerometro = (Spinner) findViewById(R.id.spinner_acelerometro);
        Spinner spinnerGyroscopio = (Spinner) findViewById(R.id.spinner_gyroscopio);
        Spinner spinnerMagnetometro = (Spinner) findViewById(R.id.spinner_magnetometro);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> acel = ArrayAdapter.createFromResource(this,
                R.array.spinner_acelerometro, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> gyro = ArrayAdapter.createFromResource(this,
                R.array.spinner_gyroscopio, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> magn = ArrayAdapter.createFromResource(this,
                R.array.spinner_magnetometro, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        acel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        magn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gyro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerAcelerometro.setAdapter(acel);
        spinnerGyroscopio.setAdapter(gyro);
        spinnerMagnetometro.setAdapter(magn);
        //inicializa variaveis
        swtAceler = "0"; swtGyro = "0"; swtMagne = "0";
        //chama metodo que verifica se os sensores existem
        verificaSensoresExistentes();
    }

    public void abrirTerceiraTela(View view){
        //verificaSensoresExistentes();
        Intent sac = new Intent(this, SensorActivity.class);
        sac.putExtra("nome", nome);
        sac.putExtra("idade", idade);
        sac.putExtra("altura", altura);
        sac.putExtra("peso", peso);
        sac.putExtra("swtAceler", String.valueOf(swtAcelerometro.isChecked()));
        sac.putExtra("swtGyro", String.valueOf(swtGyroscopio.isChecked()));
        sac.putExtra("swtMagne", String.valueOf(swtMagnetometro.isChecked()));
        sac.putExtra("sAceler", sAcelerometro.getSelectedItem().toString());
        sac.putExtra("sGyro", sGyroscopio.getSelectedItem().toString());
        sac.putExtra("sMagne", sMagnetometro.getSelectedItem().toString());
        startActivity(sac);
    }
    public void verificaSensoresExistentes(){

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //Se não existir sensor, então desabilita o switch referente a ele
        if (!(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)){
            swtAcelerometro.setEnabled(false);
            sAcelerometro.setEnabled(false);

        }
        if (!(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null)){
            swtGyroscopio.setEnabled(false);
            sGyroscopio.setEnabled(false);

        }
        if (!(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null)){
            swtMagnetometro.setEnabled(false);
            sMagnetometro.setEnabled(false);

        }
    }
}