package com.example.wanderwings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class numberverification extends AppCompatActivity {
    Button verifyButton;
    PinView numberByUser;
    TextView otpInfo;
    ProgressBar pBar;
    String firebaseOTP;
    String verifyNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numberverification);
        verifyButton = findViewById(R.id.verifyButton);
        numberByUser = findViewById(R.id.enteredOTP);
        otpInfo = findViewById(R.id.otpInfo);
        pBar = findViewById(R.id.authProgress);
        Intent intent = getIntent();
        verifyNumber = intent.getStringExtra("verifyNumber");
        otpInfo.setText("An OTP has been sent to "+verifyNumber);
        sendOTP(verifyNumber);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String OTPbyUser = numberByUser.getText().toString();
                if(OTPbyUser.trim().length()<6||OTPbyUser.isEmpty()){
                    numberByUser.setError("Invalid OTP");
                    numberByUser.requestFocus();;
                    return;
                }
                pBar.setVisibility(View.VISIBLE);
                verifyOTP(OTPbyUser);
            }
        });

    }


    private void sendOTP(String verifyNumber) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                firebaseOTP = s;
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if(code!=null){
                    pBar.setVisibility(View.VISIBLE);
                    numberByUser.setText(code);
                    verifyOTP(code);

                }

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(numberverification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(verifyNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOTP(String code) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(firebaseOTP,code);
            signInTheUserWithCredential(credential);
    }

    private void signInTheUserWithCredential(PhoneAuthCredential credential) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    SharedPreferences storedData = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE);
                    SharedPreferences.Editor editor = storedData.edit();
                    editor.putBoolean("aLogin", true);
                    editor.putString("number",verifyNumber);
                    editor.apply();
                    Intent intent = new Intent(numberverification.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(numberverification.this, "Login failed", Toast.LENGTH_SHORT).show();
                    pBar.setIndeterminate(false);
                    pBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}