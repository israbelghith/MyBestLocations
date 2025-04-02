package israa.belghith.mybestlocations;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import israa.belghith.mybestlocations.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Définir la toolbar comme action bar
        setSupportActionBar(binding.appBarMain.toolbar);

        // Configuration du DrawerLayout et NavigationView
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Navigation Controller
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        // Configuration de la navigation avec le drawer
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery) // fragments que tu veux rendre accessibles
                .setOpenableLayout(drawer)
                .build();

        // Lier le NavController à la toolbar et le DrawerLayout
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Gérer les interactions avec le menu de navigation
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_gallery) {
                // Naviguer vers GalleryFragment
                navController.navigate(R.id.nav_gallery);
            }
            else if (id == R.id.nav_home) {
                // Naviguer vers GalleryFragment
                navController.navigate(R.id.nav_home);
            }

            // Fermer le drawer après sélection
            drawer.closeDrawers();
            return true;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Gérer la navigation vers le haut (retour en arrière)
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
