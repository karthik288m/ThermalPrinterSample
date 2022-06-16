package com.thermal.printer.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.thermal.printer.example.bluetooth.AsyncBluetoothEscPosPrint;
import com.thermal.printer.example.bluetooth.AsyncEscPosPrint;
import com.thermal.printer.example.bluetooth.AsyncEscPosPrinter;
import com.thermal.printer.example.bluetooth.MyBluetoothPrintersConnections;

public class MainActivity extends AppCompatActivity {


    EditText tv_print_text;
    AppCompatButton btn_print;
    String textToPrint;

    public static final int PERMISSION_BLUETOOTH = 1;
    public static final int PERMISSION_BLUETOOTH_ADMIN = 2;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 3;
    public static final int PERMISSION_BLUETOOTH_SCAN = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_print_text = findViewById(R.id.et_print_input);
        btn_print = findViewById(R.id.btn_print);


        requestBluetoothPermission();

        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!tv_print_text.getText().toString().isEmpty()){
                    textToPrint = tv_print_text.getText().toString();
                    new AsyncBluetoothEscPosPrint(
                            MainActivity.this,
                            new AsyncEscPosPrint.OnPrintFinished() {
                                @Override
                                public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
                                    Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !");
                                    Toast.makeText(MainActivity.this, "Printer not connected", Toast.LENGTH_SHORT).show();

                                    browseBluetoothDevice();
                                    //  browseBluetoothDevice();
                                }

                                @Override
                                public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
                                    Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !");
                                    Toast.makeText(MainActivity.this, "Print Success", Toast.LENGTH_SHORT).show();

                                }
                            }
                    )
                            .execute(getTestPrinter(selectedDevice));
                } else {
                    Toast.makeText(MainActivity.this, "Empty Print text", Toast.LENGTH_SHORT).show();
                }


            }

        });
    }


    private BluetoothConnection selectedDevice;
    public void browseBluetoothDevice() {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

        if (bluetoothDevicesList != null) {


            selectedDevice = MyBluetoothPrintersConnections.selectFirstPaired();
        } else {
            Toast.makeText(MainActivity.this, "Bluetooth device not found", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Asynchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getTestPrinter(DeviceConnection printerConnection) {

        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 203, 58f, 32);



        return printer.addTextToPrint(


                "[C]"+textToPrint

        );

    }

    public  void requestBluetoothPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);
        }
    }
}