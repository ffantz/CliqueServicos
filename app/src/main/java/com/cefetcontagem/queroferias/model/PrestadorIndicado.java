package com.cefetcontagem.queroferias.model;

public class PrestadorIndicado implements Comparable<PrestadorIndicado>{

    private String qtIndicacoes;
    private String id;
    private Servico servico;

    public void PrestadorIndicado(){}

    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
    }

    public String getQtIndicacoes() { return qtIndicacoes; }
    public void setQtIndicacoes(String qtIndicacoes) { this.qtIndicacoes = qtIndicacoes; }

    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }

    public int compareTo(PrestadorIndicado prest) {
        // TODO Auto-generated method stub

        int qtIndicacoesInt = Integer.parseInt(qtIndicacoes);
        int qtIndicacoesAux = Integer.parseInt(prest.getQtIndicacoes());

        if(qtIndicacoesInt<qtIndicacoesAux) {
            return 1;
        }
        return -1;

    }


}
