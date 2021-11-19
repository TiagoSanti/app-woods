package com.example.woods;

public class Especie {
    private String idEspecie;
    private String nome;
    private String descricao;
    private String iconeURL;

    public Especie() {
    }

    public Especie(String nome, String descricao, String iconeURL) {
        this.nome = nome;
        this.descricao = descricao;
        this.iconeURL = iconeURL;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIdEspecie() {
        return idEspecie;
    }

    public void setIdEspecie(String idEspecie) {
        this.idEspecie = idEspecie;
    }

    public String getIconeURL() {
        return iconeURL;
    }

    public void setIconeURL(String iconeURL) {
        this.iconeURL = iconeURL;
    }
}