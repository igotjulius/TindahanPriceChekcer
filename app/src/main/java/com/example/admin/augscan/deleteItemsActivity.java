package com.example.admin.augscan;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class deleteItemsActivity extends AppCompatActivity {
    public static TextView resultdeleteview;
    private FirebaseAuth firebaseAuth;
    Button scantodelete, deletebtn;
    RecyclerView mrecyclerview;
    DatabaseReference deldatabaseReference, serdatabaseReference;
    ImageButton searchbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_items);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String finaluser= users.getEmail();
        String resultemail = finaluser.replace(".","");
        deldatabaseReference = FirebaseDatabase.getInstance("https://inventory-a1b50-default-rtdb.firebaseio.com/").getReference("Users");
        serdatabaseReference = FirebaseDatabase.getInstance("https://inventory-a1b50-default-rtdb.firebaseio.com/").getReference("Users").child(resultemail).child("Items");
        resultdeleteview = findViewById(R.id.barcodedelete);
        scantodelete = findViewById(R.id.buttonscandelete);
        deletebtn= findViewById(R.id.deleteItemToTheDatabasebtn);
        mrecyclerview = findViewById(R.id.recyclerViews);
        searchbtn = findViewById(R.id.imageButtonsearch2);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        mrecyclerview.setLayoutManager(manager);
        mrecyclerview.setHasFixedSize(true);

        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));


        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchtext = resultdeleteview.getText().toString();
                firebasesearch(searchtext);
            }
        });
        scantodelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanCodeActivitydel.class));
            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletefrmdatabase();
            }
        });


    }


    public void deletefrmdatabase()
    {
        String deletebarcodevalue = resultdeleteview.getText().toString();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String finaluser=users.getEmail();
        String resultemail = finaluser.replace(".","");
        if(!TextUtils.isEmpty(deletebarcodevalue)){
            deldatabaseReference.child(resultemail).child("Items").child(deletebarcodevalue).removeValue();
            Toast.makeText(deleteItemsActivity.this,"Item is Deleted",Toast.LENGTH_SHORT).show();
            resultdeleteview.setText("");
        }
        else{
            Toast.makeText(deleteItemsActivity.this,"Please scan Barcode",Toast.LENGTH_SHORT).show();
        }
    }
    public void firebasesearch(String searchtext) {
        Query firebaseSearchQuery = serdatabaseReference.orderByChild("itembarcode").startAt(searchtext).endAt(searchtext + "\uf8ff");
        FirebaseRecyclerAdapter<Items, scanItemsActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Items, scanItemsActivity.UsersViewHolder>
                (Items.class,
                        R.layout.list_layout,
                        scanItemsActivity.UsersViewHolder.class,
                        firebaseSearchQuery) {
            @Override
            protected void populateViewHolder(scanItemsActivity.UsersViewHolder viewHolder, Items model, int position) {

                viewHolder.setDetails(getApplicationContext(),model.getItembarcode(),model.getItemcategory(),model.getItemname(),model.getItemprice());
            }
        };
        mrecyclerview.setAdapter(firebaseRecyclerAdapter);
    }

}
