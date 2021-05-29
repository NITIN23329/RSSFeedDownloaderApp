package com.nitin.rssfeeddownloaderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting Async Task");
        DownloadDataTask task = new DownloadDataTask();
        task.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
        Log.d(TAG, "onCreate: ending onCreate");
    }
    private  class DownloadDataTask extends AsyncTask<String,Void,String>{
        // An asynchronous task is defined by a computation that runs on a background thread and whose result is published on the UI thread.
        // This helps us to run our task in background(like downloading) in parallel with the app so that for heavy tasks, app don't get freeze.
        // Class AsyncTask<Params,Progress,Result> is a abstract class having 3 parameters.
        // 1st parameter: The data type of parameters upon which we work, in our case we will send url as a String.
        // 2nd parameter: The data type of progress which is going to be displaced, Void means we don't want to display our progress.
        // 3rd parameter: The data type of our result, in our case it would be xml

        private static final String TAG = "DownloadDataTask";
        @Override   // after completion of doInBackground(), it will be called by passing the return value of doInBackground()
        protected void onPostExecute(String xmlData) {
            super.onPostExecute(xmlData);
//            Log.d(TAG, "onPostExecute: parameter is: " + s);
            ParseApps parser = ParseApps.getInstance();
            if(!parser.parse(xmlData)) Log.e(TAG, "onPostExecute: can not parse rss feed successfully");
            else {
                for(FeedEntry appInfo: parser.getAppInfoList()) System.out.println(appInfo);
            }
        }

        @Override   // this runs in other thread asynchronously
        protected String doInBackground(String ... str) {
            // str is treated as an array
            Log.d(TAG, "doInBackground: starts with: "+str[0]);
            // we will only do the task for 1st element in str only
            String rssFeed = downloadXML(str[0]);
            if(rssFeed==null) Log.e(TAG, "doInBackground: can't download the rssFeed");
            return rssFeed;
        }

        private String downloadXML(String urlPath){
            StringBuilder xmlResult = new StringBuilder();
            try{
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection(); // opening connection with urlPath
                //when we connect with a http, server gives us response code like 404(Can not found)
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "downloadXML: the response code: "+ responseCode);

                // buffered reader is used as it is fast.
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                for(;;){
                    String str = bufferedReader.readLine();
                    if(str==null)break; //readline until lines are left, if no more line left, str will be null
                    xmlResult.append(str).append('\n');
                }
                bufferedReader.close();
                return xmlResult.toString();

            }catch (MalformedURLException e){
                Log.e(TAG, "downloadXML: Invalid URL: "+e.getMessage() );
            }catch (IOException e){
                Log.e(TAG, "downloadXML: Got IOException: "+e.getMessage());
            }catch (SecurityException e){
                Log.e(TAG, "downloadXML: can not establish connection with url due to : "+e.getMessage());
            }
            return null;    // if we can not read the content in URL successfully, return null

        }
    }

}