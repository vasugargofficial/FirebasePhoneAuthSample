package com.example.phychologytest;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import static android.widget.Toast.LENGTH_LONG;

public class VerifyPhoneActivity extends AppCompatActivity {




    private String VerificationId;
    private TextView timer;
    private CountDownTimer countDownTimer1,countDownTimer2;
    public long timeleft=60000;
    private FirebaseAuth mAuth;
    EditText etCode;
    String phoneNo;
    TextView term;
    int flag=0;
    boolean timeout=false;
    DatabaseReference myref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify_phone);

        phoneNo= getIntent().getStringExtra("number");
        FirebaseApp.initializeApp(this);
        mAuth= FirebaseAuth.getInstance();
        etCode= (EditText)findViewById(R.id.etCode);
        sendVerificationCode(phoneNo);
        timer=(TextView)findViewById(R.id.timer);
        term=(TextView)findViewById(R.id.term);
        term.setText("Please check your phone +91"+phoneNo+" You will receive a message from us with your passcode");

        myref = FirebaseDatabase.getInstance().getReference("Users");
        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("Mobile_Number")
                .equalTo(phoneNo);
        query.addValueEventListener(valueEventListener);


        countDownTimer1=new CountDownTimer(timeleft,1000) {
            @Override
            public void onTick(long l) {
                timeleft=l;
                updateTimer();
            }

            @Override
            public void onFinish() {
                VerificationId="0";
                timeout=true;
            }
        }.start();


    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //artistList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    flag=1;
                }

            }
            else
            {
                flag=0;
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

            //Toast.makeText(VerifyPhoneActivity.this,"NOT FOUND",Toast.LENGTH_LONG ).show();
        }
    };
    private void sendVerificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber( "+91"+number ,60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,mCallbacks);

    }
    public void updateTimer(){
        int seconds=(int)timeleft/1000;
        String time;
        time="00:";
        if(seconds<10)time+="0";
        time+=seconds;
        timer.setText(time);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            VerificationId=s;

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String Code = phoneAuthCredential.getSmsCode();
            if(Code!=null){
                etCode.setText(Code);
                VerifyCode(Code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),LENGTH_LONG).show();
        }
    };

    private void VerifyCode(String code){

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId,code);
        signInWithCredential(credential);


    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(flag==0) {
                                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("mykey", phoneNo);
                                startActivity(intent);
                            }
                            else {
                                Intent intent= new Intent(getApplicationContext(),Home.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }

                        else {
                            if(timeout)
                                Toast.makeText(getApplicationContext(),"Time Out",LENGTH_LONG).show();
                            else
                            Toast.makeText(getApplicationContext(),"Not Valid PassCode",LENGTH_LONG).show();

                        }
                    }


                });

    }



    public void BuVerify(View view) {
        String ucode=etCode.getText().toString().trim();
        if(ucode.isEmpty() || ucode.length()<6){
            etCode.setError("Invalid format");
            etCode.requestFocus();
            return;
        }
        VerifyCode(ucode);

    }

    public void resendcode(View view) {
        if(timeout) {
            timeout=false;
            sendVerificationCode(phoneNo);
            timeleft = 60000;
            countDownTimer2 = new CountDownTimer(timeleft, 1000) {
                @Override
                public void onTick(long l) {
                    timeleft = l;
                    updateTimer();
                }

                @Override
                public void onFinish() {
                    VerificationId = "0";
                    timeout=true;
                }
            }.start();
        }
    }
}
