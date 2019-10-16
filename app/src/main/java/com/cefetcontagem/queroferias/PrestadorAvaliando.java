package com.cefetcontagem.queroferias;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;

import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.model.Cliente;
import com.cefetcontagem.queroferias.model.Mensagem;
import com.cefetcontagem.queroferias.model.Prestador;
import com.cefetcontagem.queroferias.model.Servico;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class PrestadorAvaliando extends AppCompatActivity {
    private RatingBar avaliacao;
    private Button Avaliar;
    private int numeroDePessoas=0;
    private double nota, media;
    private Mensagem mensagem;

    private DatabaseReference databaseReference1;
    private ValueEventListener valueEventListener1;

    private DatabaseReference  referenciaCliente;
    private ValueEventListener valueEventListenerAvaliacao;

    private String idCliente;
    private Cliente cliente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestador_avaliando);

        Mensagem idMensagem = (Mensagem) getIntent().getSerializableExtra("mensagem");
        String str1 = idMensagem.getIdMensagem();
        idCliente = idMensagem.getIdCliente();

        databaseReference1 = ConfiguracaoFirebase.getFirebase().child("Mensagens").child(str1);

        avaliacao = (RatingBar) findViewById(R.id.estrelas);
        Avaliar = (Button) findViewById(R.id.buttonAvaliar);

        valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    mensagem = dataSnapshot.getValue(Mensagem.class);
                    //mensagem.setIdFornecedor(mensagem.getIdFornecedor());
                    mensagem.setIdCliente(mensagem.getIdCliente());
                    mensagem.setIdServico(mensagem.getIdServico());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference1.addListenerForSingleValueEvent(valueEventListener1);

        referenciaCliente = ConfiguracaoFirebase.getFirebase().child("Cliente").child(idCliente);
        valueEventListenerAvaliacao = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    cliente = dataSnapshot.getValue(Cliente.class);
                    cliente.setNumeroAvaliadores(cliente.getNumeroAvaliadores());
                    cliente.setMedia(cliente.getMedia());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        referenciaCliente.addListenerForSingleValueEvent(valueEventListenerAvaliacao);

        Avaliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Double media;
                int numeroAvaliadores;
                Double notaTotal;

                media = Double.parseDouble(cliente.getMedia());
                numeroAvaliadores = Integer.parseInt(cliente.getNumeroAvaliadores());

                notaTotal = media*numeroAvaliadores;
                notaTotal += avaliacao.getRating();
                numeroAvaliadores++;
                media = notaTotal/numeroAvaliadores;

                String qtAvaliadores = String.valueOf(numeroAvaliadores);

                referenciaCliente.child("numeroAvaliadores").setValue(qtAvaliadores.trim());
                referenciaCliente.child("media").setValue(media.toString().trim());

                finish();
            }


        });
    }
}
