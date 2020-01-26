package com.example.meow;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private EditText emailET;
    private EditText passwordET;
     FirebaseAuth firebaseAuth;

    @VisibleForTesting
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();

        setButtonListeners();
    }
    private void setButtonListeners(){
        //login button
        findViewById(R.id.login_b).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRegistrationLogin();
            }
        });
        


//

//        //update password - for signed in user
//        findViewById(R.id.update_password_b).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updatePassword();
//            }
//        });


    }
    @Override
    public void onStart() {
        super.onStart();
        showAppropriateOptions();
    }
    private void  handleRegistrationLogin(){
        final String email = emailET.getText().toString();
        final String password = passwordET.getText().toString();

        if (!validateEmailPass(email, password)) {
            return;
        }

        //show progress dialog
        showProgressDialog();

        //perform login and account creation depending on existence of email in firebase
        performLoginOrAccountCreation(email, password);
    }
    private void performLoginOrAccountCreation(final String email, final String password){
        firebaseAuth.fetchProvidersForEmail(email).addOnCompleteListener(
                this, new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "checking to see if user exists in firebase or not");
                            ProviderQueryResult result = task.getResult();

                            if(result != null && result.getProviders()!= null
                                    && result.getProviders().size() > 0){
                                Log.d(TAG, "User exists, trying to login using entered credentials");
                                performLogin(email, password);
                            }else{
                                Log.d(TAG, "User doesn't exist, creating account");
                                registerAccount(email, password);
                            }
                        } else {
                            Log.w(TAG, "User check failed", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "There is a problem, please try again later.",
                                    Toast.LENGTH_SHORT).show();

                        }
                        //hide progress dialog
                        hideProgressDialog();
                        //enable and disable login, logout buttons depending on signin status
                        showAppropriateOptions();
                    }
                });
    }
    private void performLogin(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "login success");
                        } else {
                            Log.e(TAG, "Login fail", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //hide progress dialog
                        hideProgressDialog();
                        //enable and disable login, logout buttons depending on signin status
                        showAppropriateOptions();
                    }
                });
    }
    void registerAccount(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "account created");
                        } else {
                            Log.d(TAG, "register account failed", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "account registration failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //hide progress dialog
                        hideProgressDialog();
                        //enable and disable login, logout buttons depending on signin status
                        showAppropriateOptions();
                    }
                });
    }

    private boolean validateEmailPass(String email , String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            emailET.setError("Required.");
            valid = false;
        }else if(!email.contains("@")){
            emailET.setError("Not an email id.");
            valid = false;
        } else{
            emailET.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordET.setError("Required.");
            valid = false;
        }else if(password.length() < 6){
            passwordET.setError("Min 6 chars.");
            valid = false;
        }else {
            passwordET.setError(null);
        }

        return valid;
    }
//    private boolean validateResetPassword(String password) {
//        boolean valid = true;
//        if (TextUtils.isEmpty(password) || password.length() < 6) {
//            valid = false;
//        }
//        return valid;
//    }
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait!");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void showAppropriateOptions(){
        hideProgressDialog();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Toast.makeText(this, user.getDisplayName(), Toast.LENGTH_SHORT).show();
            Intent i=new Intent(MainActivity.this,Problem.class);
            startActivity(i);
            finish();

            // Send to Problem page
        } else {
            Toast.makeText(this, "Login please", Toast.LENGTH_SHORT).show();
            //Login
        }
    }

//    private void sendEmailVerificationMsg() {
//        findViewById(R.id.verify_b).setEnabled(false);
//
//        final FirebaseUser user = firebaseAuth.getCurrentUser();
//        user.sendEmailVerification()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        findViewById(R.id.verify_b).setEnabled(true);
//                        if (task.isSuccessful()) {
//                            Toast.makeText(MainActivity.this,
//                                    "Verification email has been sent to " + user.getEmail(),
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.e(TAG, "Error in sending verification email",
//                                    task.getException());
//                            Toast.makeText(MainActivity.this,
//                                    "Failed to send verification email.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
    //non-singed in user reset password email
//    private void sendResetPasswordEmail() {
//        final String email = ((EditText) findViewById(R.id.reset_password_email))
//                .getText().toString();
//        firebaseAuth.sendPasswordResetEmail(email)
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//                        if (task.isSuccessful()) {
//                            Toast.makeText(MainActivity.this,
//                                    "Reset password code has been emailed to "
//                                            + email,
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.e(TAG, "Error in sending reset password code",
//                                    task.getException());
//                            Toast.makeText(MainActivity.this,
//                                    "There is a problem with reset password, try later.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

//        private void updatePassword () {
//
//            final FirebaseUser user = firebaseAuth.getCurrentUser();
//            final String newPwd = ((EditText) findViewById(R.id.update_password_t)).getText().toString();
//            if (!validateResetPassword(newPwd)) {
//                Toast.makeText(MainActivity.this,
//                        "Invalid password, please enter valid password",
//                        Toast.LENGTH_SHORT).show();
//                return;
//            }
//            try {
//                user.updatePassword(newPwd)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(MainActivity.this,
//                                            "Password has been updated",
//                                            Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Log.e(TAG, "Error in updating passowrd",
//                                            task.getException());
//                                    Toast.makeText(MainActivity.this,
//                                            "Failed to update passwrod.",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//            }
//            catch (Exception e)
//            {
//                Toast.makeText(this, "Unable to update Password.", Toast.LENGTH_SHORT).show();
//            }
//            }



    private void logOut() {
        firebaseAuth.signOut();
        showAppropriateOptions();
    }
    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}