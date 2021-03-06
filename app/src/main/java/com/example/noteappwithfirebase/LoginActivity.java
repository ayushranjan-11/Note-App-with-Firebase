package com.example.noteappwithfirebase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    int AUTHUI_REQUEST_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this,MainActivity.class));
            this.finish();
        }

    }

    public void HandleLoginRegister(View view) {

        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        Intent i = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(provider).setLogo(R.drawable.appyfluxlogo).build();

        startActivityForResult(i,AUTHUI_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AUTHUI_REQUEST_CODE) {
            if(resultCode== RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "onActivityResult: "+user.getEmail());
                if(user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                    //New User
                    Toast.makeText(this, "Welcome new User", Toast.LENGTH_SHORT).show();
                } else {
                    //Old User
                    Toast.makeText(this, "Welcome Back", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                this.finish();

            } else {
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if(response == null) {
                    Log.d(TAG, "onActivityResult: User cancelled the login request");
                } else {
                    Log.e(TAG, "onActivityResult: ",response.getError() );
                }
            }
        }
    }
}