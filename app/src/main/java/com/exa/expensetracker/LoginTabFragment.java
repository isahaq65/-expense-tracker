package com.exa.expensetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginTabFragment extends Fragment {

  EditText email, pass;
  Button loginB;

    //firebase
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;



   @Override
   public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
       View root = inflater.inflate(R.layout.log_in_fragment, container, false);

       email= root.findViewById(R.id.email);
       pass=root.findViewById(R.id.pass);
       loginB=root.findViewById(R.id.login);


       mAuth= FirebaseAuth.getInstance();
       progressDialog= new ProgressDialog(getActivity());


       email.setTranslationY(800);
       pass.setTranslationY(800);
       loginB.setTranslationY(800);


       email.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();
       pass.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
       loginB.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(800).start();


       loginB.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String emailLogin= email.getText().toString().trim();
               String passLogin= pass.getText().toString().trim();

               if(TextUtils.isEmpty(emailLogin)){
                   email.setError("Email required");
                   return;
               }
               if(TextUtils.isEmpty(passLogin)){
                   pass.setError("Email required");
                   return;
               }else{
                   progressDialog.setMessage("login in  processing");
                   progressDialog.setCanceledOnTouchOutside(false);
                   progressDialog.show();

                   mAuth.signInWithEmailAndPassword(emailLogin, passLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {

                           if(task.isSuccessful()){
                               progressDialog.dismiss();
                               Intent intent= new Intent(getActivity(), MainActivity.class);
                               getActivity().startActivity(intent);
                               getActivity().finish();
                           }

                       }
                   });


               }

           }
       });





       return root;
   }



}
