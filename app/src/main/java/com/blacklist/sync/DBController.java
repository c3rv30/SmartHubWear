package com.blacklist.sync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by SmartHub on 24-02-17.
 */

public class DBController extends SQLiteOpenHelper{

    public DBController(Context applicationcontext) {
        super(applicationcontext, "user.db", null, 1);
    }

    // Create Tables
    @Override
    public void onCreate(SQLiteDatabase database) {

        String query, query2, query3, query4;
        query = "CREATE TABLE users ( userId INTEGER, userRut TEXT, numList TEXT)";
        database.execSQL(query);
        //query2 = "CREATE TABLE estadisticas ( asisId INTEGER PRIMARY KEY, asisRut TEXT, fecha TEXT, updateStatus TEXT)";
        query2 = "CREATE TABLE estadisticas ( asisId INTEGER PRIMARY KEY, asisRut TEXT, fecha TEXT, equipAsig TEXT, dispId TEXT, updateStatus TEXT)";
        database.execSQL(query2);
        query3 = "CREATE TABLE equipo ( equipoId INTEGER, equipoNom TEXT)";
        database.execSQL(query3);
        query4 = "CREATE TABLE equipasignado ( asignId INTEGER PRIMARY KEY, nomEqui TEXT, dispId TEXT)";
        database.execSQL(query4);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String query, query2, query3, query4;
        query = "DROP TABLE IF EXISTS users";
        database.execSQL(query);
        query2 = "DROP TABLE IF EXISTS estadisticas";
        database.execSQL(query2);
        query3 = "DROP TABLE IF EXISTS equipo";
        database.execSQL(query3);
        query4 = "DROP TABLE IF EXISTS equipasignado";
        database.execSQL(query4);
        onCreate(database);
    }


    public void deleteFromTable(){
        SQLiteDatabase database = this.getWritableDatabase();
        String query;
        query = "DELETE FROM users;";
        database.execSQL(query);
    }

    /**
     * Inserts User into SQLite DB
     * @param queryValues
     */
    public void insertUser(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", queryValues.get("userId"));
        values.put("userRut", queryValues.get("userRut"));
        values.put("numList", queryValues.get("numList"));
        database.insert("users", null, values);
        database.close();
    }

    public void insertRutEstadisticas(String rut, String fecha, String equipo, String ID) {
        //String date = setDate.setDateScaner();
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("asisRut", rut.toString().trim());
        values.put("fecha", fecha);
        values.put("equipAsig", equipo.toString().trim());
        values.put("dispId", ID.toString().trim());
        values.put("updateStatus", "no");
        database.insert("estadisticas", null, values);
        database.close();
    }

    public void insertEquipo(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("equipoId", queryValues.get("equipoId"));
        values.put("equipoNom", queryValues.get("equipoNom"));
        database.insert("equipo", null, values);
        database.close();
    }

    public void insertEquipoAsignado(String nom, String dispId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put("idDisp", queryValues.get("idDisp"));
        values.put("nomEqui",nom);
        values.put("dispId", dispId);
        database.insert("equipasignado", null, values);
        database.close();
    }

    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllUsers() {
        ArrayList<HashMap<String, String>> usersList;
        usersList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM users";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("userId", cursor.getString(0));
                map.put("userRut", cursor.getString(1));
                map.put("numList", cursor.getString(2));
                usersList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return usersList;
    }



    /**
     * Get Rut of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getBlackUser(String receptor){
        String rut = receptor;
        rut.toString().replace(" ", "");
        //String rut = "17107682k";
        ArrayList<HashMap<String, String>> usersList;
        usersList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM users WHERE userRut = '"+rut+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                // map.put("userId", cursor.getString(0));
                //map.put("userRut", cursor.getString(1));
                map.put("numList", cursor.getString(2));
                usersList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return usersList;
    }

    /**
     * Get Rut of Asis from estadisticas
     * @return
     */
    public ArrayList<HashMap<String, String>> getAsisEstadis(String receptor, String fecha){
        String rut = receptor;
        rut.toString().replace(" ", "");
        //String rut = "17107682k";
        ArrayList<HashMap<String, String>> usersList;
        usersList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM estadisticas WHERE asisRut = '"+rut+"' AND fecha = '"+fecha+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("asisId", cursor.getString(0));
                map.put("asisRut", cursor.getString(1));
                usersList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return usersList;
    }

    /**
     * Get list of asis from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllAsistentes(String fecha) {
        ArrayList<HashMap<String, String>> usersList;
        usersList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM estadisticas WHERE fecha = '"+fecha+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("rut", cursor.getString(0));
                map.put("fecha", cursor.getString(1));
                usersList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return usersList;
    }


    public ArrayList<HashMap<String, String>> getAllAsis() {
        ArrayList<HashMap<String, String>> usersList;
        usersList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM estadisticas";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("asisId", cursor.getString(0));
                map.put("asisRut", cursor.getString(1));
                map.put("fecha", cursor.getString(2));
                map.put("equipAsig", cursor.getString(3));
                map.put("dispId", cursor.getString(4));
                usersList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return usersList;
    }

    public ArrayList<HashMap<String, String>> getAllEquipo() {
        ArrayList<HashMap<String, String>> usersList;
        usersList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM equipo";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("equipoId", cursor.getString(0));
                map.put("equipoNom", cursor.getString(1));
                usersList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return usersList;
    }
    // Consulta para poblar Spinner
    public ArrayList<String> getAllEquipo2(){
        ArrayList<String> my_array = new ArrayList<String>();
        try{
            String selectQuery = "SELECT * FROM equipo";
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    String ID = cursor.getString(0);
                    String NAME = cursor.getString(1);
                    my_array.add(NAME);
                } while (cursor.moveToNext());
            }
            database.close();
        }catch(Exception e){
            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG);
        }
        return my_array;
    }

    public ArrayList<HashMap<String, String>> getAllEquipoAsignado() {
        ArrayList<HashMap<String, String>> usersList;
        usersList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM equipasignado";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("asignId", cursor.getString(0));
                map.put("nomEqui", cursor.getString(1));
                map.put("dispId", cursor.getString(2));
                usersList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return usersList;
    }

    public HashMap<String, String> getAllEquipoAsignadoNew() {
        HashMap<String, String> usersList;
        usersList = new HashMap<String, String>();
        String selectQuery = "SELECT nomEqui,dispId FROM equipasignado";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                //map.put("asignId", cursor.getString(0));
                map.put("nomEqui", cursor.getString(0));
                map.put("dispId", cursor.getString(1));
                usersList = map;
            } while (cursor.moveToNext());
        }
        database.close();
        return usersList;
    }

    // Consulta para poblar Spinner
    public String getEquipoAsignado(){
        String equipo = new String();
        try{
            String selectQuery = "SELECT nomEqui FROM equipasignado";
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    String NAME = cursor.getString(0);
                    equipo = NAME;
                } while (cursor.moveToNext());
            }
            database.close();
        }catch(Exception e){
            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG);
        }
        return equipo;
    }

    // Consulta para poblar Spinner
    public String getIdDispAsignado(){
        String dispId = new String();
        try{
            String selectQuery = "SELECT dispId FROM equipasignado";
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    String ID = cursor.getString(0);
                    dispId = ID;
                } while (cursor.moveToNext());
            }
            database.close();
        }catch(Exception e){
            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG);
        }
        return dispId;
    }

    // Consulta para poblar Spinner
    public String getPruebaEsta(){
        String dispId = new String();
        try{
            String selectQuery = "SELECT dispId FROM estadisticas";
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    String ID = cursor.getString(0);
                    dispId = ID;
                } while (cursor.moveToNext());
            }
            database.close();
        }catch(Exception e){
            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG);
        }
        return dispId;
    }


    /**
     * Compose JSON out of SQLite records
     * @return
     */
    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM estadisticas where updateStatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("asisId", cursor.getString(0));
                map.put("asisRut", cursor.getString(1));
                map.put("fecha", cursor.getString(2));
                map.put("equipAsig", cursor.getString(3));
                map.put("dispId", cursor.getString(4));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }


    /**
     * Get Sync status of SQLite
     * @return
     */
    public String getSyncStatus(){
        String msg = null;
        if(this.dbSyncCount() == 0){
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        }else{
            msg = "DB Sync needed";
        }
        return msg;
    }


    /**
     * Get SQLite records that are yet to be Synced
     * @return
     */
    public int dbSyncCount(){
        int count = 0;
        String selectQuery = "SELECT * FROM estadisticas where updateStatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        database.close();
        return count;
    }


    /**
     * Update Sync status against each User ID
     * @param id
     * @param status
     */
    public void updateSyncStatus(String id, String status){
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update estadisticas set updateStatus = '"+ status +"' where asisId="+"'"+ id +"'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void updateSyncStatusToNo(){
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update estadisticas set updateStatus = '"+"no"+"' where updateStatus = '"+"yes"+"'";
        //Update clientes Set nombre='Jos�' Where nombre='Pepe'
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }



    public void vaciarEquipoAsignado(){
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "DELETE FROM equipasignado";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    public void vaciarEquipo(){
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "DELETE FROM equipo";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

}
