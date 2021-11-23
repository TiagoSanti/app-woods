package com.example.woods;

import com.google.firebase.firestore.FirebaseFirestore;

public class Especie {
    private String nome;

    public Especie() {
    }

    public Especie(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void save() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("especies").add(this);
    }
}