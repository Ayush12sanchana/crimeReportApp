package com.example.crimereport01;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    public EditText name, age, nic , email, password , rePassword;
    public Button registerBtn;
    public FirebaseAuth mFirebaseAuth;
    public FirebaseFirestore mfirebaseFirestore;
    public String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        nic = findViewById(R.id.nic);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.repassword);
        registerBtn = findViewById(R.id.register_btn);
        mfirebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();



        //Start User Registration
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name01=name.getText().toString();
                final String age01=age.getText().toString();
                final String nic01=nic.getText().toString();
                final String email01=email.getText().toString();
                final String password01 = password.getText().toString();
                final String repassword01 = rePassword.getText().toString();

                if(TextUtils.isEmpty(name01)){
                    name.setError("name required");
                    name.requestFocus();
                }


                else if(TextUtils.isEmpty(age01)){
                    age.setError("age required");
                    age.requestFocus();
                }

                else if(TextUtils.isEmpty(nic01)){
                    nic.setError("age required");
                    nic.requestFocus();
                }
                else if(TextUtils.isEmpty(email01)){
                    email.setError("age required");
                    email.requestFocus();
                }

                else if(!(name01.isEmpty() && age01.isEmpty() && nic01.isEmpty() && email01.isEmpty() && password01.isEmpty())) {

                    if(password01.equals(repassword01)) {

                        mFirebaseAuth.createUserWithEmailAndPassword(email01, password01).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                userId = mFirebaseAuth.getCurrentUser().getUid();


                                // first we need to upload user photo to firebase storage and get url


                                DocumentReference documentReference = mfirebaseFirestore.collection("Users").document(userId);
                                Map<String, Object> user = new HashMap<>();
                                user.put("Uid", userId);
                                user.put("name", name01);
                                user.put("age", age01);
                                user.put("mail", email01);
                                user.put("password", password01);
                                user.put("nic", nic01);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(RegistrationActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("Uid", userId);

                                        //Intent intent = new Intent(registrationtwo.this, login01.class);
                                        //intent.putExtras(bundle);
                                        //startActivity(intent);

                                        Intent mainIntent = new Intent(RegistrationActivity.this,LoginActivity.class);
                                        RegistrationActivity.this.startActivity(mainIntent);
                                        RegistrationActivity.this.finish();

                                    }
                                });


                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(RegistrationActivity.this, "Password is not Same", Toast.LENGTH_SHORT).show();
                        password.setText(null);
                        rePassword.setText(null);
                    }

                    /////////
                }
            }
                                       });
                        //End User Registration

    }
            }