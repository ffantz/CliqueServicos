package com.cefetcontagem.queroferias;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.cefetcontagem.queroferias.model.Prestador;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class PrestadorEditarPerfil extends AppCompatActivity {

    private Prestador prestador;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private EditText editNome;
    private EditText editCPF;
    private EditText editTelefone;
    private EditText editRua;
    private EditText editNumero;
    private EditText editBairro;
    private EditText editCidade;
    private EditText editEstado;

    private Button botaoVoltar;
    private Button botaoSalvar;

    private Preferencias preferencias;

    private String identificador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestador_editar_perfil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Editar");     //Titulo para ser exibido na sua Action Bar em frente à seta

        preferencias = new Preferencias(PrestadorEditarPerfil.this);
        identificador = preferencias.getIdentificador();

        editNome = (EditText) findViewById(R.id.nome);
        editCPF = (EditText) findViewById(R.id.cpf);
        editTelefone = (EditText) findViewById(R.id.telefone);

        editRua = (EditText) findViewById(R.id.rua);
        editNumero = (EditText) findViewById(R.id.numero);
        editBairro = (EditText) findViewById(R.id.bairro);
        editCidade = (EditText) findViewById(R.id.cidade);
        editEstado = (EditText) findViewById(R.id.estado);

        botaoSalvar = (Button) findViewById(R.id.buttonSalvar);
        botaoVoltar = (Button) findViewById(R.id.buttonVoltar);

        databaseReference = ConfiguracaoFirebase.getFirebase().child("Fornecedores").child(identificador);

        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = editNome.getText().toString().trim();

                databaseReference.child("nome").setValue(editNome.getText().toString().trim());
                databaseReference.child("cpf").setValue(editCPF.getText().toString().trim());
                databaseReference.child("telefone").setValue(editTelefone.getText().toString().trim());

                databaseReference.child("rua").setValue(editRua.getText().toString().trim());
                databaseReference.child("numero").setValue(editNumero.getText().toString().trim());
                databaseReference.child("bairro").setValue(editBairro.getText().toString().trim());
                databaseReference.child("cidade").setValue(editCidade.getText().toString().trim());
                databaseReference.child("estado").setValue(editEstado.getText().toString().trim());

                finish();

            }
        });

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                        prestador = dataSnapshot.getValue(Prestador.class);
                        editNome.setText(prestador.getNome());
                        editCPF.setText(prestador.getCpf());
                        editTelefone.setText(prestador.getTelefone());
                        editRua.setText(prestador.getRua());
                        editNumero.setText(prestador.getNumero());
                        editBairro.setText(prestador.getBairro());
                        editCidade.setText(prestador.getCidade());
                        editEstado.setText(prestador.getEstado());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);

    }

    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                finish();
                return true;

            default:
                return true;
        }
    }
}