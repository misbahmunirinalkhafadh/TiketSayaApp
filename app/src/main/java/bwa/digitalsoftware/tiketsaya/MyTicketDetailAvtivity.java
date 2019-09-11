package bwa.digitalsoftware.tiketsaya;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyTicketDetailAvtivity extends AppCompatActivity {

    DatabaseReference reference;

    TextView tv_nama_wisata, tv_lokasi, tv_date_wisata, tv_time_wisata, tv_ketentuan;
    LinearLayout btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ticket_detail_avtivity);

        tv_nama_wisata = findViewById(R.id.tv_nama_wisata);
        tv_lokasi = findViewById(R.id.tv_lokasi);
        tv_date_wisata = findViewById(R.id.tv_date_wisata);
        tv_time_wisata = findViewById(R.id.tv_time_wisata);
        tv_ketentuan = findViewById(R.id.tv_ketentuan);
        btn_back = findViewById(R.id.btn_back);

        //mengambil data intent
        Bundle bundle = getIntent().getExtras();
        final String nama_wisata_baru = bundle.getString("nama_wisata");

        //mengambil data dari database
        reference = FirebaseDatabase.getInstance().getReference().child("Wisata").child(nama_wisata_baru);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_nama_wisata.setText(dataSnapshot.child("nama_wisata").getValue().toString());
                tv_lokasi.setText(dataSnapshot.child("lokasi").getValue().toString());
                tv_date_wisata.setText(dataSnapshot.child("date_wisata").getValue().toString());
                tv_time_wisata.setText(dataSnapshot.child("time_wisata").getValue().toString());
                tv_ketentuan.setText(dataSnapshot.child("ketentuan").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoprofile = new Intent(MyTicketDetailAvtivity.this,MyProfileActivity.class);
                startActivity(gotoprofile);
            }
        });
    }
}
