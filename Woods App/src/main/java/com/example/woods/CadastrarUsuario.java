package com.example.woods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class CadastrarUsuario extends AppCompatActivity {

    private EditText edtEmail, edtSenha, edtConfirmarSenha;
    private TextView txtErroCadastro;
    private Button btnCriarConta;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmailCadastro);
        edtSenha = findViewById(R.id.edtSenhaCadastro);
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha);
        txtErroCadastro = findViewById(R.id.txtErroCadastro);
        btnCriarConta = findViewById(R.id.btnCriarConta);

        txtErroCadastro.setVisibility(View.INVISIBLE);

        btnCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, senha, confSenha;

                email = edtEmail.getText().toString();
                senha = edtSenha.getText().toString();
                confSenha = edtConfirmarSenha.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(senha) || TextUtils.isEmpty(confSenha)) {
                    txtErroCadastro.setText("Existe(m) campo(s) não preenchido(s)");
                    txtErroCadastro.setVisibility(View.VISIBLE);
                }
                else if(!senha.equals(confSenha)) {
                    txtErroCadastro.setText("Senhas não coincidem");
                    txtErroCadastro.setVisibility(View.VISIBLE);
                }
                else {

                    mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                txtErroCadastro.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(CadastrarUsuario.this, MainActivity.class));
                                finish();
                            }
                            else {
                                String erro = task.getException().getMessage();

                                if(erro.equals("The email address is already in use by another account.")) {
                                    txtErroCadastro.setText("O email inserido já está cadastrado.");
                                    txtErroCadastro.setVisibility(View.VISIBLE);
                                }
                                else if(erro.equals("The given password is invalid. [ Password should be at least 6 characters ]")){
                                    txtErroCadastro.setText("A senha deve conter, pelo menos, 6 caracteres.");
                                    txtErroCadastro.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}