package com.example.ikhsanwisnuadjig.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference mRef;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("order");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();

                Order order = new Order();
                order.setDari("A");
                order.setKe("B");

                final String postKey = mRef.push().getKey();
                mRef.child(postKey).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        progressDialog.hide();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView tvDari;

        public OrderViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            tvDari = (TextView) itemView.findViewById(R.id.tv_recycler_dari);
        }

        public void setTvDari(String dari){
            tvDari.setText(dari);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Order,OrderViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(
                Order.class,
                R.layout.list_item,
                OrderViewHolder.class,
                mRef
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Order model, int position) {

                final String postId = getRef(position).getKey();

                viewHolder.tvDari.setText(model.getDari());

                viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View ciew){
                        mRef.child(postId).child("dari").setValue("popo");
                    }
                });

                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View view) {
                        mRef.child(postId).setValue(null);
                        return false;
                    }
                });

//                viewHolder.mView.setOnClickListener(new View.OnLongClickListener(){
//                    @Override
//                    public boolean onLongClick(View view){
//                        mRef.child(postId).setValue(null);
//                        return false;
//                    }
//                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
}