package com.example.thatapp.connection;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class Connection {
    public static HttpURLConnection createConnection()
    {
        HttpURLConnection connection=null;
        try
        {
            //change url on different networks
            URL url=new URL("http://192.168.43.80/dealin/route.php");
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

        }catch(Exception e)
        {
            Log.d("connection failed",e.toString());
        }
        return connection;
    }
}
