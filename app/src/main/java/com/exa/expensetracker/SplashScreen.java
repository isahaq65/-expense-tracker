package com.exa.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
  private  FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

       // SystemClock.sleep(9000);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
        if(user == null) {
            Intent mainintent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(mainintent);
            finish();

        }
        else{
            Intent mainintent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(mainintent);
            finish();

        }

            }
        }, 5000);





    }
}