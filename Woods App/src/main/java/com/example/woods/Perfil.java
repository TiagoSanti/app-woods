package com.example.woods;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Perfil extends Fragment {

    private Context context;

    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        RecyclerView RV = view.findViewById(R.id.recyclerLoc);

        List<Localizacao> listLoc = new ArrayList<>();
        listLoc = getListLoc();

        final Handler handler = new Handler(Looper.getMainLooper());
        List<Localizacao> finalListLoc = listLoc;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RV.setLayoutManager(new LinearLayoutManager(context));
                RecyclerView.Adapter RVAdapter = new RVAdapterLocalizacoes(finalListLoc);
                RV.setAdapter(RVAdapter);
            }
        }, 1000);


        documentReference = db.collection("usuarios").document(userID);
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

    private List<Localizacao> getListLoc() {
        List<Localizacao> listLoc = new ArrayList<>();

        db.collection("localizacoes")
                .whereEqualTo("idUsuarioResponsavel", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listLoc.add(document.toObject(Localizacao.class));
                                Log.d("santi_userLocsSize 1", ""+listLoc.size());
                            }


                        }
                    }
                });
        return listLoc;
    }
}