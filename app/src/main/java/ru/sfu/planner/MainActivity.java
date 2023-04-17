package ru.sfu.planner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static int getPixels(int unit, float size) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int)TypedValue.applyDimension(unit, size, metrics);
    }

    public static SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    private static final int LAYOUT_MARGIN = getPixels(TypedValue.COMPLEX_UNIT_DIP, 10);
    private static final int ICON_M_E = getPixels(TypedValue.COMPLEX_UNIT_DIP, 2);
    private static final int ICON_SIZE = getPixels(TypedValue.COMPLEX_UNIT_SP, 15);
    private static final int TEXT_P_RIGHT = getPixels(TypedValue.COMPLEX_UNIT_DIP, 5);
    private static final int TEXT_P_LEFT = getPixels(TypedValue.COMPLEX_UNIT_DIP, 1);
    private static final float TEXT_SIZE = 15.0f;

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    private LinearLayout eventsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Planner);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventsLayout = findViewById(R.id.events_layout);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        updateList();
    }

    private void updateList() {
        eventsLayout.removeAllViews();

        db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM event;", null);
        while(cursor.moveToNext()){
            EventModel event = EventModel.fromDB(cursor);

            if (event == null) continue;
            newEvent(event);
        }
        cursor.close();
    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> updateList()
    );

    public void toNewEvent(View view) {
        Intent intent = new Intent(this, NewEventActivity.class);
        mStartForResult.launch(intent);
    }

    public void toEditEvent(View view, String id) {
        Intent intent = new Intent(this, EditEventActivity.class);
        intent.putExtra("id", id);
        mStartForResult.launch(intent);
    }


    private void newEvent(EventModel event) {
        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(LAYOUT_MARGIN, LAYOUT_MARGIN, LAYOUT_MARGIN, LAYOUT_MARGIN);
        constraintLayout.setLayoutParams(layoutParams);

        int task_card = R.drawable.task_card_new;
        try {
            Date d = dateFormat.parse(event.date);
            if (d!= null && d.before(new Date())) {
                task_card = R.drawable.task_card_old;
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }

        constraintLayout.setBackground(
                ContextCompat.getDrawable(this, task_card)
        );
        constraintLayout.setId(View.generateViewId());
        constraintLayout.setElevation(ICON_M_E);
        eventsLayout.addView(constraintLayout);


        ImageView pencil = new ImageView(this);
        ConstraintLayout.LayoutParams pencilParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        pencilParams.rightToRight = constraintLayout.getId();
        pencilParams.baselineToBaseline = constraintLayout.getId();
        pencil.setPadding(ICON_M_E, ICON_M_E, ICON_M_E, ICON_M_E);
        pencil.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pencil)
        );
        pencil.setLayoutParams(pencilParams);
        pencil.setClickable(true);
        pencil.setOnClickListener(view -> toEditEvent(view, event.id));
        constraintLayout.addView(pencil);


        TextView title = new TextView(this);
        title.setId(View.generateViewId());
        title.setTextColor(
                ContextCompat.getColor(this,  R.color.dark_purple)
        );
        title.setText(event.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        ConstraintLayout.LayoutParams titleParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        titleParams.leftToLeft = constraintLayout.getId();
        titleParams.topToTop = constraintLayout.getId();
        title.setLayoutParams(titleParams);
        constraintLayout.addView(title);


        ImageView calendar = new ImageView(this);
        calendar.setId(View.generateViewId());
        ConstraintLayout.LayoutParams calendarParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        calendarParams.leftToLeft = constraintLayout.getId();
        calendarParams.topToBottom = title.getId();
        calendarParams.height = ICON_SIZE;
        calendarParams.width = ICON_SIZE;
        calendarParams.setMargins(ICON_M_E, ICON_M_E, ICON_M_E, ICON_M_E);
        calendar.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.calendar)
        );
        calendar.setLayoutParams(calendarParams);
        constraintLayout.addView(calendar);


        TextView date = new TextView(this);
        date.setId(View.generateViewId());
        date.setTextColor(
                ContextCompat.getColor(this,  R.color.purple)
        );
        date.setText(event.date);
        date.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        ConstraintLayout.LayoutParams dateParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dateParams.leftToRight = calendar.getId();
        dateParams.topToBottom = title.getId();
        date.setPadding(TEXT_P_LEFT,0, TEXT_P_RIGHT,0);
        date.setLayoutParams(dateParams);
        constraintLayout.addView(date);


        ImageView weather = new ImageView(this);
        weather.setId(View.generateViewId());
        ConstraintLayout.LayoutParams weatherParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        weatherParams.leftToRight = date.getId();
        weatherParams.topToBottom = title.getId();
        weatherParams.height = ICON_SIZE;
        weatherParams.width = ICON_SIZE;
        weatherParams.setMargins(ICON_M_E, ICON_M_E, ICON_M_E, ICON_M_E);
        weather.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.sun)
        );
        weather.setLayoutParams(weatherParams);
        constraintLayout.addView(weather);


        TextView temperature = new TextView(this);
        temperature.setId(View.generateViewId());
        temperature.setTextColor(
                ContextCompat.getColor(this,  R.color.purple)
        );
        temperature.setText(" ");
        temperature.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        ConstraintLayout.LayoutParams temperatureParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        temperatureParams.leftToRight = weather.getId();
        temperatureParams.topToBottom = title.getId();
        temperature.setPadding(TEXT_P_LEFT,0, TEXT_P_RIGHT, 0);
        temperature.setLayoutParams(temperatureParams);
        constraintLayout.addView(temperature);


        ImageView geo = new ImageView(this);
        geo.setId(View.generateViewId());
        ConstraintLayout.LayoutParams geoParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        geoParams.leftToRight = temperature.getId();
        geoParams.topToBottom = title.getId();
        geoParams.height = ICON_SIZE;
        geoParams.width = ICON_SIZE;
        geoParams.setMargins(ICON_M_E, ICON_M_E, ICON_M_E, ICON_M_E);
        geo.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.geo_marker)
        );
        geo.setLayoutParams(geoParams);
        constraintLayout.addView(geo);


        TextView address = new TextView(this);
        address.setTextColor(
                ContextCompat.getColor(this,  R.color.purple)
        );
        address.setText(event.address);
        address.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        ConstraintLayout.LayoutParams addressParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        addressParams.leftToRight = geo.getId();
        addressParams.topToBottom = title.getId();
        address.setPadding(TEXT_P_LEFT,0, TEXT_P_RIGHT,0);
        address.setLayoutParams(addressParams);
        constraintLayout.addView(address);
    }

    @Override
    protected void onDestroy() {
        if (db != null) db.close();
        super.onDestroy();
    }

}