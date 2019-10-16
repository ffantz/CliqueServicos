package com.cefetcontagem.queroferias.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cefetcontagem.queroferias.activities.ContratosClienteActivity;
import com.cefetcontagem.queroferias.config.Base64config;
import com.cefetcontagem.queroferias.R;
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.cefetcontagem.queroferias.model.Cliente;
import com.cefetcontagem.queroferias.model.Conhecidos;
import com.cefetcontagem.queroferias.model.Indicadores;
import com.cefetcontagem.queroferias.model.Prestador;
import com.cefetcontagem.queroferias.model.PrestadorIndicado;
import com.cefetcontagem.queroferias.model.Servico;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class CategoriasClienteAdapter extends ArrayAdapter<Servico> {
    private Context context;
    private ArrayList<Servico> servicos;
    private ArrayList<Servico> servicosOriginal;
    private ArrayList<String> conhecidos;
    private ValueEventListener valueEventListener;

    private DatabaseReference databaseReference;
    private Servico servicoAux;
    private Prestador prestador;

    private Preferencias preferencias;

    private String identificador1,identificador2;

    private PrestadorIndicado prestadorIndicado;
    private ArrayList<PrestadorIndicado> prestadoresIndicados;

    private ArrayList<String> indicadores;

    private ArrayList<String> emailsPrestadores;

    public CategoriasClienteAdapter(@NonNull Context c,  @NonNull ArrayList<Servico> objects) {
        super(c,0, objects);
        this.context = c;
        this.servicos = objects;
        this.servicosOriginal = objects;

        preferencias = new Preferencias(c);
        identificador2 = preferencias.getIdentificador();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        emailsPrestadores = new ArrayList<String>();
        conhecidos = new ArrayList<String>();


        if(servicos != null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_categorias, parent, false);

            TextView textServico = (TextView) view.findViewById(R.id.textEmail);
            TextView textCategoria = (TextView) view.findViewById(R.id.textEndereco);
            TextView preco = (TextView) view.findViewById(R.id.textPreco);
            TextView nome = (TextView) view.findViewById(R.id.textNome);
            final RatingBar estrelas = (RatingBar) view.findViewById(R.id.estrelas);
            TextView indicacao = (TextView) view.findViewById(R.id.textindicacao);

            final Servico servico = servicos.get(position);
            servicoAux=servico;

            textServico.setText(servico.getServico());
            textCategoria.setText(servico.getCategoria());
            preco.setText(servico.getPreco());
            nome.setText(servico.getNome());

            Log.i("msggg",servico.getIdentificador());

            databaseReference = ConfiguracaoFirebase.getFirebase().child("Fornecedores");

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildren() != null){
                        for(DataSnapshot data: dataSnapshot.getChildren()) {
                            prestador = data.getValue(Prestador.class);

                            Base64config aux = new Base64config();
                            identificador1 = aux.criptografa64(prestador.getEmail());
                            emailsPrestadores.add(identificador1);

                            if(servico.getIdentificador().equals(identificador1)) {
                                float media = Float.parseFloat(prestador.getMedia());
                                estrelas.setRating(media);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            };

            databaseReference.addListenerForSingleValueEvent(valueEventListener);




            databaseReference =  ConfiguracaoFirebase.getFirebase().child(identificador2).child("Conhecidos");
            Log.i("msg", identificador2);

            ValueEventListener valueEventListener2 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildren() != null){
                        for(DataSnapshot data: dataSnapshot.getChildren()) {
                            Conhecidos conhecido = data.getValue(Conhecidos.class);
                            String conhecidoFinal= conhecido.getEmailConhecido();
                            conhecidos.add(conhecidoFinal);

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            databaseReference.addListenerForSingleValueEvent(valueEventListener2);

            //----------------------------------------------------------------------------------



            for(int z=0;z<servicos.size();z++){
                indicacao(servicos.get(z),servicos.get(z).getIdentificador());
            }



        }

        Collections.sort(prestadoresIndicados);

        return view;

    }

    public void indicacao(final Servico servico, final String identificador){


        prestadoresIndicados = new ArrayList<PrestadorIndicado>();



        DatabaseReference Referenceindicado = ConfiguracaoFirebase.getFirebase().
                child("Indicacoes").child(servico.getCategoria()).child(identificador);

        ValueEventListener valueEventListener3 = new ValueEventListener() {
            int i=0;
            int qtIndicadores = 0;
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String str = data.getKey();
                        for(int i=0;i<conhecidos.size();i++){
                            if(data.hasChild(conhecidos.get(i))){
                                qtIndicadores++;
                            }
                        }
                    }

                    prestadorIndicado = new PrestadorIndicado();
                    prestadorIndicado.setId(identificador);
                    prestadorIndicado.setQtIndicacoes(String.valueOf(qtIndicadores));
                    prestadorIndicado.setServico(servico);

                    prestadoresIndicados.add(prestadorIndicado);



                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        Referenceindicado.addListenerForSingleValueEvent(valueEventListener3);



    }
}