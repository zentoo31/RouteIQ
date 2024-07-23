package com.zentoodevs.login.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zentoodevs.login.fragments.MapsFragment;
import com.zentoodevs.login.R;
import com.zentoodevs.login.fragments.cameraFragment;
import com.zentoodevs.login.fragments.walletFragment;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();
        String user = intent.getStringExtra("user");

        // Crear el bundle con los datos del usuario
        Bundle bundle = new Bundle();
        bundle.putString("user", user);

        // Inicializar el fragmento con los datos
        MapsFragment mapsFragment = new MapsFragment();
        mapsFragment.setArguments(bundle);

        String startLocation = intent.getStringExtra("startLocation");
        String endLocation = intent.getStringExtra("endLocation");
        bundle.putString("startLocation", startLocation);
        bundle.putString("endLocation", endLocation);
        // Reemplazar el fragmento
        replaceFragment(mapsFragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.rutaFragment) {
                // Pasar los datos del usuario al fragmento
                MapsFragment fragment = new MapsFragment();
                fragment.setArguments(bundle);
                replaceFragment(fragment);
                return true;
            } else if (item.getItemId() == R.id.scanFragment) {
                // Pasar los datos del usuario al fragmento
                cameraFragment fragment = new cameraFragment();
                fragment.setArguments(bundle);
                replaceFragment(fragment);
                return true;
            } else if (item.getItemId() == R.id.walletFragment) {
                // Pasar los datos del usuario al fragmento
                walletFragment fragment = new walletFragment();
                fragment.setArguments(bundle);
                replaceFragment(fragment);
                return true;
            }
            return false;
        });


    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main, fragment);
        fragmentTransaction.commit();
    }
}