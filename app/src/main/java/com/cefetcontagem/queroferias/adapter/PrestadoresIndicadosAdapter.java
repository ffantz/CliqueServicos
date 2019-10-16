package com.cefetcontagem.queroferias.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cefetcontagem.queroferias.R;
import com.cefetcontagem.queroferias.model.PrestadorIndicado;

import java.util.ArrayList;

public class PrestadoresIndicadosAdapter extends ArrayAdapter<PrestadorIndicado> {
    private Context context;
    private ArrayList<PrestadorIndicado> prest;

    public PrestadoresIndicadosAdapter(Context c, ArrayList<PrestadorIndicado> objects){
        super(c, 0, objects);
        this.context = c;
        this.prest = objects;
    }

    public View getView(int position, View convertView,ViewGroup parent) {
        View view = null;

        if(prest != null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_categorias, parent, false);

            TextView textServico = (TextView) view.findViewById(R.id.textEmail);
            TextView textCategoria = (TextView) view.findViewById(R.id.textEndereco);
            TextView preco = (TextView) view.findViewById(R.id.textPreco);
            TextView nome = (TextView) view.findViewById(R.id.textNome);
            final RatingBar estrelas = (RatingBar) view.findViewById(R.id.estrelas);
            TextView indicacao = (TextView) view.findViewById(R.id.textindicacao);

            PrestadorIndicado prestadorIndicado = prest.get(position);

            textServico.setText(prestadorIndicado.getServico().getServico());
            textCategoria.setText(prestadorIndicado.getServico().getCategoria());
            preco.setText(prestadorIndicado.getServico().getPreco());
            nome.setText(prestadorIndicado.getServico().getNome());

        }

        return view;
    }

}
