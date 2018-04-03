package com.example.theresa.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class AddActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    EditText editTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        database = FirebaseDatabase.getInstance();
    }

    public void addButtonClicked(View view)
    {
        editTask= (EditText) findViewById(R.id.editTask);
        String name= editTask.getText().toString();
        long date= System.currentTimeMillis();
        String status= "true";

        SimpleDateFormat sdf= new SimpleDateFormat("MMMM dd, yyyy h:mm a");
        String dateFormat= sdf.format(date);

        myRef= database.getInstance().getReference().child("Tasks");
        DatabaseReference newTask= myRef.push();
        newTask.child("name").setValue(name);
        newTask.child("time").setValue(dateFormat);
        newTask.child("bool").setValue("true");

        Intent mainIntent= new Intent(AddActivity.this, MainActivity.class);
        startActivity(mainIntent);

        Toast.makeText(AddActivity.this, "Done adding task", Toast.LENGTH_SHORT).show();
    }
}
