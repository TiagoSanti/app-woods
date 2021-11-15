package com.example.woods;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private EditText edtEmailLogin, edtSenha;
    private Button btnLogin;
    private TextView txtErroLogin, txtCadastro;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();

        edtEmailLogin = findViewById(R.id.edtEmailLogin);

        try {
            bundle = this.getIntent().getExtras();
            String userEmail;
            userEmail = bundle.get("userEmail").toString();
            edtEmailLogin.setText(userEmail);
        }
        catch (NullPointerException nullPointerException) {
            Log.e("dev_NPElogin", "NPElogin");
        }

        edtSenha = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);
        txtErroLogin = findViewById(R.id.txtErroLogin);
        txtCadastro = findViewById(R.id.txtCadastro);
        progressBar = findViewById(R.id.progressBar);

        txtErroLogin.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmailLogin.getText().toString();
                String senha = edtSenha.getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(senha)) {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                txtErroLogin.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(Login.this, MainActivity.class));
                                finish();
                            }
                            else {
                                progressBar.setVisibility(View.INVISIBLE);
                                txtErroLogin.setText("Email e/ou senha incorreto(s)");
                                txtErroLogin.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                else {
                    txtErroLogin.setText("Existe(m) campo(s) não preenchido(s)");
                    txtErroLogin.setVisibility(View.VISIBLE);
                }
            }
        });

        txtCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, CadastrarUsuario.class));
            }
        });
    }
}
