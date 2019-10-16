package com.cefetcontagem.queroferias;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;

import com.cefetcontagem.queroferias.R;
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.model.Cliente;
import com.cefetcontagem.queroferias.model.Contrato;
import com.cefetcontagem.queroferias.model.Mensagem;
import com.cefetcontagem.queroferias.model.Prestador;
import com.cefetcontagem.queroferias.model.Servico;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class ClienteAvaliando extends AppCompatActivity {
    private RatingBar avaliacao;
    private CheckBox indicar;
    private Button Avaliar;
    private int numeroDePessoas;
    private double nota, media;
    private Mensagem mensagem;
    private Servico servico;

    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;
    private ValueEventListener valueEventListener1;
    private ValueEventListener valueEventListener2;

    private DatabaseReference referenciaPrestador;
    private ValueEventListener valueEventListenerAvaliacao;

    private String idFornecedor;
    private Prestador prestador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_avaliando);

        numeroDePessoas = 0;

        Mensagem idMensagem = (Mensagem) getIntent().getSerializableExtra("mensagem");
        String str1 = idMensagem.getIdMensagem();
        String strIdServico = idMensagem.getIdServico();
        idFornecedor = idMensagem.getIdFornecedor();

        avaliacao = (RatingBar) findViewById(R.id.estrelas);
        indicar = (CheckBox) findViewById(R.id.checkbox);
        Avaliar = (Button) findViewById(R.id.buttonAvaliar);

        databaseReference1 = ConfiguracaoFirebase.getFirebase().child("Mensagens").child(str1);
        valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    mensagem = dataSnapshot.getValue(Mensagem.class);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference1.addListenerForSingleValueEvent(valueEventListener1);

        databaseReference2 = ConfiguracaoFirebase.getFirebase().child("ServicosDisponiveis").child(strIdServico);
        valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    servico = dataSnapshot.getValue(Servico.class);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference2.addListenerForSingleValueEvent(valueEventListener2);

        referenciaPrestador = ConfiguracaoFirebase.getFirebase().child("Fornecedores").child(idFornecedor);
        valueEventListenerAvaliacao = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    prestador = dataSnapshot.getValue(Prestador.class);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        referenciaPrestador.addListenerForSingleValueEvent(valueEventListenerAvaliacao);

        Avaliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double media;
                int numeroAvaliadores;
                Double notaTotal;

                media = Double.parseDouble(prestador.getMedia());
                numeroAvaliadores = Integer.parseInt(prestador.getNumeroAvaliadores());

                notaTotal = media*numeroAvaliadores;
                notaTotal += avaliacao.getRating();
                numeroAvaliadores++;
                media = notaTotal/numeroAvaliadores;

                String qtAvaliadores = String.valueOf(numeroAvaliadores);

                servico.setMedia(String.valueOf(media));

                referenciaPrestador.child("numeroAvaliadores").setValue(qtAvaliadores.trim());
                referenciaPrestador.child("media").setValue(media.toString().trim());

                //avaliarServico();

                if(indicar.isChecked()){
                    String categoria = servico.getCategoria();
                    String fornecedor = mensagem.getIdFornecedor();
                    String cliente = mensagem.getIdCliente();

                    DatabaseReference referenciaIndicacao;
                    referenciaIndicacao = ConfiguracaoFirebase.getFirebase().child("Indicacoes");
                    referenciaIndicacao.child(categoria).child(fornecedor).child(cliente).setValue(cliente);
                }

                finish();
            }


        });

    }

    private void avaliarServico(){
        databaseReference2.setValue(servico);
        databaseReference2 = ConfiguracaoFirebase.getFirebase().child("Servicos").child(servico.getIdentificador()).child(servico.getIdServico());
        databaseReference2.setValue(servico);

    }

}
