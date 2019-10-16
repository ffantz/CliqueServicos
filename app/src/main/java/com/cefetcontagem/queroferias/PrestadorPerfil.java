package com.cefetcontagem.queroferias;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.cefetcontagem.queroferias.model.Prestador;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class PrestadorPerfil extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private TextView editNome;
    private TextView editEmail;
    private TextView editEndereco;

    private Button botaoVoltar;
    private Button botaoEditar;

    private Preferencias preferencias;

    private String identificador;

    private Prestador prestador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestador_perfil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Perfil");     //Titulo para ser exibido na sua Action Bar em frente à seta

        preferencias = new Preferencias(PrestadorPerfil.this);
        identificador = preferencias.getIdentificador();

        editNome = (TextView) findViewById(R.id.textNome);
        editEmail = (TextView) findViewById(R.id.textEmail);
        editEndereco = (TextView) findViewById(R.id.textEndereco);

        botaoEditar = (Button) findViewById(R.id.buttonEditar);
        botaoVoltar = (Button) findViewById(R.id.buttonVoltar);

        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        botaoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrestadorPerfil.this, PrestadorEditarPerfil.class);
                startActivity(intent);
                finish();
            }
        });

        databaseReference = ConfiguracaoFirebase.getFirebase().child("Fornecedores").child(identificador);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    prestador = dataSnapshot.getValue(Prestador.class);

                    editNome.setText(prestador.getNome());
                    editEmail.setText(prestador.getEmail());

                    String endereco = "Rua " + prestador.getRua() + ", número " + prestador.getNumero()
                            + ", " + prestador.getBairro() + " - " + prestador.getCidade() + ", "
                            + prestador.getEstado();
                    editEndereco.setText(endereco);

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
                finish();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                return true;

            default:
                return true;
        }
    }
}
