package com.example.media;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class PageForSignup extends AppCompatActivity {

    private Button signup;
    private TextView Signin;

    private EditText emailET, paswwordET, username;
    private ProgressBar bar;

    private FirebaseAuth objectFirebaseAuth;
    private static final String CollectionName = "Users";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    DocumentReference objectDocumentReference;
    Task<QuerySnapshot> objectDocumentReference2;
    private FirebaseFirestore objectFirebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_for_signup);

        ConnectWithXML();
    }

    private void ConnectWithXML() {
        try {
            signup = findViewById(R.id.Signup);
            Signin = findViewById(R.id.SignTV);

            emailET = findViewById(R.id.Username);
            paswwordET = findViewById(R.id.Password);
            username = findViewById(R.id.Uname);

            Signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Change();
                }
            });
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkuser();
                }
            });

            mAuth = FirebaseAuth.getInstance();
            objectFirebaseAuth = FirebaseAuth.getInstance();

            objectFirebaseFirestore = FirebaseFirestore.getInstance();

            bar = findViewById(R.id.ProgressBar);
        } catch (Exception ex) {
            Toast.makeText(this, "ConnectWithXML: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void Change() {
        startActivity(new Intent(PageForSignup.this, MainActivity.class));
        finish();
    }

    private void checkuser() {
        try {
            bar.setVisibility(View.VISIBLE);
            if (!emailET.getText().toString().isEmpty()) {
                if (objectFirebaseAuth != null) {
                    signup.setEnabled(false);
                    objectFirebaseAuth.fetchSignInMethodsForEmail(emailET.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    boolean check = task.getResult().getSignInMethods().isEmpty();
                                    if (!check) {
                                        signup.setEnabled(true);

                                        bar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(PageForSignup.this, "User Already Exists", Toast.LENGTH_SHORT).show();

                                    } else if (check) {
                                        bar.setVisibility(View.INVISIBLE);

                                        signup.setEnabled(true);
                                        Signup();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            bar.setVisibility(View.INVISIBLE);
                            signup.setEnabled(true);

                            Toast.makeText(PageForSignup.this, "Fails To Check If User Exists" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                emailET.requestFocus();
                signup.setEnabled(true);

                bar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Email and Password is Empty", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            bar.setVisibility(View.INVISIBLE);
            signup.setEnabled(true);

            Toast.makeText(this, "Check User Error" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void Signup() {
        try {
            bar.setVisibility(View.VISIBLE);
            if (!emailET.getText().toString().isEmpty()
                    &&
                    !paswwordET.getText().toString().isEmpty()) {
                if (objectFirebaseAuth != null) {
                    bar.setVisibility(View.INVISIBLE);
                    signup.setEnabled(false);

                    objectFirebaseAuth.createUserWithEmailAndPassword(emailET.getText().toString(),
                            paswwordET.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            objectFirebaseFirestore.collection(CollectionName).document(username.getText().toString()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.getResult().exists()) {
                                                bar.setVisibility(View.INVISIBLE);
                                                username.requestFocus();
                                                Toast.makeText(PageForSignup.this, "Document Already Exists", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Map<String, Object> objectMap = new HashMap<>();
                                                objectMap.put("Username", username.getText().toString());
                                                objectMap.put("Email", emailET.getText().toString());
                                                objectFirebaseFirestore.collection(CollectionName)
                                                        .document(username.getText().toString()).set(objectMap)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                bar.setVisibility(View.INVISIBLE);
                                                                Toast.makeText(PageForSignup.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                bar.setVisibility(View.INVISIBLE);
                                                                Toast.makeText(PageForSignup.this, "Fails to add data", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                            if (authResult.getUser() != null) {

                                objectFirebaseAuth.signOut();
//                                emailET.setText("");
//                                username.setText("");
//                                paswwordET.setText("");
                                emailET.requestFocus();

                                signup.setEnabled(true);
                                bar.setVisibility(View.INVISIBLE);
                                Change();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            signup.setEnabled(true);
                            emailET.requestFocus();

                            bar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PageForSignup.this, "Failed To Create User" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (emailET.getText().toString().isEmpty()) {
                signup.setEnabled(true);
                bar.setVisibility(View.INVISIBLE);

                emailET.requestFocus();
                Toast.makeText(this, "Please Enter The Email", Toast.LENGTH_SHORT).show();
            } else if (paswwordET.getText().toString().isEmpty()) {
                signup.setEnabled(true);
                bar.setVisibility(View.INVISIBLE);

                paswwordET.requestFocus();
                Toast.makeText(this, "Please Enter The Password", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {

            signup.setEnabled(true);
            bar.setVisibility(View.INVISIBLE);

            emailET.requestFocus();
            Toast.makeText(this, "Signup Error" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
