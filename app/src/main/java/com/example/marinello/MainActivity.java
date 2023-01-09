package com.example.marinello;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.marinello.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinNowButton, loginButton;
    private ProgressDialog loadingBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowButton= (Button) findViewById(R.id.main_join_now_btn);
        loginButton= (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Fattoria Marinello");
        toolbar.setTitleTextColor(Color.WHITE);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        String userMailKey = Paper.book().read(Prevalent.userPhoneKey);
        String userPassKey = Paper.book().read(Prevalent.userPasswordKey);

        if(userMailKey != "" && userPassKey != ""){
            if(!TextUtils.isEmpty(userMailKey) &&
                !TextUtils.isEmpty(userPassKey)){
                AllowAccess(userMailKey, userPassKey);
                loadingBar.setTitle("Sei già loggato");
                loadingBar.setMessage("Abbi pazienza");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

            }
        }

    }

    private void AllowAccess(String userMailKey, String userPassKey) {
        FirebaseAuth fAuth;
        fAuth = FirebaseAuth.getInstance();

        fAuth.signInWithEmailAndPassword(userMailKey, userPassKey).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Login effettuato con successo!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        //Prevalent.currentOnlineUser = usersData;
                        intent.putExtra("mail",userMailKey);
                        startActivity(intent);

                }else{
                    Toast.makeText(MainActivity.this, "non sei presente nei nostri sistemi", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }
        });
    }
}