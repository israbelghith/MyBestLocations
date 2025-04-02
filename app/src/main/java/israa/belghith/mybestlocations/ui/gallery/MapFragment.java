package israa.belghith.mybestlocations.ui.gallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;



import israa.belghith.mybestlocations.R;


import android.content.Intent;
import android.app.Activity;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



public class MapFragment extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_fragment);

        // Initialiser la carte
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Obtenir la position actuelle de l'utilisateur
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(latLng -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("latitude", latLng.latitude);
            resultIntent.putExtra("longitude", latLng.longitude);
            setResult(Activity.RESULT_OK, resultIntent);
            finish(); // Ferme l'activité et retourne les résultats
        });


        // Activer le bouton de localisation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Récupérer la dernière position connue
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                // Ajouter un marqueur et retourner les coordonnées
                mMap.setOnMapClickListener(latLng -> {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Position sélectionnée"));

                    // Retourner les coordonnées à l'activité précédente
                    Intent intent = new Intent();
                    intent.putExtra("latitude", latLng.latitude);
                    intent.putExtra("longitude", latLng.longitude);
                    setResult(RESULT_OK, intent);
                    finish();
                });
            }
        });
    }
}
