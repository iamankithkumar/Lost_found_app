package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class dataforlost extends AppCompatActivity {
    private RecyclerView postlist ;
    private DatabaseReference postref, UserRef;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    String current_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataforlost);


        Button buttonforfound = findViewById(R.id.postforlost);
        buttonforfound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dataforlost.this,postlostmain.class);
                startActivity(intent);
            }
        });
        ImageButton buttonforhome = findViewById(R.id.imageButton);
        buttonforhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dataforlost.this,LostFoundmainActivity.class);
                startActivity(intent);
            }
        });

        postlist = (RecyclerView) findViewById(R.id.recyclerviewforlostposts);
        postlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postlist.setLayoutManager(linearLayoutManager);
        fetch();

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Registered Users");


    }

   private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference().child("LostItems");

        FirebaseRecyclerOptions<modellostposts> options =
                new FirebaseRecyclerOptions.Builder<modellostposts>().setQuery(query, new SnapshotParser<modellostposts>() {
                    @NonNull
                    @Override
                    public modellostposts parseSnapshot(@NonNull DataSnapshot snapshot) {

                        return new modellostposts(snapshot.child("FullName").getValue().toString(),
                                snapshot.child("Message").getValue().toString(),


                                snapshot.child("date").getValue().toString(),
                                snapshot.child("time").getValue().toString(),
                                snapshot.child("Imagelink").getValue().toString(),
                                snapshot.child("PhoneNumber").getValue().toString());


                    }
                }).build();

        adapter = new FirebaseRecyclerAdapter<modellostposts, viewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull modellostposts model) {

                holder.setDatetv(modellostposts.getDate());
                holder.setFullNametv(modellostposts.getFullName());
                holder.setMessagetv(modellostposts.getMessage());
                holder.setTimetv(modellostposts.getTime());
                holder.setPhonetv(modelfoundposts.getPhoneNumber());
                Glide.with(holder.checklostimageIv.getContext()).load(modellostposts.getImagelink()).into(holder.checklostimageIv);




            }


            @NonNull
            @Override
            public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lostitemspostviewstyle, parent, false);
                return new viewHolder(view) {
                };
            }
        };
        postlist.setAdapter(adapter);


    }

    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }









    public class viewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root ;


        //views from xml file

        ImageView checklostimageIv;
        TextView FullNametv, datetv, timetv, Messagetv,Phonetv;
        Button FoundButton;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            //init views

            root = itemView.findViewById(R.id.list_root);
            checklostimageIv = itemView.findViewById(R.id.lostimginfeed);
            FullNametv = itemView.findViewById(R.id.lostuser);

            datetv = itemView.findViewById(R.id.lostdate);
            timetv = itemView.findViewById(R.id.losttime);
            Phonetv = itemView.findViewById(R.id.lostpostphonetv);
            Messagetv = itemView.findViewById(R.id.lostmsg);
            FoundButton = itemView.findViewById(R.id.reportbtn);


        }




        public void setFullNametv(String string){
            FullNametv.setText(string);
        }
        public void setMessagetv(String string){
            Messagetv.setText(string);
        }
        public void setDatetv(String string){
            datetv.setText(string);
        }




        public void setTimetv(String string){
            timetv.setText(string);



        }

        public void setPhonetv(String string) {Phonetv.setText(string);

        }
    }
}