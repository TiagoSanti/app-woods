package com.example.woods;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser == null) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.mapa:
                Toast.makeText(getApplicationContext(), "MAPA", Toast.LENGTH_LONG).show();

                break;

            case R.id.classificacao:
                Toast.makeText(getApplicationContext(), "CLASSIFICAÇÃO", Toast.LENGTH_LONG).show();

                break;

            case R.id.perfil:
                Toast.makeText(getApplicationContext(), "PERFIL", Toast.LENGTH_LONG).show();

                break;

            case R.id.sair:
                Intent it = new Intent(MainActivity.this, Login.class);
                FirebaseUser currentUser = mAuth.getCurrentUser();

                it.putExtra("userEmail", currentUser.getEmail());
                Log.e("dev_userEmail", currentUser.getEmail());

                mAuth.signOut();

                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);

                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}