package com.cefetcontagem.queroferias.model;

import com.cefetcontagem.queroferias.config.Base64config;

public class Prestador {

    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private String categoria;
    private String telefone;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String tipo;
    private String numeroAvaliadores;
    private String media;

    public Prestador(){}

    public String getTipo(){ return tipo; }
    public void setTipo(String tipo){ this.tipo = tipo; }

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

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }


    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getRua() {
        return rua;
    }
    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNumeroAvaliadores(){return numeroAvaliadores;}

    public void setNumeroAvaliadores(String numeroAvaliadores){this.numeroAvaliadores=numeroAvaliadores;}

    public String getMedia(){return media;}
    public void setMedia(String media){this.media=media;}

}
