package com.example.joaquin.gps_coordutm;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by joaquin on 20/04/16.
 */
public class MiLocationListener implements LocationListener {
    CoordenadasActivity mprincipalActivity;

    public CoordenadasActivity getMprincipalActivity() {
        return mprincipalActivity;
    }

    public void setMprincipalActivity(CoordenadasActivity mprincipalActivity) {
        //Aquí el this. es fundamental para distinguir parámetro de variable miembro,
        //si no compila felizmente pero casca en ejecución
        this.mprincipalActivity = mprincipalActivity;
    }

    @Override
    public void onLocationChanged(Location loc) {
        // Este método se ejecuta cada vez que el GPS recibe nuevas coordenadas
        // debido a la detección de un cambio de ubicación

        double dblLatitud = loc.getLatitude();
        double dblLongitud = loc.getLongitude();

        /*Documentación de Location
        http://developer.android.com/reference/android/location/Location.html*/
        String strText;

        //Redondeamos a 5 decimales de grado pues es suficiente
        strText = String.format("%1.5f", dblLatitud);
        mprincipalActivity.mtxtviwLatitud.setText(strText);

        //Redondeamos a 5 decimales de grado pues es suficiente
        strText = String.format("%1.5f", dblLongitud);
        mprincipalActivity.mtxtviwLongitud.setText(strText);

        /* Exactitud (accuracy)
        We define accuracy as the radius of 68% confidence. In other words, if you draw a circle
        centered at this location's latitude and longitude, and with a radius equal to the accuracy,
        then there is a 68% probability that the true location is inside the circle.
        */
        if (loc.hasAccuracy())
            //Tomamos sólo la parte entera
            mprincipalActivity.mtxtviwExactitud.setText(String.format("%.0f", loc.getAccuracy()));
        else
            mprincipalActivity.mtxtviwExactitud.setText("No tiene exactitud");

        //Rumdo dispositivo
        if (loc.hasBearing())
            mprincipalActivity.mtxtviwRumboDisp.setText(puntoCardinal(loc.getBearing()));
        else
            mprincipalActivity.mtxtviwRumboDisp.setText("No tiene rumbo");

        //Llamamos a la búsqueda de dirección y cálculo UTM
        mprincipalActivity.setDireccionYUTM(loc);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Este método se ejecuta cuando el GPS es desactivado
        mprincipalActivity.mtxtviwEstadoGPS.setText("GPS ha sido desactivado");
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Este método se ejecuta cuando el GPS es activado
        mprincipalActivity.mtxtviwEstadoGPS.setText("GPS ha sido activado");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Este método se ejecuta cada vez que se detecta un cambio en el
        // status del proveedor de localización (GPS)
        // Los diferentes Status son:
        // OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
        // TEMPORARILY_UNAVAILABLE -> Temporalmente no disponible pero se
        // espera que esté disponible en breve
        // AVAILABLE -> Disponible

        switch (status) {
            case 0:
                // OUT_OF_SERVICE
                mprincipalActivity.mtxtviwBitacora.setText("GPS fuera de servicio");
                break;
            case 1:
                //TEMPORARILY_UNAVAILABLE
                mprincipalActivity.mtxtviwBitacora.setText("GPS temporalmente no disponible");
                break;
            case 2:
                //AVAILABLE
                mprincipalActivity.mtxtviwBitacora.setText("GPS disponilbe");
                break;
        }
    }

    private String puntoCardinal (float fltAngulo) {

        String strAngulo = String.format("%03.1f", fltAngulo);
        String strPuntoCardinal = "X";

        //https://es.wikipedia.org/wiki/Rosa_de_los_vientos#Anexo:_Tabulaci.C3.B3n_angular_de_los_puntos_cardinales_por_cuadrantes
        if (fltAngulo > 337.5 || fltAngulo <= 22.5 )
            strPuntoCardinal =  "N";
        else if (fltAngulo > 22.5 && fltAngulo <= 67.5)
            strPuntoCardinal =  "NE";
        else if (fltAngulo > 67.5 && fltAngulo <= 112.5)
            strPuntoCardinal =  "E";
        else if (fltAngulo > 112.5 && fltAngulo <= 157.5)
            strPuntoCardinal =  "SE";
        else if (fltAngulo > 157.5 && fltAngulo <= 202.5)
            strPuntoCardinal =  "S";
        else if (fltAngulo > 202.5 && fltAngulo <= 247.5)
            strPuntoCardinal =  "SO";
        else if (fltAngulo > 247.5 && fltAngulo <= 292.5)
            strPuntoCardinal =  "O";
        else if (fltAngulo > 292.5 && fltAngulo <= 337.5)
            strPuntoCardinal =  "NO";

        return strAngulo + " " + strPuntoCardinal;
    }
}
