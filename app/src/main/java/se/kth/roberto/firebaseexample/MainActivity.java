package se.kth.roberto.firebaseexample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MY ACTIVITY - FIREBASE";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    public void loadStudents(View view) {
        final DatabaseReference reference = database.getReference("students");
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot m : dataSnapshot.getChildren() ) {
                            String text = m.child("name").getValue(String.class);
                            Log.d(TAG, "Key is " + m.getKey() + " Value is: " + text);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }

    public void registerStudentEvent(View view) {
        final DatabaseReference reference = database.getReference();
        DatabaseReference ref = reference.child("students").child("0");
        ref.addValueEventListener(
                new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot m : dataSnapshot.getChildren() ) {
                            Log.d(TAG, "Key is " + m.getKey() + " Value is: " + m.getValue());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void changeName(View view) {
        DatabaseReference ref = database.getReference().child("students").child("0");
        ref.child("name").setValue("Erik");
        //ref.child("surname").setValue("Erikson");
        ref.child("nationality").setValue("Italian");

        ref.child("surname").setValue(null);
    }

    public void addStudent(View view) {
        HashMap<String, Object> va = new HashMap<String, Object>();
        HashMap<String, Object> grades = new HashMap<String, Object>();

        grades.put("DD2536", "A");
        grades.put("DD2537", "B");

        va.put("name", "New student");
        va.put("birthday", "12/12/2008");
        va.put("grades", grades);

        DatabaseReference ref = database.getReference().child("students");
        DatabaseReference ref2 = ref.push();
        ref2.setValue(va);
    }


    public void logout(View view) {
        mAuth.signOut();
    }

    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void login(View view) {
        mAuth.signInWithEmailAndPassword("robertog@kth.se", "123456")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, "Failure",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
