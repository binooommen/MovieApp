package com.example.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector 
{
   // database name
   private static final String DATABASE_NAME = "UserMoviesBino";
      
   private SQLiteDatabase database; // for interacting with the database
   private DatabaseOpenHelper databaseOpenHelper; // creates the database

   // public constructor for DatabaseConnector
   public DatabaseConnector(Context context) 
   {
      // create a new DatabaseOpenHelper
      databaseOpenHelper = 
         new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
   }

   // open the database connection
   public void open() throws SQLException 
   {
      // create or open a database for reading/writing
      database = databaseOpenHelper.getWritableDatabase();
   }

   // close the database connection
   public void close() 
   {
      if (database != null)
         database.close(); // close the database connection
   } 

   // inserts a new Movie in the database
   public long insertMovie(String name, String director, String producer,  
      String actor, String actress, String release, String budget) 
   {
      ContentValues newMovie = new ContentValues();
      newMovie.put("name", name);
      newMovie.put("director", director);
      newMovie.put("producer", producer);
      newMovie.put("actor", actor);
      newMovie.put("actress", actress);
      newMovie.put("release", release);
      newMovie.put("budget", budget);

      open(); // open the database
      long rowID = database.insert("movies", null, newMovie);
      close(); // close the database
      return rowID;
   } 

   // updates an existing Movie in the database
   public void updateMovie(long id, String name, String director, 
      String producer, String actor, String actress, String release, String budget) 
   {
      ContentValues editMovie = new ContentValues();
      editMovie.put("name", name);
      editMovie.put("director", director);
      editMovie.put("producer", producer);
      editMovie.put("actor", actor);
      editMovie.put("actress", actress);
      editMovie.put("release", release);
      editMovie.put("budget", budget);

      open(); // open the database
      database.update("movies", editMovie, "_id=" + id, null);
      close(); // close the database
   } // end method updateMovie

   // return a Cursor with all Movie names in the database
   public Cursor getAllMovies() 
   {
      return database.query("movies", new String[] {"_id", "name"}, 
         null, null, null, null, "name");
   } 

   // return a Cursor containing specified Movie's information 
   public Cursor getOneMovie(long id) 
   {
      return database.query(
         "movies", null, "_id=" + id, null, null, null, null);
   } 

   // delete the Movie specified by the given String name
   public void deleteMovie(long id) 
   {
      open(); // open the database
      database.delete("movies", "_id=" + id, null);
      close(); // close the database
   } 
   
   private class DatabaseOpenHelper extends SQLiteOpenHelper 
   {
      // constructor
      public DatabaseOpenHelper(Context context, String name,
         CursorFactory factory, int version) 
      {
         super(context, name, factory, version);
      }

      // creates the Movies table when the database is created
      @Override
      public void onCreate(SQLiteDatabase db) 
      {
         // query to create a new table named Movies
         String createQuery = "CREATE TABLE movies" +
            "(_id integer primary key autoincrement," +
            "name TEXT, director TEXT, producer TEXT, " +
            "actor TEXT, actress TEXT, release TEXT, budget TEXT);";
                  
         db.execSQL(createQuery); // execute query to create the database
      } 

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
          int newVersion) 
      {
      }
   } // end class DatabaseOpenHelper
} // end class DatabaseConnector

