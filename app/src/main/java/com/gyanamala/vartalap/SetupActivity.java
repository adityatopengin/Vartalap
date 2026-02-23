package com.gyanamala.vartalap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetupActivity extends AppCompatActivity {

    private TextInputLayout inputLayout;
    private TextInputEditText editUsername;
    private MaterialButton btnContinue;
    
    private DatabaseReference usersRef;
    private SharedPreferences prefs;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        prefs = getSharedPreferences("VartalapPrefs", MODE_PRIVATE);
        if (prefs.contains("my_username")) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_setup);

        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        inputLayout = findViewById(R.id.input_layout_username);
        editUsername = findViewById(R.id.edit_username);
        btnContinue = findViewById(R.id.btn_continue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requestedName = editUsername.getText().toString().trim();
                
                if (requestedName.isEmpty()) {
                    inputLayout.setError("Username cannot be empty");
                    return;
                }
                if (requestedName.length() < 3) {
                    inputLayout.setError("Name must be at least 3 letters");
                    return;
                }

                inputLayout.setError(null);
                performHandshake(requestedName);
            }
        });
    }

    private void performHandshake(final String username) {
        btnContinue.setEnabled(false);
        btnContinue.setText("Verifying...");

        usersRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String registeredDeviceId = snapshot.child("deviceId").getValue(String.class);
                    
                    if (deviceId.equals(registeredDeviceId)) {
                        saveAndProceed(username);
                    } else {
                        inputLayout.setError("This name is taken by another device.");
                        resetButton();
                    }
                } else {
                    usersRef.child(username).child("deviceId").setValue(deviceId);
                    saveAndProceed(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                inputLayout.setError("Network error. Try again.");
                resetButton();
            }
        });
    }

    private void saveAndProceed(String username) {
        prefs.edit().putString("my_username", username).apply();
        goToMainActivity();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void resetButton() {
        btnContinue.setEnabled(true);
        btnContinue.setText("Continue");
    }
}
