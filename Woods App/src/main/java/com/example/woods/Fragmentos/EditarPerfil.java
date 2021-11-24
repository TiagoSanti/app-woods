package com.example.woods.Fragmentos;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.bumptech.glide.Glide;
import com.example.woods.R;
import com.example.woods.Colecoes.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditarPerfil extends Fragment {

    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private final FirebaseFirestore dbReference = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private Usuario usuario;

    private FragmentManager fragmentManager;
    private Uri mImageUri;

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
        Button btnCarregarFoto = view.findViewById(R.id.btnCarregarFotoUsuario);
        Button btnSalvar = view.findViewById(R.id.btnSalvarEditar);
        Button btnCancelar = view.findViewById(R.id.btnCancelarEdicao);

        documentReference = dbReference.collection("usuarios").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                usuario = documentSnapshot.toObject(Usuario.class);

                if(!usuario.getFotoURL().isEmpty()) {
                    Glide.with(getActivity().getApplicationContext()).load(usuario.getFotoURL()).into(imgUsuario);
                }

                edtNome.setText(usuario.getNome());
                edtSobrenome.setText(usuario.getSobrenome());
            }
        });

        ActivityResultLauncher<String> mGetContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                mImageUri = result;
                imgUsuario.setImageURI(mImageUri);
            }
        });

        btnCarregarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
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

                checkInfo(txtErro, nome, sobrenome, senha, rptSenha);
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

    private void checkInfo(TextView txtErro, String nome, String sobrenome, String senha, String rptSenha) {
        if(!TextUtils.isEmpty(nome) && !TextUtils.isEmpty(sobrenome)) {
            if(!TextUtils.isEmpty(senha) && !TextUtils.isEmpty(rptSenha)) {
                if(senha.equals(rptSenha)) {
                    if(senha.length() >= 6) {
                        txtErro.setVisibility(View.INVISIBLE);

                        updateUsuario(nome, sobrenome);
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

                updateUsuario(nome, sobrenome);

                voltarPerfil();
            }
        }
        else {
            txtErro.setText("Os campos Nome e Sobrenome não podem estar vazios.");
            txtErro.setVisibility(View.VISIBLE);
        }
    }

    public void updateUsuario(String nome, String sobrenome) {
        usuario.setNome(nome);
        usuario.setSobrenome(sobrenome);

        if(mImageUri != null) {
            StorageReference imgNome = storageReference.child("FotosUsuarios").child(usuario.getId());
            imgNome.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {
                        imgNome.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()) {
                                    usuario.setFotoURL(task.getResult().toString());
                                    documentReference.set(usuario);
                                }
                            }
                        });
                    }
                }
            });
        }
        else {
            documentReference.set(usuario);
        }
    }

    public void voltarPerfil() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, new Perfil());
        transaction.commitNow();
    }
}