package com.example.harrycheng.a4_v2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class resultActivity extends AppCompatActivity implements Observer{
    private userModel m;

    @Override
    public void update(Observable o, Object arg){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_view);
        this.m = userModel.getInstance();
        m.addObserver(this);

        TextView w = findViewById(R.id.userName);
        String name = "Name: "+m.getName();
        if(w != null) w.setText(name);

        w = findViewById(R.id.score);
        if(w != null) w.setText("Your Score: "+m.getScord()+"/"+m.getNumOfQuestion());
    }

    public void logOutClick2(View v){
        m.clear();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
        startActivity(intent);
    }

    public void toTopicSelect(View v){
        String name = m.getName();
        m.clear();
        m.setName(name);
        Intent intent = new Intent(this, topicChooseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
        startActivity(intent);
    }
}
