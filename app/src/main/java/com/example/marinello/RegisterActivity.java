package com.example.marinello;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marinello.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private Button createAccountButton;
    private EditText inputName, inputPhoneNumber, inputPass;
    private ProgressDialog loadingBar;
    //String userdID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountButton = (Button) findViewById(R.id.register_btn);
        inputName = (EditText) findViewById(R.id.register_username_input);
        inputPass = (EditText) findViewById(R.id.register_password_input);
        inputPhoneNumber = (EditText) findViewById(R.id.register_mail_input);
        loadingBar = new ProgressDialog(this);

        fAuth = FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

    }
    private void createAccount(){
        String name = inputName.getText().toString();
        String phone = inputPhoneNumber.getText().toString().trim();
        String pass = inputPass.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Per favore, inserisci il tuo nome..",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Per favore, inserisci la tua mail..",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Per favore, inserisci una password..",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Crea Account");
            loadingBar.setMessage("Abbi pazienza, stiamo controllando le credenziali");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatephoneNumber(name, phone, pass);
        }
    }

    private void ValidatephoneNumber(final String name, final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);

                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "Congratulazioni, il tuo account è stato creato.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Erroe di rete: riprova tra poco...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, " " + phone + " è già presente", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Per favore, riprova con un altro numero.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}