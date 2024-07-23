package com.zentoodevs.login.fragments;

import static android.content.Intent.getIntent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.zentoodevs.login.R;
import com.zentoodevs.login.activities.BusInfoActivity;
import com.zentoodevs.login.databinding.FragmentMapsBinding;
import com.zentoodevs.login.repository.models.Bus;
import com.zentoodevs.login.repository.models.Ruta;
import com.zentoodevs.login.test.MylocationManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private FragmentMapsBinding binding;
    private MylocationManager locationManager;
    private static final String API_KEY = "AIzaSyCUEMBKexwWyBH7IzJ6QbLZtLf_wfQvs_A";
    private double currentLatitude;
    private double currentLongitude;
    private final List<Polyline> polylinesx = new ArrayList<>();
    private Marker marker1;  // Marker para la ubicacion actual o inicial
    private Marker marker2;  // Marker para la ubicacion de destino
    private final List<Integer> colors = Arrays.asList(Color.parseColor("#4fb0c6"),Color.parseColor("#b0c64f"),Color.parseColor("#c64fb0"));
    private LinearLayout container;
    private final List<TextView> addedTextViews = new ArrayList<>();
    private int currentRouteIndex = 0;
    private String mParam1;
    private String mParam2;

    public MapsFragment() {
    }

    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = new MylocationManager(requireContext());

        // Solicita permisos de ubicación en tiempo real
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            getLocation();
        }

        // Inicializa Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), API_KEY);
        }
        container = view.findViewById(R.id.linearRoutesConteiner);

        Button btnAsistent = view.findViewById(R.id.buttonIaAssistent);
        btnAsistent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ChatAIFragment.class);
                String user = getArguments().getString("user");
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });

        binding.editTextEndLocation.setOnClickListener(this::startAutocompleteActivity);
        binding.EditTextStartLocation.setOnClickListener(this::startAutocompleteActivity2);
        // Obtener referencias a los campos EditText
        EditText editTextStartLocation = view.findViewById(R.id.EditTextStartLocation);
        EditText editTextEndLocation = view.findViewById(R.id.editTextEndLocation);

        // Configurar el OnClickListener para iniciar la actividad de autocompletado para el campo de destino
        editTextEndLocation.setOnClickListener(v -> startAutocompleteIntentLauncher.launch(getAutocompleteIntent()));

        // Configurar el OnClickListener para iniciar la actividad de autocompletado para el campo de origen
        editTextStartLocation.setOnClickListener(v -> startAutocompleteIntentLauncher2.launch(getAutocompleteIntent()));
        Bundle args = getArguments();

        if (getArguments() != null) {
            String startLocation = args.getString("startLocation");
            String endLocation = args.getString("endLocation");
            Log.d("xXDXDXDXDXDd",startLocation+endLocation);
            editTextStartLocation.setText(startLocation);
            editTextEndLocation.setText(endLocation);
            Button calculateRouteButton = view.findViewById(R.id.calcRoute);
            calculateRouteButton.setOnClickListener(v -> {
                if (!startLocation.isEmpty() && !endLocation.isEmpty()) {
                    // Si ambas direcciones están presentes, obtén las coordenadas y dibuja la ruta
                    getCoordinatesFromAddress(startLocation, true);
                    getCoordinatesFromAddress(endLocation, false);
                } else {
                    Toast.makeText(getContext(), "Por favor, ingrese ambas direcciones", Toast.LENGTH_SHORT).show();
                }
            });
            // Usa las ubicaciones como necesites
        }

    }
    private Intent getAutocompleteIntent() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        return new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountries(Collections.singletonList("PE"))
                .build(requireContext());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void getLocation() {
        locationManager.getCurrentLocation(new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                    Log.d("UBICACION", currentLatitude + ", " + currentLongitude);
                    setMarkerAtLocation(currentLatitude, currentLongitude, true);  // Marker 1
                    String address = getAddressFromLocation(currentLatitude, currentLongitude);
                    TextView txtCurrentLocation = getView().findViewById(R.id.textViewCurrentLocation);
                    txtCurrentLocation.setText(address);
                    EditText editTextStartLocation = getView().findViewById(R.id.EditTextStartLocation);
                    editTextStartLocation.setText(address);
                } else {
                    Toast.makeText(getContext(), "Ubicación no disponible", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setMarkerAtLocation(double latitude, double longitude, boolean isMarker1) {
        LatLng location = new LatLng(latitude, longitude);
        // Eliminar el marcador existente si se necesita
        if (isMarker1) {
            if (marker1 != null) {
                marker1.remove();
            }
            marker1 = mMap.addMarker(new MarkerOptions().position(location).title("Ubicación inicial"));
            drawRouteIfNeeded();
        } else {
            if (marker2 != null) {
                marker2.remove();
            }
            marker2 = mMap.addMarker(new MarkerOptions().position(location).title("Ubicación de destino"));
            drawRouteIfNeeded();

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 5); // Obtener hasta 5 resultados para mayor precisión
            for (Address address : addresses) {
                if (isStreetAddress(address)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        stringBuilder.append(address.getAddressLine(i)).append("\n");
                    }
                    return stringBuilder.toString().trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Dirección no disponible";
    }

    private void getCoordinatesFromAddress(String addressString,boolean whoMarker) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(addressString, 1);
            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getContext(), "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                return;
            }
            Address address = addresses.get(0);
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            setMarkerAtLocation(latitude, longitude, whoMarker);  // Marker 2
            Log.d("Geocoder", "Latitud: " + latitude + ", Longitud: " + longitude);
            Toast.makeText(getContext(), "Latitud: " + latitude + ", Longitud: " + longitude, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al obtener las coordenadas", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isStreetAddress(Address address) {
        return address.getThoroughfare() != null && address.getSubThoroughfare() != null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(getContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startAutocompleteActivity(View view) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountries(Collections.singletonList("PE"))
                .build(getContext());
        startAutocompleteIntentLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> startAutocompleteIntentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AutocompleteActivity.RESULT_OK) {
                    Intent data = result.getData();
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    if (place.getLatLng() != null) {
                        setMarkerAtLocation(place.getLatLng().latitude, place.getLatLng().longitude, false);  // Marker 2
                        EditText editTextEndLocation = getView().findViewById(R.id.editTextEndLocation);
                        editTextEndLocation.setText(place.getName());
                    }
                } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent(result.getData());
                    Log.e("FragmentMaps", "Error: " + status.getStatusMessage());
                }
            });

    public void startAutocompleteActivity2(View view) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountries(Collections.singletonList("PE"))
                .build(getContext());
        startAutocompleteIntentLauncher2.launch(intent);
    }

    private final ActivityResultLauncher<Intent> startAutocompleteIntentLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AutocompleteActivity.RESULT_OK) {
                    Intent data = result.getData();
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    if (place.getLatLng() != null) {
                        setMarkerAtLocation(place.getLatLng().latitude, place.getLatLng().longitude, true);  // Marker 1
                        EditText editTextStartLocation = getView().findViewById(R.id.EditTextStartLocation);
                        editTextStartLocation.setText(place.getName());
                    }
                } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent(result.getData());
                    Log.e("FragmentMaps", "Error: " + status.getStatusMessage());
                }
            });

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawRouteIfNeeded();
    }
    private void drawRouteIfNeeded() {
        // Verificar si se han establecido las ubicaciones de origen y destino
        if (marker1 != null && marker2 != null) {
            LatLng origin = marker1.getPosition();
            LatLng destination = marker2.getPosition();
            // Crear solicitud de direcciones entre el origen y el destino
            DirectionsResult directionsResult = getDirections(origin, destination);
            // Dibujar la ruta en el mapa
            if (directionsResult != null) {
                // Eliminar los TextView anteriores
                for (TextView textView : addedTextViews) {
                    container.removeView(textView);
                }
                addedTextViews.clear();
                List<Ruta> rutas = drawRoute(directionsResult);
                Button btnSig = getView().findViewById(R.id.next_route_button);
                // Definir un OnClickListener para el botón btnSig
                btnSig.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Incrementa el índice de la ruta actual, o reinícialo si se alcanza el límite
                        currentRouteIndex = (currentRouteIndex + 1) % rutas.size();
                        // Limpia la vista container antes de agregar los nuevos TextView
                        container.removeAllViews();
                        // Limpia las polilíneas anteriores del mapa
                        deletePolylines();
                        // Obtén la lista de autobuses y polilíneas de la ruta actual
                        List<Bus> buses = rutas.get(currentRouteIndex).getBuses();
                        List<PolylineOptions> polylines = rutas.get(currentRouteIndex).getPolylines();
                        // Muestra los autobuses de la ruta actual
                        for (Bus bus : buses) {
                            TextView txtAutobus = new TextView(getContext());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            int marginInDp = 3;
                            final float scale = getResources().getDisplayMetrics().density;
                            int marginInPx = (int) (marginInDp * scale + 0.5f);
                            params.setMargins(0, marginInPx, 0, 0);
                            txtAutobus.setLayoutParams(params);
                            txtAutobus.setText("Ruta: " + bus.getNameShort());
                            txtAutobus.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.custom_edittext2));
                            txtAutobus.setTextColor(bus.getColor());
                            txtAutobus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Iniciar ActivityBusInfo con los detalles del autobús
                                    Intent intent = new Intent(getContext(), BusInfoActivity.class);
                                    intent.putExtra("busName", bus.getNameShort());
                                    intent.putExtra("busDistance", bus.getDistance());
                                    intent.putExtra("busDuration", bus.getDuration());
                                    intent.putExtra("DurationAll",bus.getDurationAll());
                                    intent.putExtra("DistanceAll",bus.getDistanceAll());
                                    startActivity(intent);
                                }
                            });
                            Drawable drawableStart = ContextCompat.getDrawable(getContext(), R.drawable.baseline_directions_bus_24);
                            if (drawableStart != null) {
                                drawableStart.setTint(bus.getColor());
                                txtAutobus.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null);
                            }
                            container.addView(txtAutobus);
                        }
                        // Dibuja las polilíneas de la ruta actual en el mapa
                        for (PolylineOptions polylineOptions : polylines) {
                            Polyline polyline = mMap.addPolyline(polylineOptions);
                            polylinesx.add(polyline);
                        }
                    }
                });

            }
        }
    }

    private DirectionsResult getDirections(LatLng origin, LatLng destination) {
        try {
            GeoApiContext geoApiContext = new GeoApiContext.Builder()
                    .apiKey(API_KEY)
                    .build();

            // Realizar la solicitud de direcciones con modo de transporte "transit"
            return DirectionsApi.newRequest(geoApiContext)
                    .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                    .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                    .mode(TravelMode.TRANSIT) // Especificar "transit" como modo de transporte
                    .alternatives(true)
                    .await();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deletePolylines(){
        for (Polyline polyline : polylinesx) {
            polyline.remove();
        }
        polylinesx.clear();
    }

    private List<Ruta> drawRoute(DirectionsResult directionsResult) {
        List<Ruta> rutas = new ArrayList<>();
        for (DirectionsRoute route : directionsResult.routes) {
            deletePolylines();
            List<Bus> buses = new ArrayList<>(); // Lista de autobuses para la ruta actual
            List<PolylineOptions> polylines = new ArrayList<>(); // Lista de polilíneas para la ruta actual
            // Iterar sobre todas las patas de la ruta
            for (DirectionsLeg leg : route.legs) {
                // Iterar sobre todos los pasos de la pata
                for (DirectionsStep step : leg.steps) {
                    // Obtener el modo de viaje del paso
                    TravelMode travelMode = step.travelMode;
                    int color = getRouteColor(travelMode); // Valor por defecto
                    // Obtener el nombre corto y color del transporte público (si aplica)
                    if (travelMode == TravelMode.TRANSIT && step.transitDetails != null) {
                        String nameShort = step.transitDetails.line.shortName;
                        String distance = step.distance.toString();
                        String duration = step.duration.toString();
                        String durationAll = leg.duration.toString();
                        String distanceAll = leg.distance.toString();

                        Bus bus = new Bus(nameShort, distance, duration, color, durationAll, distanceAll);
                        buses.add(bus);
                        // Asignar color a los autobuses según su posición en la lista
                        if (buses.size() == 1) {
                            color = colors.get(0);
                            bus.setColor(colors.get(0));
                        } else if (buses.size() == 2) {
                            color = colors.get(1);
                            bus.setColor(color);
                        } else if (buses.size() == 3) {
                            color = colors.get(2);
                            bus.setColor(color);
                        }
                    }
                    // Obtener la polilínea del paso
                    EncodedPolyline encodedPolyline = step.polyline;
                    List<LatLng> decodedPath = PolyUtil.decode(encodedPolyline.getEncodedPath());
                    // Dibujar la polilínea en el mapa con el color correspondiente

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(decodedPath)
                            .color(color)
                            .width(7);
                    Polyline polyline = mMap.addPolyline(polylineOptions);
                    polylinesx.add(polyline);
                    polylines.add(polylineOptions);
                }
            }
            // Crear una nueva Ruta con la lista de autobuses y polilíneas, y agregarla a la lista de rutas
            rutas.add(new Ruta(buses, polylines));
        }
        return rutas;
    }

    private int getRouteColor(TravelMode travelMode) {
        // Asignar colores según el modo de viaje
        switch (travelMode) {
            case WALKING:
                return Color.GRAY;  // Color para caminar
            case TRANSIT:
                return Color.RED;   // Color para transporte público
            case DRIVING:
                return Color.BLACK; // Color para conducir
            default:
                return Color.GRAY;  // Color por defecto
        }
    }



}