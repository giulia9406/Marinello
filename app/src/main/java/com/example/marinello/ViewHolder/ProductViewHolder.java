package com.example.marinello.ViewHolder;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marinello.Interface.ItemCLickListener;
import com.example.marinello.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDescription, getTxtProductPrice;
    public ImageView imageView;
    public ItemCLickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        getTxtProductPrice = (TextView) itemView.findViewById(R.id.product_price);

    }

    public void setItemClickListener(ItemCLickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(),false);
    }
}
