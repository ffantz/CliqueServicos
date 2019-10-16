package com.cefetcontagem.queroferias;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.cefetcontagem.queroferias.config.Base64config;
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.cefetcontagem.queroferias.model.Cliente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class ClienteConhecidos extends AppCompatActivity {


    private DatabaseReference referencia;
    private Preferencias preferencias;

    private EditText editConhecido;
    private String identificador;
    private String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_conhecidos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Conhecidos");     //Titulo para ser exibido na sua Action Bar em frente à seta
        editConhecido = (EditText) findViewById(R.id.emailconhecido);
        preferencias = new Preferencias(ClienteConhecidos.this);
        identificador = preferencias.getIdentificador();
        str = "";
        referencia = ConfiguracaoFirebase.getFirebase().child("Cliente").child(identificador);
    }

    public void onclickAdd(View view){
        str = editConhecido.getText().toString().trim();
        String emailCodificado = Base64config.criptografa64(str);
        referencia.child("Conhecidos").push().setValue(emailCodificado);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, ClienteMenu.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }
}
