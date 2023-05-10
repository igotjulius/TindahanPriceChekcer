package com.example.admin.augscan;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword,editTextcPassword;
    public Button UserRegisterBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.nameRegister);
        editTextEmail = findViewById(R.id.emailRegister);
        editTextPassword = findViewById(R.id.passwordRegister);
        editTextcPassword= findViewById(R.id.confirmPassword);
        UserRegisterBtn= findViewById(R.id.button_register);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        UserRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }

    private void registerUser() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString().trim();
        String cpassword = editTextcPassword.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Please enter a name");
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Please enter an email");
            editTextEmail.requestFocus();
            return;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Not a valid email address");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Please enter a password");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be more than 7 characters");
            editTextPassword.requestFocus();
            return;
        }
        if(!password.equals(cpassword)){
            editTextcPassword.setError("Password do not match");
            editTextcPassword.requestFocus();
            return;
        }



        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {


                            final User user = new User(
                                    name,
                                    email

                            );
                            //important to retrive data and send data based on user email
                            FirebaseUser usernameinfirebase = mAuth.getCurrentUser();
                            String UserID = usernameinfirebase.getEmail();
                            String result = UserID.substring(0, UserID.indexOf("@"));
                            String resultemail = UserID.replace(".","");

                            FirebaseDatabase.getInstance("https://inventory-a1b50-default-rtdb.firebaseio.com/").getReference("Users")
                                    .child(resultemail).child("UserDetails")
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {

                                        Toast.makeText(RegisterActivity.this, "Registration Success", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterActivity.this,dashboardActivity.class));
                                    } else {
                                        //display a failure message
                                    }
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }




//    //Set UserDisplay Name
//    private void userProfile()
//    {
//        FirebaseUser user = mAuth.getCurrentUser();
//        if(user!= null)
//        {
//            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                    .setDisplayName(editTextName.getText().toString().trim())
//                    //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))  // here you can set image link also.
//                    .build();
//
//            user.updateProfile(profileUpdates)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//
//                            }
//                        }
//                    });
//        }
//    }



}
