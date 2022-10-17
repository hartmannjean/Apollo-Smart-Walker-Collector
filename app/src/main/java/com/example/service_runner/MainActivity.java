package com.example.service_runner;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{
    TextView nome, idade, altura, peso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        nome = findViewById(R.id.editTextNome);
        idade = findViewById(R.id.editTextIdade);
        altura = findViewById(R.id.editTextAltura);
        peso = findViewById(R.id.editTextPeso);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (!Environment.isExternalStorageManager()){
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }
    }


    public void abrirSegundaTela(View view){
        Intent cla = new Intent(this, CheckListActivity.class);
        cla.putExtra("nome", nome.getText().toString());
        cla.putExtra("idade", idade.getText().toString());
        cla.putExtra("altura", altura.getText().toString());
        cla.putExtra("peso", peso.getText().toString());
        startActivity(cla);
    }
}
