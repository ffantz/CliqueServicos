package com.cefetcontagem.queroferias;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Lista_Fornecedores_Disp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__fornecedores__disp);
        //acessa funcao do bd
    }




    private void acessaDados(){
        //Aqui eu preciso daquela string que foi passada por bundle? sla
        //Aí aqui eu faço a consulta no firebase comparando o nome
        //vou deixar o codigo da pesquisa aqui só tem q trocar as variaveis pelo valor da q ta no bundle

        //databaseReference.orderByChild("searchLastName")   <-- //a child é o nó, no nosso caso categoria do fornecedor
               // .startAt(queryText)                             // a string da pesquisa
               // .endAt(queryText+"\uf8ff");                  // string da pesquisa ao infinito, exemplo: Digita-se Fre, aparecem Fred,freddy,frederico etc
    }

}
