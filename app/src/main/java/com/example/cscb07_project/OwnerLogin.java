package com.example.cscb07_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OwnerLogin extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference ref;
    private EditText inputEmail;
    private EditText inputPassword;
    private int tolerance = 3;
    private boolean found = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_login);
    }

    public void onOwnerRegister(View view){
        startActivity(new Intent(this, OwnerRegister.class));
    }

    public void ownerLogin(View view){
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);

        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Owners");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String e = ds.child("email").getValue(String.class);
                    String p = ds.child("password").getValue(String.class);
                    if(e.equals(email)){
                        found = true;
                        // System.out.println("found email");
                        String username = ds.child("username").getValue(String.class);
                        String login = ds.child("login").getValue(String.class);
                        String locked = ds.child("locked").getValue(String.class);
                        if(locked.equals("true")){
                            Toast.makeText(OwnerLogin.this, "Your account has been locked", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(login.equals("true")){
                            Intent intent = new Intent(OwnerLogin.this, StoreOwnerPage.class);
                            intent.putExtra("message", username);
                            startActivity(intent);
                            return;
                        }
                        else if(p.equals(password)){
                            Toast.makeText(OwnerLogin.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            write(username, "login", "true");
                            Intent intent = new Intent(OwnerLogin.this, StoreOwnerPage.class);
                            intent.putExtra("message", username);
                            startActivity(intent);
                            return;
                        }
                        else{
                            tolerance = tolerance - 1;
                            if(tolerance > 0){
                                inputEmail.setText("");
                                inputPassword.setText("");
                                Toast.makeText(OwnerLogin.this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                            if(tolerance == 0){
                                write(username, "locked", "true");
                                Toast.makeText(OwnerLogin.this, "Your account will be locked", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        System.out.println(found);

        if(!found){
            Toast.makeText(OwnerLogin.this, "Email not registered", Toast.LENGTH_SHORT).show();
            System.out.println("Email not found");
        }
    }

    public void write(String username, String field, String data){
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Owners");
        ref.child(username).child(field).setValue(data);
    }
}