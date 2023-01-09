package com.example.marinello;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.marinello.Model.Products;
import com.example.marinello.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class SearchActivity extends AppCompatActivity {

    private Button searchBtn;
    private EditText inputText;
    private RecyclerView searchList;
    private String searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        inputText = findViewById(R.id.search_product_name);
        searchBtn = findViewById(R.id.search_btn);
        searchList = findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        Toolbar toolbar = findViewById(R.id.toolbarSearch);
        toolbar.setTitle("Cerca Prodotti");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput = inputText.getText().toString();
                onStart();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Prodotti");

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>().
                        setQuery(reference.orderByChild("nome_prodotto").startAt(searchInput), Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull ProductViewHolder productViewHolder, int i, @NonNull @NotNull Products products) {
                        productViewHolder.txtProductName.setText(products.getNome_prodotto() );
                        productViewHolder.txtProductDescription.setText(products.getDescrizione() );
                        productViewHolder.getTxtProductPrice.setText("Prezzo = " + products.getPrezzo() + "€");
                        Picasso.get().load(products.getImmagine()).into(productViewHolder.imageView);

                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(SearchActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("pid",products.getPid());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @NotNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        searchList.setAdapter(adapter);
        adapter.startListening();

    }
}