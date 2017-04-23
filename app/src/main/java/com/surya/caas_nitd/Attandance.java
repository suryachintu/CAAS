package com.surya.caas_nitd;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Attandance extends AppCompatActivity {

    private static final String TAG = Attandance.class.getSimpleName();
    private DatabaseReference mRef;
    @BindView(R.id.attandance_recycler)
    RecyclerView mRecyclerView;
    private ArrayList<String> mArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attandance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();

        String roll = user.getEmail().split("@")[0];
        Log.e(TAG,roll);

        mRef = FirebaseDatabase.getInstance().getReference();

        mArrayList = new ArrayList<>();

        final AttandanceAdapter adapter = new AttandanceAdapter();

        mRef.child("Students").child("141200035").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.e(TAG,dataSnapshot.toString());

                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {

                    if (snapshot.getValue().equals("Absent")||snapshot.getValue().equals("Present")){

                        String value = snapshot.getValue().toString();

                        String x = (value.equals("Present") ? "P" : "A");

                        mArrayList.add(snapshot.getKey() + "="+ x);
                        adapter.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

    }

    public class AttandanceAdapter extends RecyclerView.Adapter<AttandanceAdapter.ViewHolder>{

        public AttandanceAdapter() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attandance_list_item,parent,false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.mClassTime.setText(mArrayList.get(position).split("=")[0]);
            holder.mStatus.setText(mArrayList.get(position).split("=")[1]);

        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            @BindView(R.id.class_time)
            TextView mClassTime;
            @BindView(R.id.status)
            TextView mStatus;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }

}
