package android.dkh.com.mygame;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by MyPC on 19/04/2018.
 */

public class MyTask extends AsyncTask<String, Void, Void> {
    Socket s;
    private static PrintWriter printWriter;
    private static String ip=" 192.168.1.11";

    @Override
    protected Void doInBackground(String... voids) {
        String message = voids[0];
        try {
            s = new Socket(ip, 5000);
            printWriter = new PrintWriter(s.getOutputStream());
            printWriter.write(message);
            printWriter.flush();
            printWriter.close();
            s.close();

        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
