package com.example.trab2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

public class CadastrarUsuario extends AppCompatActivity {

    EditText edtUsername, edtSenha, edtConfirmarSenha;
    TextView txtErroCadastro;
    Button btnCriarConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);

        edtUsername = findViewById(R.id.edtEmailCadastro);
        edtSenha = findViewById(R.id.edtSenha);
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha);
        txtErroCadastro = findViewById(R.id.txtErroCadastro);
        btnCriarConta = findViewById(R.id.btnCriarConta);

        txtErroCadastro.setVisibility(View.INVISIBLE);

        btnCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, senha, confSenha;

                username = edtUsername.getText().toString();
                senha = edtSenha.getText().toString();
                confSenha = edtConfirmarSenha.getText().toString();

                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(senha) || TextUtils.isEmpty(confSenha)) {
                    txtErroCadastro.setText("Existe(m) campo(s) não preenchido(s)");
                    txtErroCadastro.setVisibility(View.VISIBLE);
                }
                else if(!senha.equals(confSenha)) {
                    txtErroCadastro.setText("Senhas não coincidem");
                    txtErroCadastro.setVisibility(View.VISIBLE);
                }
                //else if(EMAIL JÁ CADASTRADO) {

                // }
                else {
                    //CADASTRAR USUARIO
                }
            }
        });
    }
}