package israa.belghith.mybestlocations.ui.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import israa.belghith.mybestlocations.Config;
import israa.belghith.mybestlocations.JSONParser;
import israa.belghith.mybestlocations.R;
import israa.belghith.mybestlocations.databinding.FragmentGalleryBinding;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private ActivityResultLauncher<Intent> mapActivityResultLauncher;


    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> locationPermissionRequest;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

      /*  final TextView textView = binding.textGallery;
       galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/
        // Récupérer les champs de saisie
        EditText etPseudo = binding.etPseudo;
        EditText etNumero = binding.etNumero;
        EditText etLongitude = binding.etLongitude;
        EditText etLatitude = binding.etLatitude;
        Button btnEnregistrer = binding.btnEnregistrer;
        Button btnRetourHome = binding.btnRetour;
        Button btnAjouterPosition = binding.btnMap;

        // Navigation vers la page d'accueil lorsque le bouton "Retour à l'Accueil" est cliqué
        btnRetourHome.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.nav_home);  // Assurez-vous que l'ID est correct dans votre fichier de navigation
        });

        // Ajouter la position en utilisant la longitude et la latitude
      /*  btnAjouterPosition.setOnClickListener(v -> {
            // Remplacer le fragment actuel par le fragment de carte
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new MapFragment()); // Utiliser le bon ID du conteneur
            transaction.addToBackStack(null); // Ajouter à la pile d'historique pour permettre le retour
            transaction.commit();
        });*/
        btnAjouterPosition.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapFragment.class);  // Remplacez MapFragment par une activité réelle
            mapActivityResultLauncher.launch(intent);
        });



        mapActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        double latitude = data.getDoubleExtra("latitude", 0);
                        double longitude = data.getDoubleExtra("longitude", 0);
                        binding.etLatitude.setText(String.valueOf(latitude));
                        binding.etLongitude.setText(String.valueOf(longitude));
                    }
                }
        );
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        getLastKnownLocation();
                    } else {
                        Toast.makeText(getActivity(), "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
                    }
                }
        );



        btnEnregistrer.setOnClickListener(v -> {
            String pseudo = etPseudo.getText().toString().trim();
            String numero = etNumero.getText().toString().trim();
            String longitude = etLongitude.getText().toString().trim();
            String latitude = etLatitude.getText().toString().trim();

            if (pseudo.isEmpty() || numero.isEmpty() || longitude.isEmpty() || latitude.isEmpty()) {
                Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Envoyer les données
            envoyerPosition(pseudo, numero, longitude, latitude);
        });
        getLastKnownLocation();

        return root;
    }
    private void envoyerPosition(String pseudo, String numero, String longitude, String latitude) {
        new Thread(() -> {
            HashMap<String, String> params = new HashMap<>();
            params.put("pseudo", pseudo);
            params.put("numero", numero);
            params.put("longitude", longitude);
            params.put("latitude", latitude);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser.makeHttpRequest(Config.URL_ADDPOSITION, "POST", params);

            if (jsonObject != null) {
                try {
                    String message = jsonObject.getString("message");
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    binding.etLatitude.setText(String.valueOf(latitude));
                    binding.etLongitude.setText(String.valueOf(longitude));
                } else {
                    Toast.makeText(getActivity(), "Impossible d'obtenir la localisation", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}