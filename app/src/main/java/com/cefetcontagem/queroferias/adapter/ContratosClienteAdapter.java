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
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.model.Mensagem;
import com.cefetcontagem.queroferias.model.Servico;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContratosClienteAdapter extends ArrayAdapter<Mensagem> {
    private Context context;
    private ArrayList<Mensagem> mensagens;

    private TextView textContrato;
    private TextView textMensagem;
    private TextView textFornecedor;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private Mensagem mensagem;

    public ContratosClienteAdapter(@NonNull Context c, @NonNull ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context = c;
        this.mensagens = objects;

        //recuperaçao firebase: uma unica vez

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if(mensagens != null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_contratos_cliente, parent, false);

            mensagem = mensagens.get(position);

            textContrato = (TextView) view.findViewById(R.id.textContrato);
            textMensagem = (TextView) view.findViewById(R.id.textStatus);
            textFornecedor = (TextView) view.findViewById(R.id.textFornecedor);

            textMensagem.setText(mensagem.getMensagem());
            textContrato.setText("Status do contrato:");

            databaseReference = ConfiguracaoFirebase.getFirebase()
                    .child("ServicosDisponiveis");

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildren() != null){
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            Servico servico = data.getValue(Servico.class);
                            Log.i("valor", "msg.idServico: " + mensagem.getIdServico() + " - servico.idServico: " + servico.getIdServico());
                            if(mensagem.getIdServico().equals(servico.getIdServico())){
                                String fornecedor = servico.getNome() + " - " + servico.getServico();
                                textFornecedor.setText(fornecedor);
                            }
                        }

                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            databaseReference.addListenerForSingleValueEvent(valueEventListener);
        }
        return view;
    }



}
