package ru.sfu.planner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EventModel {
   public String id = "-1";
   public String title, description, address, date;

   public EventModel(String title, String description, String address, String date) {
      this.title = title;
      this.description = description;
      this.address = address;
      this.date = date;
   }

   public EventModel(String id, String title, String description, String address, String date) {
      this.id = id;
      this.title = title;
      this.description = description;
      this.address = address;
      this.date = date;
   }

   public static EventModel fromDB(Cursor cursor) {
      try {
         return new EventModel(
                 cursor.getString(0),
                 cursor.getString(1),
                 cursor.getString(2),
                 cursor.getString(3),
                 cursor.getString(4)
         );
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return null;
      }
   }

   public void saveToDB(SQLiteDatabase db) {
      db.execSQL("INSERT INTO event (title,description,address,date)" +
              "VALUES (?,?,?,?);", new String[]{title,description,address,date});
   }

   public void update(SQLiteDatabase db) {
      db.execSQL("UPDATE event " +
              "SET title=?, description=?, address=?, date=? " +
              "WHERE id=?;",
              new String[]{title,description,address,date,id});
   }

   public void delete(SQLiteDatabase db) {
      db.execSQL("DELETE FROM event WHERE id=?;", new String[]{this.id});
   }


}
