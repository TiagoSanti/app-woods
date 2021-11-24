package com.example.woods.Fragmentos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.woods.Colecoes.Localizacao;
import com.example.woods.R;
import com.example.woods.Colecoes.Usuario;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Maps extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    int TAG_CODE_PERMISSION_LOCATION;
    float LOCATION_REFRESH_DISTANCE = 1;
    long LOCATION_REFRESH_TIME = 100;

    private Activity activity;
    private Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Marker marker;
    List<Marker> markers = new ArrayList<>();
    private FusedLocationProviderClient client;
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            getCurrentLocation();
        }
    };
    private LatLng myLocation;
    private Button btnFiltrarCancelar, btnAddLoc, btnPermitirLoc;
    private boolean adicionarLoc = false;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        context = activity.getApplicationContext();

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        btnFiltrarCancelar = view.findViewById(R.id.btnFiltrarCancelar);
        btnAddLoc = view.findViewById(R.id.btnAdicionarLoc);
        btnPermitirLoc = view.findViewById(R.id.btnPermitirLoc);

        if (checkLocPermission()) {
            btnPermitirLoc.setVisibility(View.INVISIBLE);
        }

        btnAddLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocPermission()) {
                    if (!adicionarLoc) {
                        adicionarLoc = true;
                        marker = null;

                        btnFiltrarCancelar.setText("Cancelar");
                        btnAddLoc.setText("Prosseguir");
                        btnPermitirLoc.setVisibility(View.INVISIBLE);

                        moveMapToLocation(myLocation, 19);
                    } else {
                        if (marker != null) {
                            createLocAddWindow();
                        } else {
                            // TODO pedir para adicionar um marcador
                        }
                    }
                } else {
                    // TODO pedir permissão de novo
                }
            }
        });

        btnFiltrarCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adicionarLoc) {
                    btnFiltrarCancelar.setText("Filtrar");
                    btnAddLoc.setText("Adicionar Localização");

                    moveMapToLocation(myLocation, 13);

                    adicionarLoc = false;
                    if (marker != null) {
                        marker.remove();
                        marker = null;
                    }
                } else {
                    createFilterWindow();
                }
            }
        });

        btnPermitirLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askLocPermission();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapClickListener(this);

        client = LocationServices.getFusedLocationProviderClient(context);

        if (checkLocPermission()) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            moveToCurrentLocation();
        } else {
            askLocPermission();
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        double lat = latLng.latitude;
        double lon = latLng.longitude;
        LatLng latLngClick = new LatLng(lat, lon);

        if (adicionarLoc) {
            try {
                marker.remove();
                marker = mMap.addMarker(new MarkerOptions().position(latLngClick));
            } catch (NullPointerException e) {
                marker = mMap.addMarker(new MarkerOptions().position(latLngClick));
            }
        }
    }

    public void createFilterWindow() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog;

        final View view = getLayoutInflater().inflate(R.layout.popup_filtro, null);

        Spinner spinner = view.findViewById(R.id.spinnerFiltro);
        Button btnConfirmar = view.findViewById(R.id.btnConfirmarFiltro);

        final String[] especie = new String[1];
        ArrayList<String> especiesArray = getArrayEspecies();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.spinner,
                especiesArray);
        spinner.setAdapter(adapter);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                especie[0] = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Marker> markers = addMarkers(especie[0]);
                dialog.dismiss();
            }
        });
    }

    public void createLocAddWindow() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog;
        final String[] especie = new String[1];
        ArrayList<String> especiesArray;
        ArrayAdapter<String> adapter;

        final View view = getLayoutInflater().inflate(R.layout.popup_add, null);

        TextView txtErro = view.findViewById(R.id.txtErroAddLoc);
        EditText edtNomeEspecie = view.findViewById(R.id.edtNomeEspecie);
        Button btnVoltar = view.findViewById(R.id.btnVoltarAddLoc);
        Button btnConfirmarLoc = view.findViewById(R.id.btnConfirmarAddLoc);
        Spinner spinnerEspecies = view.findViewById(R.id.spinnerEspecies);

        especiesArray = getArrayEspecies();

        adapter = new ArrayAdapter<String>(context,
                R.layout.spinner,
                especiesArray);
        spinnerEspecies.setAdapter(adapter);
        spinnerEspecies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                especie[0] = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        btnConfirmarLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEspecie = edtNomeEspecie.getText().toString();

                if (especie[0].equals("Nenhum") && inputEspecie.isEmpty()) {
                    txtErro.setText("Nenhuma espécie foi selecionada.");
                    txtErro.setVisibility(View.VISIBLE);
                } else if (!especie[0].equals("Nenhum") && !inputEspecie.isEmpty()) {
                    txtErro.setText("Selecione uma especie existente OU adicione uma nova espécie.");
                    txtErro.setVisibility(View.VISIBLE);
                } else {
                    txtErro.setVisibility(View.INVISIBLE);

                    Localizacao localizacao;
                    String nomeEspecie;
                    if (!especie[0].equals("Nenhum")) {
                        nomeEspecie = especie[0];
                    } else {
                        nomeEspecie = inputEspecie;
                    }
                    String idUsuarioResponsavel = FirebaseAuth.getInstance().getUid();
                    double latitude = marker.getPosition().latitude;
                    double longitude = marker.getPosition().longitude;

                    localizacao = new Localizacao(nomeEspecie, idUsuarioResponsavel, latitude, longitude);
                    localizacao.save();

                    documentReference = db.collection("usuarios").document(idUsuarioResponsavel);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Usuario usuario = documentSnapshot.toObject(Usuario.class);
                            usuario.alterarPontuacao(1);
                            usuario.save();
                        }
                    });

                    if (marker != null) {
                        marker.remove();
                        marker = null;
                    }
                    btnFiltrarCancelar.setText("Filtrar");
                    btnAddLoc.setText("Adicionar Localização");
                    dialog.dismiss();
                }
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private List<Marker> addMarkers(String especieFiltro) {
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).remove();
        }

        Random rnd = new Random();
        int color = rnd.nextInt(256);

        db.collection("localizacoes")
                .whereEqualTo("nomeEspecie", especieFiltro)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document != null) {
                                    Location location = new Location("marker");
                                    location.setLatitude((double) document.get("latitude"));
                                    location.setLongitude((double) document.get("longitude"));

                                    if (myLocation != null) {
                                        Location myLoc = new Location("my loc");
                                        myLoc.setLatitude(myLocation.latitude);
                                        myLoc.setLongitude(myLocation.longitude);

                                        double distance = myLoc.distanceTo(location);

                                        if (distance < 12000) {
                                            markers.add(mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                                    .title(especieFiltro)
                                                    .icon(BitmapDescriptorFactory.defaultMarker(color))));
                                        }
                                    }
                                }
                            }
                        }
                    }
                });

        return markers;
    }

    private ArrayList<String> getArrayEspecies() {
        ArrayList<String> especiesArray = new ArrayList<>();

        especiesArray.add("Nenhum");

        db.collection("especies")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                especiesArray.add(document.getId());
                            }
                        }
                    }
                });

        return especiesArray;
    }

    private void moveMapToLocation(LatLng latLng, int zoom) {
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    if (latLng != null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
                    }
                }
            });
        }
    }

    private void getCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }

    private void moveToCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                myLocation = latLng;
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                            }
                        });
                    }
                }
            }
        });
    }

    public boolean checkLocPermission() {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;
    }

    public void askLocPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                TAG_CODE_PERMISSION_LOCATION);
    }
}