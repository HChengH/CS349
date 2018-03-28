package com.example.harrycheng.a4_v2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.*;

import java.util.*;

public class topicChooseActivity extends AppCompatActivity implements Observer{
    private userModel m;

    @Override
    public void update(Observable o, Object arg){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_question_view);

        this.m = userModel.getInstance();

        m.addObserver(this);
        TextView w = findViewById(R.id.welcome);
        w.setText("Welcome "+ m.getName());
    }


    public void logOutClick(View v){
        m.clear();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
        startActivity(intent);
    }

    public void loadClick(View v){
        Intent intent = new Intent(topicChooseActivity.this, questionActivity.class);

        Spinner sp = findViewById(R.id.spinner);
        String value = sp.getSelectedItem().toString();
        m.setNumOfQuestion(Integer.parseInt(value));

        startActivity(intent);
    }
}
