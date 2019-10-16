package com.cefetcontagem.queroferias.model;

import java.io.Serializable;

public class Servico implements Serializable{
    private String servico;
    private String categoria;
    private String preco;
    private String descricao;
    private String nome;
    private String identificador;
    private String idServico;
    private String media;

    public Servico(){}

    public String getIdServico() { return idServico; }

    public void setIdServico(String idServico){ this.idServico = idServico; }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getMedia(){
        return media;
    }

    public void setMedia(String media){this.media=media;}

}
