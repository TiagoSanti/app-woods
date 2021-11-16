package com.example.woods;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditarPerfil extends Fragment {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private FragmentManager fragmentManager;

    public EditarPerfil() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_perfil, container, false);

        String userID = user.getUid();

        ImageView imgUsuario = view.findViewById(R.id.imgUsuarioEditar);
        EditText edtNome = view.findViewById(R.id.edtEditarNome);
        EditText edtSobrenome = view.findViewById(R.id.edtEditarSobrenome);
        EditText edtSenha = view.findViewById(R.id.edtNovaSenha);
        EditText edtRptSenha = view.findViewById(R.id.edtRepetirNovaSenha);
        TextView txtErro = view.findViewById(R.id.txtErroEditarPerfil);
        Button btnSalvar = view.findViewById(R.id.btnSalvarEditar);
        Button btnCancelar = view.findViewById(R.id.btnCancelarEdicao);

        reference.child("usuario").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                edtNome.setText(snapshot.child("nome").getValue().toString());
                edtSobrenome.setText(snapshot.child("sobrenome").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome, sobrenome, senha, rptSenha;

                nome = edtNome.getText().toString();
                sobrenome = edtSobrenome.getText().toString();
                senha = edtSenha.getText().toString();
                rptSenha = edtRptSenha.getText().toString();

                if(!TextUtils.isEmpty(nome) && !TextUtils.isEmpty(sobrenome)) {
                    if(!TextUtils.isEmpty(senha) && !TextUtils.isEmpty(rptSenha)) {
                        if(senha.equals(rptSenha)) {
                            if(senha.length() >= 6) {
                                txtErro.setVisibility(View.INVISIBLE);

                                reference.child("usuario").child(userID).child("nome").setValue(nome);
                                reference.child("usuario").child(userID).child("sobrenome").setValue(sobrenome);
                                user.updatePassword(senha);

                                voltarPerfil();
                            }
                            else {
                                txtErro.setText("A senha deve conter no mínimo 6 caracteres.");
                                txtErro.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            txtErro.setText("Senhas não coincidem.");
                            txtErro.setVisibility(View.VISIBLE);
                        }
                    }
                    else if(TextUtils.isEmpty(senha) ^ TextUtils.isEmpty(rptSenha)) {
                        txtErro.setText("Campos de senha invalidamente preenchidos.");
                        txtErro.setVisibility(View.VISIBLE);
                    }
                    else {
                        txtErro.setVisibility(View.INVISIBLE);

                        reference.child("usuario").child(userID).child("nome").setValue(nome);
                        reference.child("usuario").child(userID).child("sobrenome").setValue(sobrenome);

                        voltarPerfil();
                    }
                }
                else {
                    txtErro.setText("Os campos Nome e Sobrenome não podem estar vazios.");
                    txtErro.setVisibility(View.VISIBLE);
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltarPerfil();
            }
        });

        return view;
    }

    public void voltarPerfil() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, new Perfil(), "Perfil");
        transaction.commitNow();
    }
}