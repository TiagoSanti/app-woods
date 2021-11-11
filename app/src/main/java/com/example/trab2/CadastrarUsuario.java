package com.example.trab2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CadastrarUsuario extends AppCompatActivity {

    EditText edtUsername, edtSenha, edtConfirmarSenha;
    TextView txtErroCadastro;
    Button btnCriarConta;

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);

        db = new DBHelper(this);

        edtUsername = findViewById(R.id.edtUsernameCadastro);
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
                }
                else if(!senha.equals(confSenha)) {
                    txtErroCadastro.setText("Senhas não coincidem");
                }
                else {
                    if(db.verificarUsuario(username)) {
                        txtErroCadastro.setText("Nome de usuário já existente");
                    }
                    else {
                        db.inserir(username, senha);
                    }
                }
            }
        });
    }
}