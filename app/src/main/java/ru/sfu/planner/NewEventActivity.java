package ru.sfu.planner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewEventActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private final static Calendar calendar = Calendar.getInstance();
    private EditText newDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        databaseHelper = new DatabaseHelper(getApplicationContext());

        newDate = (EditText)findViewById(R.id.newDate);

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,day);
            newDate.setText(MainActivity.dateFormat.format(calendar.getTime()));
        };
        newDate.setOnClickListener(view -> new DatePickerDialog(
                NewEventActivity.this,
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show());

    }

    public void back(View view) {
        this.finish();
    }

    public void toMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void save(View view) {
        db = databaseHelper.getReadableDatabase();

        new EventModel(
                ((EditText)findViewById(R.id.newTitle)).getText().toString(),
                ((EditText)findViewById(R.id.newDescription)).getText().toString(),
                ((EditText)findViewById(R.id.newAddress)).getText().toString(),
                newDate.getText().toString()
        ).saveToDB(db);

        this.finish();
    }

    @Override
    protected void onDestroy() {
        if (db != null) db.close();
        super.onDestroy();
    }

}