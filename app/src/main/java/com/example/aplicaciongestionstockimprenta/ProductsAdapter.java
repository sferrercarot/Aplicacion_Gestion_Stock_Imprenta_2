package com.example.aplicaciongestionstockimprenta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product producto);
    }

    private final Context context;
    private final List<Product> data;
    private final int uid;
    private final String password;
    private final OnProductClickListener listener;

    public ProductsAdapter(Context context, List<Product> data, int uid, String password, OnProductClickListener listener) {
        this.context = context;
        this.data = data;
        this.uid = uid;
        this.password = password;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = data.get(position);
        if (p == null) return;

        holder.tvName.setText(p.getName());
        holder.tvQty.setText("Stock: " + p.getCantidadStock());

        if (p.isStockBajo()) {
            holder.tvWarning.setText("¡Stock por debajo del mínimo!");
            holder.tvWarning.setVisibility(View.VISIBLE);
        } else {
            holder.tvWarning.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onProductClick(p));
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvWarning;

        ProductViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvQty = v.findViewById(R.id.tvQty);
            tvWarning = v.findViewById(R.id.tvWarning);
        }
    }
}
