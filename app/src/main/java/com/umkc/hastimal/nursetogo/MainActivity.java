package com.umkc.hastimal.nursetogo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Locale;

import android.os.AsyncTask;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.ibm.cio.dto.QueryResult;
import com.ibm.cio.watsonsdk.SpeechDelegate;
import com.ibm.cio.watsonsdk.TextToSpeech;


public class MainActivity extends Activity implements SpeechDelegate {

    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear,buttonBackward,buttonForward,buttonLeft,buttonRight,buttonHeadUp,buttonHeadDown;
    ImageButton recordButton;
    String command;
    Boolean checkupdate=false;

    public final static String username = "********************************";
    public final static String password = "********************************";
    public final static String apiURL = "https://stream.watsonplatform.net/speech-to-text-beta/api";
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);

        buttonBackward=(Button) findViewById(R.id.backward);
        buttonForward=(Button) findViewById(R.id.forward);
        buttonLeft=(Button) findViewById(R.id.left);
        buttonRight=(Button) findViewById(R.id.right);
        buttonHeadUp=(Button) findViewById(R.id.headUp);
        buttonHeadDown=(Button) findViewById(R.id.headDown);
        recordButton =(ImageButton) findViewById(R.id.recordButton);

        recordButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Log.d(".....Stop Button","...pressed");

                //command="stop";
                checkupdate=true;

                Speak();
            }

        });
        buttonHeadDown.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Log.d("......HeadDown Button","...pressed");
                // TODO Auto-generated method stub
                command="head down";
                checkupdate=true;//
                //for headDown
            }

        });
        buttonHeadUp.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Log.d(".....HeadUp Button","...pressed");
                // TODO Auto-generated method stub
                command="head up";
                checkupdate=true;
            }

        });
        buttonRight.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Log.d(".....Right Button","...pressed");
                // TODO Auto-generated method stub
                command="right";
                checkupdate=true;
            }

        });
        buttonLeft.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Log.d(".....Left Button","...pressed");
                // TODO Auto-generated method stub
                command="left";
                checkupdate=true;
            }

        });
        buttonBackward.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Log.d(".....Backward Button","...pressed");
                // TODO Auto-generated method stub
                command="backward";
                checkupdate=true;
            }

        });
        buttonForward.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Log.d(".....Forward Button","...pressed");
                // TODO Auto-generated method stub
                command="forward";
                checkupdate=true;
            }

        });
        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonClear.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(".....Clear Button","...pressed");
                textResponse.setText("");
            }});
    }
    OnClickListener buttonConnectOnClickListener =
            new OnClickListener(){


                @Override
                public void onClick(View arg0) {
                    Log.d(".....Connect Button","...pressed");
                    MyClientTask myClientTask = new MyClientTask(
                            editTextAddress.getText().toString(),
                            Integer.parseInt(editTextPort.getText().toString()));
                    myClientTask.execute();
                }};

    public void Speak()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Start speaking!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "speech not supported :(",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onOpen() {
        // the  connection to the STT service is successfully opened
    }

    public void onError(String error) {
        // error interacting with the STT service
    }

    public void onClose(int code, String reason, boolean remote) {
        // the connection with the STT service was just closed
    }

    public void onMessage(String message) {
        // a message comes from the STT service with recognition results
    }

    @Override
    public void receivedMessage(int i, QueryResult queryResult) {

    }

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        MyClientTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }
        @Override
        protected Void doInBackground(Void... arg0) {

            OutputStream outputStream;
            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                Log.d("MyClient Task", "Destination Address : " + dstAddress);
                Log.d("MyClient Task", "Destination Port : " + dstPort + "");
                outputStream = socket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);

                while (true) {
                    if(checkupdate)
                    {
                        Log.d("Command", command);
                        Log.d("checkUpdate", checkupdate.toString());
                        printStream.print(command);
                        printStream.flush();
                        Log.d("Socket connection", socket.isClosed()+"");
                        checkupdate=false;
                    }
                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            super.onPostExecute(result);
        }





    }
}
