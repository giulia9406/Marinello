package com.example.marinello;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marinello.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettinsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText userPhoneEditText, nomeEditText, passEditText, addressEditText;
    private TextView profileChangeBtn, closeTextBtn, saveTextBtn;
    private Button securityQuestionBtn;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageReferenceProfilePictureRef;
    private String checker = "" ;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settins);

        storageReferenceProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Immagine profilo");
        toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        toolbar.setTitle("Impostazioni");
        toolbar.setTitleTextColor(Color.WHITE);

        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        userPhoneEditText = (EditText) findViewById(R.id.settings_phone);
        nomeEditText = (EditText) findViewById(R.id.settings_name);
        passEditText = (EditText) findViewById(R.id.settings_pass);
        addressEditText = (EditText) findViewById(R.id.settings_address);

        profileChangeBtn = (TextView) findViewById(R.id.profile_image_change_btn);
        closeTextBtn = (TextView) findViewById(R.id.close_settings_btn);
        saveTextBtn = (TextView) findViewById(R.id.update_account_settings_btn);
        securityQuestionBtn = findViewById(R.id.security_questions_btn);

        userInfoDisplay(profileImageView, nomeEditText, userPhoneEditText, addressEditText);

        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettinsActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "settings");
                startActivity(intent);
            }
        });

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked")){
                    userInfoSaved();
                }else{
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettinsActivity.this);

            }
        });

    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name",nomeEditText.getText().toString());
        userMap.put("address",addressEditText.getText().toString());
       userMap. put("password", passEditText.getText().toString());

        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        //String nuovoPhone = userPhoneEditText.getText().toString();
       //ref.child(Prevalent.currentOnlineUser.getPhone()).setValue(nuovoPhone);
        //ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettinsActivity.this, HomeActivity.class));
        Toast.makeText(SettinsActivity.this, "Profilo aggiornato correttamente", Toast.LENGTH_SHORT).show();
        finish();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode==RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
             imageUri = result.getUri();
             profileImageView.setImageURI(imageUri);
        }else{
            Toast.makeText(this, "Errore, riprovare", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettinsActivity.this,SettinsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(nomeEditText.getText().toString())){
            Toast.makeText(this, "Nome obbligatorio", Toast.LENGTH_SHORT).show();
        }else    if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "indirizzo obbligatorio", Toast.LENGTH_SHORT).show();
        }else    if (TextUtils.isEmpty(userPhoneEditText.getText().toString())){
            Toast.makeText(this, "Numero obbligatorio", Toast.LENGTH_SHORT).show();
        }else if(checker.equals("clicked")){
            uploadImage();
        }

    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Aggiornamento Profilo");
        progressDialog.setMessage("Stiamo aggiornando il tuo account, solo un attimo");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null){
            final StorageReference fileRef = storageReferenceProfilePictureRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name",nomeEditText.getText().toString());
                        userMap.put("address",addressEditText.getText().toString());
                        userMap.put("password",passEditText.getText().toString());
                        //userMap.put("Ordine num",userPhoneEditText.getText().toString());
                        userMap.put("image",myUrl);


                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
                        //Prevalent.currentOnlineUser.setPhone(userPhoneEditText.getText().toString());
                       // String nuovoPhone = userPhoneEditText.getText().toString();
                        //ref.child(Prevalent.currentOnlineUser.getPhone()).setValue(nuovoPhone);
                        progressDialog.dismiss();
                        startActivity(new Intent(SettinsActivity.this, HomeActivity.class));
                        Toast.makeText(SettinsActivity.this, "Profilo aggiornato correttamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(SettinsActivity.this, "Errore", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "immagine non selezionata", Toast.LENGTH_SHORT).show();
        }

    }

    private void userInfoDisplay(final CircleImageView profileImageView,final EditText nomeEditText, final
                                 EditText userPhoneEditText, final EditText addressEditText) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(
                Prevalent.currentOnlineUser.getPhone());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("immagine").exists()){
                        String image= snapshot.child("immagine").getValue().toString();
                        String nome= snapshot.child("name").getValue().toString();
                        String phone= snapshot.child("phone").getValue().toString();
                        String pass= snapshot.child("pass").getValue().toString();
                        String address= snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        nomeEditText.setText(nome);
                        userPhoneEditText.setText(phone);
                        passEditText.setText(pass);
                        addressEditText.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}