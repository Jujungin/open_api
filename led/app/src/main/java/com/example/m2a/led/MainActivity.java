package com.example.m2a.led;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;


public class MainActivity extends AppCompatActivity
{
    private final static int DEVICES_DIALOG = 1;
    private final static int ERROR_DIALOG = 2;

    public static Context mContext;
    public static AppCompatActivity activity;

    // using file create, read, write
    final static String foldername = Environment.getExternalStorageDirectory().toString()+"/";
    final static String filename = "LOG.txt";

    TextView myLabel, mRecv;
    EditText myTextbox;
    static BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    //음성
    Intent intent;
    SpeechRecognizer mRecognizer;
    TextView textView;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // String time for LOG_DATA

        // path test code
        //Toast.makeText(this, getFilesDir().getAbsolutePath(), Toast.LENGTH_LONG).show();

        Toast.makeText(this, foldername, Toast.LENGTH_LONG).show();
        // create text file and folder




        Button sendButton = (Button)findViewById(R.id.send);
        myLabel = (TextView)findViewById(R.id.label);
        myTextbox = (EditText)findViewById(R.id.entry);
        mRecv = (TextView)findViewById(R.id.recv);

        mContext = this;
        activity=this;

        //BLE test
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            ErrorDialog("This device is not implement Bluetooth.");
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            ErrorDialog("This device is disabled Bluetooth.");
            return;
        }
        else
        //BLE paring list
        //if BLE connect , call doConnect function
            DeviceDialog();


        //if press Send button, call sendData
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    sendData();
                }
                catch (IOException ex) { }
            }

        });

        //edit1
        //start record function
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO
                );
            }
        }

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(recognitionListener);


        textView = (TextView) findViewById(R.id.textView);

        Button button = (Button) findViewById(R.id.button01);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer.startListening(intent);
            }
        });
        Button button_web = (Button) findViewById(R.id.button2);
        button_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_web = new Intent(MainActivity.this, webActivity.class);
                startActivity(intent_web);
            }
        });
    }

    // LOG contents
    public void OnFileWrite(String cmd) {
        String now = new SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(new Date());
        String contents = now+"_"+cmd+"\n";

        WriteTextFile(foldername, filename, contents);

    }

    // LOG write
    public void WriteTextFile(String foldername, String filename, String contents) {
        try{
            File dir = new File(foldername+"DCIM/work_data");
            if(!dir.exists()) {
                dir.mkdir();

                Toast.makeText(this, "success", Toast.LENGTH_LONG).show();
            }

            File log_file = new File(foldername+"DCIM/work_data/"+filename);


            FileOutputStream fos = new FileOutputStream(foldername+"DCIM/work_data/"+filename, true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();

            writer.close();
            fos.close();

        } catch(IOException e) {

        }
    }

    static public Set<BluetoothDevice> getPairedDevices() {
        return mBluetoothAdapter.getBondedDevices();
    }

    @Override
    public void onBackPressed() {
        doClose();
        super.onBackPressed();
    }


    // press back key or get exceoption, then start thread what end this thread, in-out stream
    // close socket
    public void doClose() {
        workerThread.interrupt();
        new CloseTask().execute();
    }



    public void doConnect(BluetoothDevice device) {
        mmDevice = device;

        // Standard SerialPortService ID
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {

            // creat socket using UUID service of BLE module
            // saving UUID for using serial communication
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);

            // end BLE search
            mBluetoothAdapter.cancelDiscovery();

            //start ConnectTask
            new ConnectTask().execute();
        } catch (IOException e) {
            Log.e("", e.toString(), e);
            ErrorDialog("doConnect "+e.toString());
        }
    }

    private class ConnectTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                // BLE socket connect
                mmSocket.connect();

                // bringing in-out stream
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();

                beginListenForData();


            } catch (Throwable t) {
                Log.e( "", "connect? "+ t.getMessage() );
                doClose();
                return t;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Object result) {
            // print BLE connect status
            myLabel.setText("Bluetooth Opened");
            if (result instanceof Throwable)
            {
                Log.d("","ConnectTask "+result.toString() );
                ErrorDialog("ConnectTask "+result.toString());

            }
        }
    }
    private class CloseTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground(Void... params) {
            try {
                try{mmOutputStream.close();}catch(Throwable t){/*ignore*/}
                try{mmInputStream.close();}catch(Throwable t){/*ignore*/}
                mmSocket.close();
            } catch (Throwable t) {
                return t;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Throwable) {
                Log.e("",result.toString(),(Throwable)result);
                ErrorDialog(result.toString());
            }
        }
    }



    public void DeviceDialog()
    {
        if (activity.isFinishing()) return;

        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
        MyDialogFragment alertDialog = MyDialogFragment.newInstance(DEVICES_DIALOG, "");
        alertDialog.show(fm, "");
    }



    public void ErrorDialog(String text)
    {
        if (activity.isFinishing()) return;

        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
        MyDialogFragment alertDialog = MyDialogFragment.newInstance(ERROR_DIALOG, text);
        alertDialog.show(fm, "");
    }


    void beginListenForData()
    {
        final Handler handler = new Handler(Looper.getMainLooper());

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == '\n')
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");

                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            mRecv.setText(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    // String is exist where Textbox, then start sendData function
    void sendData() throws IOException
    {
        String msg = myTextbox.getText().toString();
        if ( msg.length() == 0 ) return;

        msg += "\n";
        Log.d(msg, msg);
        mmOutputStream.write(msg.getBytes());
        myLabel.setText("Data Sent");
        myTextbox.setText(" ");
    }

    // record function
    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int i) {
            textView.setText("너무 늦게 말하면 오류뜹니다");

        }

        @Override
        public void onResults(Bundle bundle) {
            String key = "";
            String[] cmd;

            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = bundle.getStringArrayList(key);

            Button sendButton = (Button)findViewById(R.id.send);

            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            textView.setText(rs[0]);
            cmd = textView.getText().toString().split("");

            for( int i=0 ; i<cmd.length ; i++) {
                if( cmd[i].equals("불")) {
                    for( int j=i ; j<cmd.length ; j++) {
                        if( cmd[j].equals("켜")) {
                            myTextbox.setText("a");
                            textView.setText("a");
                            OnFileWrite("on");
                        } else if( cmd[j].equals("꺼")) {
                            myTextbox.setText("b");
                            textView.setText("b");
                            OnFileWrite("off");
                        }

                    }
                }
            }

            //sendButton.performClick();   //수정1
            try {
                sendData();
            } catch(IOException ex) {

            }
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };
}

