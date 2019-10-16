package com.cefetcontagem.queroferias.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cefetcontagem.queroferias.R;
import com.cefetcontagem.queroferias.model.Servico;

import java.util.ArrayList;

public class ServicosCadastradosAdapter extends ArrayAdapter<Servico> {
    private Context context;
    private ArrayList<Servico> servicos;

    public ServicosCadastradosAdapter(@NonNull Context c,  @NonNull ArrayList<Servico> objects) {
        super(c, 0, objects);
        this.context = c;
        this.servicos = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if(servicos != null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_servicos_cadastrados, parent, false);

            TextView textServico = (TextView) view.findViewById(R.id.textEmail);
            TextView textCategoria = (TextView) view.findViewById(R.id.textEndereco);
            TextView preco = (TextView) view.findViewById(R.id.textPreco);

            Servico servico = servicos.get(position);

            textServico.setText(servico.getServico());
            textCategoria.setText(servico.getCategoria());
            preco.setText(servico.getPreco());

        }

        return view;

    }
}
