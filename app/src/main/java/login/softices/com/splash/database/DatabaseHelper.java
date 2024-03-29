package login.softices.com.splash.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import login.softices.com.splash.utility.Utility;
import login.softices.com.splash.activities.User;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASEVERSION = 1;

    public static final String DATABASENAME = "UserManager.db";

    // User table name
    private static final String TABLE_USER = "user";

    // User Table Columns names
    private static final String COLUMN_USER_FIRST_NAME = "user_first_name";
    private static final String COLUMN_USER_LAST_NAME = "user_last_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_USER_GENDER = "user_gender";
    public static final String COLUMN_USER_PHOTO = "user_photo";

    // create table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_EMAIL + " TEXT PRIMARY KEY ,"
            + COLUMN_USER_FIRST_NAME + " TEXT,"
            + COLUMN_USER_LAST_NAME + " TEXT,"
            + COLUMN_USER_PASSWORD + " TEXT,"
            + COLUMN_USER_GENDER + " TEXT,"
            + COLUMN_USER_PHOTO + " blob"
            + ")";

    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    /**
     * Constructor
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super( context, DATABASENAME, null, DATABASEVERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( CREATE_USER_TABLE );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL( DROP_USER_TABLE );

        // Create tables again
        onCreate( db );

    }

    /**
     * This method is to create user record
     *
     * @param user
     */
    public boolean addUser(User user) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put( COLUMN_USER_PHOTO, Utility.getBytes( user.getPhoto()));
            values.put( COLUMN_USER_FIRST_NAME, user.getFirst_name() );
            values.put( COLUMN_USER_LAST_NAME, user.getLast_name() );
            values.put( COLUMN_USER_EMAIL, user.getEmail() );
            values.put( COLUMN_USER_PASSWORD, user.getPassword() );
            values.put( COLUMN_USER_GENDER, user.getGender() );
            db.insert( TABLE_USER, null, values );
            db.close();
            return true;
        } catch (Exception e) {
            Log.e( "addUser", e + "" );
        }
        return false;
    }

    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return list
     */
    public List<User> getAllUser() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_EMAIL,
                COLUMN_USER_FIRST_NAME,
                COLUMN_USER_LAST_NAME,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_GENDER
        };
        // sorting orders
        String sortOrder =
                COLUMN_USER_FIRST_NAME + " ASC";
        List<User> userList = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query( TABLE_USER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder ); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setEmail( cursor.getString( cursor.getColumnIndex( COLUMN_USER_EMAIL ) ) );
                user.setFirst_name( cursor.getString( cursor.getColumnIndex( COLUMN_USER_FIRST_NAME ) ) );
                user.setLast_name( cursor.getString( cursor.getColumnIndex( COLUMN_USER_LAST_NAME ) ) );
                user.setGender( cursor.getString( cursor.getColumnIndex( COLUMN_USER_GENDER ) ) );
                user.setPassword( cursor.getString( cursor.getColumnIndex( COLUMN_USER_PASSWORD ) ) );
                // Adding user record to list
                userList.add( user );
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return userList;
    }

    /**
     * This method to update user record
     *
     * @param user
     */
    public boolean updateUser(User user) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put( COLUMN_USER_PHOTO,Utility.getBytes(user.getPhoto()) );
            values.put( COLUMN_USER_FIRST_NAME, user.getFirst_name() );
            values.put( COLUMN_USER_LAST_NAME, user.getLast_name() );
            values.put( COLUMN_USER_EMAIL, user.getEmail() );
            values.put( COLUMN_USER_PASSWORD, user.getPassword() );
            values.put( COLUMN_USER_GENDER, user.getGender() );


            // updating row
            db.update( TABLE_USER, values, COLUMN_USER_EMAIL + " = ?",
                    new String[]{String.valueOf( user.getEmail() )} );
            db.close();
            return true;
        } catch (Exception e) {
            Log.e( "updateUser", e + "" );
        }
        return false;
    }

    public List<User> getcurrentUser(String email) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_PHOTO,
                COLUMN_USER_EMAIL,
                COLUMN_USER_FIRST_NAME,
                COLUMN_USER_LAST_NAME,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_GENDER
        };
        // sorting orders
        String sortOrder = COLUMN_USER_FIRST_NAME + " ASC";
        List<User> userList = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ? ";

        // selection argument
        String[] selectionArgs = {email};
        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query( TABLE_USER, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder ); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setEmail( cursor.getString( cursor.getColumnIndex( COLUMN_USER_EMAIL ) ) );
                user.setFirst_name( cursor.getString( cursor.getColumnIndex( COLUMN_USER_FIRST_NAME ) ) );
                user.setLast_name( cursor.getString( cursor.getColumnIndex( COLUMN_USER_LAST_NAME ) ) );
                user.setGender( cursor.getString( cursor.getColumnIndex( COLUMN_USER_GENDER ) ) );
                user.setPassword( cursor.getString( cursor.getColumnIndex( COLUMN_USER_PASSWORD ) ) );
                user.setPhoto(Utility.getPhoto(cursor.getBlob(cursor.getColumnIndex(COLUMN_USER_PHOTO))));
                // Adding user record to list
                userList.add( user );
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return userList;
    }


    /**
     * This method is to delete user record
     *
     * @param
     */
    public void deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete( TABLE_USER, COLUMN_USER_EMAIL + " = ?",
                new String[]{email} );
        db.close();
    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @return true/false
     */
    public boolean checkUser(String email) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_EMAIL
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query( TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null );                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @param password
     * @return true/false
     */
    public boolean checkUser(String email, String password) {

        String[] columns = {
                COLUMN_USER_EMAIL
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";

        String[] selectionArgs = {email, password};


        Cursor cursor = db.query( TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null );                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        Log.e( "cursorChecker", cursorCount + "," + email + "," + password );
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }
}
