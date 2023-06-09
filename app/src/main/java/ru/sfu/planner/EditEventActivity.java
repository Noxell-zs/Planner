package ru.sfu.planner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class EditEventActivity extends AppCompatActivity {
    private final static Calendar calendar = Calendar.getInstance();
    private SQLiteDatabase db;
    private EventModel eventModel;
    private EditText editTitle, editDate, editAddress, editDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());

        try {
            String id = getIntent().getExtras().getString("id");

            db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM event WHERE id=?;",
                    new String[]{id}
            );
            cursor.moveToNext();
            eventModel = EventModel.fromDB(cursor);
            cursor.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            this.finish();
        }

        editTitle = findViewById(R.id.editTitle);
        editDate = findViewById(R.id.editDate);
        editAddress = findViewById(R.id.editAddress);
        editDescription = findViewById(R.id.editDescription);

        editTitle.setText(eventModel.title);
        editDate.setText(eventModel.date);
        editAddress.setText(eventModel.address);
        editDescription.setText(eventModel.description);

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,day);
            editDate.setText(MainActivity.dateFormat.format(calendar.getTime()));
        };
        editDate.setOnClickListener(view -> new DatePickerDialog(
                EditEventActivity.this,
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show());
    }

    public void back(View view) {
        this.finish();
    }

    public void save(View view) {
        eventModel.title = editTitle.getText().toString();
        eventModel.date = editDate.getText().toString();
        eventModel.address = editAddress.getText().toString();
        eventModel.description = editDescription.getText().toString();

        eventModel.update(db);
        this.finish();
    }

    public void delete(View view) {
        eventModel.delete(db);
        this.finish();
    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent intent;
                if(result.getResultCode() == Activity.RESULT_OK && (intent = result.getData()) != null){
                    String accessMessage = intent.getStringExtra("location");
                    GeoPoint geoPoint = GeoPoint.fromDoubleString(accessMessage, ',');
                    ServiceAPI.setAddress(geoPoint, editAddress);
                    System.out.println(geoPoint.getLatitude());
                }
            });

    public void toMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        mStartForResult.launch(intent);
    }


    @Override
    protected void onDestroy() {
        if (db != null) db.close();
        super.onDestroy();
    }

}