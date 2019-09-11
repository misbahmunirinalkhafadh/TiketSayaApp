package bwa.digitalsoftware.tiketsaya;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {

    ImageView photo_edit_profile;
    EditText et_nama_lengkap, et_bio, et_username, et_password, et_email_address;
    Button btn_save, btn_add_new_photo;
    LinearLayout btn_back;

    Uri photo_location;
    Integer photo_max = 1;

    DatabaseReference reference;
    StorageReference storage;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        photo_edit_profile = findViewById(R.id.photo_edit_profile);
        btn_add_new_photo = findViewById(R.id.btn_add_new_photo);

        et_nama_lengkap = findViewById(R.id.et_nama_lengkap);
        et_bio = findViewById(R.id.et_bio);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_email_address = findViewById(R.id.et_email_address);

        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);

        getUsernameLocal();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
        storage = FirebaseStorage.getInstance().getReference().child("Photousers").child(username_key_new);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                et_nama_lengkap.setText(dataSnapshot.child("nama_lengkap").getValue().toString());
                et_bio.setText(dataSnapshot.child("bio").getValue().toString());
                et_username.setText(dataSnapshot.child("username").getValue().toString());
                et_password.setText(dataSnapshot.child("password").getValue().toString());
                et_email_address.setText(dataSnapshot.child("email_address").getValue().toString());
                Picasso.with(EditProfileActivity.this).load(dataSnapshot.child("url_photo_profile").getValue().toString())
                        .centerCrop().fit().into(photo_edit_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ubah state menjadi loading
                btn_save.setEnabled(false);
                btn_save.setText("Loading...");

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("nama_lengkap").setValue(et_nama_lengkap.getText().toString());
                        dataSnapshot.getRef().child("bio").setValue(et_bio.getText().toString());
                        dataSnapshot.getRef().child("username").setValue(et_username.getText().toString());
                        dataSnapshot.getRef().child("password").setValue(et_password.getText().toString());
                        dataSnapshot.getRef().child("email_address").setValue(et_email_address.getText().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                //Validasi untuk file (apakah ada?)
                if (photo_location != null) {
                    final StorageReference storageReference = storage.child(System.currentTimeMillis() + "." + getFileExtension(photo_location));
                    storageReference.putFile(photo_location).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String uri_photo = uri.toString();
                                    reference.getRef().child("url_photo_profile").setValue(uri_photo);
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    //Berpindah Activity
                                    Intent gotomyprofile = new Intent(EditProfileActivity.this, MyProfileActivity.class);
                                    startActivity(gotomyprofile);
                                }
                            });
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            //Berpindah Activity
//                            Intent gotosuccess = new Intent(RegisterTwoActivity.this, SuccessRegisterActivity.class);
//                            startActivity(gotosuccess);
                        }
                    });
                }
            }
        });

        btn_add_new_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPhoto();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backtomyprofile = new Intent(EditProfileActivity.this, HomeActivity.class);
                startActivity(backtomyprofile);
            }
        });
    }

    String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void findPhoto() {
        Intent pic = new Intent();
        pic.setType("image/*");
        pic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pic, photo_max);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == photo_max && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photo_location = data.getData();
            Picasso.with(this).load(photo_location).centerCrop().fit().into(photo_edit_profile);
        }
    }

    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }
}
