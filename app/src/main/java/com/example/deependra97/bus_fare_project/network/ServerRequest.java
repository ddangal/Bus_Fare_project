package com.example.deependra97.bus_fare_project.network;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.example.deependra97.bus_fare_project.app.Utilities;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by deependra97 on 7/22/2017.
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class ServerRequest extends AsyncTask<Void,Void,String> {


    public   interface  OnDataReceiver{
        void onSuccess(String res);
        void onError(String message);
    }
    String url ;
    OnDataReceiver listener;
    public ServerRequest(String url, OnDataReceiver listener)
    {
        Utilities.log("On constructor or server request");
        this.listener = listener;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        Log.d("","preExecute");
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Utilities.log("Do in background");
            String s = getDate(url);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("","postExecute + " + s);
        //TODO : CREATE CALL BACK
        if (s.isEmpty()){
            listener.onError("Connection failed");
        }else{
            listener.onSuccess(s);
        }
        super.onPostExecute(s);
    }
    public String getDate(String url) throws Exception
    {
        OkHttpClient ok = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = ok.newCall(request).execute();
        return response.body().string();

    }

}

