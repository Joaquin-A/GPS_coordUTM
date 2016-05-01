package com.example.joaquin.gps_coordutm;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

/**
Manejo de las preferencias
 */
public class ConfiguracionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        SharedPreferences shaprePreferencias = getSharedPreferences("CONFIGURACION_GPSCOORDUTM", Context.MODE_PRIVATE);


        //Referencias a obj. del layout
        CheckBox cheboxDireccion = (CheckBox) findViewById(R.id.checkBoxDireccion);
        EditText edttxtTamanoSMS = (EditText) findViewById(R.id.editTextTamanoSMS);
        Switch switchSufijo = (Switch) findViewById(R.id.switchSufijo);

        //Si hay conf. guardada previamente ponemos las views con ese valor
        if (shaprePreferencias.contains("Direccion"))
            cheboxDireccion.setChecked(shaprePreferencias.getBoolean("Direccion", false));
        if (shaprePreferencias.contains("Sufijo"))
            switchSufijo.setChecked(shaprePreferencias.getBoolean("Sufijo", false));
        if (shaprePreferencias.contains("TamanoSMS"))
            edttxtTamanoSMS.setText(((Integer) shaprePreferencias.getInt("TamanoSMS", 140)).toString());


        //Añadimos listener para controlar la entrada de caracteres en el EditText
        edttxtTamanoSMS.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(ConfiguracionActivity.this, "A cambiado texto", Toast.LENGTH_SHORT).show();

                SharedPreferences shaprePreferencias = getSharedPreferences("CONFIGURACION_GPSCOORDUTM", Context.MODE_PRIVATE);
                SharedPreferences.Editor shapreEditor = shaprePreferencias.edit();

                if (s.toString().length() == 0) {
                    Toast.makeText(ConfiguracionActivity.this, "Si se deja en blanco se supondrá 140", Toast.LENGTH_LONG).show();
                    shapreEditor.putInt("TamanoSMS", 140);
                } else {
                    shapreEditor.putInt("TamanoSMS", Integer.parseInt(s.toString()));
                }
                shapreEditor.commit();  //¡¡Que no se olvide esto o no hacemos na!!
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    //Se ha pulsado checkBoxDireccion
    public void direccion (View v) {
        SharedPreferences shaprePreferencias = getSharedPreferences("CONFIGURACION_GPSCOORDUTM", Context.MODE_PRIVATE);
        SharedPreferences.Editor shapreEditor = shaprePreferencias.edit();

        boolean blnChecked = ((CheckBox) v).isChecked();

        shapreEditor.putBoolean("Direccion", blnChecked);
        shapreEditor.commit();  //¡¡Que no se olvide esto o no hacemos na!!
    }

    //Se ha pulsado switchSufijo
    public void sufijo2(View v) {
        SharedPreferences shaprePreferencias = getSharedPreferences("CONFIGURACION_GPSCOORDUTM", Context.MODE_PRIVATE);
        SharedPreferences.Editor shapreEditor = shaprePreferencias.edit();

        boolean blnChecked = ((Switch) v).isChecked();

        Toast.makeText(ConfiguracionActivity.this, String.format("Switch %b ", blnChecked), Toast.LENGTH_SHORT).show();

        shapreEditor.putBoolean("Sufijo", blnChecked);
        shapreEditor.commit();  //¡¡Que no se olvide esto o no hacemos na!!
    }
}
