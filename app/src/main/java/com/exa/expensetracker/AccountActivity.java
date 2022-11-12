package com.exa.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.exa.expensetracker.LoginActivity;
import com.exa.expensetracker.R;
import com.google.firebase.auth.FirebaseAuth;

public class AccountActivity extends AppCompatActivity {

    private Toolbar toolbarProfile;
    private TextView profileEmail;
    private Button updateBtn;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My profile");


//        toolbarProfile= findViewById(R.id.logout_toolbar);
//        setSupportActionBar(toolbarProfile);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle("My profile");

        profileEmail= findViewById(R.id.email_profile);

        logoutBtn= (Button) findViewById(R.id.logout_button);
        updateBtn=(Button)findViewById(R.id.update_profile_button);




        profileEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AccountActivity.this)
                        .setTitle("Expense Tracker")
                        .setMessage("Are you sure you want to exit")
                        .setCancelable(false)
                        .setPositiveButton("Yes",(dialogInterface, i) -> {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent=new Intent(AccountActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                        })
                        .setNegativeButton("No", null)
                        .show();


            }
        });






    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            Intent intent= new Intent( AccountActivity.this,MainActivity.class);
            startActivity(intent);
            onBackPressed();


        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onBackPressed() {
//        finish();
//        super.onBackPressed();
//    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode== KeyEvent.KEYCODE_BACK)   {
            Intent intent= new Intent( AccountActivity.this,MainActivity.class);
            startActivity(intent);

            finish();
        }
        return true;
    }
}