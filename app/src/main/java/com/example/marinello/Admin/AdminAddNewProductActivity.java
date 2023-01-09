package com.example.marinello.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.marinello.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, description, price, pName, saveCurrentDate, saveCurrentTime ;
    private Button addNewProductButton;
    private ImageView inputProductImage;
    private EditText inputProductName, inputProductDescription, inputProductPrice;
    private static final int galleryPick = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference productImagesRef;
    private DatabaseReference productsRef;
    private ProgressDialog loadingBar;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        categoryName = getIntent().getExtras().get("category").toString();
        productImagesRef = FirebaseStorage.getInstance().getReference().child("Immagini prodotto");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Prodotti");

        //Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();

        toolbar = (Toolbar) findViewById(R.id.toolbarAddNewProduct);
        //toolbar.setTitle("Aggiungi nuovo prodotto");
        //toolbar.setTitleTextColor(Color.WHITE);

        addNewProductButton = (Button) findViewById(R.id.add_new_product);
        inputProductImage = (ImageView) findViewById(R.id.select_product_image);
        inputProductName = (EditText) findViewById(R.id.product_name);
        inputProductDescription = (EditText) findViewById(R.id.product_description);
        inputProductPrice = (EditText) findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);

        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });

    }//onCreate
        private void openGallery() {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, galleryPick);
        }//openGallery

    public void validateProductData(){
        description = inputProductDescription.getText().toString();
        price = inputProductPrice.getText().toString();

        pName = inputProductName.getText().toString();

        if(ImageUri == null){
            Toast.makeText(this, "L'immagine del prodotto è obbligatoria", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Per favore, inserisci la descrizione del prodotto", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(price)){
            Toast.makeText(this, "Per favore, inserisci il prezzo del prodotto", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(pName)){
            Toast.makeText(this, "Per favore, inserisci il nome del prodotto", Toast.LENGTH_SHORT).show();
        }else{
            storeProductInformation();
        }

    }

    private void storeProductInformation(){

        loadingBar.setTitle("Inserimento prodotto...");
        loadingBar.setMessage("Abbi pazienza, stiamo aggiungendo il nuovo prodotto");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MM, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        //ora creo una chiave random UNICA unendo, per ogni prodotto,
        //il tempo della creazione - in questo modo sarà sicuramente unica

        productRandomKey = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = productImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Errore: "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Immagine caricata correttamente!", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                       if(!task.isSuccessful()){
                           throw task.getException();

                       }
                       downloadImageUrl = filePath.getDownloadUrl().toString();
                       return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){

                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Ho preso correttamente l'Url dell'immagine", Toast.LENGTH_SHORT).show();
                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });


    }

    private void saveProductInfoToDatabase(){
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("data", saveCurrentDate);
        productMap.put("tempo", saveCurrentTime);
        productMap.put("descrizione", description);
        productMap.put("immagine", downloadImageUrl);
        productMap.put("categoria", categoryName);
        productMap.put("prezzo", price);
        productMap.put("nome_prodotto", pName);

        productsRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent (AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);

                    loadingBar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Prodotto aggiunto correttamente", Toast.LENGTH_SHORT).show();
                }else{
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Errore: "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            inputProductImage.setImageURI(ImageUri);
        }
    }
}