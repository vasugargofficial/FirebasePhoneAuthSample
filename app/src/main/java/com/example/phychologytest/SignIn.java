package com.example.phychologytest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {

    EditText etname,etgender;
    ProgressBar prog;
    String uname,ugender;
    boolean cross=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        etname=(EditText)findViewById(R.id.etname);
        etgender=(EditText)findViewById(R.id.etgender);
        prog= (ProgressBar)findViewById(R.id.progBar);
        prog.setVisibility(View.GONE);

    }

    public void busignin(View view) {

        uname=etname.getText().toString();
        ugender=etgender.getText().toString();
        if(uname.isEmpty()) {
            etname.setError("Name is Required");
            etname.requestFocus();
            return;
        }
        if(ugender.isEmpty()) {
            etgender.setError("Gender is Required");
            etgender.requestFocus();
            return;
        }
        // Write a message to the database
        Intent intent = getIntent();
        prog.setVisibility(View.VISIBLE);
        String number = intent.getStringExtra("mykey");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        String id= currentFirebaseUser.getUid();

        myRef.child("Users").child(id).child("Mobile_Number").setValue(number);
        myRef.child("Users").child(id).child("Username").setValue(uname);
        myRef.child("Users").child(id).child("Gender").setValue(ugender);
        cross=true;
        Intent intent2 = new Intent(SignIn.this, Home.class);
        startActivity(intent2);
        finish();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(cross==false)
            FirebaseAuth.getInstance().signOut();
    }
}

