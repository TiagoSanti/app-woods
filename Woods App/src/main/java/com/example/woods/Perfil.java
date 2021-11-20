package com.example.woods;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Perfil extends Fragment {

    private Context context;

    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private final FirebaseFirestore dbReference = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private Usuario usuario;
    private FragmentManager fragmentManager;

    public Perfil() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        String userID = user.getUid();

        ImageView usuarioFoto = view.findViewById(R.id.imgUsuario);
        ImageView imgVerificado = view.findViewById(R.id.imgVerificado);
        TextView txtNomeSobrenome = view.findViewById(R.id.txtNomeSobrenomePerfil);
        TextView txtPontuacao = view.findViewById(R.id.txtPontuacaoPerfil);
        Button btnEditar = view.findViewById(R.id.btnEditarPerfil);

        documentReference = dbReference.collection("usuarios").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    usuario = documentSnapshot.toObject(Usuario.class);

                    if(!usuario.getFotoURL().isEmpty()) {
                        Glide.with(context).load(usuario.getFotoURL()).into(usuarioFoto);
                    }

                    txtNomeSobrenome.setText(usuario.getNome() + " " + usuario.getSobrenome());
                    txtPontuacao.setText("Pontuação: " + usuario.getPontuacao());

                    if(usuario.isVerificado()) {
                        imgVerificado.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, new EditarPerfil());
                transaction.commitNow();
            }
        });

        return view;
    }
}