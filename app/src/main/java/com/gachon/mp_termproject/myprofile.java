package com.gachon.mp_termproject;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

public class myprofile extends AppCompatActivity {

    TextView myname;
    TextView mymoney;
    String userEmail;
    String userName;
    String phoneNum;
    int reward;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        myname = findViewById(R.id.my_name);
        mymoney = findViewById(R.id.my_money);

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("email");
        userName = intent.getStringExtra("name");
        phoneNum=intent.getStringExtra("phone");
        reward=intent.getIntExtra("reward", 0);

        readUserInfo(userName, reward);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        Button change_name=findViewById(R.id.change_name);
        change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeNameDialog();
            }
        });

        Button change_pass=findViewById(R.id.change_pass);
        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangePasswordDialog();
            }
        });

    }

    private void readUserInfo(String name, int reward) {
        myname.setText(name);
        mymoney.setText(String.valueOf(reward));
    }

    private void showChangeNameDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_name, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.edit_text_name);

        dialogBuilder.setTitle("Change Name");
        dialogBuilder.setMessage("Enter your new name:");
        dialogBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newName = editTextName.getText().toString();
                updateUserName(newName);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 취소 버튼 클릭 시 아무 동작하지 않음
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void updateUserName(String newName) {
        DatabaseReference userRef = mDatabaseRef.child("UserAccount");
        userRef.orderByChild("emailId").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userId = snapshot.getKey();
                        userRef.child(userId).child("name").setValue(newName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 업데이트 실패 시 처리할 로직
            }
        });
    }


    private void showChangePasswordDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextCurrentPassword = dialogView.findViewById(R.id.edit_text_current_password);
        final EditText editTextNewPassword = dialogView.findViewById(R.id.edit_text_new_password);

        dialogBuilder.setTitle("Change Password");
        dialogBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String currentPassword = editTextCurrentPassword.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();

                // 비밀번호 변경 로직 호출
                changeUserPassword(currentPassword, newPassword);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 취소 버튼 클릭 시 아무 동작하지 않음
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void changeUserPassword(String currentPassword, final String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(userEmail, currentPassword);

        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // 인증 성공
                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 비밀번호 변경 성공
                        Toast.makeText(myprofile.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 비밀번호 변경 실패
                        Toast.makeText(myprofile.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 인증 실패
                Toast.makeText(myprofile.this, "Authentication failed. Please check your current password", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
