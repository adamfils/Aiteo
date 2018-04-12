package com.adamapps.aiteo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class NewPost extends AppCompatActivity {

    Button createPostButton;
    EditText postTextContent;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;
    DatabaseReference postReference;
    DatabaseReference publicPost;
    DatabaseReference userPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        firebaseDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        postTextContent = findViewById(R.id.post_text_input);
        if (auth.getCurrentUser() != null)
            postReference = firebaseDatabase.getReference().child("post");
        userPost = firebaseDatabase.getReference().child("userPost");


        createPostButton = findViewById(R.id.create_post_btn);
        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(postTextContent.getText().toString())){
                    YoYo.with(Techniques.Shake).duration(500).playOn(createPostButton);
                    return;
                }
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", new Locale("en", "US"));
                SimpleDateFormat DateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("en", "US"));
                String time = simpleDateFormat.format(date);
                String dates = DateFormat.format(date);
                //Toast.makeText(NewPost.this, "" + DateFormat.format(date1), Toast.LENGTH_SHORT).show();
                YoYo.with(Techniques.RubberBand).duration(500).playOn(createPostButton);
                final HashMap<String,Object> map = new HashMap<>();
                map.put("time",time);
                map.put("date",dates);
                map.put("caption",postTextContent.getText().toString());
                map.put("uid",auth.getCurrentUser().getUid());
                userPost.child(auth.getCurrentUser().getUid()).push()
                        .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        postReference.push().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                onBackPressed();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NewPost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewPost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                //Toast.makeText(NewPost.this, ""+map, Toast.LENGTH_SHORT).show();


            }
        });

    }
}
