package com.umkc.hastimal.nursetogo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.os.Bundle;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
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
import com.ibm.cio.watsonsdk.SpeechToText;
import com.ibm.cio.watsonsdk.TextToSpeech;


public class MainActivity extends Activity implements SpeechDelegate {

    TextView textResponse;
    TextView textSTT;
    TextView textTTS;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear,buttonBackward,buttonForward,buttonLeft,buttonRight,buttonHeadUp,buttonHeadDown, buttonTTS;
    ImageButton buttonStop;
    ImageButton recordButton;
    String command;
    Boolean checkupdate=false;



    private final int REQ_CODE_SPEECH_INPUT = 100;
    private boolean isServiceActive = false;
    private boolean speaking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);
        textSTT = (TextView)findViewById(R.id.textView);
        textTTS = (TextView)findViewById(R.id.ttsText);
        buttonBackward=(Button) findViewById(R.id.backward);
        buttonForward=(Button) findViewById(R.id.forward);
        buttonLeft=(Button) findViewById(R.id.left);
        buttonRight=(Button) findViewById(R.id.right);
        buttonHeadUp=(Button) findViewById(R.id.headUp);
        buttonHeadDown=(Button) findViewById(R.id.headDown);
        buttonStop = (ImageButton)findViewById(R.id.stop);

        recordButton =(ImageButton) findViewById(R.id.recordButton);
        buttonTTS = (Button)findViewById(R.id.ttsButton);

        buttonStop.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Log.d(".....Stop Button","...pressed");
                // TODO Auto-generated method stub
                command="stop";
                checkupdate=true;
            }

        });

        recordButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {

                //command="stop";
//                checkupdate=true;

                //Speak();


                if(!isServiceActive)
                {
                    try {

                        //activate sst service (one time call)
                        activateSTT();

                        //service activator. do not try again
                        isServiceActive = true;

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }

                if(!speaking) {
                    Log.d("Record button","...pressed");

                    speaking = true;

                    textSTT.setText(""); //clear text

                    SpeechToText.sharedInstance().recognize();

                }
                else {

                    Log.d("Stop recording button","...pressed");

                    speaking = false;

                    SpeechToText.sharedInstance().stopRecording();
                }
            }

        });

        buttonTTS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    TextToSpeech.sharedInstance().initWithContext(getWatsonTTSServiceUrl(),getApplicationContext() );

                    TextToSpeech.sharedInstance().setUsername(ttsUsername);
                    TextToSpeech.sharedInstance().setPassword(ttsPassword);

                    TextToSpeech.sharedInstance().synthesize(textTTS.getText().toString());

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
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

    public void activateSTT() throws URISyntaxException {
        SpeechToText.sharedInstance().initWithContext(this.getWatsonSTTServiceUrl(), this.getApplicationContext(), false);

        SpeechToText.sharedInstance().setUsername(sttUsername);
        SpeechToText.sharedInstance().setPassword(sttPassword);
        SpeechToText.sharedInstance().setDelegate(this); //this main activity is the delegate


    }

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

        Log.d("STT service", "established!");


    }

    public void onError(String error) {

        Log.d("STT service","established!");
    }

    public void onClose(int code, String reason, boolean remote) {

        Log.d("STT service","closed!");
    }

    public void onMessage(String message) {
        Log.d("RCV: ", message);

        textSTT.append(message);
    }

    //@Override
    public void receivedMessage(int i, QueryResult queryResult) {

    }

    private URI getWatsonSTTServiceUrl() throws URISyntaxException {
        return new URI(sttApiURL);
    }

    private URI getWatsonTTSServiceUrl() throws URISyntaxException {
        return new URI(ttsApiURL);
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
