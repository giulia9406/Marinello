package com.example.marinello.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.marinello.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    private Button applyChangesBtn, deleteBtn;
    private EditText name, price, description;
    private ImageView imageView;
    private String productID="";
    private DatabaseReference productsRef;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        toolbar = (Toolbar) findViewById(R.id.toolbarMaintain);
        toolbar.setTitle("Modifica Prodotto");
        toolbar.setTitleTextColor(Color.WHITE);

        productID = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Prodotti").child(productID);

        applyChangesBtn = findViewById(R.id.apply_changes_btn);
        name = findViewById(R.id.product_name_maintain);
        price = findViewById(R.id.product_price_maintain);
        description = findViewById(R.id.product_description_maintain);
        imageView = findViewById(R.id.product_image_maintain);

        deleteBtn = findViewById(R.id.delete_product_btn);


        displaySpecificProductInfo();

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteThisProduct();
            }
        });

    }

    private void deleteThisProduct() {
       // public void onClick(View v) {
            CharSequence options[] = new CharSequence[]{
                    "Sì",
                    "No"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(AdminMaintainProductsActivity.this);
            builder.setTitle("Sei sicuro di voler eliminare il prodotto?");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    if(i == 0){
                        //String uID = getRef(i).getKey();
                       // removeOrder(uID);
                        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {

                                Intent intent = new Intent (AdminMaintainProductsActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(AdminMaintainProductsActivity.this, "Il prodotto è stato rimosso.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else{
                        finish();
                    }

                }
            });

            builder.show();


            /*productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {

                    Intent intent = new Intent (AdminMaintainProductsActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(AdminMaintainProductsActivity.this, "Il prodotto è stato rimosso.", Toast.LENGTH_SHORT).show();
                }
            });

             */
        //}
    }

    private void applyChanges() {
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDescription = description.getText().toString();

        if(pName.equals("")){
            Toast.makeText(this, "Scrivi il nome del prodotto", Toast.LENGTH_SHORT).show();
        }else if(pPrice.equals("")){
            Toast.makeText(this, "Scrivi il prezzo del prodotto", Toast.LENGTH_SHORT).show();
        }else if(pDescription.equals("")){
            Toast.makeText(this, "Scrivi la descrizione del prodotto", Toast.LENGTH_SHORT).show();
        }else{
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productID);
            productMap.put("descrizione", pDescription);
            productMap.put("prezzo", pPrice);
            productMap.put("nome_prodotto", pName);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AdminMaintainProductsActivity.this, "Modifiche avvenute correttamente!", Toast.LENGTH_SHORT).show();
                        Intent intent =new Intent (AdminMaintainProductsActivity.this, AdminCategoryActivity.class);
                        startActivity(intent);
                        finish();



                    }
                }
            });

        }

    }

    private void displaySpecificProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String pName = snapshot.child("nome_prodotto").getValue().toString();
                    String pPrice = snapshot.child("prezzo").getValue().toString();
                    String pDescription = snapshot.child("descrizione").getValue().toString();
                    String pImage = snapshot.child("immagine").getValue().toString();

                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);
                    Picasso.get().load(pImage).into(imageView);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}