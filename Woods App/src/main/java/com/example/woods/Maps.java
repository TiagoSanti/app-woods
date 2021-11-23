package com.example.woods;

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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class Maps extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    int TAG_CODE_PERMISSION_LOCATION;
    float LOCATION_REFRESH_DISTANCE = 1;
    long LOCATION_REFRESH_TIME = 100;

    private Activity activity;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private Query query;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Marker marker;
    private FusedLocationProviderClient client;
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            getCurrentLocation();

            if(myLocation != null) {
                //Log.i("santi_CurrLoc", myLocation.toString());
            }
        }
    };
    private LatLng latLngClick, myLocation;
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

        if(checkLocPermission()) {
            btnPermitirLoc.setVisibility(View.INVISIBLE);
        }

        btnAddLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLocPermission()) {
                    if(!adicionarLoc) {
                        adicionarLoc = true;
                        marker = null;
                        Log.i("santi_addLocClick", "Adicionar Loc " + adicionarLoc);

                        btnFiltrarCancelar.setText("Cancelar");
                        btnAddLoc.setText("Prosseguir");
                        btnPermitirLoc.setVisibility(View.INVISIBLE);

                        moveMapToLocation(myLocation, 19);
                    } else {
                        if(marker != null) {
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
                if(adicionarLoc) {
                    btnFiltrarCancelar.setText("Filtrar");
                    btnAddLoc.setText("Adicionar Localização");

                    moveMapToLocation(myLocation, 13);

                    adicionarLoc = false;
                    if(marker != null) {
                        marker.remove();
                        marker = null;
                    }
                } else {
                    // TODO implementar filtro
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

        addBoundMarkers();
        getArrayLoc("Aceroleira");

        // Permissão para acessar a localização
        if(checkLocPermission()) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            moveToCurrentLocation();
        } else {
            //shouldShowRequestPermissionRationale("Sua experiência com o aplicativo será limitada porque não poderemos fornecer todos os nossos recursos sem a permissão de localizá-lo. Deseja autorizar o rastreio?");
            askLocPermission();


            if(checkLocPermission()) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                moveToCurrentLocation();
            } else {
                // TODO pedir permissão de novo
            }
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        double lat = latLng.latitude;
        double lon = latLng.longitude;
        latLngClick = new LatLng(lat, lon);

        if(adicionarLoc) {
            try {
                marker.remove();
                marker = mMap.addMarker(new MarkerOptions().position(latLngClick));
            } catch (NullPointerException e) {
                marker = mMap.addMarker(new MarkerOptions().position(latLngClick));
            }
        }
    }

    public void createLocAddWindow() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog;
        final String[] especie = new String[1];
        ArrayList<String> especiesArray;
        ArrayAdapter<String> adapter;

        final View view = getLayoutInflater().inflate(R.layout.popup, null);

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
                //Log.i("santi_item", "Selected item: " + especie[0]);
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

                if(especie[0].equals("Nenhum") && inputEspecie.isEmpty()) {
                    txtErro.setText("Nenhuma espécie foi selecionada.");
                    txtErro.setVisibility(View.VISIBLE);
                } else if(!especie[0].equals("Nenhum") && !inputEspecie.isEmpty()) {
                    txtErro.setText("Selecione uma especie existente OU adicione uma nova espécie.");
                    txtErro.setVisibility(View.VISIBLE);
                } else {
                    txtErro.setVisibility(View.INVISIBLE);

                    Localizacao localizacao;
                    String nomeEspecie;
                    if(!especie[0].equals("Nenhum")) {
                        nomeEspecie = especie[0];
                    } else {
                        nomeEspecie = inputEspecie;
                    }
                    String fotoURL = "";
                    String idUsuarioResponsavel = FirebaseAuth.getInstance().getUid();
                    double latitude = marker.getPosition().latitude;
                    double longitude = marker.getPosition().longitude;

                    localizacao = new Localizacao(nomeEspecie, fotoURL, idUsuarioResponsavel, latitude, longitude);
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
                    Log.i("santi_locAddSuccess", "Loc Add Success!");

                    if(marker != null) {
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

    private void addBoundMarkers() {
        db.collection("localizacoes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Location location = new Location("marker");
                                location.setLatitude((double) document.get("latitude"));
                                location.setLongitude((double) document.get("longitude"));

                                Location myLoc = new Location("my loc");
                                myLoc.setLatitude(myLocation.latitude);
                                myLoc.setLongitude(myLocation.longitude);

                                double distance = myLoc.distanceTo(location);

                                Log.i("santi_locDistance", "distance: " + distance);
                                if(distance < 2000) {

                                }
                            }
                        }
                    }
                });
    }

    private ArrayList<String> getArrayLoc(String especie) {
        ArrayList<String> locArray = new ArrayList<>();
        final DocumentReference[] documentReference = new DocumentReference[1];

        db.collection("localizacoes")
                .whereEqualTo("nomeEspecie", especie)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                locArray.add(document.getId());
                                Log.d("santi_locId", "Loc dog id: " + document.getId());

                            }
                        }
                    }
                });

        return locArray;
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
                        } else {
                            Log.d("santi_especiesError", "Error getting documents: ", task.getException());
                        }
                    }
                });

        return especiesArray;
    }

    private void moveMapToLocation(LatLng latLng, int zoom) {
        if(mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    if(latLng != null) {
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
                if(location != null) {
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
                if(location != null) {
                    if(mapFragment != null) {
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
        ActivityCompat.requestPermissions(activity, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION },
                TAG_CODE_PERMISSION_LOCATION);
    }

    /*
    public void showLocPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setCancelable(true);
        builder.setTitle("Permissão de rastreio negada");

        builder.setMessage("Sua experiência com o aplicativo será limitada porque não poderemos " +
                "fornecer todos os nossos recursos sem a permissão de localizá-lo. " +
                "Deseja autorizar o rastreio?");

        builder.setPositiveButton("Confirmar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        askLocPermission();
                        Log.i("santi_confirm", "confirm permission -> ask again");
                    }
                });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }
     */
}