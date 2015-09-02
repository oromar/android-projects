/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Date;
import java.util.HashSet;

import br.com.oromar.dev.android.sunshine.data.WeatherContract;
import br.com.oromar.dev.android.sunshine.data.WeatherDbHelper;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.CITY_NAME);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COORD_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COORD_LONG);
        locationColumnHashSet.add(WeatherContract.LocationEntry.LOCATION_SETTING);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testLocationTable() {
        // First step: Get reference to writable database
        SQLiteDatabase database = new WeatherDbHelper(mContext).getWritableDatabase();
        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContract.LocationEntry.LOCATION_SETTING, "olinda");
        contentValues.put(WeatherContract.LocationEntry.CITY_NAME, "olinda");
        contentValues.put(WeatherContract.LocationEntry.COORD_LONG, 105.63);
        contentValues.put(WeatherContract.LocationEntry.COORD_LAT, 96.10);

        // Insert ContentValues into database and get a row ID back

        long rowId = database.insert(WeatherContract.LocationEntry.TABLE_NAME, null, contentValues);

        // Query the database and receive a Cursor back

        Cursor cursor = database.query(true, WeatherContract.LocationEntry.TABLE_NAME, null, null, null, null, null, null, null);

        // Move the cursor to a valid database row
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        if (cursor.moveToFirst()){
            String locationSetting = cursor.getString(cursor.getColumnIndex(WeatherContract.LocationEntry.LOCATION_SETTING));
            String cityName = cursor.getString(cursor.getColumnIndex(WeatherContract.LocationEntry.CITY_NAME));
            double coordLat = cursor.getDouble(cursor.getColumnIndex(WeatherContract.LocationEntry.COORD_LAT));
            double coordLong = cursor.getDouble(cursor.getColumnIndex(WeatherContract.LocationEntry.COORD_LONG));
            assertEquals(locationSetting, contentValues.getAsString(WeatherContract.LocationEntry.LOCATION_SETTING));
            assertEquals(cityName, contentValues.getAsString(WeatherContract.LocationEntry.CITY_NAME));
            assertEquals(coordLat, contentValues.getAsDouble(WeatherContract.LocationEntry.COORD_LAT));
            assertEquals(coordLong, contentValues.getAsDouble(WeatherContract.LocationEntry.COORD_LONG));
        }

        // Finally, close the cursor and database
        cursor.close();
        database.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testWeatherTable() {
        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.

        // First step: Get reference to writable database
        SQLiteDatabase database = new WeatherDbHelper(mContext).getWritableDatabase();
        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContract.LocationEntry.LOCATION_SETTING, "olinda");
        contentValues.put(WeatherContract.LocationEntry.CITY_NAME, "olinda");
        contentValues.put(WeatherContract.LocationEntry.COORD_LONG, 105.63);
        contentValues.put(WeatherContract.LocationEntry.COORD_LAT, 96.10);

        // Insert ContentValues into database and get a row ID back

        long rowId = database.insert(WeatherContract.LocationEntry.TABLE_NAME, null, contentValues);

        ContentValues secondContentValues = new ContentValues();
        secondContentValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, rowId);
        secondContentValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, new Date().getTime());
        secondContentValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 10.36);
        secondContentValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 12.69);
        secondContentValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 78);
        secondContentValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 60);
        secondContentValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 12.56);
        secondContentValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "short description");
        secondContentValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 12);
        secondContentValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 2);


        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.

        // First step: Get reference to writable database

        long row = database.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, secondContentValues);

        // Create ContentValues of what you want to insert
        // (you can use the createWeatherValues TestUtilities function if you wish)

        // Insert ContentValues into database and get a row ID back

        // Query the database and receive a Cursor back

        Cursor cursor = database.query(WeatherContract.WeatherEntry.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                assertEquals(
                        secondContentValues.getAsString(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC),
                        cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)));
                assertEquals(
                        secondContentValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_HUMIDITY),
                        cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY)));
                assertEquals(
                        secondContentValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP),
                        cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)));
                assertEquals(
                        secondContentValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP),
                        cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)));
                assertEquals(
                        secondContentValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED),
                        cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED)));
                assertEquals(
                        secondContentValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID),
                        cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID)));
                assertEquals(
                        secondContentValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_DEGREES),
                        cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES)));
                assertEquals(
                        secondContentValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_PRESSURE),
                        cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE)));
                assertEquals(
                        secondContentValues.getAsLong(WeatherContract.WeatherEntry.COLUMN_LOC_KEY).longValue(),
                        cursor.getLong(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_LOC_KEY)));
                assertEquals(
                        secondContentValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_PRESSURE),
                        cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE)));
            }
        }

        // Move the cursor to a valid database row

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)

        // Finally, close the cursor and database
        cursor.close();
        database.close();
    }


    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
    public long insertLocation() {
        return -1L;
    }
}
