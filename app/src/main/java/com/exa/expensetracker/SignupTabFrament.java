package com.exa.expensetracker;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupTabFrament  extends Fragment {

    EditText name ;
    EditText emailSignup;
    EditText passSignup;
    EditText phone;
    Button signupB;

    //firebase
   private FirebaseAuth mAuth;
   private ProgressDialog progressDialog;
   private DatabaseReference refData;
    private String id;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.sign_up_fragment, container, false);

        name = root.findViewById(R.id.name_sign_up);
        emailSignup= root.findViewById(R.id.email_sign_up);
        passSignup = root.findViewById(R.id.pass_sign_up);
        phone = root.findViewById(R.id.phone_sign_up);
        signupB= root.findViewById(R.id.signup);

        mAuth= FirebaseAuth.getInstance();
        progressDialog= new ProgressDialog(getActivity());

        name.setTranslationY(800);
        emailSignup.setTranslationY(800);
        passSignup.setTranslationY(800);
        phone.setTranslationY(800);
        signupB.setTranslationY(800);

        name.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();
        emailSignup.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();
        phone.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();
        signupB.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();
        passSignup.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();


        signupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namefield = name.getText().toString().trim();
                String emailfield = emailSignup.getText().toString().trim();
                String phonefield = phone.getText().toString().trim();
                String passfield = passSignup.getText().toString().trim();

                if (TextUtils.isEmpty(namefield)) {
                    name.setError("Name required");
                }
                if (TextUtils.isEmpty(emailfield)) {
                    emailSignup.setError("Email required");
                }
                if (TextUtils.isEmpty(phonefield)) {
                    phone.setError("phone required");
                }
                if (TextUtils.isEmpty(passfield)) {
                    passSignup.setError("password required");
                } else{

                    progressDialog.setMessage("Registration in  processing");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();


                    mAuth.createUserWithEmailAndPassword(emailfield, passfield).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                id=mAuth.getUid();

                                 refData= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                                 UserDataModel userDataModel= new UserDataModel(id,namefield, phonefield, emailfield );
                                 refData.setValue(userDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if(task.isSuccessful()){
                                             Toast.makeText(getContext(), "user data Complete", Toast.LENGTH_SHORT).show();

                                         }
                                         else{
                                             Toast.makeText(getContext(), "user data not complete", Toast.LENGTH_SHORT).show();
                                         }

                                     }
                                 });


                                Toast.makeText(getContext(), "Registration Complete", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "Registration Not Complete", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });

            }
            }
        });



        return root;
    }
}
