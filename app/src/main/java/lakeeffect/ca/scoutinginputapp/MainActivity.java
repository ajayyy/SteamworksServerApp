package lakeeffect.ca.scoutinginputapp;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView connected;
    int connectedDevices = 1;
    Thread thread;

    BluetoothSocket bluetoothSocket;
    BluetoothAdapter bluetoothAdapter;
    OutputStream out;
    InputStream in;

    ArrayList<String> deviceNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivityForResult(intent, 3);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(turnOn, 0);

        Button connect = ((Button) findViewById(R.id.connect));
        assert(connect != null);
//        Thread bluetooth = new BluetoothConnection();/

        Thread bluetooth = new Thread(){
            public void run() {
                while(true) {
                    try {
                        final BluetoothServerSocket bss = bluetoothAdapter.listenUsingRfcommWithServiceRecord("SteamworksScoutingApp", UUID.fromString("6ba6afdc-6a0a-4b1d-a2bf-f71ac108b636"));
                        bluetoothSocket = bss.accept();
                        System.out.println("accepted");
                        out = bluetoothSocket.getOutputStream();
                        in = bluetoothSocket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bluetoothSocket.isConnected()) {
                        new BluetoothConnection(bluetoothSocket, out, in, MainActivity.this).start();
                    }
                }
            }
        };
        bluetooth.start();

        openPullDataDialog();

//        connect.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
////                Set<BluetoothDevice> pairedDevices = ba.getBondedDevices();
////                final BluetoothDevice[] devices = pairedDevices.toArray(new BluetoothDevice[0]);
////                String[] names = new String[devices.length];
////                if (pairedDevices.size() > 0) {
////                    for (int i=0;i<devices.length;i++) {
////                        names[i] = devices[i].getName();
////                    }
//////                    new AlertDialog.Builder(MainActivity.this)
//////                            .setTitle("Which device should I connect to?")
//////                            .setMultiChoiceItems(names, null, new DialogInterface.OnMultiChoiceClickListener(){
//////                                @Override
//////                                public void onClick(DialogInterface dialog, final int which, boolean isChecked) {
//////                                    dialog.dismiss();
//////                                    thread = new Thread(){
//////                                        public void run(){
//////                                            BluetoothSocket bluetoothSocket = null;
//////                                            OutputStream out = null;
//////                                            InputStream in = null;
//////                                            try {
//////
//////                                                bluetoothSocket = devices[which].createRfcommSocketToServiceRecord(UUID.fromString("6ba6afdc-6a0a-4b1d-a2bf-f71ac108b636"));
//////                                                bluetoothSocket.connect();
//////                                                out = bluetoothSocket.getOutputStream();
//////                                                in = bluetoothSocket.getInputStream();
//////                                                //CONNECTED!!
//////                                                runOnUiThread(new Runnable() {
//////                                                    @Override
//////                                                    public void run() {
//////                                                        Toast.makeText(MainActivity.this, "connected!",
//////                                                                Toast.LENGTH_LONG).show();
//////                                                    }
//////                                                });
//////                                            } catch (IOException e) {
//////                                                e.printStackTrace();
//////                                            }
//////                                            String data = "";
//////                                            Log.d("HELLO", "KJLDJJLADS");
//////                                            while(out != null && in != null && bluetoothSocket.isConnected()){
//////                                                Log.d("HELLO", "");
//////                                                try {
//////
//////                                                    byte[] bytes = new byte[200];
//////                                                    Log.d("HELLO", "Reading" + bytes);
//////                                                    int amount = in.read(bytes);
//////                                                    Log.d("HELLO", "Read" + amount);
//////                                                    if(amount>0)  bytes = Arrays.copyOfRange(bytes, 0, amount);//puts data into bytes and cuts bytes
//////                                                    else continue;
//////                                                    String message = new String(bytes, Charset.forName("UTF-8"));
//////                                                    if (bytes.length > 0){
//////                                                        runOnUiThread(new Runnable() {
//////                                                            @Override
//////                                                            public void run() {
//////                                                                Toast.makeText(MainActivity.this, "Starting To Save......",
//////                                                                        Toast.LENGTH_LONG).show();
//////                                                            }
//////                                                        });
//////                                                        save(data + message);
//////                                                        out.write("done".getBytes(Charset.forName("UTF-8")));
//////                                                        Log.d("HELLO", "DONE SENT" + bytes);
//////                                                        data = "";
//////                                                        Log.d("HELLO", "Useless" + amount);
//////                                                    }else {
//////                                                        data += message;
//////                                                    }
//////                                                    final byte[] bytes2 = bytes;
//////                                                    Log.d("HELLO", "KJLDJJLADS" + bytes);
//////
//////                                                } catch (IOException e) {
//////                                                    e.printStackTrace();
//////                                                }
//////
//////
//////                                            }
//////                                            Log.d("HELLO", "KJLDJJLADS sssssss");
//////                                            runOnUiThread(new Runnable() {
//////                                                @Override
//////                                                public void run() {
//////                                                    Toast.makeText(MainActivity.this, "Connection Ended",
//////                                                            Toast.LENGTH_LONG).show();
//////                                                }
//////                                            });
//////                                        }
//////                                    };
//////                                    thread.start();
//////                                }
//////                            })
//////                            .setIcon(android.R.drawable.ic_dialog_alert)
//////                            .show();
////                }else{
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            Toast.makeText(MainActivity.this, "No devices paired...",
////                                    Toast.LENGTH_LONG).show();
////                        }
////                    });
////                    return;
////                }
//            }
//        });
    }

    /**
     * Opens the dialog that chooses which device to pull data from
     */
    public void openPullDataDialog(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        final BluetoothDevice[] devices = pairedDevices.toArray(new BluetoothDevice[0]);

        String[] names = new String[devices.length];
        if (pairedDevices.size() > 0) {
            for (int i = 0; i < devices.length; i++) {
                names[i] = devices[i].getName();
            }
        }

        new AlertDialog.Builder(MainActivity.this)
        .setTitle("Which device?")
        .setMultiChoiceItems(names, null, new DialogInterface.OnMultiChoiceClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        })
        .create()
        .show();
    }

    @Override
    public void onBackPressed(){

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            if(in!=null) in.close();
            if(out!=null) out.close();
            if(bluetoothSocket!=null) bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String parse(String string){
        String[] data = string.split("\n")[1].split(","); //auto
        String message = "Available: ";
        ArrayList<Integer> available = new ArrayList<>();

        for(int i=0;i<data.length;i+=3){
            if(Boolean.parseBoolean(data[i])){
                message += getDefense(i/3) + ", ";
                available.add(new Integer(i/3));
            }
        }

        message+= "\nReached: ";
        for(int i=1;i<data.length;i+=3){
            if(Boolean.parseBoolean(data[i])){
                message += getDefense((i-1)/3) + ", ";
            }
        }

        message+= "\nCrossed: ";
        for(int i=2;i<data.length;i+=3){
            if(Boolean.parseBoolean(data[i])){
                message += getDefense((i-2)/3) + ", ";
            }
        }

        data = string.split("\n")[2].split(","); //teleop

        for(Integer i: available){
            message += "\n" + getDefense(i) + ": " + data[i*2] + " attemps and " + data[i*2+1] + " crossed";
        }

        message += "Low Goals: " + data[data.length-4] + " attemps and " + data[data.length-3] + " goals scored";
        message += "High Goals: " + data[data.length-2] + " attemps and " + data[data.length-1] + " goals scored";

            return message;
        }

    public String getDefense(int i){
        switch(i){
            case 0:
                return "Low Bar";
            case 1:
                return "Portcullis";
            case 2:
                return "Cheval de Frise";
            case 3:
                return "Moat";
            case 4:
                return "Rampart";
            case 5:
                return "Drawbridge";
            case 6:
                return "Sally Port";
            case 7:
                return "Rock Wall";
            case 8:
                return "Rough Terrain";
        }
        return "";
    }

    public static void save(String data){

        System.out.println(data);

        File sdCard = Environment.getExternalStorageDirectory();

        File file = new File(sdCard.getPath() + "/#ScoutingData/" + data.split(":")[0] + ".csv");

        String labels = data.split(":")[1];
        data = data.replaceFirst(data.split(":")[0] + ":" + data.split(":")[1], "");

        try {
            boolean newfile = false;
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
                newfile = true;
            }

            FileOutputStream f = new FileOutputStream(file, true);

            OutputStreamWriter out = new OutputStreamWriter(f);

            if(newfile) out.write(labels);
            out.write(data);

//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(MainActivity.this, "Saved",
//                            Toast.LENGTH_LONG).show();
//                }
//            });

            out.close();
            f.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
