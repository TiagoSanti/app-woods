package com.example.woods;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Localizacao {
    private String idLocalizacao;
    private String nomeEspecie;
    private String idUsuarioResponsavel;
    private double latitude;
    private double longitude;
    private boolean isVerificado;

    public Localizacao() {
    }

    public Localizacao(String nomeEspecie, String idUsuarioResponsavel, double latitude, double longitude) {
        this.nomeEspecie = nomeEspecie;
        this.idUsuarioResponsavel = idUsuarioResponsavel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isVerificado = false;
    }

    public String getIdLocalizacao() {
        return idLocalizacao;
    }

    public void setIdLocalizacao(String idLocalizacao) {
        this.idLocalizacao = idLocalizacao;
    }

    public String getNomeEspecie() {
        return nomeEspecie;
    }

    public void setNomeEspecie(String nomeEspecie) {
        this.nomeEspecie = nomeEspecie;
    }

    public String getIdUsuarioResponsavel() {
        return idUsuarioResponsavel;
    }

    public void setIdUsuarioResponsavel(String idUsuarioResponsavel) {
        this.idUsuarioResponsavel = idUsuarioResponsavel;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public boolean isVerificado() {
        return isVerificado;
    }

    public void setVerificado(boolean verificado) {
        isVerificado = verificado;
    }

    public void save() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("localizacoes").document();
        String id = ref.getId();
        this.setIdLocalizacao(id);
        db.collection("localizacoes").document(id).set(this);
    }
}
