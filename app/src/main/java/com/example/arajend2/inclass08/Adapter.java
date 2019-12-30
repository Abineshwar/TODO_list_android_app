package com.example.arajend2.inclass08;


import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.example.sachi.inclass08.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.arajend2.inclass08.MainActivity.checkMap;
import static com.example.arajend2.inclass08.MainActivity.checkedArray;
import static com.example.arajend2.inclass08.MainActivity.childUpdates;
import static com.example.arajend2.inclass08.MainActivity.keys;
import static com.example.arajend2.inclass08.MainActivity.taskMap;

public class Adapter extends ArrayAdapter<Task> implements CompoundButton.OnCheckedChangeListener{
   SparseBooleanArray States;

    private static class ViewHolder {
        static TextView time;
        static TextView Title;
        static TextView Author;
        static CheckBox checkbox;
    }

    Context context;

    private ArrayList<Task> List;

    public Adapter(ArrayList<Task> articleList, Context context) {
        super(context, R.layout.layout, articleList);
        this.context=context;
        this.List =articleList;
        States = new SparseBooleanArray(articleList.size());
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.layout, parent, false);

            viewHolder.Title = (TextView) convertView.findViewById(R.id.Title);
            viewHolder.Author = (TextView) convertView.findViewById(R.id.Author);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox);
            ViewHolder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
          }

        lastPosition = position;

        viewHolder.Title.setText(dataModel.getTitle());
        viewHolder.Author.setText(dataModel.getPriority());
        viewHolder.checkbox.setTag(position);
        viewHolder.time.setText(dataModel.getTime());

          if  (dataModel.getStatus().equals("completed")){
            viewHolder.checkbox.setChecked(States.get(position, true));
        }
        else{
            viewHolder.checkbox.setChecked(States.get(position, false));
        }

        viewHolder.checkbox.setOnCheckedChangeListener(this);
        return convertView;
    }


    public boolean isChecked(int position) {
        return States.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        States.put(position, isChecked);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {

        States.put((Integer) buttonView.getTag(), isChecked);
        if (checkedArray.contains((Integer) buttonView.getTag())){
            checkedArray.remove((Integer) buttonView.getTag());
        }
        else{
            checkedArray.add((Integer) buttonView.getTag());
        }
        checkMap.put((Integer) buttonView.getTag(),true);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        List<String> keyList = new ArrayList<String>(childUpdates.keySet());
        if (checkedArray.size()>0) {
            for (Integer i : checkedArray) {
                Task task = taskMap.get(keys.get(i));
                task.setStatus("completed");
                Map<String, Object> postValues = task.toMap();
                taskMap.remove(keys.get(i));
                childUpdates.put(keys.get(i), postValues);
            }
            database.child("task").updateChildren(childUpdates);
        }
    }

}