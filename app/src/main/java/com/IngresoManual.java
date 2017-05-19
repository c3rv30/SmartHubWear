package com;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.blacklist.sync.DBController;
import com.blacklist.sync.SampleBC;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.MainActivity.validarRut;
import static java.lang.System.out;


/**
 * Created by c3rv30 on 3/27/17.
 */

public class IngresoManual extends AppCompatActivity {

    //DB Class to perform DB related operations
    DBController controller = new DBController(this);
    // Progress Dialog Object
    ProgressDialog prgDialog;
    HashMap<String, String> queryValues;

    Switch switchChile;
    Switch switchOtro;
    EditText editRut;
    private Calendar calendar;
    private int year, month, day;

    ImageView checkCenter;

    List<Map<String, String>> listData = new ArrayList<Map<String, String>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_manual);

        editRut = (EditText) findViewById(R.id.editTextRutManual);
        switchChile = (Switch) findViewById(R.id.switch_chile);
        switchOtro = (Switch) findViewById(R.id.switch_otro);



        // Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        //prgDialog.setMessage("Transferring Data from Remote MySQL DB and Syncing SQLite. Please wait...");
        prgDialog.setMessage("Transfiriendo Datos del servidor remoto, espere...");
        prgDialog.setCancelable(false);
        // BroadCase Receiver Intent Object
        Intent alarmIntent = new Intent(getApplicationContext(), SampleBC.class);
        // Pending Intent Object
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Alarm Manager Object
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Alarm Manager calls BroadCast for every Ten seconds (10 * 1000), BroadCase further calls service to check if new records are inserted in
        // Remote MySQL DB
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 5000, 60 * 1000, pendingIntent);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        // Set Date
        //fecha = (TextView)findViewById(R.id.textView4);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        editRut.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("click", "onMTouch");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                return false;

            }
        });

        switchChile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchOtro.setChecked(false);
                } else {
                    switchOtro.setChecked(true);
                }
            }
        });
        switchOtro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchChile.setChecked(false);
                } else {
                    switchChile.setChecked(true);
                }
            }
        });
    }

    public void back_button(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void validarManual(View view){
        String pasar;
        pasar = editRut.getText().toString().trim();
        if(pasar.isEmpty()){
            Toast toast = Toast.makeText(this, "DEBE INGRESAR UN RUT", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            if(switchChile.isChecked()){
                editRut.setText("");
                boolean b = validarRut(pasar);
                if(b){
                    validarAsis(pasar);
                    //System.out.println(b);
                }else{
                    Toast toast = Toast.makeText(this, "RUT NO VALIDO", Toast.LENGTH_SHORT);
                    toast.show();
                    editRut.setText("");
                }
            }else if(switchOtro.isChecked()){
                editRut.setText("");
                validarAsis(pasar);
            }
        }
    }



    public static boolean validarRut(String rut) {
        boolean validacion = false;
        try {
            rut = rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));
            char dv = rut.charAt(rut.length() - 1);
            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validacion = true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return validacion;
    }

    public void validarAsis(String pasar) {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        String year2;
        year2 = Integer.toString(year);
        String month2;
        month2 = Integer.toString(month + 1);
        String day2;
        day2 = Integer.toString(day);

        String fecha;
        fecha = day2 + "/" + month2 + "/" + year2;
        fecha.trim().toString();

        // Get User records from SQLite DB
        ArrayList<HashMap<String, String>> userList = controller.getAsisEstadis(pasar, fecha);
        // If users exists in SQLite DB
        if (userList.size() != 0) {
            beepNoPass();
            msgingreso();
            //checkCenter.setImageResource(R.drawable.warning_check);
        } else {
            getRut(pasar);
        }
    }

    public void getRut(String pasar) {
        // Get User records from SQLite DB
        ArrayList<HashMap<String, String>> userList = controller.getBlackUser(pasar);
        // If users exists in SQLite DB
        if (userList.size() != 0) {
            for (int a = 0; a < userList.size(); a++) {
                HashMap<String, String> tmpData = (HashMap<String, String>) userList.get(a);
                Set<String> key = tmpData.keySet();
                Iterator it = key.iterator();
                while (it.hasNext()) {
                    String hmKey = (String) it.next();
                    String hmData = (String) tmpData.get(hmKey);
                    out.println("Key: " + hmKey + " & Data: " + hmData);
                    //nomList.setText("Numero de Lista: "+hmData);
                }
            }
            beepNoPass();
            msgNoPass();
            //checkCenter.setImageResource(R.drawable.invalid_check);
        } else {
            //nomList.setText("");
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            String year2;
            year2 = Integer.toString(year);
            String month2;
            month2 = Integer.toString(month + 1);
            String day2;
            day2 = Integer.toString(day);

            String fecha;
            fecha = day2 + "/" + month2 + "/" + year2;
            fecha.trim().toString();
            beepPass();
            msgPass();
            //checkCenter.setImageResource(R.drawable.valid_check);

            String equipo = controller.getEquipoAsignado();
            String ID = controller.getIdDispAsignado();

            equipo.toString().trim();
            ID.toString().trim();

            controller.insertRutEstadisticas(pasar, fecha, equipo, ID);
        }
    }


    public void msgPass(){
        Toast toast = Toast.makeText(this, "PUEDE INGRESAR", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void msgNoPass() {
        Toast toast = Toast.makeText(this, "NO PUEDE INGRESAR", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void msgingreso() {
        Toast toast = Toast.makeText(this, "RUT YA REGISTRADO HOY", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void beepPass(){
        MediaPlayer mp = MediaPlayer.create(this, R.raw.valid_beep);
        mp.start();
    }

    public void beepNoPass(){
        //MediaPlayer mp = MediaPlayer.create(this, R.raw.beep_no_pass);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.denied_beep_v2);
        mp.start();
    }


    // Method to Sync MySQL to SQLite DB
    public void syncSQLiteMySQLDB() {
        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        //prgDialog.show();
        // Make Http call to getusers.php
        //client.post("http://192.168.1.139:8888/getusers.php", params, new AsyncHttpResponseHandler() {
        client.post("http://idcontrol.cc/SmartHub_dev/getusers.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                // Hide ProgressBar
                prgDialog.hide();
                // Update SQLite DB with response sent by getusers.php
                updateSQLite(response);
                Toast.makeText(getApplicationContext(), "Lista Negra Actualizada", Toast.LENGTH_LONG).show();
            }


            // When error occured
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateSQLite(String response){
        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            // If no of array elements is not zero
            if(arr.length() != 0){
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    System.out.println(obj.get("userId"));
                    System.out.println(obj.get("userRut"));
                    System.out.println(obj.get("numList"));
                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    // Add userID extracted from Object
                    queryValues.put("userId", obj.get("userId").toString());
                    // Add userName extracted from Object
                    queryValues.put("userRut", obj.get("userRut").toString());
                    // Add userName extracted from Object
                    queryValues.put("numList", obj.get("numList").toString());
                    // Insert User into SQLite DB
                    controller.insertUser(queryValues);
                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("Id", obj.get("userId").toString());
                    map.put("status", "1");
                    usersynclist.add(map);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                // Permite cambiar estado de sincronizacion de datos en servidor
                //updateMySQLSyncSts(gson.toJson(usersynclist));
                // Reload the Main Activity
                //reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*// Method to inform remote MySQL DB about completion of Sync activity
    public void updateMySQLSyncSts(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("syncsts", json);
        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
        client.post("http://idcontrol.cc/SmartHub_dev/updatesyncsts.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                //Toast.makeText(getApplicationContext(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "El Servidor a sido informado de la sincronizacion", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                //Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Ocurrio un Error", Toast.LENGTH_LONG).show();
            }
        });
    }*/

/*    // Reload MainActivity
    public void reloadActivity() {
        Intent objIntent = new Intent(getApplicationContext(), MainActivitySync.class);
        startActivity(objIntent);
    }*/

    public void syncSQLiteToMySQLDB(){
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<HashMap<String, String>> userList =  controller.getAllAsis();
        if(userList.size()!=0){
            if(controller.dbSyncCount() != 0){
                prgDialog.show();
                params.put("usersJSON", controller.composeJSONfromSQLite());
                client.post("http://idcontrol.cc/SmartHub_dev/insertasistentes.php",params ,new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        System.out.println(response);
                        prgDialog.hide();
                        try {
                            JSONArray arr = new JSONArray(response);
                            System.out.println(arr.length());
                            for(int i=0; i<arr.length();i++){
                                JSONObject obj = (JSONObject)arr.get(i);
                                System.out.println(obj.get("id"));
                                System.out.println(obj.get("status"));
                                controller.updateSyncStatus(obj.get("id").toString(),obj.get("status").toString());
                            }
                            Toast.makeText(getApplicationContext(), "Sincronizacion completa!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            Log.e("Error", "Response");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // TODO Auto-generated method stub
                        prgDialog.hide();
                        if(statusCode == 404){
                            Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                        }else if(statusCode == 500){
                            Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Error inesperado! [Error mas comun: Dispositivo sin conexion a internet ]", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(), "SQLite and Remote MySQL DBs are in Sync!", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "No hay Datos para Subir", Toast.LENGTH_LONG).show();
        }
    }

}
