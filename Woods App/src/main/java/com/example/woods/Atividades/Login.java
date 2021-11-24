package com.example.woods.Atividades;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.woods.R;
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
                                startActivity(new Intent(Login.this, Main.class));
                                finish();
                            }
                            else {
                                progressBar.setVisibility(View.INVISIBLE);
                                txtErroLogin.setText(R.string.email_senha_incorreto);
                                txtErroLogin.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                else {
                    txtErroLogin.setText(R.string.campos_nao_preenchidos);
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