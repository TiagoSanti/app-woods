package com.example.woods;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RVAdapterLocalizacoes extends RecyclerView.Adapter<RVAdapterLocalizacoes.ViewHolder> {

    private final List<Localizacao> localizacoes;

    public RVAdapterLocalizacoes(List<Localizacao> localizacoes) {
        this.localizacoes = localizacoes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_loc_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtNomeEspecie.setText(localizacoes.get(position).getNomeEspecie());
        holder.txtLat.setText("Latitude: " + localizacoes.get(position).getLatitude());
        holder.txtLong.setText("Longitude: " + localizacoes.get(position).getLongitude());
    }

    @Override
    public int getItemCount() {
        return localizacoes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtNomeEspecie, txtLat, txtLong;
        OnItemListener onItemListener;

        public ViewHolder(View itemView) {
            super(itemView);

            txtNomeEspecie = itemView.findViewById(R.id.txtNomeEspecie);
            txtLat = itemView.findViewById(R.id.txtLat);
            txtLong = itemView.findViewById(R.id.txtLong);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //onItemListener.onItemListener(getAdapterPosition());
        };
    }

    public interface OnItemListener {
        void onItemListener(int position);
    }
}