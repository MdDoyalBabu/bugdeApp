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

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {


    private ImageView imageView;
    private EditText email;
    private EditText pasword;
    private Button loginBtn;
    private TextView singUP;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        email=findViewById(R.id.email);
        pasword=findViewById(R.id.password);
        loginBtn=findViewById(R.id.login);
        singUP=findViewById(R.id.singUP_textview_id);

        auth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);


        singUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegistationActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
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
                    progressDialog.setMessage("Login in progress");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    auth.signInWithEmailAndPassword(emailN,passwordN).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Error"+task.getException().toString(), Toast.LENGTH_SHORT).show();

                                 progressDialog.dismiss();
                            }

                        }
                    });
                }


            }
        });

    }
}