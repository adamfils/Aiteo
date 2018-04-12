package com.adamapps.aiteo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Welcome extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_INN = 100;
    Button googleBtn, skipBtn;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                            .child("userInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userName");
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue(String.class) == null) {
                                dialogView();
                                return;
                            } else {
                                startActivity(new Intent(Welcome.this, Home.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    setContentView(R.layout.activity_welcome);
                    houseKeeping();
                }
            }
        };
    }

    private void houseKeeping() {
        googleBtn = findViewById(R.id.google_sign_in);
        skipBtn = findViewById(R.id.skip_btn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
    }


    private void googleSignIn() {
        YoYo.with(Techniques.RubberBand).duration(500).playOn(googleBtn);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_INN);

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_INN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (auth.getCurrentUser() != null) {
                            if (authResult.getAdditionalUserInfo().isNewUser()) {
                                Toast.makeText(Welcome.this, "Welcome " + auth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                                dialogView();
                            } else {
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                        .child("userInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userName");
                                userRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue(String.class) == null) {
                                            dialogView();
                                            return;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                Toast.makeText(Welcome.this, "Welcome Back " + auth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                            }
                            finish();
                            startActivity(new Intent(Welcome.this, Home.class));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Welcome.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void dialogView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Welcome.this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.single_dialog_view, null);
        final EditText fullName = view.findViewById(R.id.full_name_text);
        final EditText userName = view.findViewById(R.id.user_name_text);
        Button saveButton = view.findViewById(R.id.save_info_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(fullName.getText().toString()) && !TextUtils.isEmpty(userName.getText().toString())) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(view);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("userName", userName.getText().toString().trim());
                    map.put("fullName", fullName.getText().toString().trim());

                    FirebaseDatabase.getInstance().getReference().child("userInfo")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), Home.class));
                        }
                    });

                } else {
                    YoYo.with(Techniques.Shake).duration(500).playOn(view);
                }
            }
        });
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        auth.removeAuthStateListener(authStateListener);
    }
}
