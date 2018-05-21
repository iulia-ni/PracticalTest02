package ro.pub.cs.systems.eim.practicaltest02.network;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (command)!");
            String command = bufferedReader.readLine();
            if (command == null || command.isEmpty() ) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (ccommand)");
                return;
            }
            String client = socket.getInetAddress().toString();

            HashMap<String, String> data = serverThread.getData();

            if (command.startsWith("set")) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Received set command");
                if (data.containsKey(client)) {
                    String[] time = command.split(" ");
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Contains ip; override " + time[1]);
                    serverThread.setData(client, time[1]);
                } else {
                    serverThread.setData(client, getCurrentTimeStamp());
                }
            }

            if (command.equalsIgnoreCase("reset")) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Received reset command");
                serverThread.resetData(client);
            }

            if (command.equalsIgnoreCase("poll")) {
                String result = null;
                if (!data.containsKey(client)){
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] No alarm");
                    result = "None\n";
                } else {

                    String time = data.get(client);
                    Calendar rightNow = Calendar.getInstance();
                    int hour = rightNow.get(Calendar.HOUR_OF_DAY);
                    int minute = rightNow.get(Calendar.MINUTE);
                    int setHour;
                    int setMinute;
                    String[] parts = time.split(":");
                    setHour = Integer.parseInt(parts[0]);
                    setMinute = Integer.parseInt(parts[1]);
                    if (setHour >= hour && setMinute >= minute ) {
                        Log.i(Constants.TAG, "[COMMUNICATION THREAD] active");
                        result = "active";
                    } else {
                        Log.i(Constants.TAG, "[COMMUNICATION THREAD] active");
                        result = "inactive";
                    }

                }
                if (result == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                    return;
                }

                printWriter.println(result);
                printWriter.flush();
            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
    public String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }
}
