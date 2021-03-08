package com.example.crimereport01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public TextView registerBtn;
    public EditText userName,password;
    public Button loginBtn;
    public FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login_btn);
        registerBtn = findViewById(R.id.registerBtn);
        mFirebaseAuth = FirebaseAuth.getInstance();


        //Start Registration Button Function//
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
        //End Registration Button Function//


        //Start Login Button Functions

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null ){
                }
                else{
                    Toast.makeText(LoginActivity.this,"Please Login",Toast.LENGTH_SHORT).show();
                }
            }
        };

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userName.getText().toString();
                String pwd = password.getText().toString();
                if(email.isEmpty()){
                    userName.setError("Please enter email id");
                    userName.requestFocus();
                }
                else  if(pwd.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else  if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
                }
                else  if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Login Error, Please Login Again",Toast.LENGTH_SHORT).show();
                                password.setText(null);
                                userName.setText(null);
                            }
                            else{
                                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

                                if (mFirebaseUser != null) {

                                    UID = mFirebaseUser.getUid();

                                   Bundle bundle = new Bundle();
                                   bundle.putString("uid",UID);

                                    Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                }




                            }
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this,"Error Occurred!",Toast.LENGTH_SHORT).show();

                }

            }
        });


        //End Login Button Functions

    }
}
