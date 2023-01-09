package com.example.marinello.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.marinello.Interface.ItemCLickListener;
import com.example.marinello.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtProductName, txtProductPrice, txtProductQuantity;
    private ItemCLickListener itemCLickListener;

    public CartViewHolder(View itemView){
        super(itemView);
        txtProductName = itemView.findViewById(R.id.cart_product_name);
        txtProductPrice = itemView.findViewById(R.id.cart_product_price);
        txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity);

    }

    @Override
    public void onClick(View view) {
        itemCLickListener.onClick(view, getAdapterPosition(),false);

    }

    public void setItemCLickListener(ItemCLickListener itemCLickListener) {
        this.itemCLickListener = itemCLickListener;
    }
}
