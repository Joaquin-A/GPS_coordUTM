package com.example.joaquin.gps_coordutm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;

public class CoordenadasActivity extends AppCompatActivity {

    public TextView mtxtviwLatitud, mtxtviwLongitud,
            mtxtviwEasting, mtxtviwNorthing, mtxtviwZona, mtxtviwLetraZona,
            mtxtviwNumSatelites, mtxtviwEstadoGPS, mtxtviwBitacora,
            mtxtviwExactitud, mtxtviwRumboDisp, mtxtviwVelocidadMS, mtxtviwVelocidadKmH;

    public Button mbttnSMS, mbttnPuntos;

    private boolean mblnPrefDireccion;
    public boolean mblnPrefRumboSiVelocidad, mblnPrefSmsSiLocalizacion;
    public boolean mblnHayLocalizacion = false;

    public TextView mtxtviwDistanciaHasta, mtxtviwRumboHacia;
    private TextView mtxtviwEtiquetaDistanciaHasta, mtxtviwEtiquetaRumboHacia, mtxtviwEtiquetaPuntoRef;
    private TextView mtxtviwLatitudPunto, mtxtviwLongitudPunto;

    static final int IDENTIFICADOR_START_FOR_RESULT_PUNTOS = 1;  // The request code
    public String mstrLatitudPunto, mstrLongitudPunto;
    public boolean mblnPunto = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordenadas);

        //Creamos referencias a las views
        mtxtviwLatitud = (TextView) findViewById(R.id.textviewLatitud);
        mtxtviwLongitud = (TextView) findViewById(R.id.textviewLongitud);
        mtxtviwEasting = (TextView) findViewById(R.id.textviewEasting);
        mtxtviwNorthing = (TextView) findViewById(R.id.textviewNorthing);
        mtxtviwZona = (TextView) findViewById(R.id.textviewZona);
        mtxtviwLetraZona = (TextView) findViewById(R.id.textviewLetraZona);
        mtxtviwNumSatelites = (TextView) findViewById(R.id.textviewNumSatelites);
        mtxtviwEstadoGPS = (TextView) findViewById(R.id.textviewEstadoGPS);
        mtxtviwBitacora = (TextView) findViewById(R.id.textviewBitacora);
        mtxtviwExactitud = (TextView) findViewById(R.id.textviewExactitud);
        mtxtviwRumboDisp = (TextView) findViewById(R.id.textviewRumboDispositivo);
        mtxtviwVelocidadMS = (TextView) findViewById(R.id.textviewVelocidadMS);
        mtxtviwVelocidadKmH = (TextView) findViewById(R.id.textviewVelocidadKmH);
        mbttnSMS = (Button) findViewById(R.id.buttonSMS);
        mbttnPuntos = (Button) findViewById(R.id.buttonPuntos);


        mtxtviwEtiquetaPuntoRef = (TextView) findViewById(R.id.textviewEtiquetaPuntoRef);
        mtxtviwEtiquetaRumboHacia = (TextView) findViewById(R.id.textviewEtiquetaRumboHacia);
        mtxtviwRumboHacia = (TextView) findViewById(R.id.textviewRumboHacia);
        mtxtviwEtiquetaDistanciaHasta = (TextView) findViewById(R.id.textviewEtiquetaDistanciaHasta);
        mtxtviwDistanciaHasta = (TextView) findViewById(R.id.textviewDistanciaHasta);

        mtxtviwLatitudPunto = (TextView) findViewById(R.id.textviewLatitudPunto);
        mtxtviwLongitudPunto = (TextView) findViewById(R.id.textviewLongitudPunto);

        //Creamos un listener para manejar el obj Location
        MiLocationListener mlocListener = new MiLocationListener();
        mlocListener.setMprincipalActivity(this);

        /*Servicio LocationManager del sistema Android y agregar un nuevo LocationListener de
        actualizaciones de ubicación*/
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(CoordenadasActivity.this, "Faltan permisos", Toast.LENGTH_SHORT).show();
        } else
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) mlocListener);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        //Comprobamos preferencias
        SharedPreferences shaprePreferencias = getSharedPreferences("CONFIGURACION_GPSCOORDUTM", Context.MODE_PRIVATE);
        mblnPrefDireccion = shaprePreferencias.getBoolean("Direccion", false);
        mblnPrefRumboSiVelocidad = shaprePreferencias.getBoolean("RumboSiVelocidad", true);
        mblnPrefSmsSiLocalizacion = shaprePreferencias.getBoolean("SmsSiLocalizacion", true);

        //Vemos si se activa el botón SMS
        activaBotonSMS();

        /* TODO Usar substitución de valores en cadenas en vez de concatenar. Es sugerido para facilitar i18n
        Toast.makeText(this, String.format("onCreate mblnPrefDireccion es %s", mblnPrefDireccion), Toast.LENGTH_SHORT).show();
         */
    }

    //Vemos si activamos el botón SMS
    private void activaBotonSMS() {
        if (mblnPrefSmsSiLocalizacion)
            mbttnSMS.setEnabled(mblnHayLocalizacion);
        else
            mbttnSMS.setEnabled(true);
    }

    //Vemos si activamos las TextView relativas a punto referencia
    private void activaPuntoReferencia() {
        mtxtviwDistanciaHasta.setEnabled(true);
        mtxtviwRumboHacia.setEnabled(true);;
        mtxtviwEtiquetaDistanciaHasta.setEnabled(true);
        mtxtviwEtiquetaRumboHacia.setEnabled(true);
        mtxtviwEtiquetaPuntoRef.setEnabled(true);
    }



    //Este es llamado por el servicio/listener
    public void calculaUTM(Location loc) {
        LatLng latlngLocalizacion = new LatLng(loc.getLatitude(), loc.getLongitude());
        UTMRef utmrefLocalizacion = latlngLocalizacion.toUTMRef();

        Double dblEasting = utmrefLocalizacion.getEasting();
        Double dblNorthing = utmrefLocalizacion.getNorthing();
        char chrLatZone = utmrefLocalizacion.getLatZone();
        int intLngZone = utmrefLocalizacion.getLngZone();

        //Mostramos coord UTM con 7 cifras (rellena ceros por la izq. si necesario
        mtxtviwEasting.setText(String.format("%07d", dblEasting.intValue()));
        mtxtviwNorthing.setText(String.format("%07d", dblNorthing.intValue()));

        //No va hacer un cast
        //También se podría usar String.valueOf();
        mtxtviwZona.setText(Integer.toString(intLngZone));
        mtxtviwLetraZona.setText(Character.toString(chrLatZone));

        //Vemos si se activa el botón SMS (depende de configuración)
        activaBotonSMS();

        //Activamos botón Puntos
        mbttnPuntos.setEnabled(true);
    }


    public void aConfiguracion (View v) {
        Intent intentAConfiguracion = new Intent();

        // Intent explícito
        intentAConfiguracion.setClass(this, ConfiguracionActivity.class);
        startActivity(intentAConfiguracion);
    }

    public void aPuntos (View v) {
        String strLatitud = ((TextView) findViewById(R.id.textviewLatitud)).getText().toString();
        String strLongitud = ((TextView) findViewById(R.id.textviewLongitud)).getText().toString();

        Intent intentAPuntos = new Intent();

        // Intent explícito
        intentAPuntos.setClass(this, PuntosActivity.class);
        intentAPuntos.putExtra("latitud", strLatitud);
        intentAPuntos.putExtra("longitud", strLongitud);
        //Lanazamo actividad pero esperando resultado
        startActivityForResult(intentAPuntos, IDENTIFICADOR_START_FOR_RESULT_PUNTOS);
    }

    public void aSMS (View v) {
        String strLatitud = ((TextView) findViewById(R.id.textviewLatitud)).getText().toString();
        String strLongitud = ((TextView) findViewById(R.id.textviewLongitud)).getText().toString();
        String strCoordenadas;

        if (!strLatitud.isEmpty() && !strLongitud.isEmpty())
            //Open Street Map, Google Maps... sólo entienden las coordenadas en formato decimal inglés
            //No consigo usar Formatter correctamente, así que lo paso a . usando func. de cadena
            strCoordenadas = strLatitud.replace(',' , '.') + "," + strLongitud.replace(',' , '.');
        else
            strCoordenadas = "No hay coordenadas establecidas";


        Intent intentASMS = new Intent();

        // Intent explícito
        intentASMS.setClass(this, SMSActivity.class);
        intentASMS.putExtra("coordenadas", strCoordenadas);
        startActivity(intentASMS);
    }

    @Override
    //Esperamos respuesta de startActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (IDENTIFICADOR_START_FOR_RESULT_PUNTOS) : {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(CoordenadasActivity.this, "Copiamos el punto de referencia", Toast.LENGTH_SHORT).show();
                    mstrLatitudPunto = data.getStringExtra("latitud");
                    mstrLongitudPunto = data.getStringExtra("longitud");

                    mtxtviwLatitudPunto.setText(mstrLatitudPunto);
                    mtxtviwLongitudPunto.setText(mstrLongitudPunto);

                    mblnPunto = true;
                }
                break;
            }
        }
    }
}
