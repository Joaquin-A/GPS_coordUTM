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
        Bundle bundleExtra = loc.getExtras();
        float fltRumbo = loc.getBearing();
        float fltVelocidad = loc.getSpeed();

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
            mprincipalActivity.mtxtviwExactitud.setText("--");

        //Velocidad dispositivo
        if (fltVelocidad  > 0.0) {
            mprincipalActivity.mtxtviwVelocidadMS.setText(String.format("%.1f m/s", fltVelocidad));
            //Pasamos a km/h
            mprincipalActivity.mtxtviwVelocidadKmH.setText(String.format("%.1f km/h", fltVelocidad * 3.6));
            //Mostramos Rumbo dispositivo
            if (fltRumbo != 0.0)
                mprincipalActivity.mtxtviwRumboDisp.setText(puntoCardinal(fltRumbo));
            else
                mprincipalActivity.mtxtviwRumboDisp.setText("--");
        } else {
            mprincipalActivity.mtxtviwVelocidadMS.setText("--");
            mprincipalActivity.mtxtviwVelocidadKmH.setText("--");
            //Mostramos rumbo dispositivo condicionado a las preferencias
            if (!mprincipalActivity.mblnPrefRumboSiVelocidad) {
                if (fltRumbo != 0.0)
                    mprincipalActivity.mtxtviwRumboDisp.setText(puntoCardinal(fltRumbo));
                else
                    mprincipalActivity.mtxtviwRumboDisp.setText("--");
            } else
                mprincipalActivity.mtxtviwRumboDisp.setText("-x-");
        }

        mprincipalActivity.mtxtviwNumSatelites.setText(String.format("%d", bundleExtra.getInt("satellites", -999)));

        //Ponemos el indicador de que hay localización

        mprincipalActivity.mblnHayLocalizacion = true;

        //Llamamos a cálculo UTM
        mprincipalActivity.calculaUTM(loc);

        //Si hay punto de referencia seleccionado mostramos rumbo y distancia a él
        if (mprincipalActivity.mblnPunto) {
            Location locatnPunto = new Location("gps");
            String strCoordenada;
            Float fltCoordenada = new Float(0.0);

            strCoordenada = mprincipalActivity.mstrLatitudPtoRef;
            //No consigo usar Formatter correctamente, así que paso ',' a '.' usando func. de cadena
            locatnPunto.setLatitude(fltCoordenada.parseFloat(strCoordenada.replace(',' , '.')));

            strCoordenada = mprincipalActivity.mstrLongitudPtoRef;
            //No consigo usar Formatter correctamente, así que paso ',' a '.' usando func. de cadena
            locatnPunto.setLongitude(fltCoordenada.parseFloat(strCoordenada.replace(',' , '.')));

            float fltRumboHacia = loc.bearingTo(locatnPunto);
            /*Me volví loco comprobando que bearingTo() devuelve nº<0 cuando estamos al E o S del
             pto. de ref y hay que calcular 360-nº*/
            if (fltRumboHacia < 0) {
                fltRumboHacia = 360 + fltRumboHacia; //"Sumamos" pq en sí es negativo
            }

            mprincipalActivity.mtxtviwRumboHacia.setText(puntoCardinal(fltRumboHacia));
            mprincipalActivity.mtxtviwDistanciaHasta.setText(String.format("%.1f", loc.distanceTo(locatnPunto)));
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Este método se ejecuta cuando el GPS es desactivado
        mprincipalActivity.mtxtviwEstadoGPS.setText(String.format("%s desactivado", provider));
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Este método se ejecuta cuando el GPS es activado
        mprincipalActivity.mtxtviwEstadoGPS.setText(String.format("%s activado", provider));
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

        //ToDo: Usar directamente las etiquetas en vez del valor para legibilidad
        switch (status) {
            case 0:
                // OUT_OF_SERVICE
                mprincipalActivity.mtxtviwBitacora.setText(String.format("%s fuera de servicio", provider));
                break;
            case 1:
                //TEMPORARILY_UNAVAILABLE
                mprincipalActivity.mtxtviwBitacora.setText(String.format("%s temporalmente no disponible", provider));
                break;
            case 2:
                //AVAILABLE
                mprincipalActivity.mtxtviwBitacora.setText(String.format("%s disponilbe", provider));
                break;
        }
    }

    private String puntoCardinal (float fltAngulo) {

        String strAngulo = String.format("%03.1f", fltAngulo);
        String strPuntoCardinal = "Desconocido";

        //https://es.wikipedia.org/wiki/Rosa_de_los_vientos#Anexo:_Tabulaci.C3.B3n_angular_de_los_puntos_cardinales_por_cuadrantes
        if ((337.5 < fltAngulo && fltAngulo <= 360) || (0 <= fltAngulo && fltAngulo <= 22.5))
            strPuntoCardinal =  "N";
        else if (22.5 < fltAngulo && fltAngulo <= 67.5)
            strPuntoCardinal =  "NE";
        else if (67.5 < fltAngulo && fltAngulo <= 112.5)
            strPuntoCardinal =  "E";
        else if (112.5 < fltAngulo && fltAngulo <= 157.5)
            strPuntoCardinal =  "SE";
        else if (157.5 < fltAngulo && fltAngulo <= 202.5)
            strPuntoCardinal =  "S";
        else if (202.5 < fltAngulo && fltAngulo <= 247.5)
            strPuntoCardinal =  "SO";
        else if (247.5 < fltAngulo && fltAngulo <= 292.5)
            strPuntoCardinal =  "O";
        else if (292.5 < fltAngulo && fltAngulo <= 337.5)
            strPuntoCardinal =  "NO";
        //Cosa rara 1. Ángulo negativo
        else if (292.5 < fltAngulo && fltAngulo <= 337.5)
            strPuntoCardinal =  "¡<0!";
        //Cosa rara 2. Ángulo > 360
        else if (292.5 < fltAngulo && fltAngulo <= 337.5)
            strPuntoCardinal =  "!>360!";

        return strAngulo + " " + strPuntoCardinal;
    }
}
