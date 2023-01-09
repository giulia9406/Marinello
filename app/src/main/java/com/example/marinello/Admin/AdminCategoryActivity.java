package com.example.marinello.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.marinello.HomeActivity;
import com.example.marinello.MainActivity;
import com.example.marinello.R;

public class AdminCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView olio, vino;

    private Button logoutBtn, checkOrdersBtn, maintainProductsBtn, goHomeBtn;

    private CardView cardHome, cardProd, cardOrdini, cardLogout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        toolbar = (Toolbar) findViewById(R.id.toolbarAdminCategory);
        toolbar.setTitle("Aggiungi nuovo prodotto");
        toolbar.setTitleTextColor(Color.WHITE);

        /*
        logoutBtn = (Button) findViewById(R.id.admin_logout_btn);
        checkOrdersBtn= (Button) findViewById(R.id.check_orders_btn);
        maintainProductsBtn= (Button) findViewById(R.id.maintain_btn);
        goHomeBtn = (Button)findViewById(R.id.go_to_home_btn);
         */
        cardHome = (CardView) findViewById(R.id.go_home_card);
        cardProd= (CardView) findViewById(R.id.modifica_prod_card);
        cardOrdini= (CardView) findViewById(R.id.ordini_card);
        cardLogout= (CardView) findViewById(R.id.logout_card);

        cardHome.setOnClickListener(this);
        cardProd.setOnClickListener(this);
        cardOrdini.setOnClickListener(this);
        cardLogout.setOnClickListener(this);

/*
        goHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, HomeActivity.class);
                intent.putExtra("Admin", "Admin");
                startActivity(intent);

            }
        });

        maintainProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, HomeActivity.class);
                intent.putExtra("Admin", "Admin");
                startActivity(intent);


            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        checkOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);

            }
        });


 */
        olio = (ImageView) findViewById(R.id.olio);
        vino = (ImageView) findViewById(R.id.vino);

        olio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "Olio");
                startActivity(intent);
            }
        });

        vino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "Vino");
                startActivity(intent);
            }
        });
    }

    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.go_home_card :
                i = new Intent(this,HomeActivity.class);
                i.putExtra("Admin", "Admin");
                startActivity(i);
                break;

            case R.id.ordini_card:
                i = new Intent(this,AdminNewOrdersActivity.class);
                startActivity(i);
                break;
            case R.id.logout_card:
                i = new Intent(this,MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;

            case R.id.modifica_prod_card:
                i = new Intent(this,HomeActivity.class);
                i.putExtra("Admin", "Admin");
                startActivity(i);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}