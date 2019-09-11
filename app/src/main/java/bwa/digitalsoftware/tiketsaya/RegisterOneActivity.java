package bwa.digitalsoftware.tiketsaya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterOneActivity extends AppCompatActivity {

    EditText username, password, email_address;
    Button btn_continue;
    LinearLayout btn_back;

    DatabaseReference reference;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_one);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email_address = findViewById(R.id.email_address);
        btn_continue = findViewById(R.id.btn_continue);
        btn_back = findViewById(R.id.btn_back);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_continue.setEnabled(false);
                btn_continue.setText("Loading...");

                //Menyimpan data pada local storage (handphone)
                SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(username_key, username.getText().toString());
                editor.apply();

                //Simpan ke dalam database
                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username.getText().toString());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("username").setValue(username.getText().toString());
                        dataSnapshot.getRef().child("password").setValue(password.getText().toString());
                        dataSnapshot.getRef().child("email_address").setValue(email_address.getText().toString());
                        dataSnapshot.getRef().child("user_balance").setValue(700);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //Berpindah Activity
                Intent gotoregistertwo = new Intent(RegisterOneActivity.this, RegisterTwoActivity.class);
                startActivity(gotoregistertwo);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backtosignin = new Intent(RegisterOneActivity.this, SigninActivity.class);
                startActivity(backtosignin);
            }
        });
    }
}
