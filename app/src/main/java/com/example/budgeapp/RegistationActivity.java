package com.example.budgeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistationActivity extends AppCompatActivity {


    private ImageView imageView;
    private EditText email;
    private EditText pasword;
    private Button regbtn;
    private TextView singIN;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registation);

        email=findViewById(R.id.emailR);
        pasword=findViewById(R.id.passwordR);
        regbtn=findViewById(R.id.regBtn);
        singIN=findViewById(R.id.singIN_textview_id);


        auth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        singIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegistationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailN=email.getText().toString().trim();
                String passwordN=pasword.getText().toString().trim();

                if (TextUtils.isEmpty(emailN)){
                    email.setError("email is required");
                }
                if (TextUtils.isEmpty(passwordN)){
                    pasword.setError("Password is required");
                }
                else {
                    progressDialog.setMessage("Registration in progress");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    auth.createUserWithEmailAndPassword(emailN,passwordN).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                Intent intent=new Intent(RegistationActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(RegistationActivity.this, "Error"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }


            }
        });


    }
}