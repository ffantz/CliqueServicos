package com.cefetcontagem.queroferias.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cefetcontagem.queroferias.R;
import com.cefetcontagem.queroferias.config.Base64config;
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.cefetcontagem.queroferias.model.Cliente;
import com.cefetcontagem.queroferias.model.Contrato;
import com.cefetcontagem.queroferias.model.Mensagem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DetalheContratoActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private Preferencias preferencias;

    private TextView textNome;
    private TextView textEndereco;
    private TextView textEmail;
    private TextView textData;
    private TextView textDetalhe;

    private Button voltar;
    private Button aceitar;
    private Button recusar;

    private RatingBar estrelas;

    private Cliente cliente;
    private Contrato contrato;

    private String identificador;
    private String identificadorUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_contrato);

        preferencias = new Preferencias(getApplicationContext());
        contrato = (Contrato) getIntent().getSerializableExtra("contrato");
        if(contrato != null){
            textNome = (TextView) findViewById(R.id.textNomeCliente);
            textEmail = (TextView) findViewById(R.id.textEndereco);
            textEndereco = (TextView) findViewById(R.id.textEmail);
            textData = (TextView) findViewById(R.id.textData);
            textDetalhe = (TextView) findViewById(R.id.textDetalhe);
            estrelas = (RatingBar) findViewById(R.id.estrelas);

            String data = "Data limite para o serviço: " + contrato.getData();
            textData.setText(data);

            String detalhe = "Descrição do serviço pelo cliente:\n" + contrato.getDetalhe();
            textDetalhe.setText(detalhe);

            voltar = (Button) findViewById(R.id.buttonVoltar);
            aceitar = (Button) findViewById(R.id.buttonAceitar);
            recusar = (Button) findViewById(R.id.buttonRecusar);

            carregarDadosCliente();

            voltar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            aceitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aceitarContrato();
                    Toast.makeText(DetalheContratoActivity.this, "Contrato aceito.", Toast.LENGTH_SHORT).show();
                    finish();

                }
            });

            recusar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recusarContrato();
                    Toast.makeText(DetalheContratoActivity.this, "Contrato recusado.", Toast.LENGTH_SHORT).show();
                    finish();

                }
            });

        }

    }

    private void aceitarContrato(){
        preferencias = new Preferencias(DetalheContratoActivity.this);
        identificadorUsuario = preferencias.getIdentificador();

        databaseReference = ConfiguracaoFirebase.getFirebase();
        String idPush = String.valueOf(databaseReference.child("GeradorPush").push());
        String[] aux = idPush.split("/");
        idPush = aux[4];

        Mensagem mensagem = new Mensagem();
        mensagem.setIdContrato(contrato.getIdContrato());
        mensagem.setIdFornecedor(contrato.getIdFornecedor());
        mensagem.setIdCliente(contrato.getIdCliente());
        mensagem.setIdServico(contrato.getIdServico());
        mensagem.setIdMensagem(contrato.getIdMensagem());
        mensagem.setData(contrato.getData());
        mensagem.setDetalhe(contrato.getDetalhe());
        mensagem.setMensagem("Contrato aceito");

        databaseReference = ConfiguracaoFirebase.getFirebase().child("ContratosPendentes").child(contrato.getIdContrato());
        databaseReference.removeValue();

        databaseReference = ConfiguracaoFirebase.getFirebase();
        Log.i("valor", "id msg: " + contrato.getIdMensagem());
        databaseReference.child("Mensagens").child(contrato.getIdMensagem()).setValue(mensagem);

        Toast.makeText(this, "Contrato aceito!", Toast.LENGTH_SHORT).show();
        finish();

    }

    private void recusarContrato(){
        preferencias = new Preferencias(DetalheContratoActivity.this);
        identificadorUsuario = preferencias.getIdentificador();

        Mensagem mensagem = new Mensagem();
        mensagem.setIdContrato(contrato.getIdContrato());
        mensagem.setIdFornecedor(contrato.getIdFornecedor());
        mensagem.setIdCliente(contrato.getIdCliente());
        mensagem.setIdServico(contrato.getIdServico());
        mensagem.setIdMensagem(contrato.getIdMensagem());
        mensagem.setData(contrato.getData());
        mensagem.setDetalhe(contrato.getDetalhe());
        mensagem.setMensagem("Contrato recusado");

        databaseReference = ConfiguracaoFirebase.getFirebase().child("ContratosPendentes").child(contrato.getIdContrato());
        databaseReference.removeValue();

        databaseReference = ConfiguracaoFirebase.getFirebase();
        databaseReference.child("Mensagens").child(contrato.getIdMensagem()).setValue(mensagem);

        Toast.makeText(this, "Contrato recusado!", Toast.LENGTH_SHORT).show();
        finish();

    }

    private void carregarDadosCliente(){
        databaseReference = ConfiguracaoFirebase.getFirebase().child("Cliente")
                .child(contrato.getIdCliente());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    cliente = dataSnapshot.getValue(Cliente.class);

                    textNome.setText(cliente.getNome());
                    String endereco = cliente.getRua() + ", "
                            + cliente.getNumero()
                            + ", bairro "
                            + cliente.getBairro() + ", "
                            + cliente.getCidade() + " - "
                            + cliente.getEstado().toUpperCase();

                    textEndereco.setText(endereco);
                    textEmail.setText(cliente.getEmail());

                    cliente.setMedia(cliente.getMedia());

                    Float media = Float.parseFloat(cliente.getMedia());

                    estrelas.setRating(media);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);

    }
}