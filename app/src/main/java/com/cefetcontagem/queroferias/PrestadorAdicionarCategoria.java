package com.cefetcontagem.queroferias;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cefetcontagem.queroferias.config.Base64config;
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.cefetcontagem.queroferias.model.Prestador;
import com.cefetcontagem.queroferias.model.Servico;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PrestadorAdicionarCategoria extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private Spinner spinnerCategoria;
    private Spinner spinnerServico;
    private EditText editPreco;
    private EditText editDescricao;
    private Button buttonAdicionar;

    private ArrayAdapter<CharSequence> adapterCategoria;
    private ArrayAdapter<CharSequence> adapterServico;

    private AdapterView.OnItemSelectedListener adapterView;

    private String servico, categoria, nome, identificador;

    private Servico servicoAtual;

    private Prestador prestador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretador_adicionar_categoria);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Adicionar Categoria");     //Titulo para ser exibido na sua Action Bar em frente à seta

        Preferencias preferencias = new Preferencias(PrestadorAdicionarCategoria.this);
        nome = preferencias.getNome();
        identificador = preferencias.getIdentificador();

        carregarDadosPrestador();

        //Spinner de categoria DINAMICAMENTE
        spinnerCategoria = (Spinner) findViewById(R.id.spinner_categoria);
        adapterCategoria = ArrayAdapter.createFromResource(this, R.array.categorias, android.R.layout.simple_spinner_dropdown_item);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCategoria);

        //Spinner de serviço DINAMICO
        spinnerServico = (Spinner) findViewById(R.id.spinner_servico);

        //Adapter para o segundo spinner
        inicializarAdapterView();

        //Açao do primeiro spinner
        spinnerCategoria.setOnItemSelectedListener(adapterView);

        editPreco = (EditText) findViewById(R.id.editPreco);
        editDescricao = (EditText) findViewById(R.id.editDescricao);
        buttonAdicionar = (Button) findViewById(R.id.buttonAdicionarServico);

        spinnerServico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                servico = adapterView.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buttonAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                servicoAtual = new Servico();
                servicoAtual.setCategoria(categoria);
                servicoAtual.setServico(servico);
                servicoAtual.setNome(prestador.getNome());
                servicoAtual.setDescricao(editDescricao.getText().toString());
                servicoAtual.setPreco(editPreco.getText().toString());
                servicoAtual.setIdentificador(identificador);

                //Salva a mesma informação em dois meios para manipulação no lado do cliente
                databaseReference = ConfiguracaoFirebase.getFirebase();
                String id = String.valueOf(databaseReference.child("Servicos").child(identificador).push());
                String[] aux = id.split("/");
                id = aux[5];
                servicoAtual.setIdServico(id);

                databaseReference = ConfiguracaoFirebase.getFirebase();
                databaseReference.child("Servicos").child(identificador).child(id).setValue(servicoAtual);

                databaseReference = ConfiguracaoFirebase.getFirebase();
                databaseReference.child("ServicosDisponiveis").child(id).setValue(servicoAtual);

                Toast.makeText(PrestadorAdicionarCategoria.this, "Serviço cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();

            }
        });

    }

    private void carregarDadosPrestador(){
        databaseReference = ConfiguracaoFirebase.getFirebase().child("Fornecedores").child(identificador);
        valueEventListener = new ValueEventListener() {
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

        databaseReference.addListenerForSingleValueEvent(valueEventListener);

    }

    private void inicializarAdapterView(){
        adapterView = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.arquitetura, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 1:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.arquitetura, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 2:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.barber, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 3:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.beleza, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 4:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.bemestar, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 5:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.consultoria, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 6:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.ensino, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 7:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.festa, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 8:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.fotografia, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 9:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.limpeza, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 10:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.manutencao, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 11:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.mecanica, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 12:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.moveis, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 13:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.obras, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 14:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.seguranca, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 15:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.tecnologia, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 16:
                        adapterServico = ArrayAdapter.createFromResource(PrestadorAdicionarCategoria.this, R.array.van, android.R.layout.simple_spinner_dropdown_item);
                        break;

                }

                adapterServico.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerServico.setAdapter(adapterServico);
                categoria = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, PrestadorMenu.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                return true;

            default:
                return true;
        }
    }

}
