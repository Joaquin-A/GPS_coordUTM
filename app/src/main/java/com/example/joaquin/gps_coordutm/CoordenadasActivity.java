package com.example.joaquin.gps_coordutm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;

public class CoordenadasActivity extends AppCompatActivity {

    public TextView mtxtviwLatitud, mtxtviwLongitud,
            mtxtviwEasting, mtxtviwNorthing, mtxtviwZona, mtxtviwLetraZona,
            mtxtviwSatelites, mtxtviwEstadoGPS, mtxtviwDireccion, mtxtviwBitacora,
            mtxtviwExactitud, mtxtviwRumboDisp;

    public boolean mblnPrefDireccion;

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
        mtxtviwSatelites = (TextView) findViewById(R.id.textviewSatelites);
        mtxtviwEstadoGPS = (TextView) findViewById(R.id.textviewEstadoGPS);
        mtxtviwDireccion = (TextView) findViewById(R.id.textviewDireccion);
        mtxtviwBitacora = (TextView) findViewById(R.id.textviewBitacora);
        mtxtviwExactitud = (TextView) findViewById(R.id.textviewExactitud);
        mtxtviwRumboDisp = (TextView) findViewById(R.id.textviewRumboDispositivo);

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

        /* TODO Usar substitución de valores en cadenas en vez de concatenar. Es sugerido para facilitar i18n
        Toast.makeText(this, String.format("onCreate mblnPrefDireccion es %s", mblnPrefDireccion), Toast.LENGTH_SHORT).show();
         */
    }

        //Este es llamado por el servicio/listener
    public void setDireccionYUTM(Location loc) {
        if (mblnPrefDireccion) {
            //Obtener la dirección de la calle a partir de la latitud y la longitud
            if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
                try {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    //Obtener un listado de direcciones que figuran alrededor de las coordenadas
                    List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                    if (!list.isEmpty()) {
                        Address address = list.get(0);
                        mtxtviwDireccion.setText("Mi dirección es: \n" + address.getAddressLine(0));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else
            mtxtviwDireccion.setText("Búsqueda desactivada");

        calculaUTM(loc);
    }

    private void calculaUTM(Location loc) {
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
    }


    public void aConfiguracion (View v) {
        Intent intentAConfiguracion = new Intent();

        // Intent explícito
        intentAConfiguracion.setClass(this, ConfiguracionActivity.class);
        startActivity(intentAConfiguracion);
    }

    public void aPuntos (View v) {
        Intent intentAPuntos = new Intent();

        // Intent explícito
        intentAPuntos.setClass(this, PuntosActivity.class);
        startActivity(intentAPuntos);
    }

    public void aSMS (View v) {
        String strLatitud = ((TextView) findViewById(R.id.textviewLatitud)).getText().toString();
        String strLongitud = ((TextView) findViewById(R.id.textviewLongitud)).getText().toString();

        //Open Street Map, Google Maps... sólo entienden las coordenadas en formato inglés
        String strCoordenadas = strLatitud.replace(',' , '.') + "," + strLongitud.replace(',' , '.');

        Intent intentASMS = new Intent();

        // Intent explícito
        intentASMS.setClass(this, SMSActivity.class);
        intentASMS.putExtra("coordenadas", strCoordenadas);
        startActivity(intentASMS);
    }
}
