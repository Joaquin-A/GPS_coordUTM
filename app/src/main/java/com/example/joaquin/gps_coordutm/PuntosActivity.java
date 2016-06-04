package com.example.joaquin.gps_coordutm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PuntosActivity extends AppCompatActivity {

    private TextView mtxtviwLatitud, mtxtviwLongitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntos);

        mtxtviwLatitud = (TextView) findViewById(R.id.textviewLatitud);
        mtxtviwLongitud = (TextView) findViewById(R.id.textviewLongitud);

        //Ponemos en el texto de textviewCoordenadas el String recibido con el intent invocante
        Intent intentInvocante = getIntent();
        String strLatitud = intentInvocante.getStringExtra("latitud");
        String strLongitud = intentInvocante.getStringExtra("longitud");
        mtxtviwLatitud.setText(strLatitud);
        mtxtviwLongitud.setText(strLongitud);
    }

    public void seleccionar(View v) {
        //ToDo hacer una selección de verdad

        //Valor de retorno a la actividad invocante
        Intent resultIntent = getIntent();
        resultIntent.putExtra("latitud", mtxtviwLatitud.getText().toString());
        resultIntent.putExtra("longitud", mtxtviwLongitud.getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }
}
