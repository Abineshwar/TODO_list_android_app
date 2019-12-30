
package com.example.arajend2.inclass08;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sachi.inclass08.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    static HashMap<Integer,Boolean> checkMap = new HashMap<>();
    static ArrayList<Integer> checkedArray = new ArrayList<Integer>();
    EditText data;
    Button add;
    Spinner spinner;
    static ListView listView;
    static Adapter adapter;
    static ArrayList<Task> taskList = new ArrayList<>();
    Task task = new Task();
    static Map<String, Object> childUpdates = new HashMap<>();
    static ArrayList<String> keys = new ArrayList<>();
    static Map<String, Task> taskMap = new HashMap<>();

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = findViewById(R.id.data);
        add =  findViewById(R.id.addButton);
        spinner =  findViewById(R.id.prioritySpinner);
        listView = findViewById(R.id.newsArticleList);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        ArrayList<String> categories = new ArrayList<>();
        categories.add("High");
        categories.add("Medium");
        categories.add("Low");

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        spinner.setAdapter(dataAdapter);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getText().length()>0) {
                    task.setTitle(data.getText().toString());
                    task.setStatus("pending");
                    PrettyTime p = new PrettyTime();
                    try {
                        Calendar cal = Calendar.getInstance();
                        Date createdTime = cal.getTime();
                        task.setTime(p.format(createdTime));
                    } catch (Exception e) {
                        task.setTime("");
                        e.printStackTrace();
                    }

                    final String key = database.child("task").push().getKey();
                    Map<String, Object> postValues = task.toMap();
                    childUpdates.clear();
                    childUpdates.put(key, postValues);
                }
                    database.child("task").updateChildren(childUpdates);
                    database.child("task").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Toast.makeText(getApplicationContext(),
                                    "The list has been updated", Toast.LENGTH_SHORT)
                                    .show();
                            Iterable<DataSnapshot> child = dataSnapshot.getChildren();
                            taskList.clear();
                            taskMap.clear();
                            keys.clear();
                            for (DataSnapshot c : child){
                                Task task = c.getValue(Task.class);
                                keys.add(c.getKey());
                                taskList.add(task);
                                taskMap.put(c.getKey(),task);
                            }
                            showAdapter();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            //}
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                final int i = pos;
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                                MainActivity.this);

                        alertDialog2.setTitle("Confirm Delete");

                        alertDialog2.setMessage("Are you sure?");

                        alertDialog2.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Write your code here to execute after dialog
                                        final List<String> keyList = new ArrayList<String>(childUpdates.keySet());
                                        database.child("task").child(keys.get(i)).removeValue();
                                        keys.remove(keys.get(i));
                                        dialog.cancel();
                                    }
                                });
                        alertDialog2.setNegativeButton("NO",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        alertDialog2.show();
                        }
                });
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.i1:
                Toast.makeText(getApplicationContext(),"Select All",Toast.LENGTH_LONG).show();
                showAdapter();
                return true;
            case R.id.i2:
                Toast.makeText(getApplicationContext(),"Show Completed",Toast.LENGTH_LONG).show();
                ArrayList<Task> completedtasks = new ArrayList<>();
                for (Task t: taskList){
                    if (t.getStatus().equals("completed")){
                        completedtasks.add(t);
                    }
                    if (completedtasks.size()>0){
                        adapter = new Adapter(completedtasks,getApplicationContext());
                        listView.setAdapter(adapter);
                    }
                }
                return true;
            case R.id.i3:
                Toast.makeText(getApplicationContext(),"Show Pending",Toast.LENGTH_LONG).show();
                ArrayList<Task> pendingtasks = new ArrayList<>();
                for (Task t: taskList){
                    if (t.getStatus().equals("pending")){
                        pendingtasks.add(t);
                    }
                    if (pendingtasks.size()>0){
                        adapter = new Adapter(pendingtasks,getApplicationContext());
                        listView.setAdapter(adapter);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showAdapter(){
        adapter = new Adapter(taskList,getApplicationContext());
        listView.setAdapter(adapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String i = parent.getItemAtPosition(position).toString();
        task.setPriority(i);
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
