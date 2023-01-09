package com.example.marinello;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marinello.Model.Cart;
import com.example.marinello.Prevalent.Prevalent;
import com.example.marinello.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView txtTotalAmount, txtMsg1;
    private Toolbar toolbar;
    private boolean visitato = false;
    private int overTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        toolbar = (Toolbar) findViewById(R.id.toolbarCart);
        toolbar.setTitle("Carrello");
        toolbar.setTitleTextColor(Color.WHITE);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProcessBtn = (Button) findViewById(R.id.next_process_btn);
        txtTotalAmount = (TextView) findViewById(R.id.total_price);

        txtMsg1 = (TextView) findViewById(R.id.msg1);

        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(overTotalPrice == 0 ){
                    Toast.makeText(CartActivity.this,"Impossibile procedere, carrello vuoto",Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                    intent.putExtra("Prezzo totale", String.valueOf(overTotalPrice));
                    startActivity(intent);
                    finish();
                }


            }
        });

    }

    public void onStart(){
        super.onStart();
        checkOrderState();
        //txtTotalAmount.setText("Prezzo totale=  "+ String.valueOf(overTotalPrice) + "€");
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartListRef.child("User View").
                        child(Prevalent.currentOnlineUser.getPhone())
                        .child("Prodotti"), Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull CartViewHolder holder, int i, @NonNull @NotNull Cart model) {
                        holder.txtProductQuantity.setText("Quantità: "+model.getQuantity());
                        holder.txtProductPrice.setText("Prezzo: "+model.getPrice() );
                        holder.txtProductName.setText(model.getPname());

                        System.out.println("model.getPrice()");
                        System.out.println(model.getPrice());


                        String result = model.getPrice().replaceAll("[€]","");
                        System.out.println("result");
                        System.out.println(result);
                        int oneTypeProductPrice = 0;
                        if(visitato == false) {
                            oneTypeProductPrice = ((Integer.valueOf(result))) *
                                    Integer.valueOf(model.getQuantity());

                        }
                        overTotalPrice = overTotalPrice + oneTypeProductPrice;

                        System.out.println(overTotalPrice);
                        txtTotalAmount.setText("Prezzo totale:  "+ String.valueOf(overTotalPrice) + "€");

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]{
                                        "Modifica",
                                        "Rimuovi"

                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Opzioni Carrello");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(i == 0 ){
                                            Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("pid", model.getPid());
                                            visitato = true;
                                            startActivity(intent);
                                        }
                                        if(i == 1){
                                            cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                                                    .child("Prodotti").child(model.getPid())
                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(CartActivity.this,"Prodotto rimosso",
                                                                Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                        startActivity(intent);

                                                    }
                                                }
                                            });

                                        }
                                    }
                                });

                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @NotNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void checkOrderState(){
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().
        child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if(shippingState.equals("shipped")){
                        txtTotalAmount.setText("Gentile "+userName + "\n il tuo ordine " +
                                "è stato spedito correttamente");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setText("Complimenti, il tuo ordine è stato spedito.Presto riceverai il tuo pacco.");
                        txtMsg1.setVisibility(View.VISIBLE);
                        nextProcessBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this,"Potrai acquistare altri prodotti una volta" +
                                "che il tuo ordine è stato confermato",Toast.LENGTH_LONG).show();

                    }else if(shippingState.equals("not shipped")){
                        txtTotalAmount.setText("Stato spedizione = non spedito");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        nextProcessBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this,"Potrai acquistare altri prodotti una volta" +
                                "che il tuo ordine è stato confermato",Toast.LENGTH_LONG).show();



                    }
                }else{
                    System.out.println("SONO QUIIIIIIIIIIIII");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


}