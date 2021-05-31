package com.example.budgeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgeapp.handler.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;

public class BudgetActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private TextView totalBudgetAmountTextView;
    private RecyclerView recyclerView;

    private DatabaseReference budgeRef;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        fab=findViewById(R.id.fab);
        recyclerView=findViewById(R.id.recyclerView);
        totalBudgetAmountTextView=findViewById(R.id.totalBudgetAmountTextView);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        auth=FirebaseAuth.getInstance();
        budgeRef= FirebaseDatabase.getInstance().getReference().child("budge").child(auth.getCurrentUser().getUid());
        progressDialog=new ProgressDialog(this);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                additem();
            }
        });

       budgeRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               int totalAmount = 0;

               for (DataSnapshot snap: snapshot.getChildren()){
                   Data data = snap.getValue(Data.class);
                   totalAmount += data.getAmount();
                   String sTotal = String.valueOf("Month budget: $"+ totalAmount);
                   totalBudgetAmountTextView.setText(sTotal);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });



    }

    private void additem() {

        //show alertdialog

        AlertDialog.Builder myDialog=new AlertDialog.Builder(this);
        LayoutInflater layoutInflater=LayoutInflater.from(this);
        View view=layoutInflater.inflate(R.layout.input_layout,null);
        myDialog.setView(view);

        final AlertDialog dialog=myDialog.create();
        dialog.setCancelable(false);

        //end alertDialog

        final Spinner itemspinner=view.findViewById(R.id.itemsspinner);
        final EditText amount=view.findViewById(R.id.amount);
        final Button cancel=view.findViewById(R.id.cancel);
        final Button save=view.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String budgeAmount=amount.getText().toString();
                String budgeItem=itemspinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(budgeAmount)){
                    amount.setError("Amount is required");
                    return;
                }
                if (budgeItem.equals("Select item")){
                    Toast.makeText(BudgetActivity.this, "Select a vaild item", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.setMessage("adding a budget item");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    String id=budgeRef.push().getKey();
                    DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
                    Calendar calendar=Calendar.getInstance();
                    String date=dateFormat.format(calendar.getTime());

                    MutableDateTime epoch=new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now=new DateTime();
                    Months months =Months.monthsBetween(epoch, now);

                    Data data=new Data(budgeItem,date,id,null,Integer.parseInt(budgeAmount),months.getMonths());

                    // push data

                    budgeRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(BudgetActivity.this, "Budget item added successful", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(BudgetActivity.this, "Error"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });

                }
                dialog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> optionn=new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(budgeRef,Data.class)
                .build();


        FirebaseRecyclerAdapter<Data,MyViewHolder> adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>(optionn) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setItemAmount("Allocated amount: $"+ model.getAmount());
                holder.setDate("On: "+model.getDate());
                holder.setItemName("BudgetItem: "+model.getItem());

                holder.notes.setVisibility(View.GONE);
                switch (model.getItem()){
                    case "Transport":
                        holder.imageView.setImageResource(R.drawable.transport);
                        break;
                    case "Food":
                        holder.imageView.setImageResource(R.drawable.food);
                        break;
                    case "House":
                        holder.imageView.setImageResource(R.drawable.history);
                        break;
                    case "Entertainment":
                        holder.imageView.setImageResource(R.drawable.entertrainment);
                        break;
                    case "Education":
                        holder.imageView.setImageResource(R.drawable.education);
                        break;
                    case "Charity":
                        holder.imageView.setImageResource(R.drawable.chearity);
                        break;
                    case "Apparel":
                        holder.imageView.setImageResource(R.drawable.shirt);
                        break;
                    case "Health":
                        holder.imageView.setImageResource(R.drawable.hospital);
                        break;
                    case "Personal":
                        holder.imageView.setImageResource(R.drawable.personal);
                        break;
                    case "Other":
                        holder.imageView.setImageResource(R.drawable.other);
                        break;
                }


            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new MyViewHolder(view);
            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ImageView imageView;
        public TextView notes, date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            notes = itemView.findViewById(R.id.note);
            date = itemView.findViewById(R.id.date);

        }

        public  void setItemName (String itemName){
            TextView item = mView.findViewById(R.id.item);
            item.setText(itemName);
        }

        public void setItemAmount(String itemAmount){
            TextView amount = mView.findViewById(R.id. amount);
            amount.setText(itemAmount);
        }

        public void setDate(String itemDate){
            TextView date = mView.findViewById(R.id.date);
            date.setText(itemDate);
        }
    }
}