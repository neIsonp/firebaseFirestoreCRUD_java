package com.example.firebasefirestorecrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditorActivity extends AppCompatActivity {

    EditText inputName, inputEmail;
    Button btnSave;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;
    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        btnSave = findViewById(R.id.btnSave);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("loading the data");

        btnSave.setOnClickListener(v -> {
            if(inputName.getText().length() > 0 && inputEmail.getText().length() > 0){
                saveData(inputName.getText().toString(), inputEmail.getText().toString());

            }else{
                Toast.makeText(this, "Please Insert the data", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();

        if(intent != null){
            id = intent.getStringExtra("id");
            inputName.setText(intent.getStringExtra("name"));
            inputEmail.setText(intent.getStringExtra("email"));
        }
    }

    private void saveData(String name, String email){

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);

        progressDialog.show();

        if(id.length() > 0){

            db.collection("users").document(id).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(EditorActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(EditorActivity.this, "Error to update", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                    finish();
                }
            });

        }else{
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(EditorActivity.this, "Save Sucessfuly", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditorActivity.this, "Failed to save the add in firestore", Toast.LENGTH_SHORT).show();
                        }
                    });
        }



    }
}