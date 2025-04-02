package israa.belghith.mybestlocations.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import israa.belghith.mybestlocations.Config;
import israa.belghith.mybestlocations.JSONParser;
import israa.belghith.mybestlocations.Position;
import israa.belghith.mybestlocations.R;
import israa.belghith.mybestlocations.databinding.FragmentHomeBinding;
import israa.belghith.mybestlocations.ui.gallery.GalleryFragment;

public class HomeFragment extends Fragment {
    ArrayList<Position> data = new ArrayList<Position>();
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up the button click listener
        binding.btnloadId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Execute the download task
                new Telechargement().execute();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Refactored Telechargement class using ExecutorService
    class Telechargement {

        private AlertDialog alert;

        public void execute() {
            // Use an ExecutorService to handle the background task
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    // Background task logic here
                    onPreExecute();
                    doInBackground();
                    onPostExecute();
                }
            });
        }

        // Called before the background task starts
        private void onPreExecute() {
            // Show the loading dialog
            AlertDialog.Builder dialog = new AlertDialog.Builder(HomeFragment.this.getActivity());
            dialog.setTitle("Téléchargement");
            dialog.setMessage("Veuillez patienter");

            alert = dialog.create();
            alert.show();
        }

        // Background task logic
        private void doInBackground() {
            // Perform the network request (similar to what was in AsyncTask's doInBackground)
            JSONParser parser = new JSONParser();
            JSONObject result = parser.makeRequest(Config.URL_GETALL);

            // Log the response
            Log.d("response", result.toString());

            try {
                int success = result.getInt("success");
                if (success == 0) {
                    String message = result.getString("message");
                    Log.d("message", result.toString());
                } else {
                    JSONArray tableau = result.getJSONArray("positions");
                    data.clear();
                    for (int i = 0; i < tableau.length(); i++) {
                        JSONObject ligne = tableau.getJSONObject(i);

                        int idposition = ligne.getInt("idposition");
                        String pseudo = ligne.getString("pseudo");
                        String numero = ligne.getString("numero");
                        String longitude = ligne.getString("longitude");
                        String latitude = ligne.getString("latitude");
                        Position p = new Position(idposition, pseudo, numero, longitude, latitude);
                        data.add(p);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1500); // Simulating network delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Called on the UI thread after background work is done
        private void onPostExecute() {
            // Dismiss the loading dialog
            if (alert != null && alert.isShowing()) {
                alert.dismiss();
            }

            // Set the data to the list view
            ArrayAdapter<Position> ad = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_expandable_list_item_1, data);
            binding.listviewId.setAdapter(ad);
        }
    }
}
