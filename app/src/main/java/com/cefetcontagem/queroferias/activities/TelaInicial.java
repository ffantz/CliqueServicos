package com.cefetcontagem.queroferias.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cefetcontagem.queroferias.ClienteLogin;
import com.cefetcontagem.queroferias.PrestadorLogin;
import com.cefetcontagem.queroferias.R;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.google.firebase.auth.FirebaseAuth;

public class TelaInicial extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    private Button cliente;
    private Button prestador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TelaInicial.this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            }
            //            //    ActivityCompat#requestPermissions
            //            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        cliente = (Button) findViewById(R.id.buttonPrestador);
        prestador = (Button) findViewById(R.id.buttonCliente);

        cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TelaInicial.this, PrestadorLogin.class);
                startActivity(intent);
                finish();

            }
        });

        prestador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TelaInicial.this, ClienteLogin.class);
                startActivity(intent);
                finish();

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

       // verificaUsuarioLogado();

    }

    private void verificaUsuarioLogado(){
        if(firebaseAuth.getCurrentUser() != null){
            Preferencias preferencias = new Preferencias(TelaInicial.this);

            String tipo = preferencias.getTipo();
            if(tipo != null){
                if(tipo.equals("forncedor")){
                    Intent intent = new Intent(TelaInicial.this, PrestadorLogin.class);
                    startActivity(intent);
                    finish();

                }else{
                    Intent intent = new Intent(TelaInicial.this, ClienteLogin.class);
                    startActivity(intent);
                    finish();

                }

            }

        }

    }

}
