package com.example.harrycheng.a4_v2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.*;

import java.util.Observable;
import java.util.Observer;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;

public class questionActivity extends AppCompatActivity implements Observer{
    private userModel m;
    private int currentQuestion=1;

    // All attributes
    private TextView questionDes;
    private ImageView image;
    private RadioGroup single;
    private RadioGroup multi;
    private Button prev;
    private Button next;
    private TextView numOfQuest;
    private String[] questions;
    TypedArray allAnswers;
    TypedArray correctAns;


    @Override
    public void update(Observable o, Object arg){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.question_view_landscape);
        }else{
            setContentView(R.layout.question_view);
        }


        this.m = userModel.getInstance();
        m.addObserver(this);

        loadAllAttributes();
        loadCurrentQuestion();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        storeCurrentQuestion(currentQuestion-1);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.question_view_landscape);
            loadAllAttributes();
            loadCurrentQuestion();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.question_view);
            loadAllAttributes();
            loadCurrentQuestion();
        }
    }

    public void logOutClick2(View v){
        m.clear();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
        startActivity(intent);
    }

    public void loadAllAttributes(){
        questionDes = findViewById(R.id.question);
        image = findViewById(R.id.imageView);
        single = findViewById(R.id.SingleGroup);
        multi = findViewById(R.id.MultiGroup);
        prev = findViewById(R.id.prevQ);
        next = findViewById(R.id.nextQ);
        numOfQuest = findViewById(R.id.questCount);
        questions = getResources().getStringArray(R.array.questions);
        allAnswers = getResources().obtainTypedArray(R.array.allAnswers);
        correctAns = getResources().obtainTypedArray(R.array.correctAnswers);

        TextView w = findViewById(R.id.userName);
        String name = "Name: "+m.getName();
        if(w != null) w.setText(name);

        w = findViewById(R.id.questCount);
        if(w != null) w.setText(currentQuestion+"/"+m.getNumOfQuestion());

        if(currentQuestion == m.getNumOfQuestion()){
            ((Button)findViewById(R.id.nextQ)).setText("Finish");
        }

        if(currentQuestion == 1) {
            this.prev.setEnabled(false);
        }

        if(currentQuestion == m.getNumOfQuestion()) {
            this.next.setText("Finish");
        }
    }

    public void loadCurrentQuestion(){
        this.questionDes.setText(questions[currentQuestion-1]);
        int id = allAnswers.getResourceId(currentQuestion-1,0);
        String[] array = getResources().getStringArray(id);
        int count = multi.getChildCount();

        id = correctAns.getResourceId(currentQuestion-1, 0);
        String[] corans = getResources().getStringArray(id);

        ArrayList<Integer> currentSelected = m.getResult(currentQuestion-1);
        // load all possible answers...
        if(corans.length > 1){
            single.setVisibility(View.GONE);
            multi.setVisibility(View.VISIBLE);
            int numOfApplied = 0;
            int tempSelected = -1;
            int totalSelected = 0;
            if(currentSelected != null && currentSelected.size() > 0) {
                totalSelected = currentSelected.size()-1;
            }

            for(int i = 0; i <count; ++i){
                View b = multi.getChildAt(i);
                if(b instanceof CheckBox) {
                    ((CheckBox) b).setText(array[i]);

                    if(currentSelected != null && currentSelected.size() > 0) {
                        tempSelected = currentSelected.get(numOfApplied);
                    }

                    if(tempSelected == i){
                        ((CheckBox) b).setChecked(true);
                        if(totalSelected > numOfApplied){
                            ++numOfApplied;
                        }
                    }else{
                        ((CheckBox) b).setChecked(false);
                    }
                }
            }
        }
        else{
            single.setVisibility(View.VISIBLE);
            multi.setVisibility(View.GONE);
            int Butcount = 0;
            int selected = -1;
            if(currentSelected != null && currentSelected.size() > 0) {
                 selected = currentSelected.get(0);
            }

            for(int i = 0; i <count; ++i){
                View b = single.getChildAt(i);
                if(b instanceof RadioButton) {
                    ((RadioButton) b).setText(array[i]);
                    if (Butcount == selected) {
                        ((RadioButton) b).setChecked(true);
                    }
                    else{
                        ((RadioButton) b).setChecked(false);
                    }
                    ++Butcount;
                }
            }
        }

        id = getResources().getIdentifier("image"+currentQuestion, "drawable", getPackageName());
        this.image.setImageResource(id);
        this.numOfQuest.setText(currentQuestion+"/"+m.getNumOfQuestion());
    }

    public void storeCurrentQuestion(int index){
        ArrayList<Integer> currentRest = new ArrayList<Integer>();
        int current = 0;
        int tempId;
        View temp;

        if(single.getVisibility() == View.VISIBLE){
            int count = single.getChildCount();
            int id = single.getCheckedRadioButtonId();

            for(int i = 0; i < count; ++i){
                temp = single.getChildAt(i);
                if(temp instanceof RadioButton){
                    tempId = temp.getId();
                    if(tempId == id) break;
                    ++current;
                }
            }

            currentRest.add(current);
        }else{
            int count = multi.getChildCount();
            for(int i = 0; i < count; ++i){
                temp = multi.getChildAt(i);
                if(temp instanceof CheckBox){
                    if(((CheckBox)temp).isChecked()){
                        currentRest.add(current);
                    }
                    ++current;
                }
            }
        }
        m.setResult(currentRest, index);
    }

    public void prevQuest(View v){
        storeCurrentQuestion(this.currentQuestion-1);
        this.currentQuestion--;
        if(this.currentQuestion == 1){
            this.prev.setEnabled(false);
        }

        if(this.currentQuestion < m.getNumOfQuestion()){
            this.next.setText("NEXT");
        }
        loadCurrentQuestion();
    }

    public void calculateScore(){
        int ttScore = 0;
        int id;
        for(int i = 0; i < m.getNumOfQuestion(); ++i){
            ArrayList<Integer> reult = m.getResult(i);
            id = correctAns.getResourceId(i, 0);
            String[] corans = getResources().getStringArray(id);

            if(reult.size() == corans.length) {
                for (int j = 0; j < corans.length; ++j) {
                    String s = corans[j];
                    char t = s.toCharArray()[0];
                    if(reult.get(j) != (t - 'A')){
                        --ttScore;
                        break;
                    }
                }
                ++ttScore;
            }
        }
        m.setScore(ttScore);
    }

    public void nextQuest(View v){
        storeCurrentQuestion(this.currentQuestion-1);
        this.currentQuestion++;
        if(this.currentQuestion > 1){
            this.prev.setEnabled(true);
        }

        if(this.currentQuestion == this.m.getNumOfQuestion()) {
            this.next.setText("Finish");
        }else if(this.currentQuestion > this.m.getNumOfQuestion()){
            currentQuestion--;
            // load final score page...
            calculateScore();
            Intent intent = new Intent(this, resultActivity.class);
            startActivity(intent);
        }
        loadCurrentQuestion();
    }
}
