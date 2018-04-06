package com.example.theresa.todoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView taskList;
    private DatabaseReference mDatabase;
    private TextView emptyView;
    private Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.holder, new WeatherFragment())
                    .commit();
        }

        long mdates= System.currentTimeMillis();
        SimpleDateFormat dateForms= new SimpleDateFormat("MMMM dd, yyyy");
        String dateStrings = dateForms.format(mdates);

        Calendar cal= Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, +1);
        String endDate= cal.getTime().toString();

        SimpleDateFormat dateFormatss= new SimpleDateFormat("MMMM dd, yyyy");
        Date dates= new Date(endDate);
        String tom = dateFormatss.format(dates);


        emptyView = (TextView) findViewById(R.id.empty_view);
        taskList= (RecyclerView) findViewById(R.id.task_list);
        taskList.setHasFixedSize(true);
        taskList.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tasks");
        mQuery = mDatabase.orderByChild("time").startAt(dateStrings).endAt(tom);

        TextView bannerDay = (TextView) findViewById(R.id.bannerDay);
        TextView bannerDate= (TextView) findViewById(R.id.bannerDate);

        SimpleDateFormat dateFormat= new SimpleDateFormat("EEEE,");
        Date date= new Date();
        String dayOfTheWeek = dateFormat.format(date);
        bannerDay.setText(dayOfTheWeek);

        long mdate= System.currentTimeMillis();
        SimpleDateFormat dateForm= new SimpleDateFormat("dd MMMM");
        String dateString = dateForm.format(mdate);
        bannerDate.setText(dateString);

        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent= new Intent(MainActivity.this, AddActivity.class);
                startActivity(addIntent);
            }
        });



    }


    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change city");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCity(input.getText().toString());
            }
        });
        builder.show();
    }

    public void changeCity(String city){
        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
                .findFragmentById(R.id.holder);
        wf.changeCity(city);
        new CityPreference(this).setCity(city);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public TaskViewHolder(View itemView)
        {
            super(itemView);
            mView= itemView;
        }

        public void SetName(String name)
        {
            TextView task_name= (TextView) mView.findViewById(R.id.taskName);
            task_name.setText(name);
        }

        public void SetTime(String time)
        {
            TextView task_time= (TextView) mView.findViewById(R.id.taskTime);
            SimpleDateFormat dateForm= new SimpleDateFormat("hh:mm a");
            Date date= new Date(time);
            String dateString = dateForm.format(date);
            //String dateString = dateForm.format(time);
            task_time.setText(dateString);
        }

        public void SetStatus(final Boolean stat, final String task_key)
        {
            final RadioButton status = (RadioButton) mView.findViewById(R.id.doneButton);
            status.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                final DatabaseReference database= FirebaseDatabase.getInstance().getReference().child("Tasks");
                database.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        database.child(task_key).child("status").setValue(isChecked);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            });
            status.setChecked(stat);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(item.getItemId() == R.id.change_city){
            showInputDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Task, TaskViewHolder> adapter = new FirebaseRecyclerAdapter<Task, TaskViewHolder>(
                Task.class,
                R.layout.task_row,
                TaskViewHolder.class,
                mQuery)
        {
            @Override
            protected void populateViewHolder(final TaskViewHolder viewHolder, Task model, int position) {


                    final String task_key = getRef(position).getKey().toString();

                    viewHolder.SetName(model.getName());
                    viewHolder.SetTime(model.getTime());

                    DatabaseReference database= FirebaseDatabase.getInstance().getReference().child("Tasks");
                    database.child(task_key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean status= (Boolean) dataSnapshot.child("status").getValue();
                            viewHolder.SetStatus(status, task_key);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent singleTask = new Intent(MainActivity.this, SingleTask.class);
                            singleTask.putExtra("taskId", task_key);
                            startActivity(singleTask);
                        }
                    });

            }




        };
        taskList.setAdapter(adapter);
    }


}
