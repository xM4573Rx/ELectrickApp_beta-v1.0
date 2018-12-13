package com.example.jorge.controlbt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class Ajustes extends AppCompatActivity {

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