package com.example.frank.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView listApps;
    private String feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    private String feedCachedURL = "Invalidated";
    public static final String STATE_URL = "feedURL";
    public static final String STATE_LIMIT = "feedLimit";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView) findViewById(R.id.xmlListView);

        if(savedInstanceState != null){
            feedURL = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        downloadURL(String.format(feedURL, feedLimit)); //calls download with feedURL and replaces %d with feedLimit

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if(feedLimit == 10){
            menu.findItem(R.id.mnuTop10).setChecked(true);
        }else{
            menu.findItem(R.id.mnuTop25).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.mnuFree:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnuTop10:
            case R.id.mnuTop25:
                if(!item.isChecked()) {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedLimit to " + feedLimit);
                }else{
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " item feedLimit is unchanged");
                }
                    break;
            case R.id.mnuRefresh:
                feedCachedURL = "Invalidated";
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        downloadURL(String.format(feedURL, feedLimit)); //calls download with feedURL and replaces %d with feedLimit
        return true;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedURL);
        outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }

    private void downloadURL(String feedURL){
        if(!feedURL.equalsIgnoreCase(feedCachedURL)) {
            Log.d(TAG, "downloadURL: Starting Async Task");
            DownloadData downloadData = new DownloadData();
            downloadData.execute(feedURL);
            feedCachedURL = feedURL;
            Log.d(TAG, "downloadURL: Done.");

        }else{
            Log.d(TAG, "downloadURL: URL not changed");
        }
    }



    private class DownloadData extends AsyncTask<String, Void, String> { //Parameters: URL, usually progress bar(void for this), return type

        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.d(TAG, "onPostExecute: Parameter is: " + s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(
//                    MainActivity.this, R.layout.list_item, parseApplications.getApplications());
//            listApps.setAdapter(arrayAdapter);
            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record, parseApplications.getApplications());
            listApps.setAdapter(feedAdapter);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "doInBackground: Starts with:" + params[0]);
            String rssFeed = downLoadXML(params[0]);
            if(rssFeed == null)
                Log.e(TAG, "doInBackground: Error downloading" );
            return rssFeed;
        }

        private String downLoadXML(String urlPath){
            StringBuilder xmlResult = new StringBuilder();

            try{
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downLoadXML: Response Code was " + response);
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead;
                char[] inputBuffer = new char[500];

                while(true){
                    charsRead = reader.read(inputBuffer);
                    if(charsRead < 0)
                        break;
                    if(charsRead > 0)
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead) );
                }
                reader.close();
                return xmlResult.toString();
            }
            catch(MalformedURLException e){
                Log.e(TAG, "downLoadXML: Invalid URL " + e.getMessage());
            }
            catch(IOException e){
                Log.e(TAG, "downLoadXML:IO Exception Reading Data " + e.getMessage());
            }
            catch(SecurityException e){
                Log.e(TAG, "downLoadXML: Security Exception. Needs Permission" + e.getMessage());
            }
            return null;
        }

    }

}
