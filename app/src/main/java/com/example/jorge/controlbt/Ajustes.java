package com.example.jorge.controlbt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Ajustes extends AppCompatActivity  implements Proyeccion.ExampleDialogListener {

    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    private ArrayList<Items> mExampleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        createExampleList();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecyclerViewAdapter(mExampleList);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                switch (position) {
                    case 0:
                        openProyeccion();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void applyText(String selection, String number) {

        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput("Datos.txt", Activity.MODE_PRIVATE));

            archivo.write(number);
            archivo.write("#");
            archivo.write(selection);
            archivo.write("*");
            archivo.flush();
            archivo.close();
        } catch (IOException e) {

        }

        Toast.makeText(this, "Archivo creado", Toast.LENGTH_SHORT).show();

        /*Intent i = new Intent(Ajustes.this, UserInterfaz.class);//<-<- PARTE A MODIFICAR >->->
        i.putExtra("selector", selection);
        i.putExtra("number", number);*/
    }

    public void openProyeccion() {
        Proyeccion proyeccion = new Proyeccion();
        proyeccion.show(getSupportFragmentManager(), "Proyección");

    }

    public void createExampleList() {
        mExampleList = new ArrayList<>();
        mExampleList.add(new Items("Proyección"));
        mExampleList.add(new Items("Contáctanos"));
        mExampleList.add(new Items("Info. de la aplicación"));
    }
}