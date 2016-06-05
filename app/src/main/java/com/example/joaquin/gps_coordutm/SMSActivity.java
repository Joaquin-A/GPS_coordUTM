package com.example.joaquin.gps_coordutm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SMSActivity extends AppCompatActivity {

    private EditText medttxtCuerpoSMS;
    private TextView mtxtviwCaracteresTotal, mtxtviwCoordenadas;
    private Boolean mblnSufijo;
    private int mintMaxTamanoSMS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        mtxtviwCoordenadas = (TextView) findViewById(R.id.textviewCoordenadas);
        mtxtviwCaracteresTotal = (TextView) findViewById(R.id.textviewCaracteresTotal);
        medttxtCuerpoSMS = (EditText) findViewById(R.id.edittextCuerpoSMS);

        //Ponemos en el texto de textviewCoordenadas el String recibido con el intent invocante
        Intent intentInvocante = getIntent();
        String strCoordenadas = intentInvocante.getStringExtra("coordenadas");
        mtxtviwCoordenadas.setText(strCoordenadas);

        //Comprobamos configuraciones
        SharedPreferences shaprePreferencias = getSharedPreferences("CONFIGURACION_GPSCOORDUTM", Context.MODE_PRIVATE);
        mblnSufijo = shaprePreferencias.getBoolean("Sufijo", false);
        mintMaxTamanoSMS = shaprePreferencias.getInt("TamanoSMS", 140);

        //Si las coordenadas irán como sufijo en el SMS, moveremos algunos TextView
        if (mblnSufijo) reordenaPresentacion();

        /*Limitamos el nº máx de caracteres del edit para que el total tenga la longitud que queremos
        Programaticamente ponemos un valor al equivalente a maxLength en el XML del EditView
        */
        int intTamanoCoordenadas = strCoordenadas.length();
        int intTamanoRestoSMS = mintMaxTamanoSMS - intTamanoCoordenadas;
        medttxtCuerpoSMS.setFilters(new InputFilter[]{new InputFilter.LengthFilter(intTamanoRestoSMS)});

        mtxtviwCaracteresTotal.setText(Integer.toString(intTamanoCoordenadas));

        //Añadimos listener para controlar la entrada de caracteres en el EditText
        medttxtCuerpoSMS.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int intTamanoActualRestoSMS;

                intTamanoActualRestoSMS = mtxtviwCoordenadas.getText().length() + s.length();
                mtxtviwCaracteresTotal.setText(Integer.toString(intTamanoActualRestoSMS));

                if (intTamanoActualRestoSMS >= mintMaxTamanoSMS)
                    Toast.makeText(SMSActivity.this, "Tamaño máximo total de SMS alcanzado", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void reordenaPresentacion() {
        TextView txtviwEtiquetaMas = (TextView) findViewById(R.id.textviewEtiquetaMas);

        /* Si la view no tuviera layout_below, habría que crearlo
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                            ViewGroup.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.BELOW, R.id.<below_id>);
        viewToLayout.setLayoutParams(p);*/

        RelativeLayout.LayoutParams lytprmParams;


        lytprmParams = (RelativeLayout.LayoutParams) medttxtCuerpoSMS.getLayoutParams();
        lytprmParams.addRule(RelativeLayout.BELOW, R.id.textViewSMS);


        //Hay que cambiar varios pues si no habría referencias circulares en el layout
        lytprmParams = (RelativeLayout.LayoutParams) txtviwEtiquetaMas.getLayoutParams();
        lytprmParams.addRule(RelativeLayout.BELOW, R.id.edittextCuerpoSMS);

        lytprmParams = (RelativeLayout.LayoutParams) mtxtviwCoordenadas.getLayoutParams();
        lytprmParams.addRule(RelativeLayout.BELOW, R.id.textviewEtiquetaMas);


    }

    public void enviaSMS (View v) {
        String strCoordenadas = ((TextView) findViewById(R.id.textviewCoordenadas)).getText().toString();
        String strCuerpoSMS = this.medttxtCuerpoSMS.getText().toString();
        String strSMSTotal;

        if (mblnSufijo)
            strSMSTotal = strCoordenadas + " " + strCuerpoSMS;
        else
            strSMSTotal = strCuerpoSMS + " " + strCoordenadas;




        //Intent implícito (action, data)
        Uri destino = Uri.parse("smsto:"); //Si no especificamos tlf nos lo pedirá la actividad que reciba el intent
        Intent intentSMS = new Intent(Intent.ACTION_SENDTO, destino);

        intentSMS.putExtra("sms_body", strSMSTotal);
        startActivity(intentSMS);

    }
}
