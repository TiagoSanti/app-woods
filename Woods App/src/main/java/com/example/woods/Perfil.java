package com.example.woods;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Perfil extends Fragment {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private FragmentManager fragmentManager;

    public Perfil() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        String userID = user.getUid();

        ImageView usuarioFoto = (ImageView) view.findViewById(R.id.imgUsuario);
        ImageView imgVerificado = (ImageView) view.findViewById(R.id.imgVerificado);
        TextView txtNomeSobrenome = (TextView) view.findViewById(R.id.txtNomeSobrenomePerfil);
        TextView txtPontuacao = (TextView) view.findViewById(R.id.txtPontuacaoPerfil);
        Button btnEditar = (Button) view.findViewById(R.id.btnEditarPerfil);

        reference.child("usuario").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    String nome, sobrenome, pontuacao;
                    Boolean verificado;

                    nome = snapshot.child("nome").getValue().toString();
                    sobrenome = snapshot.child("sobrenome").getValue().toString();
                    pontuacao = snapshot.child("pontuacao").getValue().toString();
                    verificado = (Boolean) snapshot.child("verificado").getValue();

                    txtNomeSobrenome.setText(nome + " " + sobrenome);
                    txtPontuacao.setText("Pontuação: " + pontuacao);

                    if(verificado) {
                        imgVerificado.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, new EditarPerfil(), "Editar Perfil");
                transaction.commitNow();
            }
        });

        return view;
    }
}