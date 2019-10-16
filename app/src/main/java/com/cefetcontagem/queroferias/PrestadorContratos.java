package com.cefetcontagem.queroferias;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cefetcontagem.queroferias.adapter.ContratosFornecedorAdapter;
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.cefetcontagem.queroferias.model.Mensagem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PrestadorContratos extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private ListView listView;

    private ArrayList<Mensagem> listMensagem;
    private ArrayAdapter<Mensagem> adapterMensagem;

    private Preferencias preferencias;

    private String identificador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestador_contratos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Contratos");     //Titulo para ser exibido na sua Action Bar em frente à seta

        preferencias = new Preferencias(PrestadorContratos.this);
        identificador = preferencias.getIdentificador();

        listView = (ListView) findViewById(R.id.list_contratos_fornecedor);
        listMensagem = new ArrayList<>();
        adapterMensagem = new ContratosFornecedorAdapter(PrestadorContratos.this, listMensagem);
        listView.setAdapter(adapterMensagem);

        carregarMensagens();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PrestadorContratos.this, PrestadorAvaliando.class);

                //Somente quando passar a data limite é possivel avaliar
                Mensagem itemMensagem = listMensagem.get(position);
                intent.putExtra("mensagem", itemMensagem);




                //if(DataAtual > mensagens.getData()) {
                //Intent intent = new Intent(ContratosClienteActivity.this, ClienteAvaliando.class);
                //startActivity(intent);
                //finish();
                //}



                startActivity(intent);
                finish();
            }
        });


    }

    private void carregarMensagens(){
        databaseReference = ConfiguracaoFirebase.getFirebase().child("Mensagens");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    listMensagem.clear();

                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        Mensagem mensagem = data.getValue(Mensagem.class);

                        if(mensagem.getIdFornecedor().equals(identificador)){
                            listMensagem.add(mensagem);

                        }

                    }

                    adapterMensagem.notifyDataSetChanged();

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
                startActivity(new Intent(this, PrestadorMenu.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:
                break;
        }
        return true;
    }
}