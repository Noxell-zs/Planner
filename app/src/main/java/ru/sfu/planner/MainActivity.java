package ru.sfu.planner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Planner);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toNewEvent(View view) {
        Intent intent = new Intent(this, NewEventActivity.class);
        startActivity(intent);
    }

    public void toEditEvent(View view) {
        Intent intent = new Intent(this, EditEventActivity.class);
        startActivity(intent);
    }
}