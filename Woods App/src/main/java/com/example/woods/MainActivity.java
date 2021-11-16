package com.example.woods;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private FragmentManager fragmentManager;

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

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, new Maps(), "Maps");
        transaction.commitNow();
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
        FragmentTransaction transaction;

        switch (id) {
            case R.id.mapa:
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, new Maps(), "Maps");
                transaction.commitNow();

                break;

            case R.id.classificacao:
                Toast.makeText(getApplicationContext(), "CLASSIFICAÇÃO", Toast.LENGTH_LONG).show();

                break;

            case R.id.frag_perfil:
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, new Perfil(), "Perfil");
                transaction.commitNow();

                break;

            case R.id.sair:
                Intent it = new Intent(MainActivity.this, Login.class);
                FirebaseUser currentUser = mAuth.getCurrentUser();

                it.putExtra("userEmail", currentUser.getEmail());

                mAuth.signOut();

                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);

                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}