package com.cefetcontagem.queroferias;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cefetcontagem.queroferias.activities.TelaInicial;
import com.cefetcontagem.queroferias.config.Base64config;
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.cefetcontagem.queroferias.model.Prestador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * A login screen that offers login via email/password.
 */
public class PrestadorLogin extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private EditText emailEditText;
    private EditText senhaEditText;
    private Button buttonLogin;
    private Preferencias preferencias;

    private String email, senha, identificador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestador_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Entrar");     //Titulo para ser exibido na sua Action Bar em frente à seta

        autenticacao = FirebaseAuth.getInstance();
        preferencias = new Preferencias(PrestadorLogin.this);

        //verificarUsuarioLogado();

        emailEditText = (EditText) findViewById(R.id.email) ;
        senhaEditText = (EditText) findViewById(R.id.password);
        buttonLogin = (Button) findViewById(R.id.login_button);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailEditText.getText().toString();
                senha = senhaEditText.getText().toString();

                if(!senha.isEmpty() && !email.isEmpty()) {
                    validarLogin();

                }else{
                    Toast.makeText(PrestadorLogin.this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void validarLogin(){
        autenticacao.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            identificador = Base64config.criptografa64(email);
                            databaseReference = ConfiguracaoFirebase.getFirebase().child("Fornecedores").child(identificador);

                            valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getChildren() != null) {
                                        Prestador prestador = dataSnapshot.getValue(Prestador.class);

                                        preferencias.salvarDados(identificador, prestador.getNome(), "fornecedor");

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };

                            databaseReference.addListenerForSingleValueEvent(valueEventListener);
                            preferencias.salvarIdentificador(identificador);

                            Toast.makeText(PrestadorLogin.this, "Sucesso ao fazer login", Toast.LENGTH_SHORT).show();

                            abrirTelaPrincipal();

                        }else{
                            String erro;

                            try{
                                throw task.getException();

                            }catch (FirebaseAuthInvalidUserException e){
                                erro = "Email inválido.";

                            }catch (FirebaseAuthUserCollisionException e){
                                erro = "A conta já está sendo usada.";

                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erro = "Credenciais inválidas.";

                            }catch (Exception e){
                                erro = "Erro ao fazer login.";
                                e.printStackTrace();

                            }

                            Toast.makeText(PrestadorLogin.this, erro, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void verificarUsuarioLogado(){
        if(autenticacao.getCurrentUser() != null)
            abrirTelaPrincipal();

    }

    private void abrirTelaPrincipal(){
        Intent intent = new Intent(PrestadorLogin.this, PrestadorMenu.class);
        startActivity(intent);
        finish();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, TelaInicial.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                return true;

            default:
                return true;
        }
    }

    public void onClickCadastro(View v) {
        Intent intent = new Intent(PrestadorLogin.this, PrestadorCadastro.class);
        startActivity(intent);
    }
}

