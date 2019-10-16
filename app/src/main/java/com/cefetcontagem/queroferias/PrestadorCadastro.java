package com.cefetcontagem.queroferias;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.cefetcontagem.queroferias.config.Base64config;
import com.cefetcontagem.queroferias.config.ConfiguracaoFirebase;
import com.cefetcontagem.queroferias.config.Preferencias;
import com.cefetcontagem.queroferias.model.Prestador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;


public class PrestadorCadastro extends AppCompatActivity {
    private FirebaseAuth autentificacao;
    private DatabaseReference referencia;
    private DatabaseReference clienteRef;

    private Prestador prestador = new Prestador();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestador_cadastro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadastro");     //Titulo para ser exibido na sua Action Bar em frente à seta
        referencia = ConfiguracaoFirebase.getFirebase().child("Fornecedores");
        clienteRef = ConfiguracaoFirebase.getFirebase().child("Cliente");

    }

    public void onClickFimCadastro(View v) {

        AutoCompleteTextView nomeEditText = (AutoCompleteTextView) findViewById(R.id.nome);
        String nomeUsuario = nomeEditText.getText().toString().trim();
        prestador.setNome(nomeUsuario);

        AutoCompleteTextView EmailEditText = (AutoCompleteTextView) findViewById(R.id.email);
        String emailUsuario = EmailEditText.getText().toString().trim();
        prestador.setEmail(emailUsuario);

        TextView senhaEditText = (TextView) findViewById(R.id.password);
        String senhaUsuario = senhaEditText.getText().toString().trim();
        prestador.setSenha(senhaUsuario);

        AutoCompleteTextView cpfEditText = (AutoCompleteTextView) findViewById(R.id.cpf);
        String cpfUsuario = cpfEditText.getText().toString().trim();
        prestador.setCpf(cpfUsuario);

        AutoCompleteTextView telefoneEditText = (AutoCompleteTextView) findViewById(R.id.telefone);
        String telefoneUsuario = telefoneEditText.getText().toString().trim();
        prestador.setTelefone(telefoneUsuario);

        AutoCompleteTextView ruaEditText = (AutoCompleteTextView) findViewById(R.id.rua);
        String ruaUsuario = ruaEditText.getText().toString().trim();
        prestador.setRua(ruaUsuario);

        AutoCompleteTextView numeroEditText = (AutoCompleteTextView) findViewById(R.id.numero);
        String numeroUsuario = numeroEditText.getText().toString().trim();
        prestador.setNumero(numeroUsuario);

        AutoCompleteTextView bairroEditText = (AutoCompleteTextView) findViewById(R.id.bairro);
        String bairroUsuario = bairroEditText.getText().toString().trim();
        prestador.setBairro(bairroUsuario);

        AutoCompleteTextView cidadeEditText = (AutoCompleteTextView) findViewById(R.id.cidade);
        String cidadeUsuario = cidadeEditText.getText().toString().trim();
        prestador.setCidade(cidadeUsuario);

        AutoCompleteTextView estadoEditText = (AutoCompleteTextView) findViewById(R.id.estado);
        String estadoUsuario = estadoEditText.getText().toString().trim();
        prestador.setEstado(estadoUsuario);

        prestador.setMedia("0");
        prestador.setNumeroAvaliadores("0");

        prestador.setTipo("fornecedor");

        cadastraPrestador();

    }

    private void cadastraPrestador(){
        autentificacao = FirebaseAuth.getInstance();
        autentificacao.createUserWithEmailAndPassword(prestador.getEmail(),prestador.getSenha())
                .addOnCompleteListener(PrestadorCadastro.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String emailCodificado = Base64config.criptografa64(prestador.getEmail());
                            referencia.child(emailCodificado).setValue(prestador);
                            clienteRef.child(emailCodificado).setValue(prestador);

                            Preferencias preferencias = new Preferencias(PrestadorCadastro.this);
                            preferencias.salvarDados(emailCodificado, prestador.getNome(), prestador.getTipo());

                            Intent intent = new Intent(PrestadorCadastro.this, PrestadorLogin.class);
                            startActivity(intent);
                            finish();

                        } else {
                            String erro;

                            try{
                                throw task.getException();
                            }  catch (FirebaseAuthWeakPasswordException e) {
                                erro = "Digite uma senha mais forte, com o mínimo de 6 caracteres.";

                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erro = "Digite um email válido.";

                            } catch (FirebaseAuthUserCollisionException e) {
                                erro = "Email já cadastrado.";

                            } catch (Exception e){
                                erro = "Erro ao cadastrar Usuário.";
                                e.printStackTrace();

                            }

                            Toast.makeText(getApplicationContext(), erro, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

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
