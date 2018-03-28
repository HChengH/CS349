package com.example.harrycheng.a4_v2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.view.View;
import android.widget.*;
import android.content.Intent;

import java.util.Observable;
import java.util.Observer;


public class MainActivity extends AppCompatActivity implements Observer {
    private userModel m;

    @Override
    public void update(Observable o, Object arg){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.m = userModel.getInstance();
        this.m.addObserver(this);

        EditText edtext = findViewById(R.id.editName);
        final Button next = findViewById(R.id.NextBut);

        next.setEnabled(false);

        edtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    next.setEnabled(true);
                } else {
                    next.setEnabled(false);
                }
            }
        });
    }

    public void nextPage(View v){
        Intent intent = new Intent(MainActivity.this, topicChooseActivity.class);

        EditText edtext = findViewById(R.id.editName);
        String name = edtext.getText().toString();
        this.m.setName(name);

        startActivity(intent);
    }
}
