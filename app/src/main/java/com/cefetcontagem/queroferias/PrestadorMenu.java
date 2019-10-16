package com.cefetcontagem.queroferias;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cefetcontagem.queroferias.activities.DetalheContratoActivity;
import com.cefetcontagem.queroferias.activities.TelaInicial;
import com.cefetcontagem.queroferias.adapter.ContratosPendentesAdapter;
import com.cefetcontagem.queroferias.adapter.ServicosCadastradosAdapter;
import com.cefetcontagem.queroferias.config.Base64config;
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.cefetcontagem.queroferias.model.Contrato;
import com.cefetcontagem.queroferias.model.Prestador;
import com.cefetcontagem.queroferias.model.Servico;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PrestadorMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private Toolbar toolbar;

    private DrawerLayout drawer;

    private NavigationView navigationView;
    private View headerView;

    private ListView listView;
    private ListView listViewContratos;

    private LinearLayout linearLayout;

    private ArrayList<Servico> listServico;
    public ArrayAdapter<Servico> adapterServico;

    private ArrayList<Contrato> listContratos;
    public ArrayAdapter<Contrato> adapterContratos;

    private Preferencias preferencias;

    private String identificador;

    private TextView textNomeMenu;
    private TextView textEmailMenu;

    private TextView textoBoasVindas;
    private TextView textoServicosCadastrados;

    private Prestador prestador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestador_menu);
        setTitle("Menu");

        preferencias = new Preferencias(PrestadorMenu.this);
        identificador = preferencias.getIdentificador();

        listServico = new ArrayList<>();
        adapterServico = new ServicosCadastradosAdapter(PrestadorMenu.this, listServico);
        listView = (ListView) findViewById(R.id.list_servico);
        listView.setAdapter(adapterServico);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(PrestadorMenu.this, "Em breve, opções", Toast.LENGTH_SHORT).show();
            }
        });

        listContratos = new ArrayList<>();
        adapterContratos = new ContratosPendentesAdapter(PrestadorMenu.this, listContratos);
        listViewContratos = (ListView) findViewById(R.id.list_contratos);
        listViewContratos.setAdapter(adapterContratos);
        listViewContratos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PrestadorMenu.this, DetalheContratoActivity.class);
                Contrato contrato = listContratos.get(i);

                String id = contrato.getIdContrato();
                intent.putExtra("contrato", contrato);
                startActivity(intent);

            }
        });

        inicializacao();

    }

    private void inicializacao(){
        inicializarToolbar();
        inicializarDrawer();
        inicializarViews();
        carregarBoasVindas();
        carregarListaServicos();
        carregarContratosPendentes();

    }

    private void inicializarViews(){
        textNomeMenu = (TextView) headerView.findViewById(R.id.textNomeMenuPrestador);
        textEmailMenu = (TextView) headerView.findViewById(R.id.textEmailMenuPrestador);

        textoBoasVindas = (TextView) findViewById(R.id.textBemVindo);
        textoBoasVindas.setVisibility(View.INVISIBLE);

        textoServicosCadastrados = (TextView) findViewById(R.id.text_servicos_cadastrados);
        textoServicosCadastrados.setVisibility(View.INVISIBLE);

        linearLayout = (LinearLayout) findViewById(R.id.secao_contratos_pendentes);
        linearLayout.setVisibility(View.INVISIBLE);

    }

    private void inicializarDrawer(){
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);

    }

    private void inicializarToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Menu");

    }

    private void carregarContratosPendentes(){
        databaseReference = ConfiguracaoFirebase.getFirebase().child("ContratosPendentes");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    listContratos.clear();

                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        Contrato contrato = data.getValue(Contrato.class);

                        if(contrato.getIdFornecedor().equals(identificador)){
                            contrato.setIdContrato(data.getKey());
                            listContratos.add(contrato);

                        }

                    }

                    adapterContratos.notifyDataSetChanged();
                    linearLayout.setVisibility(View.VISIBLE);
                    listViewContratos.setVisibility(View.VISIBLE);

                }else{
                    listViewContratos.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);

    }

    private void carregarListaServicos(){
        databaseReference = ConfiguracaoFirebase.getFirebase().child("Servicos").child(identificador);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    listServico.clear();

                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        Servico servico = data.getValue(Servico.class);
                        listServico.add(servico);

                    }

                    adapterServico.notifyDataSetChanged();
                    textoServicosCadastrados.setVisibility(View.VISIBLE);

                }else{
                    listView.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);

    }

    private void carregarBoasVindas(){
        databaseReference = ConfiguracaoFirebase.getFirebase().child("Fornecedores").child(identificador);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    prestador = dataSnapshot.getValue(Prestador.class);
                    textoBoasVindas.setText("Olá, " + prestador.getNome());
                    textoBoasVindas.setVisibility(View.VISIBLE);
                    textNomeMenu.setText(prestador.getNome());
                    textEmailMenu.setText(prestador.getEmail());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    private void sair(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Preferencias preferencias = new Preferencias(this);
        preferencias.removerDados();
        Intent intent = new Intent(getApplicationContext(), TelaInicial.class);
        startActivity(intent);
        finish();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.nav_perfilPrestador:
                intent = new Intent(PrestadorMenu.this, PrestadorPerfil.class);
                startActivity(intent);
                return true;

            case R.id.nav_contratosPrestador:
                intent = new Intent(PrestadorMenu.this, PrestadorContratos.class);
                startActivity(intent);
                return true;

            case R.id.nav_adicionarServico:
                intent = new Intent(PrestadorMenu.this, PrestadorAdicionarCategoria.class);
                startActivity(intent);
                return true;

            case R.id.nav_sairPrestador:
                sair();
                return true;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        databaseReference.addValueEventListener(valueEventListener);
        super.onStart();
    }

    @Override
    protected void onResume() {
        databaseReference.addValueEventListener(valueEventListener);
        //se for atualizar lista,
        super.onResume();
    }

    @Override
    protected void onStop() {
        databaseReference.removeEventListener(valueEventListener);
        super.onStop();
    }
}
