package com.cefetcontagem.queroferias.config;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    private static DatabaseReference refeenciaFirebase;




    public static DatabaseReference getFirebase(){

        if(refeenciaFirebase == null) {
            refeenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return refeenciaFirebase;

    }

}
