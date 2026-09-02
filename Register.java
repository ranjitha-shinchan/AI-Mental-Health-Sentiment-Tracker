package com.example.mentalhealth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aniketjain.weatherapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText name,email,pass,repass,phone;      //Declaration
    Button register;
    TextView login;

    TextView CreateAccount_Text;

    View CreateAccount_Button;

    ProgressBar buttonProgress;
    boolean passwordVisible;

    Model userData;

    //-------------- Firebase ---------------
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    // ------------- Progress Dialog ----------

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        pass = (EditText) findViewById(R.id.password);
        repass = (EditText) findViewById(R.id.repassword);
        login = (TextView) findViewById(R.id.login);
        CreateAccount_Text = findViewById(R.id.buttonText);
        CreateAccount_Button = findViewById(R.id.login_button);

        //Password visible

        pass.setOnTouchListener((v, event) -> {
            final int Right = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= pass.getRight() - pass.getCompoundDrawables()[Right].getBounds().width()) {
                    int selection = pass.getSelectionEnd();
                    //Handles Multiple option popups
                    if (passwordVisible) {
                        //set drawable image here
                        pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0);
                        //for hide password
                        pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    } else {
                        //set drawable image here
                        pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility, 0);
                        //for show password
                        pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    pass.setLongClickable(false); //Handles Multiple option popups
                    pass.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        repass.setOnTouchListener((v, event) -> {
            final int Right = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= repass.getRight() - repass.getCompoundDrawables()[Right].getBounds().width()) {
                    int selection = repass.getSelectionEnd();
                    //Handles Multiple option popups
                    if (passwordVisible) {
                        //set drawable image here
                        repass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0);
                        //for hide password
                        repass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    } else {
                        //set drawable image here
                        repass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility, 0);
                        //for show password
                        repass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    repass.setLongClickable(false); //Handles Multiple option popups
                    repass.setSelection(selection);
                    return true;
                }
            }
            return false;
        });


        CreateAccount_Text.setText("Create account");
        ConstraintLayout cl = findViewById(R.id.progress_button_bg);
        cl.setBackground(getResources().getDrawable(R.drawable.positive)); //Change the button drawable to green

        firebaseAuth = FirebaseAuth.getInstance();


        CreateAccount_Button.setOnClickListener(view -> {

            SetValidation();

        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to LoginActivity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    public void SetValidation() {

        userData = new Model();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("REGISTERING ...");

        progressDialog.show();

        //Local Variable to store Password

        String password = pass.getText().toString().trim();
        String repassword = repass.getText().toString().trim();

        // Profile Data Store

        userData.setName(name.getText().toString());
        userData.setEmail(email.getText().toString());
        userData.setMno(phone.getText().toString());
        userData.setImageurl("https://firebasestorage.googleapis.com/v0/b/womensafety-fa171.appspot.com/o/profile_nav_logo.png?alt=media&token=8dc14aa7-7075-499f-bf47-cb5821271c98");

        // Firebase Database Reference

        database= FirebaseDatabase.getInstance();
        myRef=database.getReference();


        // Validate Data

        // Full name

        if (TextUtils.isEmpty(userData.name)) {

            Toast.makeText(Register.this, "Please Enter Full Name ", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }


        // Email
        if (TextUtils.isEmpty(userData.email)) {

            Toast.makeText(Register.this, "Please Enter Email ", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        // password

        if (TextUtils.isEmpty(password)) {

            Toast.makeText(Register.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        // repassword

        if (TextUtils.isEmpty(repassword)) {

            Toast.makeText(Register.this, "Please Enter RePassword", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        if (password.length() < 6) {

            Toast.makeText(Register.this, "Password Must be more than 6 digit & less than 1 digit", Toast.LENGTH_SHORT).show();
        }


        // Mobile Number

        if (TextUtils.isEmpty(userData.mno)) {

            Toast.makeText(Register.this, "Please Enter Mobile Number ", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        if (userData.mno.length() < 10) {

            Toast.makeText(Register.this, "Mobile no. must be 10 digit number! Enter Valid number. ", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }


        if (!password.equals(repassword)){
            Toast.makeText(this, "Password Not Matched", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        // Validation Done !!


        if (password.equals(repassword) && userData.mno.length() == 10 ) {

//            buttonProgress.setVisibility(View.VISIBLE);
            //      CreateAccount_Text.setVisibility(View.GONE);
            firebaseAuth.createUserWithEmailAndPassword(userData.email, password)
                    .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                myRef.child("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(userData);
                                progressDialog.dismiss();
                                Toast.makeText(Register.this, "Registeration Done", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(getApplicationContext(), Login.class));
                                startActivity(new Intent(Register.this, LoginActivity.class));
                                finish();

                            } else {
                                String msg = task.getException().toString();
                                Toast.makeText(Register.this, "Error:" + msg, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                            // ...
                        }


                    });

        }

    }

}