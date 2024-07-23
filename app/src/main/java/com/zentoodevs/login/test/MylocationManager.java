package com.zentoodevs.login.test;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.util.List;

public class MylocationManager {
    private final Context mContext;
    private final FusedLocationProviderClient fusedLocationProviderClient;

    public MylocationManager(Context context) {
        mContext = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getCurrentLocation(final LocationCallback locationCallback) {
        // Verificar permisos de ubicación
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Los permisos no están otorgados, solicitarlos al usuario
            // Aquí podrías lanzar una excepción o devolver un valor nulo para manejar este caso en la actividad
            return;
        }

        // Crear una solicitud de ubicación
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Solicitar la ubicación actual
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) {
                            // Si no se puede obtener la ubicación, se puede manejar aquí
                            return;
                        }
                        // Obtener la lista de ubicaciones
                        List<Location> locations = locationResult.getLocations();
                        // Crear un objeto LocationResult con la lista de ubicaciones obtenidas
                        LocationResult result = LocationResult.create(locations);
                        // Llamar al callback con el LocationResult
                        locationCallback.onLocationResult(result);
                    }
                }, null);
    }



}
