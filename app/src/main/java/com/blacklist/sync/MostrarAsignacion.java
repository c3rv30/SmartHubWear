package com.blacklist.sync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.idcontrolwear.smarthub.idcontrolwear.R;

import java.util.HashMap;

/**
 * Created by SmartHub on 24-02-17.
 */

public class MostrarAsignacion extends Activity{

    TextView equipo;
    TextView dispId;
    TextView textViewprueba;

    // DB Class to perform DB related operations
    DBController controller = new DBController(this);
    HashMap<String, String> queryValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mostrar_equipo);

        equipo = (TextView) findViewById(R.id.textViewEquipo);
        dispId = (TextView) findViewById(R.id.textViewIdDisp);

        // Get User records from SQLite DB
        //SArrayList<HashMap<String, String>> equipList = controller.getAllEquipoAsignado();

        //HashMap<String, String> equipList2 = controller.getAllEquipoAsignadoNew();

        String equipo1 = controller.getEquipoAsignado();
        String ID = controller.getIdDispAsignado();

        // If users exists in SQLite DB
        if (equipo1.length() != 0) {

            equipo.setText(equipo1.toString());
            dispId.setText(ID.toString());
        	/*Iterator<?> it = equipList2.entrySet().iterator();
        	while(it.hasNext()){
        		@SuppressWarnings("rawtypes")
				Map.Entry e = (Map.Entry)it.next();
        		equipo.setText(e.getKey().toString());
        		dispId.setText(e.getValue().toString());
        	}*/

        	/*for (HashMap<String, String> hashMap : equipList){
        		System.out.println(hashMap.keySet());
        		equipo.setText(hashMap.keySet().toString());

        		for(String key : hashMap.keySet()){
        			System.out.println(hashMap.get(key));
        			//Sequipo.setText(hashMap.get(key).toString());
        			dispId.setText(hashMap.get(key).toString());
        		}
        	}*/
        }
    }

    public void btnDesEquipo(View v){
        controller.vaciarEquipoAsignado();
        //setContentView(R.layout.activity_asignar_equipo);

        Intent intent = new Intent(this, AsignarEquipo.class);
        startActivity(intent);
        finish();
    }

    public void btnCerrarSesion(View v){
        //Intent objIntent = new Intent(getApplicationContext(), LoginAdmin.class);
        //startActivity(objIntent);
        setContentView(R.layout.activity_barcode_main_two);
        finish();
    }


}
