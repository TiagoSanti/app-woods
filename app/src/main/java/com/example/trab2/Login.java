package com.example.trab2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    EditText edtUsername, edtSenha;
    Button btnLogin;
    TextView txtErroLogin, txtCadastro;

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DBHelper(this);

        edtUsername = findViewById(R.id.edtUsernameLogin);
        edtSenha = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);
        txtErroLogin = findViewById(R.id.txtErroLogin);
        txtCadastro = findViewById(R.id.txtCadastro);

        txtErroLogin.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String senha = edtSenha.getText().toString();

                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(senha)) {
                    txtErroLogin.setVisibility(View.VISIBLE);
                    txtErroLogin.setText("Existe(m) campo(s) não preenchido(s)");
                }
                else {
                    if(db.verificarSenha(username, senha)) {
                        Toast.makeText(Login.this, "Login bem-sucedido", Toast.LENGTH_SHORT).show();
                        // CRIAR INTENT
                    }
                    else {
                        txtErroLogin.setVisibility(View.VISIBLE);
                        txtErroLogin.setText("Usuário ou senha incorreta");
                    }
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
