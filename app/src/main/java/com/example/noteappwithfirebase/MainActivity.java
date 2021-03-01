package com.example.noteappwithfirebase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    NoteRecyclerAdapter noteRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.mainPageRecyclerView);

        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNavigationView);
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(bottomNavigationView,navController);

    }

    private void startLoginActivity() {
        Intent i = new Intent(this,LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_Logout:
                AuthUI.getInstance().signOut(this);
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.addFragment:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);

        if(noteRecyclerAdapter!= null) {
            noteRecyclerAdapter.stopListening();
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser() == null) {
            startLoginActivity();
            return;
        }

        initRecyclerView(firebaseAuth.getCurrentUser());

//        firebaseAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
//            @Override
//            public void onSuccess(GetTokenResult getTokenResult) {
//                Log.d(TAG, "onSuccess: "+getTokenResult.getToken());
//            }
//        });
    }

    public void openFragment(View view) {
        showAlertDialog();
    }

    private void showAlertDialog() {
        EditText noteAddText = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Add Note")
                .setView(noteAddText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: " + noteAddText.getText());
                        addNote(noteAddText.getText().toString());
                    }
                })
                .setNegativeButton("Cancel",null)
                .show();
                }

                private void addNote(String text) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Note note = new Note(text,false,new Timestamp(new Date()),userId);

                    FirebaseFirestore.getInstance().collection("notes")
                            .add(note)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "onSuccess: Added Successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onFailure: ",e);
                                }
                            });
                }

                private void initRecyclerView(FirebaseUser user) {
                    Query query = FirebaseFirestore.getInstance()
                            .collection("notes")
                            .whereEqualTo("userId",user.getUid());

                    FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                            .setQuery(query,Note.class)
                            .build();

                    noteRecyclerAdapter = new NoteRecyclerAdapter(options);
                    recyclerView.setAdapter(noteRecyclerAdapter);

                    noteRecyclerAdapter.startListening();

                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                }

                ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        if(direction ==ItemTouchHelper.LEFT) {
                            Toast.makeText(MainActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();

                            NoteRecyclerAdapter.NoteViewHolder noteViewHolder = (NoteRecyclerAdapter.NoteViewHolder) viewHolder;
                            noteViewHolder.deleteItem();
                        }
                    }
                };
}