package com.example.jorge.controlbt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.UUID;

public class UserInterfaz extends AppCompatActivity{

    //1)

    public String Dato;
    public String WattHora;
    public String ActualWatt;
    public String Costtext;
    public String Watts;
    public String Proyeccion;
    public String unidad;

    public double cost;
    public double wattactual;
    public double costwatt;
    public double valor;

    public int Porcentaje=0;
    public double Aux=0.0;

    TextView watts;
    TextView tv1, tv2, tv3, tv4, tv5, tv6;
    ProgressBar progressBar;

    //-------------------------------------------
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;
    //-------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interfaz);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //2)
        //Enlaza los controles con sus respectivas vistas

        progressBar = (ProgressBar) findViewById(R.id.IdProgress);
        tv1 = (TextView) findViewById(R.id.IdBufferIn);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView7);
        tv4 = (TextView) findViewById(R.id.textView3);
        tv5 = (TextView) findViewById(R.id.WattInstantaneo);
        tv6 = (TextView) findViewById(R.id.Unidad);

        /*recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerViewAdapter());*/

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        VerificarEstadoBT();

        // Configuracion onClick listeners para los botones
        // para indicar que se realizara cuando se detecte
        // el evento de Click

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Consigue la direccion MAC desde DeviceListActivity via intent
        Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creación del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {}
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();


        String Archivos[] = fileList();
        if(ArchivoExiste(Archivos,"Datos.txt")){
            //Toast.makeText(UserInterfaz.this,"SI", Toast.LENGTH_SHORT).show();

        }else {
            //Toast.makeText(UserInterfaz.this,"NO", Toast.LENGTH_SHORT).show();
            try {
                OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput("Datos.txt", Activity.MODE_PRIVATE));

                archivo.write("0");
                archivo.write("#");
                archivo.write("$");
                archivo.write("*");
                archivo.flush();
                archivo.close();

            } catch (IOException e) {

            }

            //Toast.makeText(this, "Archivo creado", Toast.LENGTH_SHORT).show();

        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput("Datos.txt"));
            BufferedReader br = new BufferedReader(archivo);
            Proyeccion= br.readLine();

            //int s=Proyeccion.indexOf("#");

        }catch (IOException e){

        }
        valor=Double.valueOf(Proyeccion.substring(0,Proyeccion.indexOf("#")));
        unidad=Proyeccion.substring(Proyeccion.indexOf("#")+1,Proyeccion.indexOf("*"));
        //Toast.makeText(UserInterfaz.this,unidad, Toast.LENGTH_SHORT).show();
        ////////////////////////////////////////////////////////////////////////////////////////////

        tv4.setText("Proyección");
        switch (unidad) {
            case "kWh":
                // int D = (int) (wattactual*100.0/valor);
                //Aux=wattactual;

                tv1.setText(String.valueOf((int) valor));
                tv6.setText("KWH");
                break;
            case "$":
                //  int D = (int) (cost*100.0/valor);
                //Aux=cost;

                tv1.setText(String.valueOf((int)valor));
                tv6.setText("$");
                break;
        }

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {

                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);

                    int StartOfLineIndex = DataStringIN.indexOf("[");

                    if ((StartOfLineIndex > -1) ) {

                        int EndOfLineIndex=DataStringIN.indexOf("]",StartOfLineIndex);
                        if((EndOfLineIndex > -1)) {
                            Dato=DataStringIN.substring(StartOfLineIndex+1, EndOfLineIndex);//<-<- PARTE A MODIFICAR >->->
                            if(!(Dato.length()<=9)) {

                                Watts = String.valueOf(2*Double.valueOf(Dato.substring(0, Dato.indexOf("#"))));
                                WattHora = Dato.substring(Dato.indexOf("#") + 1, Dato.indexOf("*"));
                                wattactual = Float.valueOf(WattHora)* 0.002;
                                costwatt = 466.14;
                                cost = wattactual * costwatt;
                                Costtext = String.valueOf(new DecimalFormat("##.##").format(cost));
                                ActualWatt = String.valueOf(new DecimalFormat("##.###").format(wattactual));


                                tv2.setText(ActualWatt + " KWh");
                                tv3.setText("$ " + Costtext);
                                tv5.setText(Watts+" Watt");


                                Porcentaje=(int) (Aux*100/valor);
                                if(Porcentaje>=100){
                                    Porcentaje=100;
                                }

                                switch (unidad) {
                                    case "kWh":
                                        // int D = (int) (wattactual*100.0/valor);
                                        Aux=wattactual;
                                        //tv4.setText("Proyección (KWh)");
                                        break;
                                    case "$":
                                        //  int D = (int) (cost*100.0/valor);
                                        Aux=cost;
                                        //tv4.setText("Proyección ($)");
                                        break;
                                }

                                progressBar.setProgress(Porcentaje);
                                progressBar.getProgressDrawable().setColorFilter(Color.argb(255,(int)(2.55*Porcentaje),(int)(255-2.55*Porcentaje) ,0), PorterDuff.Mode.SRC_IN);
                                /*
                                if(Porcentaje>70){
                                    Toast.makeText(UserInterfaz.this,"MODULO ", Toast.LENGTH_SHORT).show();
                                   // progressBar.getIndeterminateDrawable().setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
                                    progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                                }else{
                                    progressBar.getProgressDrawable().setColorFilter(Color.argb(255,2*Porcentaje,2*Porcentaje,0), PorterDuff.Mode.SRC_IN);
                                }*/
                                //Toast.makeText(UserInterfaz.this,"MODULO " + D, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(UserInterfaz.this,"MODULO SIN ALIMENTACION", Toast.LENGTH_SHORT).show();
                            }

                            StartOfLineIndex = -1;
                            EndOfLineIndex = -1;
                            DataStringIN.delete(0, DataStringIN.length());
                        }
                    }
                }
            }
        };

        String archivos [] = fileList();

        if (ArchivoExiste(archivos, "Datos.txt")) {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput("Datos.txt"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                String AllData = "";

                while(linea != null) {
                    AllData = AllData + linea + "\n";
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            } catch (IOException e) {

            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        { // Cuando se sale de la aplicación esta parte permite
            // que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {}
    }

    private boolean ArchivoExiste(String archivos [], String FileName) {
        for(int i = 0; i < archivos.length; i++)
            if(FileName.equals(archivos[i]))
                return true;
        return false;
    }

    //Comprueba que el dispositivo Bluetooth Bluetooth está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[16];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent = new Intent(UserInterfaz.this, Ajustes.class);
                startActivity(intent);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}