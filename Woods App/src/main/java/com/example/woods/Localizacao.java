package com.example.woods;

public class Localizacao {
    private String idLocalizacao;
    private String nomeEspecie;
    private String fotoURL;
    private String idUsuarioResponsavel;
    private double latitude;
    private double longitude;
    private boolean isVerificado;

    public Localizacao() {
    }

    public Localizacao(String idLocalizacao, String nomeEspecie, String fotoURL, String idUsuarioResponsavel, double latitude, double longitude) {
        this.idLocalizacao = idLocalizacao;
        this.nomeEspecie = nomeEspecie;
        this.fotoURL = fotoURL;
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

    public String getFotoURL() {
        return fotoURL;
    }

    public void setFotoURL(String fotoURL) {
        this.fotoURL = fotoURL;
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
}
