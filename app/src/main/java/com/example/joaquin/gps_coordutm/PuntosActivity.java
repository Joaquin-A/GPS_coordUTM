package com.example.joaquin.gps_coordutm;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class PuntosActivity extends AppCompatActivity {

    private TextView mtxtviwLatitud, mtxtviwLongitud;
    private EditText medttxtDesc;
    private File mfile = null;
    static final String STR_NOMBRE_FICHERO = "gps_coordutm.csv";
    private boolean mblnNuevo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntos);

        mtxtviwLatitud = (TextView) findViewById(R.id.textviewLatitud);
        mtxtviwLongitud = (TextView) findViewById(R.id.textviewLongitud);
        medttxtDesc = (EditText) findViewById(R.id.editTextDescripcion);

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

    public void guardar(View v) {

        //Memoria externa puede desaparecer (si realmente es externa)
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            /*if (isExternalStorageReadable())
                Toast.makeText(MainActivity.this, "Abierto lectura", Toast.LENGTH_SHORT).show();
            if (isExternalStorageWritable())
                Toast.makeText(MainActivity.this, "Abierto escritura", Toast.LENGTH_SHORT).show();*/

            try {
                mfile = getFicheroStorage(STR_NOMBRE_FICHERO);

                writeToFile(mtxtviwLatitud.getText().toString(), mtxtviwLongitud.getText().toString(),
                            medttxtDesc.getText().toString());

                //writeToFile(strContenido, file);
            } catch (Throwable e) {
                Toast.makeText(PuntosActivity.this, "Falla guardar():" + e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    //To save public files on the external storage,
    public File getFicheroStorage(String strFichero) {
        // Get the directory for the user's public pictures directory.
        try {
            /*
            Environment.DIRECTORY_DOCUMENTS se añadió en Android 4.4, API 19, KITKAT
            http://developer.android.com/reference/android/os/Environment.html#DIRECTORY_DOCUMENTS
            Por mayor compatibilidad con APIs antiguas usamos DOWNLOADS
             */
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), strFichero);
            //Creamos el fichero
            if (!file.createNewFile()) {
                //No se puede crear. Posiblemente es porque ya existe
                Toast.makeText(PuntosActivity.this, "Se añadirá a " + STR_NOMBRE_FICHERO + " existente",
                        Toast.LENGTH_SHORT).show();
            } else {
                mblnNuevo = true;
                Toast.makeText(PuntosActivity.this, "Fichero " + Environment.DIRECTORY_DOWNLOADS + "/" + STR_NOMBRE_FICHERO + " creado",
                        Toast.LENGTH_LONG).show();
            }

            return file;
        } catch (Throwable e) {
            Toast.makeText(PuntosActivity.this, "Falla getFicheroStorage()", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return null;
        }
    }

    private boolean writeToFile(String strLat, String strLong, String strDesc) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(mfile, true);

            /*This class was deprecated in API level 22.
            Time time = new Time();
            time.setToNow();
            */

            GregorianCalendar grgcldAhora = new GregorianCalendar();  // "ahora" si no hay campos

            int intH = grgcldAhora.get(Calendar.HOUR_OF_DAY);

            long lngTimeStamp = grgcldAhora.getTimeInMillis() / 1000;

            if (strDesc.isEmpty()) {
                strDesc = "Sin descripción";
            }

            String strCadena = new String();
            if (mblnNuevo) {
                //Escribimos la cabecera del .csv
                strCadena = "\"Latitud\", \"Longitud\", \"Descripción\", " +
                        "       \"aaaa-mm-dd\", \"hh:mm:ss\", \"unix timestamp\", \n";
                mblnNuevo = false;
            }
            /*Open Street Map, Google Maps... sólo entienden las coordenadas en formato decimal inglés
            No consigo usar Formatter correctamente, así que lo paso a '.' usando func. de cadena
            String.replace(',' , '.');*/
            /*En vez de conseguir 2016-01-31 usando GregorianCalendar.get(Calendar.YEAR) MONT, DAY_OF_MONTH,
            lo hago formateando la salida %tF 	Full date in ISO 8601 format (YYYY-MM-DD)
             */
            /*Ídem la hora con %tT*/
            /* %n es nueva línea */
            strCadena = strCadena + String.format("%s,%s,\"%s\",%tF,%tT,%d%n",
                                            strLat.replace(',' , '.'), strLong.replace(',' , '.'), strDesc,
                                            grgcldAhora, grgcldAhora, lngTimeStamp);


            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(strCadena);
            outputStreamWriter.close();

            return true;
        } catch (Throwable e) {
            Toast.makeText(PuntosActivity.this, "Falla writeToFile()" + e.toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
