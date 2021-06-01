package com.nitin.rssfeeddownloaderapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private final String KEY_FOR_OPTION_SELECTED = "KEY_FOR_OPTION_SELECTED";
    private final String KEY_FOR_LIMIT_SELECTED = "KEY_FOR_LIMIT_SELECTED";
    private final String KEY_FOR_PREVIOUS_URL = "KEY_FOR_PREVIOUS_URL";
    private final String KEY_FOR_CURRENT_SELECTED = "KEY_FOR_CURRENT_SELECTED";

    private ListView xmlListView;
    private String optionSelected;
    private int limitSelected = 10; //default value is 10
    private String previousURL = null;
    private String textOfCurrentSelected = "Please choose a option from Menu Bar at Top-Right";
    private TextView currSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xmlListView = findViewById(R.id.xmlListView);
        currSelected = findViewById(R.id.currentSelected);
        currSelected.setText(textOfCurrentSelected);
        // using setOnItemClickListener, we get the reference of the View which got selected inside ListView
        xmlListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemClick: position of view got selected: " + position);
                // we code the following code to run the given url in browser.
                TextView tvURL = view.findViewById(R.id.tvURL);
                String url = tvURL.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }


    // added save functionality for optionSelected and limitSelected and previous processed url
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        optionSelected = savedInstanceState.getString(KEY_FOR_OPTION_SELECTED);
        limitSelected = savedInstanceState.getInt(KEY_FOR_LIMIT_SELECTED);
        previousURL = savedInstanceState.getString(KEY_FOR_PREVIOUS_URL);
        textOfCurrentSelected = savedInstanceState.getString(KEY_FOR_CURRENT_SELECTED);
        startDownload(previousURL); // we re-download cuz textViews lost their contents when when orientation changes

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(KEY_FOR_OPTION_SELECTED, optionSelected);
        outState.putInt(KEY_FOR_LIMIT_SELECTED, limitSelected);
        outState.putString(KEY_FOR_PREVIOUS_URL, previousURL);
        outState.putString(KEY_FOR_CURRENT_SELECTED, textOfCurrentSelected);
        super.onSaveInstanceState(outState);

    }

    @Override //To specify the options menu for an activity, override onCreateOptionsMenu()
    public boolean onCreateOptionsMenu(Menu menu) {
        //To use the menu in your activity, you need to inflate the menu resource (convert the XML resource into a programmable object) using MenuInflater.inflate().
        // AppCompactActivity is a context itself, we can get the context and add our R.menu.feeds_menu to it.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.feeds_menu, menu);
        // we write this setChecked() only when we rotate our phone, the app gets relaunched(this means our onCreateMenuOption get re-invoked) and default of 10 is selected.
        // now as we are saving limitSelected , we should update our checked item as well
        if (limitSelected == 10) menu.findItem(R.id.top10).setChecked(true);
        else if (limitSelected == 25) menu.findItem(R.id.top25).setChecked(true);
        return true;    // successfully inflated our menu
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/%s/limit=%d/xml";
        int idOfSelectedMenu = item.getItemId();
        boolean reloadSelected = false;
        switch (idOfSelectedMenu) {
            case R.id.menuFree:
                optionSelected = "topFreeApplications";
                textOfCurrentSelected = " Top Free Apps";
                break;
            case R.id.menuPaid:
                optionSelected = "topPaidApplications";
                textOfCurrentSelected = " Top Paid Apps";
                break;
            case R.id.menuSong:
                optionSelected = "topSongs";
                textOfCurrentSelected = " Top Songs";
                break;
            case R.id.menuMovies:
                optionSelected = "topMovies";
                textOfCurrentSelected = " Top Movies";
                break;
            case R.id.top10:
                limitSelected = 10;
                // setting the checked property if we selected this item
                if (!item.isChecked()) item.setChecked(true);
                break;
            case R.id.top25:
                limitSelected = 25;
                if (!item.isChecked()) item.setChecked(true);
                break;
            case R.id.reload:
                reloadSelected = true;
                break;
            default:    // when a sub-menu is selected
                return super.onOptionsItemSelected(item);
        }

        String currURL = String.format(url, optionSelected, limitSelected);

        // we will download only if our previous processed url does not match with current given url or a reload menu is pressed.
        // if reload is pressed, last processed url is executed as reload menu is pressed, optionSelected and limitSelected hasn't changed hence currURL hasn't changed
        if (reloadSelected || !currURL.equals(previousURL)) {
            startDownload(currURL);
        }
        return true;
    }

    private void startDownload(String currURL) {
        DownloadDataTask task = new DownloadDataTask();
        task.execute(currURL);
        currSelected.setText(limitSelected + textOfCurrentSelected);
        previousURL = currURL;
    }

    private class DownloadDataTask extends AsyncTask<String, Void, String> {
        // An asynchronous task is defined by a computation that runs on a background thread and whose result is published on the UI thread.
        // This helps us to run our task in background(like downloading) in parallel with the app so that for heavy tasks, app don't get freeze.
        // Class AsyncTask<Params,Progress,Result> is a abstract class having 3 parameters.
        // 1st parameter: The data type of parameters upon which we work, in our case we will send url as a String.
        // 2nd parameter: The data type of progress which is going to be displaced, Void means we don't want to display our progress.
        // 3rd parameter: The data type of our result, in our case it would be xml

        private static final String TAG = "DownloadDataTask";

        @Override
        // after completion of doInBackground(), it will be called by passing the return value of doInBackground()
        protected void onPostExecute(String xmlData) {

            ParseApps parseApps = new ParseApps();
            if (!parseApps.parse(xmlData)) Log.e(TAG, "onPostExecute: parsing is unsuccessful");
//            // arrayAdapter as a bridge btw UI and data source
//            // it takes the context: MainActivity.this
//            // the resource ID: R.layout.list_item, which is the View which will be used by the adapter to display contents
//            // the list of Objects which is our data source.
//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<>(MainActivity.this,R.layout.list_item,parseApps.getAppInfoList());
//            // linking ihe arrayAdapter with the view which will use this adapter
//            xmlListView.setAdapter(arrayAdapter);

            // We will now be using our CustomAdapter to display name,artist ,release date and summary of apps.
            CustomAdapterForListView<FeedEntry> customAdapter = new CustomAdapterForListView<>(MainActivity.this, R.layout.item_info, parseApps.getAppInfoList());
            xmlListView.setAdapter(customAdapter);
        }

        @Override   // this runs in other thread asynchronously
        protected String doInBackground(String... str) {
            // str is treated as an array
            // we will only do the task for 1st element in str only
            String xmlData = downloadXML(str[0]);
            if (xmlData == null) Log.e(TAG, "doInBackground: can't download the xml Data");
            return xmlData;
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();
            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // opening connection with urlPath
                //when we connect with a http, server gives us response code like 404(Can not found)
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "downloadXML: the response code: " + responseCode);

                // buffered reader is used as it is fast.
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                for (; ; ) {
                    String str = bufferedReader.readLine();
                    if (str == null)
                        break; //readline until lines are left, if no more line left, str will be null
                    xmlResult.append(str).append('\n');
                }
                bufferedReader.close();
                return xmlResult.toString();

            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadXML: Invalid URL: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadXML: Got IOException: " + e.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: can not establish connection with url due to : " + e.getMessage());
            }
            return null;    // if we can not read the content in URL successfully, return null

        }
    }

}