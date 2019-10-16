package com.cefetcontagem.queroferias.config;

import android.util.Base64;

public class Base64config {

    public static String criptografa64(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).
                replaceAll("(\\n|\\r)","");
    }

    public static String descriptografa64(String texto){
        return new String(Base64.decode(texto, Base64.DEFAULT));
    }

}
