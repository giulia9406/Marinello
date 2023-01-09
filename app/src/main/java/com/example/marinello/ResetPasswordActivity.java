package com.example.marinello;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.marinello.Model.Users;
import com.example.marinello.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check = "";
    private TextView pageTitle, titleQuestions;
    private EditText phoneNumber, question1, question2;
    private Button verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");
        pageTitle = findViewById(R.id.page_title);
        titleQuestions = findViewById(R.id.title_questions);
        phoneNumber = findViewById(R.id.find_phone_number);
        question1 = findViewById(R.id.question_1);
        question2 = findViewById(R.id.question_2);
        verifyButton = findViewById(R.id.verify_btn);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarResetPass);
        //toolbar.setTitle("Impostazioni");
       // toolbar.setTitleTextColor(Color.WHITE);

    }

    @Override
    protected void onStart() {
        super.onStart();

        phoneNumber.setVisibility(View.GONE);

        if (check.equals("settings")) {
            pageTitle.setText("Modifica Domande");
            titleQuestions.setText("Per favore, imposta le risposte per le tue domande di sicurezza");
            verifyButton.setText("Salva");

            displayPreviousAnswers();


            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAnswers();

                }
            });
        } else if (check.equals("login")) {
            phoneNumber.setVisibility(View.VISIBLE);
            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyUser();
                }
            });

        }
    }


        private void setAnswers(){
            String answer1 = question1.getText().toString().toLowerCase();
            String answer2 = question2.getText().toString().toLowerCase();

            if(question1.equals("") && question2.equals("")){
                Toast.makeText(ResetPasswordActivity.this, "Per favore, rispondi alle domande", Toast.LENGTH_SHORT).show();

            }else{
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                        child("Users").child(Prevalent.currentOnlineUser.getPhone());


                HashMap<String, Object> userdataMap = new HashMap<>();
                userdataMap.put("answer1", answer1);
                userdataMap.put("answer2", answer2);

                ref.child("Security Questions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ResetPasswordActivity.this, "Hai risposto correttamente alle domande", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                });

            }
        }

        private void displayPreviousAnswers(){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                    child("Users").child(Prevalent.currentOnlineUser.getPhone());

            ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {;
                    if(snapshot.exists()){
                        String ans1 = snapshot.child("answer1").getValue().toString();
                        String ans2 = snapshot.child("answer2").getValue().toString();
                        question1.setText(ans1);
                        question2.setText(ans2);

                    }

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        }

        private void verifyUser(){
            final String phone = phoneNumber.getText().toString();
            final String answer1 = question1.getText().toString().toLowerCase();
            final String answer2 = question2.getText().toString().toLowerCase();

            if(!phone.equals("") && !answer1.equals("") && !answer2.equals("")){
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                        child("Users").child(phone);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String mPhone = snapshot.child("phone").getValue().toString();
                            if(phone.equals(mPhone)){
                                if(snapshot.hasChild("Security Questions")){
                                    String ans1 = snapshot.child("Security Questions").child("answer1").getValue().toString();
                                    String ans2 = snapshot.child("Security Questions").child("answer2").getValue().toString();
                                    if(!ans1.equals(answer1)){
                                        Toast.makeText(ResetPasswordActivity.this, "La prima risposta è errata", Toast.LENGTH_SHORT).show();
                                    }else if(!ans2.equals(answer2)){
                                        Toast.makeText(ResetPasswordActivity.this, "La seconda risposta è errata", Toast.LENGTH_SHORT).show();
                                    }else{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                        builder.setTitle("Nuova Pasword");
                                        final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                        newPassword.setHint("Inserisci la nuova password...");
                                        builder.setView(newPassword);
                                        builder.setPositiveButton("Cambia", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int i) {
                                                if(!newPassword.getText().toString().equals("")){
                                                    ref.child("password").setValue(newPassword.getText().
                                                            toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(ResetPasswordActivity.this, "Password aggiornata con successo!", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });


                                                }

                                            }
                                        });

                                        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int i) {
                                                dialog.cancel();

                                            }
                                        });
                                        builder.show();

                                    }
                                }

                            }else{
                                Toast.makeText(ResetPasswordActivity.this, "Numero di telefono inesistente", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }else{
                Toast.makeText(this, "Per favore, completa la procedura.", Toast.LENGTH_SHORT).show();
            }
        }

    }
