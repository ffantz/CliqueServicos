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
import com.cefetcontagem.queroferias.model.Cliente;
import com.cefetcontagem.queroferias.model.Contrato;
import com.cefetcontagem.queroferias.model.Mensagem;
import com.cefetcontagem.queroferias.model.Servico;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContratosFornecedorAdapter extends ArrayAdapter<Mensagem> {
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private Context context;

    private ArrayList<Mensagem> mensagens;

    private Mensagem mensagem;

    private TextView textContrato;
    private TextView textMensagem;
    private TextView textCliente;
    private TextView textServico;

    public ContratosFornecedorAdapter(@NonNull Context c, @NonNull ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context = c;
        this.mensagens = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if(mensagens != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_contratos_fornecedor, parent, false);

            mensagem = mensagens.get(position);

            textContrato = (TextView) view.findViewById(R.id.textContrato);
            textMensagem = (TextView) view.findViewById(R.id.textStatus);
            textServico = (TextView) view.findViewById(R.id.textServico);
            textCliente = (TextView) view.findViewById(R.id.textCliente);

            textMensagem.setText(mensagem.getMensagem());
            textContrato.setText("Status do contrato:");

            carregarNomeCliente();
            carregarServico();

        }

        return view;

    }

    private void carregarServico(){
        databaseReference = ConfiguracaoFirebase.getFirebase();
        databaseReference.child("Servicos").child(mensagem.getIdFornecedor()).child(mensagem.getIdServico());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    Servico servico = dataSnapshot.getValue(Servico.class);
                    textServico.setText(servico.getServico());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);

    }

    private void carregarNomeCliente(){
        databaseReference = ConfiguracaoFirebase.getFirebase().child("Cliente").child(mensagem.getIdCliente());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    Cliente cliente = dataSnapshot.getValue(Cliente.class);
                    String texto = "Cliente: " + cliente.getNome();
                    textCliente.setText(texto);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);

    }

}
