package com.example.jorge.controlbt;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Proyeccion extends AppCompatDialogFragment {

    private EditText editTextValue;
    private Spinner spinner;
    private ExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_proyeccion, null);

        builder.setView(view).setTitle("Proyección")
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = editTextValue.getText().toString();
                        String selector = spinner.getSelectedItem().toString();
                        listener.applyText(selector, value);
                    }
                });

        editTextValue = (EditText) view.findViewById(R.id.edit_value);
        spinner = (Spinner) view.findViewById(R.id.spinner);

        String [] options = {"$", "kWh"};

        ArrayAdapter <String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, options);
        spinner.setAdapter(adapter);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyText(String selection, String number);
    }
}
