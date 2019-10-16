package com.cefetcontagem.queroferias.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cefetcontagem.queroferias.R;
import com.cefetcontagem.queroferias.config.Base64config;
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.model.Contrato;
import com.cefetcontagem.queroferias.model.Servico;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContratosPendentesAdapter extends ArrayAdapter<Contrato> {
    private Context context;
    private ArrayList<Contrato> contratos;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private TextView textNome;

    public ContratosPendentesAdapter(@NonNull Context c, @NonNull ArrayList<Contrato> objects) {
        super(c, 0, objects);
        this.context = c;
        this.contratos = objects;

        databaseReference = ConfiguracaoFirebase.getFirebase();

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if(contratos != null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_contratos_pendentes, parent, false);

            textNome = (TextView) view.findViewById(R.id.textContrato);

            Contrato contrato = contratos.get(position);
            String nome;
            String email = Base64config.descriptografa64(contrato.getIdCliente());

            textNome.setText(email);

/*
            databaseReference.child("CLiente").child(contrato.getIdCliente());

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildren() != null) {
                        Servico servico = dataSnapshot.getValue(Servico.class);
                        textNome.setText(servico.getServico());

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            databaseReference.addListenerForSingleValueEvent(valueEventListener);
            */
        }

        return view;

    }
}

