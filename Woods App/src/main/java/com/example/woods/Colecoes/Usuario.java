package com.example.woods.Colecoes;

import com.google.firebase.firestore.FirebaseFirestore;

public class Usuario {
    private String id;
    private String email;
    private String nome;
    private String sobrenome;

    private String fotoURL;
    private int pontuacao;
    private boolean isVerificado;

    public Usuario() {
    }

    public Usuario(String id, String nome, String sobrenome, String email) {
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;

        this.fotoURL = "";
        this.pontuacao = 0;
        this.isVerificado = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getFotoURL() {
        return fotoURL;
    }

    public void setFotoURL(String fotoURL) {
        this.fotoURL = fotoURL;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public boolean isVerificado() {
        return isVerificado;
    }

    public void setVerificado(boolean verificado) {
        this.isVerificado = verificado;
    }

    public void save() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(getId()).set(this);
    }

    public void alterarPontuacao(int tag) {
        switch (tag) {
            case 1: // adicionar nova localização
                this.pontuacao += 5;
                break;

            case 2: // loc up
                this.pontuacao += 1;
                break;

            case 3: // localizacao verificada
                this.pontuacao += 25;

            case 4: // localizacao removida
                this.pontuacao -= 10;
        }
    }
}