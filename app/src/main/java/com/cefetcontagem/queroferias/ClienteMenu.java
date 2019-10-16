package com.cefetcontagem.queroferias;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cefetcontagem.queroferias.activities.ContratosClienteActivity;
import com.cefetcontagem.queroferias.activities.DetalheServicoActivity;
import com.cefetcontagem.queroferias.activities.TelaInicial;
import com.cefetcontagem.queroferias.adapter.CategoriasClienteAdapter;
import com.cefetcontagem.queroferias.adapter.PrestadoresIndicadosAdapter;
import com.cefetcontagem.queroferias.config.Base64config;
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.cefetcontagem.queroferias.model.ArraysUtils;
import com.cefetcontagem.queroferias.model.Cliente;
import com.cefetcontagem.queroferias.model.Conhecidos;
import com.cefetcontagem.queroferias.model.PrestadorIndicado;
import com.cefetcontagem.queroferias.model.Servico;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ClienteMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private ArrayList<String> conhecidos;
    private ArrayList<String> fornecedoresIndicados;

    private ArrayList<Servico> servicos;
    private ArrayList<Servico> servicosAux;
    private ArrayList<Servico> servicosOriginal;

    private ArrayAdapter<Servico> adapterServicos;

    private PrestadoresIndicadosAdapter prestadorAdapter;

    private ListView listView;

    private EditText editPesquisa;
    private ImageView imagem;

    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapterCategoria;

    private TextView textNomeMenu;
    private TextView textEmailMenu;

    private Cliente cliente;

    private String identificador;

    private ArrayList<PrestadorIndicado> prestadoresIndicados;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        setTitle("Menu");

        //Inicializar Arrays
        fornecedoresIndicados = new ArrayList<>();
        prestadoresIndicados = new ArrayList<>();
        conhecidos = new ArrayList<>();
        servicos = new ArrayList<>();
        servicosAux = new ArrayList<>();
        servicosOriginal = new ArrayList<>();

        //Inicializar preferencias
        Preferencias preferencias = new Preferencias(ClienteMenu.this);
        identificador = preferencias.getIdentificador();

        textNomeMenu = (TextView) headerView.findViewById(R.id.textNomeMenu);
        textEmailMenu = (TextView) headerView.findViewById(R.id.textEmailMenu);

        //Spineer categoria
        spinner = (Spinner) findViewById(R.id.spinner_categorias);
        adapterCategoria = ArrayAdapter.createFromResource(this, R.array.categoriasBusca, android.R.layout.simple_spinner_dropdown_item);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCategoria);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String categoriaSelecionada = adapterView.getSelectedItem().toString();

                fornecedoresIndicados.clear();
                filter(categoriaSelecionada);
                verificarIndicados(categoriaSelecionada);

                if(i != 0){
                    compararIndicadoresConhecidos(categoriaSelecionada);
                    reordenarLista(categoriaSelecionada);
                    prestadoresIndicados.clear();

                }else{
                    servicos.clear();
                    servicos.addAll(servicosOriginal);
                    adapterServicos.notifyDataSetChanged();

                }
                Log.i("dados", "Lista aux: " + servicosAux.size());
                Log.i("dados", "Lista ordenada: " + prestadoresIndicados.size());
                Log.i("dados", "Lista: " + servicos.size());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        atualizarNome();

        adapterServicos = new CategoriasClienteAdapter(ClienteMenu.this, servicos);
        listView = (ListView) findViewById(R.id.listCategorias);
        listView.setAdapter(adapterServicos);

        carregarServicos();

        carregarConhecidos();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ClienteMenu.this, DetalheServicoActivity.class);
                Servico servico = servicos.get(position);
                intent.putExtra("servico", servico);
                startActivity(intent);
            }

        });

        editPesquisa = (EditText) findViewById(R.id.editPesquisa);
        imagem = (ImageView) findViewById(R.id.imagemPesquisa);
        editPesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().equals(""))
                    filter(charSequence.toString());
                else
                    spinner.setSelection(0);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = editPesquisa.getText().toString().toLowerCase().trim();
                if(!query.equals("")) {
                    filter(query);

                }else{
                    servicos.clear();
                    servicos.addAll(servicosAux);
                    listView.setAdapter(adapterServicos);

                }

            }
        });

    }

    private void reordenarLista(String categoriaSelecionada){
        Collections.sort(prestadoresIndicados);
        Log.i("dados", "Lista ordenada: " + prestadoresIndicados.size());

        servicos.clear();
        ArrayList<Servico> array3 = new ArrayList<>();
        array3.addAll(servicosAux);
        for(int i = 0; i < prestadoresIndicados.size(); i++){
            for(int j = 0; j < array3.size(); j++) {
                if(servicosAux.get(j).getCategoria().equals(categoriaSelecionada)) {
                    if (prestadoresIndicados.get(i).getId().equals(servicosAux.get(j).getIdentificador())) {
                        Log.i("qtd", "qtd: " + prestadoresIndicados.get(i).getQtIndicacoes());
                        servicos.add(servicosAux.get(j));

                    }
                }
            }

        }

        servicosAux.clear();
        adapterServicos.notifyDataSetChanged();
        servicosAux.addAll(servicosOriginal);


    }

    //Verificar
    private void compararIndicadoresConhecidos(String categoria){
        for (int i = 0; i < fornecedoresIndicados.size(); i++) {
            final int aux = i;
            databaseReference = ConfiguracaoFirebase.getFirebase().child("Indicacoes").child(categoria).child(fornecedoresIndicados.get(i));

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildren() != null) {
                        int qtd = 0;

                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            //somar 1
                            if(conhecidos.contains(data.getKey())){
                                qtd++;

                            }


                        }

                        PrestadorIndicado prestadorIndicado = new PrestadorIndicado();
                        prestadorIndicado.setId(fornecedoresIndicados.get(aux));
                        prestadorIndicado.setQtIndicacoes(String.valueOf(qtd));
                        prestadoresIndicados.add(prestadorIndicado);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            };
            databaseReference.addListenerForSingleValueEvent(valueEventListener);


        }


    }


    //Verifica se existe algum fornecedor que foi indicado na categoria
    private void verificarIndicados(final String categoria){
        databaseReference = ConfiguracaoFirebase.getFirebase().child("Indicacoes").child(categoria);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        String fornecedorIndicado = data.getKey();
                        fornecedoresIndicados.add(fornecedorIndicado);

                    }
                    compararIndicadoresConhecidos(categoria);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);

    }

    private void carregarIndicadosNaCategoria(String categoria){

        //indicacao(categoria);

    }

    //Carrega a lista de conhecidos
    private void carregarConhecidos(){
        databaseReference = ConfiguracaoFirebase.getFirebase().child("Cliente").child(identificador).child("Conhecidos");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    conhecidos.clear();

                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        String conhecidoFinal = data.getValue(String.class);
                        conhecidos.add(conhecidoFinal);

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);



    }

    private void atualizarNome(){
        databaseReference = ConfiguracaoFirebase.getFirebase().child("Cliente").child(identificador);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    cliente = dataSnapshot.getValue(Cliente.class);

                    textEmailMenu.setText(cliente.getEmail());
                    textNomeMenu.setText(cliente.getNome());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);

    }

    private void carregarServicos(){
        databaseReference = ConfiguracaoFirebase.getFirebase().child("ServicosDisponiveis");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null){
                    servicos.clear();
                    servicosAux.clear();
                    servicosOriginal.clear();

                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        Servico servico = data.getValue(Servico.class);
                        servicos.add(servico);
                        servicosAux.add(servico);
                        servicosOriginal.add(servico);

                    }

                    adapterServicos.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);

    }

    public void filter(String q){
        servicos.clear();

        for (int i = 0; i < servicosAux.size(); i++) {
            if (servicosAux.get(i).getServico().toLowerCase().contains(q.toLowerCase())
                    || servicosAux.get(i).getCategoria().toLowerCase().contains(q.toLowerCase())) {
                servicos.add(servicosAux.get(i));

            }

        }

        adapterServicos.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        databaseReference.addValueEventListener(valueEventListener);
        super.onStart();
    }

    @Override
    protected void onResume() {
        databaseReference.addValueEventListener(valueEventListener);
        super.onResume();
    }

    @Override
    protected void onStop() {
        databaseReference.removeEventListener(valueEventListener);
        super.onStop();
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
            case R.id.nav_perfil:
                intent = new Intent(ClienteMenu.this, ClientePerfil.class);
                startActivity(intent);
                return true;

            case R.id.nav_contratos:
                intent = new Intent(ClienteMenu.this, ContratosClienteActivity.class);
                startActivity(intent);
                return true;

            case R.id.nav_conhecidos:
                intent = new Intent(ClienteMenu.this, ClienteConhecidos.class);
                startActivity(intent);
                return true;

            case R.id.nav_sair:
                sair();
                return true;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    public void indicacao(String categoriaSelecionada){

        carregarConhecidos();
        carregarServicos();
        ArrayList<Servico> servicosFiltrados = new ArrayList<>();

        for(int a=0;a<servicos.size();a++){
            if(servicos.get(a).getCategoria().equals(categoriaSelecionada)){
                servicosFiltrados.add(servicos.get(a));
            }
        }

        prestadoresIndicados = new ArrayList<PrestadorIndicado>();

        if(servicosFiltrados != null) {

            for (int z = 0; z < servicosFiltrados.size(); z++) {

                servico = servicosFiltrados.get(z);

                DatabaseReference Referenceindicado = ConfiguracaoFirebase.getFirebase().
                        child("Indicacoes").child(servico.getCategoria()).child(servico.getIdentificador());
                Log.i("IdentificadorIndicacao", servico.getIdentificador());


                ValueEventListener valueEventListener3 = new ValueEventListener() {
                    int i = 0;
                    int qtIndicadores = 0;

                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildren() != null) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                String str = data.getKey();
                                for (int i = 0; i < conhecidos.size(); i++) {
                                    if (data.hasChild(conhecidos.get(i))) {
                                        qtIndicadores++;

                                        prestadorIndicado = new PrestadorIndicado();
                                        prestadorIndicado.setId(servico.getIdentificador());
                                        prestadorIndicado.setQtIndicacoes(String.valueOf(qtIndicadores));
                                        prestadorIndicado.setServico(servico);
                                        Log.i("PrestadorIndicado", prestadorIndicado.getId());

                                        prestadoresIndicados.add(prestadorIndicado);

                                    }
                                }
                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                Referenceindicado.addListenerForSingleValueEvent(valueEventListener3);


            }
        }
        Log.i("Tamanho: "," "+prestadoresIndicados.size());
    }
    */
}