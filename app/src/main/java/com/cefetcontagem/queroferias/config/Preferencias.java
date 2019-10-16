package com.cefetcontagem.queroferias.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Preferencias {
    private Context contexto;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private final String NOME_ARQUIVO = "servico.preferences";
    private final String CHAVE_NOME = "nome";
    private final String CHAVE_IDENTIFICADOR = "identificadorUsuario";
    private final String CHAVE_TIPO = "tipoUsuario";
    private final int MODE = 0;

    public Preferencias(Context c) {
        this.contexto = c;
        sharedPreferences = contexto.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = sharedPreferences.edit();

    }

    public void removerDados(){
        editor.clear();
        editor.commit();

    }

    public void salvarIdentificador(String identificador){
        editor.putString(CHAVE_IDENTIFICADOR, identificador);
        editor.commit();

    }

    public void salvarDados(String idUsuario, String nome, String tipo) {
        editor.putString(CHAVE_NOME, nome);
        editor.putString(CHAVE_IDENTIFICADOR, idUsuario);
        editor.putString(CHAVE_TIPO, tipo);
        editor.commit();

    }

    public String getIdentificador() {
        return sharedPreferences.getString(CHAVE_IDENTIFICADOR, null);

    }

    public String getNome() {
        return sharedPreferences.getString(CHAVE_NOME, null);

    }

    public String getTipo() {
        return sharedPreferences.getString(CHAVE_TIPO, null);

    }

}


