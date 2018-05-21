package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02.network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {
    //Client
    EditText addressEditText = null;
    EditText clientPortEditText = null;
    EditText commandEditText = null;
    Button executeButton = null;

    //Server
    EditText serverPort = null;
    Button startButton = null;

    //Threads
    ServerThread serverThread;
    ClientThread clientThread;

    private class StartButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPortNum = serverPort.getText().toString();
            if (serverPortNum == null || serverPortNum.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPortNum));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }

    StartButtonClickListener startButtonClickListener = new StartButtonClickListener();

    private SendCommandClickListener sendCommandClickListener = new SendCommandClickListener();
    private class SendCommandClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = addressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String command = commandEditText.getText().toString();
            if (command == null || command.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }


            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), command);
            clientThread.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);
        addressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        commandEditText = findViewById(R.id.command_edit_text);
        executeButton = findViewById(R.id.execute_button);
        serverPort = findViewById(R.id.server_port_edit_text);
        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(startButtonClickListener);
        executeButton.setOnClickListener(sendCommandClickListener);
    }
    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
