package com.example.media;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private TextView Signup;
    private EditText emailET, paswwordET;

    private Button signin, googlesignin;
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private FirebaseAuth objectFirebaseAuth;
    private LottieAnimationView lottie;

    private ProgressBar bar;
    private final static int RC_SIGN_IN = 231;

    private CallbackManager mCallbackManager;
    private NavigationView objectNavigationView;
    private ImageView headerIV;

    private DrawerLayout objectDrawerLayout;
    private Toolbar objectToolbar;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), MainPage.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectWithXML();
    }

    private void ConnectWithXML() {
        try {
            Signup = findViewById(R.id.SignupTextView);
            emailET = findViewById(R.id.Username);

            signin = findViewById(R.id.Login);
            paswwordET = findViewById(R.id.Password);

            googlesignin = findViewById(R.id.LoginGoogle);

            Signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Change();
                }
            });
            mAuth = FirebaseAuth.getInstance();

            objectFirebaseAuth = FirebaseAuth.getInstance();
            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signinuser();
                }
            });

            lottie = findViewById(R.id.Lottie);

            bar = findViewById(R.id.ProgressBar);
            createRequest();
            googlesignin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                    lottie.playAnimation();
                    lottie.setVisibility(View.VISIBLE);
//                    bar.setVisibility(View.VISIBLE);
                }
            });

        } catch (Exception ex) {
            Toast.makeText(this, "ConnectWithXML: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void Change() {
        startActivity(new Intent(MainActivity.this, Signup.class));
        finish();
    }

    public void signinuser() {
        try {
            bar.setVisibility(View.VISIBLE);
            if (!emailET.getText().toString().isEmpty() && !paswwordET.getText().toString().isEmpty()) {
                if (objectFirebaseAuth.getCurrentUser() != null) {
                    objectFirebaseAuth.signOut();
                    signin.setEnabled(false);

                    bar.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, "User Logged Out Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    objectFirebaseAuth.signInWithEmailAndPassword(emailET.getText().toString(),
                            paswwordET.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    signin.setEnabled(true);
                                    bar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(MainActivity.this, "User Logged In", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, MainPage.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            signin.setEnabled(true);
                            emailET.requestFocus();

                            bar.setVisibility(View.INVISIBLE);
                            lottie.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Fails To Sig-in User: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (emailET.getText().toString().isEmpty()) {
                signin.setEnabled(true);
                emailET.requestFocus();

                bar.setVisibility(View.INVISIBLE);
                lottie.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Please Enter The Email", Toast.LENGTH_SHORT).show();
            } else if (paswwordET.getText().toString().isEmpty()) {
                signin.setEnabled(true);
                paswwordET.requestFocus();

                bar.setVisibility(View.INVISIBLE);
                lottie.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Please Enter The Password", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {

            signin.setEnabled(true);
            emailET.requestFocus();

            bar.setVisibility(View.INVISIBLE);
            lottie.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Logging In Error" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void createRequest() {
        //Creating a send request to open a Pop-up so that user can Log-in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        //Intent in which you can select your Google account
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bar.setVisibility(View.VISIBLE);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                bar.setVisibility(View.INVISIBLE);
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                bar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Login Failed: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), MainPage.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "signInWithCredential:failure" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
