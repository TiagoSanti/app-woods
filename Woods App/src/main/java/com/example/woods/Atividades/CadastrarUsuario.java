package com.example.woods.Atividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.woods.R;
import com.example.woods.Colecoes.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class CadastrarUsuario extends AppCompatActivity {

    private EditText edtNome, edtSobrenome, edtEmail, edtSenha, edtConfirmarSenha;
    private TextView txtErroCadastro;
    private Button btnCriarConta;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();

        edtNome = findViewById(R.id.edtNomeCadastro);
        edtSobrenome = findViewById(R.id.edtSobrenomeCadastro);
        edtEmail = findViewById(R.id.edtEmailCadastro);
        edtSenha = findViewById(R.id.edtSenhaCadastro);
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenhaCadastro);
        txtErroCadastro = findViewById(R.id.txtErroCadastro);
        btnCriarConta = findViewById(R.id.btnCriarConta);

        txtErroCadastro.setVisibility(View.INVISIBLE);

        btnCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome, sobrenome, email, senha, confSenha;

                nome = edtNome.getText().toString();
                sobrenome = edtSobrenome.getText().toString();
                email = edtEmail.getText().toString();
                senha = edtSenha.getText().toString();
                confSenha = edtConfirmarSenha.getText().toString();

                if(TextUtils.isEmpty(nome)
                        || TextUtils.isEmpty(sobrenome)
                        || TextUtils.isEmpty(email)
                        || TextUtils.isEmpty(senha)
                        || TextUtils.isEmpty(confSenha)) {
                    txtErroCadastro.setText(R.string.campos_nao_preenchidos);
                    txtErroCadastro.setVisibility(View.VISIBLE);
                }
                else if(!senha.equals(confSenha)) {
                    txtErroCadastro.setText(R.string.senhas_nao_coincidem);
                    txtErroCadastro.setVisibility(View.VISIBLE);
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Usuario usuario = new Usuario(mAuth.getUid(), nome, sobrenome, email);
                                usuario.save();

                                txtErroCadastro.setVisibility(View.INVISIBLE);

                                startActivity(new Intent(CadastrarUsuario.this, Main.class));
                                finish();
                            }
                            else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    txtErroCadastro.setText(R.string.senha_minimo);
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    txtErroCadastro.setText(R.string.email_invalido);
                                } catch (FirebaseAuthUserCollisionException e) {
                                    txtErroCadastro.setText(R.string.email_cadastrado);
                                } catch (Exception e) {
                                    txtErroCadastro.setText(R.string.erro_cadastro);
                                }
                                txtErroCadastro.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }
}