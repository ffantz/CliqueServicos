package com.cefetcontagem.queroferias.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cefetcontagem.queroferias.R;
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.cefetcontagem.queroferias.model.Contrato;
import com.cefetcontagem.queroferias.model.Mensagem;
import com.cefetcontagem.queroferias.model.Servico;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

public class DetalheServicoActivity extends AppCompatActivity {
    private SimpleMaskFormatter simpleMaskFormatter;
    private MaskTextWatcher maskTextWatcher;

    private TextView nome;
    private TextView servicoPrestado;
    private TextView categoria;
    private TextView descricao;
    private TextView preco;

    private EditText editDetalhe;
    private EditText editData;

    private Button voltar;
    private Button contratar;

    private DatabaseReference databaseReference;

    private Preferencias preferencias;

    private String identificadorUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_servico);

        preferencias = new Preferencias(getApplicationContext());

        final Servico servico = (Servico) getIntent().getSerializableExtra("servico");
        if(servico != null){
            nome = (TextView) findViewById(R.id.textNomeCliente);
            servicoPrestado = (TextView) findViewById(R.id.textEmail);
            categoria = (TextView) findViewById(R.id.textEndereco);
            descricao = (TextView) findViewById(R.id.textDescricao);
            preco = (TextView) findViewById(R.id.textPreco);

            editData = (EditText) findViewById(R.id.editData);
            editDetalhe = (EditText) findViewById(R.id.editDetalhe);

            simpleMaskFormatter = new SimpleMaskFormatter("NN/NN/NNNN");
            maskTextWatcher = new MaskTextWatcher(editData, simpleMaskFormatter);
            editData.addTextChangedListener(maskTextWatcher);

            voltar = (Button) findViewById(R.id.buttonVoltar);
            contratar = (Button) findViewById(R.id.buttonContratar);

            nome.setText(servico.getNome());
            servicoPrestado.setText(servico.getServico());
            categoria.setText(servico.getCategoria());
            descricao.setText(servico.getDescricao());
            preco.setText(servico.getPreco());

            voltar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            contratar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(validarData())
                        contratarServico(servico);
                    else
                        Toast.makeText(DetalheServicoActivity.this, "Insira uma data válida.", Toast.LENGTH_SHORT).show();

                }
            });

        }else{
            Toast.makeText(DetalheServicoActivity.this, "Algum problema ocorreu, tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
            finish();

        }
    }

    private boolean validarData(){
        String data = editData.getText().toString();
        String[] info = data.split("/");
        int dia = Integer.valueOf(info[0]);
        int mes = Integer.valueOf(info[1]);
        int ano = Integer.valueOf(info[2]);

        Calendar calendar = Calendar.getInstance();

        if(dia > 0 && dia < 32 && mes > 0 && mes < 13 && ano < 2120) {
            if(ano < calendar.get(Calendar.YEAR)){
                return false;

            }else if(ano == calendar.get(Calendar.YEAR)){
                if(mes < calendar.get(Calendar.MONTH) + 1){
                    return false;

                }else if(mes == calendar.get(Calendar.MONTH) + 1){
                    if(dia < calendar.get(Calendar.DAY_OF_MONTH)){
                        return false;

                    }else{
                        return true;

                    }

                }else{
                    return true;

                }

            }else{
                return true;

            }


        }

        return false;

    }

    private void contratarServico(Servico servico){
        identificadorUsuarioLogado = preferencias.getIdentificador();

        databaseReference = ConfiguracaoFirebase.getFirebase();
        String idPush = String.valueOf(databaseReference.child("GeradorPush").push());
        String[] aux = idPush.split("/");
        idPush = aux[4];

        Contrato contrato = new Contrato();
        contrato.setIdCliente(identificadorUsuarioLogado);
        contrato.setIdFornecedor(servico.getIdentificador());
        contrato.setIdServico(servico.getIdServico());
        contrato.setIdMensagem(idPush);
        contrato.setDetalhe(editDetalhe.getText().toString());
        contrato.setData(editData.getText().toString());
        contrato.setStatus("Contrato pendente");
        contrato.setIdContrato(idPush);

        Mensagem mensagem = new Mensagem();
        mensagem.setIdContrato(contrato.getIdContrato());
        mensagem.setIdFornecedor(contrato.getIdFornecedor());
        mensagem.setIdCliente(contrato.getIdCliente());
        mensagem.setIdServico(contrato.getIdServico());
        mensagem.setIdMensagem(idPush);
        mensagem.setData(contrato.getData());
        mensagem.setDetalhe(contrato.getDetalhe());
        mensagem.setMensagem(contrato.getStatus());

        databaseReference = ConfiguracaoFirebase.getFirebase();
        databaseReference.child("Mensagens").child(mensagem.getIdMensagem()).setValue(mensagem);

        databaseReference = ConfiguracaoFirebase.getFirebase();
        databaseReference.child("ContratosPendentes")
                .child(contrato.getIdContrato())
                .setValue(contrato);

        Toast.makeText(DetalheServicoActivity.this, "Solicitação de contrato registrada", Toast.LENGTH_SHORT).show();
        finish();

    }

}
