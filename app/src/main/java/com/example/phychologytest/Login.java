package com.example.phychologytest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mobile = (EditText) findViewById(R.id.mobile);
        mAuth= FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, Home.class));
            finish();
        }

        findViewById(R.id.budone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String no = mobile.getText().toString().trim();

                if(no.isEmpty() || mobile.length() < 10){
                    mobile.setError("Enter a valid mobile");
                    mobile.requestFocus();
                    return;
                }

                Intent intent = new Intent(Login.this, VerifyPhoneActivity.class);
                intent.putExtra("number",no);
                startActivity(intent);
            }
        });
    }
}
