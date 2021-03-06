package com.example.joaquin.gps_coordutm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

/**
Manejo de las preferencias
 */
public class ConfiguracionActivity extends AppCompatActivity {

    private CheckBox mcheboxSmsSiLocalizacion;
    private Switch mswitchSmsActualORef;
    private CheckBox mcheboxRumboSiVelocidad;
    private EditText medttxtTamanoSMS;
    private Switch mswitchSufijo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        SharedPreferences shaprePreferencias = getSharedPreferences("CONFIGURACION_GPSCOORDUTM", Context.MODE_PRIVATE);


        //Referencias a obj. del layout
        referenciasAVistas();

        //Si hay conf. guardada previamente ponemos las views con ese valor
        poneVistasSegunConfiguracion(shaprePreferencias);


        //Añadimos listener para controlar la entrada de caracteres en el EditText
        medttxtTamanoSMS.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Toast.makeText(ConfiguracionActivity.this, "A cambiado texto", Toast.LENGTH_SHORT).show();

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


        //Añadimos listener porque switch tiene 2 modos de cambio de valor: onClick propiamente y arrastre.
        //OnCheckedChangeListener abarca ambos
        mswitchSufijo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences shaprePreferencias = getSharedPreferences("CONFIGURACION_GPSCOORDUTM", Context.MODE_PRIVATE);
                SharedPreferences.Editor shapreEditor = shaprePreferencias.edit();

                shapreEditor.putBoolean("Sufijo", isChecked);
                shapreEditor.commit();  //¡¡Que no se olvide esto o no hacemos na!!
            }
        });

        //Añadimos listener porque switch tiene 2 modos de cambio de valor: onClick propiamente y arrastre.
        //OnCheckedChangeListener abarca ambos
        mswitchSmsActualORef.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences shaprePreferencias = getSharedPreferences("CONFIGURACION_GPSCOORDUTM", Context.MODE_PRIVATE);
                SharedPreferences.Editor shapreEditor = shaprePreferencias.edit();

                shapreEditor.putBoolean("SmsActualORef", isChecked);
                shapreEditor.commit();  //¡¡Que no se olvide esto o no hacemos na!!
            }
        });

    }

    //Referencias a obj. del layout
    private void referenciasAVistas() {
        mcheboxSmsSiLocalizacion = (CheckBox) findViewById(R.id.checkboxSMSSiCoordenadas);
        mswitchSmsActualORef = (Switch) findViewById(R.id.switchSMSActualORef);
        mcheboxRumboSiVelocidad = (CheckBox) findViewById(R.id.checkboxRumboSiVelocidad);
        medttxtTamanoSMS = (EditText) findViewById(R.id.edittextTamanoSMS);
        mswitchSufijo = (Switch) findViewById(R.id.switchSufijo);
    }

    //Si hay conf. guardada previamente ponemos las views con ese valor
    private void poneVistasSegunConfiguracion(SharedPreferences shaprePreferencias) {
        if (shaprePreferencias.contains("SmsSiLocalizacion"))
            mcheboxSmsSiLocalizacion.setChecked(shaprePreferencias.getBoolean("SmsSiLocalizacion", true));
        if (shaprePreferencias.contains("SmsActualORef"))
            mswitchSmsActualORef.setChecked(shaprePreferencias.getBoolean("SmsActualORef", true));
        if (shaprePreferencias.contains("RumboSiVelocidad"))
            mcheboxRumboSiVelocidad.setChecked(shaprePreferencias.getBoolean("RumboSiVelocidad", true));
        if (shaprePreferencias.contains("Sufijo"))
            mswitchSufijo.setChecked(shaprePreferencias.getBoolean("Sufijo", false));
        if (shaprePreferencias.contains("TamanoSMS"))
            medttxtTamanoSMS.setText(((Integer) shaprePreferencias.getInt("TamanoSMS", 140)).toString());
    }

    //Se ha pulsado checkBoxRumboSiVelocidad
    public void rumbo (View v) {
        SharedPreferences shaprePreferencias = getSharedPreferences("CONFIGURACION_GPSCOORDUTM", Context.MODE_PRIVATE);
        SharedPreferences.Editor shapreEditor = shaprePreferencias.edit();

        boolean blnChecked = ((CheckBox) v).isChecked();

        shapreEditor.putBoolean("RumboSiVelocidad", blnChecked);
        shapreEditor.commit();  //¡¡Que no se olvide esto o no hacemos na!!

    }

    //Se ha pulsado checkBoxSmsSiLocalizacion
    public void sms (View v) {

        SharedPreferences shaprePreferencias = getSharedPreferences("CONFIGURACION_GPSCOORDUTM", Context.MODE_PRIVATE);
        SharedPreferences.Editor shapreEditor = shaprePreferencias.edit();

        boolean blnChecked = ((CheckBox) v).isChecked();

        shapreEditor.putBoolean("SmsSiLocalizacion", blnChecked);
        shapreEditor.commit();  //¡¡Que no se olvide esto o no hacemos na!!

    }

}
