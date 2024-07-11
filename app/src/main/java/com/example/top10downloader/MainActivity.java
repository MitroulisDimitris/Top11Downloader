package com.example.top10downloader;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "main activity";
    private Button button;
    private int buttonState;
    private static final String STATE_BUTTON = "buttonState";
    private ListView listApps;
    private boolean state = false;
    private String feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    String currFeedURL = String.format(feedURL, feedLimit);
    public static final String STATE_URL = "feedUrl";
    public static final String STATE_LIMIT = "feedLimit";
    public static final String STATE_CURRURL = "currFeed";


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        buttonState = savedInstanceState.getInt(STATE_BUTTON);
        button.setVisibility(buttonState);

        feedURL = savedInstanceState.getString(STATE_URL);
        feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        currFeedURL = savedInstanceState.getString(STATE_CURRURL);
        Refresh();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(STATE_BUTTON, buttonState);

        outState.putString(STATE_URL, feedURL);
        outState.putInt(STATE_LIMIT, feedLimit);
        outState.putString(STATE_CURRURL, currFeedURL);

        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //hideNavBar();



//        if (savedInstanceState != null) {
//            feedURL = savedInstanceState.getString(STATE_URL);
//            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
//            currFeedURL = savedInstanceState.getString(STATE_CURRURL);
//        }

        listApps = (ListView) findViewById(R.id.xmlListView);
        button = findViewById(R.id.button);
        View.OnClickListener ButtonListener = view -> {


            downloadURL(String.format(feedURL, feedLimit));

            button.setVisibility(View.GONE);
            buttonState = View.GONE;
        };
        button.setOnClickListener(ButtonListener);
        Log.d(TAG, "onCreate: Done");
        //Refresh();
    }


    private void hideNavBar() {
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);

//        if(feedLimit == 10){
//            menu.findItem(R.id.mnu10).setChecked(true);
//
//        }else {
//            menu.findItem(R.id.mnu25).setChecked(true);
//        }
        return true;
    }


    public void Refresh() {
        Log.d(TAG, "Refresh: "+(buttonState == View.GONE));
        if (buttonState == View.GONE) {

            downloadURL(String.format(feedURL, feedLimit));

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //called when an item is selected from menu
        int id = item.getItemId();
        switch (id) {
            case R.id.mnuFree:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnuRefresh:
                Refresh();
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + "setting feedLimit to " + feedLimit);
                } else {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + "setting feedLimit to unchanged");
                }
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        //Log.d(TAG, "currFeed: "+ currFeedURL+ "feedURL " +String.format(feedURL,feedLimit));
        //Log.d(TAG, "onOptionsItemSelected: "+ currFeedURL.equals(String.format(feedURL, feedLimit)));//just for debugging
        if (currFeedURL.equals(String.format(feedURL, feedLimit)) || buttonState != View.GONE) {
            Log.d(TAG, "url the same!: ");
        } else {
            currFeedURL = String.format(feedURL, feedLimit);
            Log.d(TAG, "url different!: ");
            downloadURL(String.format(feedURL, feedLimit));
        }
        Log.d(TAG, "onOptionsItemSelected: feedURL" + feedURL);

        return super.onOptionsItemSelected(item);
    }

    private void downloadURL(String feedURL) {
        button.setVisibility(View.GONE);
        buttonState = View.GONE;
        Log.d(TAG, "downloadURL: Starting Download");
        DownloadData downloadData = new DownloadData();
        downloadData.execute(feedURL);
        Log.d(TAG, "downloadURL: Download Finished!");


    }

    private class DownloadData extends AsyncTask<String, Void, String> {//String is url to rss feed , where void is for displaying progress bars, String for return
        private static final String TAG = "Download";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s); //s is the xml file

//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(
//                    MainActivity.this, R.layout.list_item,parseApplications.getApplications());   //array adapter will be using feed entry obj
//            listApps.setAdapter(arrayAdapter);

            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record, parseApplications.getApplications());
            listApps.setAdapter(feedAdapter);
        }


        @Override
        protected String doInBackground(String... strings) {
            //Log.d(TAG, "doInBackground:  starts with " + strings[0]);
            String RssFeed = downloadFXML(strings[0]);
            if (RssFeed == null) {
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return RssFeed;
        }

        private String downloadFXML(String urlPath) {//the reuturn from this method goes to onPostExecute
            StringBuilder xmlResult = new StringBuilder();

            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was " + response);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead;
                char[] inputBuffer = new char[500];
                while (true) {
                    charsRead = reader.read(inputBuffer);
                    if (charsRead < 0)
                        break;

                    if (charsRead > 0) {
                        //xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));

                    }
                }
                reader.close();//when buffered reader closes, input Stream reader and input stream closes as well

                return xmlResult.toString();

            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadXML: IO Exception reading data: " + e.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: Security Exception.  Needs permisson? " + e.getMessage());
            }
            return null;
        }

    }


}
