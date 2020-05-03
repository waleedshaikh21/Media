package com.example.media;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private TextView SignTV, SignupTextBottom;
    private Button googlesignin;

    private FirebaseAuth mAuth;
    private FirebaseAuth objectFirebaseAuth;
    private static final String CollectionName = "Users";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    DocumentReference objectDocumentReference;
    Task<QuerySnapshot> objectDocumentReference2;

    private FirebaseFirestore objectFirebaseFirestore;

    private ProgressBar bar;
    private final static int RC_SIGN_IN = 231;

    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ConnectWithXML();
    }

    private void ConnectWithXML() {
        try {
            SignTV = findViewById(R.id.SignTV);
            SignupTextBottom = findViewById(R.id.STV);

            SignTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Change();
                }
            });
            SignupTextBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangetoSignup();
                }
            });

            googlesignin = findViewById(R.id.LoginGoogle);
            mAuth = FirebaseAuth.getInstance();

            objectFirebaseAuth = FirebaseAuth.getInstance();
            objectFirebaseFirestore = FirebaseFirestore.getInstance();

            bar = findViewById(R.id.ProgressBar);
            createRequest();
            googlesignin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                    bar.setVisibility(View.VISIBLE);
                }
            });

        } catch (Exception ex) {
            Toast.makeText(this, "ConnectWithXML: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void Change() {
        startActivity(new Intent(Signup.this, MainActivity.class));
        finish();
    }

    public void ChangetoSignup() {
        try {
            startActivity(new Intent(Signup.this, PageForSignup.class));
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "ChangeToSignup: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Signup.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Map<String, Object> objectMap = new HashMap<>();
                            objectMap.put("Username", mAuth.getCurrentUser().getDisplayName().toString());
                            objectMap.put("Email", mAuth.getCurrentUser().getEmail().toString());
                            objectFirebaseFirestore.collection(CollectionName)
                                    .document(mAuth.getCurrentUser().getDisplayName().toString()).set(objectMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            bar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(Signup.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            bar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(Signup.this, "Fails to add data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            Intent intent = new Intent(getApplicationContext(), MainPage.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Signup.this, "signInWithCredential:failure" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
