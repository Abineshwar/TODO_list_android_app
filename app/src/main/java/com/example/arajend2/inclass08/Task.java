package com.example.arajend2.inclass08;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Task {
    String title;
    String priority;
    String status;
    String time;

    Task(){}

    Task(String title, String priority){
        this.title = title;
        this.priority = priority;
    }


    Task(String title, String priority,String status){
        this.title = title;
        this.priority = priority;
        this.status = status;
    }


    Task(String title, String priority,String status,String time){
        this.title = title;
        this.priority = priority;
        this.status = status;
        this.time = time;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("priority",priority);
        result.put("status",status);
        result.put("time",time);
        return result;
    }

    public static Task toObject(Map<String, Object> toObj){
        return new Task((String) toObj.get("title"),(String) toObj.get("priority"),(String) toObj.get("status"),(String) toObj.get("time"));

    }
}
