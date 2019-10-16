package com.cefetcontagem.queroferias.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cefetcontagem.queroferias.ClienteAvaliando;
import com.cefetcontagem.queroferias.R;
import com.cefetcontagem.queroferias.adapter.ContratosClienteAdapter;
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.cefetcontagem.queroferias.model.Mensagem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ContratosClienteActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_contratos_cliente);

        preferencias = new Preferencias(ContratosClienteActivity.this);
        identificador = preferencias.getIdentificador();

        listView = (ListView) findViewById(R.id.lista_contratos_cliente);
        listMensagem = new ArrayList<>();
        adapterMensagem = new ContratosClienteAdapter(ContratosClienteActivity.this, listMensagem);
        listView.setAdapter(adapterMensagem);

        carregarContratos();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContratosClienteActivity.this, ClienteAvaliando.class);

                Mensagem itemMensagem = listMensagem.get(position);
                //Somente quando passar a data limite é possivel avaliar

                //if(validarData(itemMensagem)) {
                    intent.putExtra("mensagem", itemMensagem);
                    startActivity(intent);
                    finish();

               // }
            }
        });

    }

    private boolean validarData(Mensagem mensagem) {
        String data = mensagem.getData();
        String[] info = data.split("/");
        int dia = Integer.valueOf(info[0]);
        int mes = Integer.valueOf(info[1]);
        int ano = Integer.valueOf(info[2]);

        Calendar calendar = Calendar.getInstance();

        if (dia > 0 && dia < 32 && mes > 0 && mes < 13 && ano < 2120) {
            if (ano < calendar.get(Calendar.YEAR)) {
                return false;

            } else if (ano == calendar.get(Calendar.YEAR)) {
                if (mes < calendar.get(Calendar.MONTH) + 1) {
                    return false;

                } else if (mes == calendar.get(Calendar.MONTH) + 1) {
                    if (dia <= calendar.get(Calendar.DAY_OF_MONTH)) {
                        return false;

                    } else {
                        return true;

                    }

                } else {
                    return true;

                }

            } else {
                return true;

            }


        }

        return false;
    }

    private void carregarContratos(){
        databaseReference = ConfiguracaoFirebase.getFirebase().child("Mensagens");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    listMensagem.clear();
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        Mensagem mensagem = data.getValue(Mensagem.class);

                        if(mensagem.getIdCliente().equals(identificador)){
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
}