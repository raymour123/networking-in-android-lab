package com.example.raymour.networkinglab;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    Button mCereal;
    Button mTea;
    Button mChocolate;
    ListView mListView;
    ArrayList<String> apiResults;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);
        mCereal = (Button) findViewById(R.id.buttonCereal);
        mTea = (Button) findViewById(R.id.buttonTea);
        mChocolate = (Button) findViewById(R.id.buttonChocolate);



        apiResults = new ArrayList<>();





        ConnectivityManager conMng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMng.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            mCereal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    apiResults.clear();
                    new ApiCallTask().execute("http://api.walmartlabs.com/v1/search?query=cereal&format=json&apiKey=6c8bqc73q859zx9s3vfzsuhg");
                    Log.v("API", "Cereal API Worked");
                }
            });
            mTea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    apiResults.clear();
                    new ApiCallTask().execute("http://api.walmartlabs.com/v1/search?query=tea&format=json&apiKey=6c8bqc73q859zx9s3vfzsuhg");
                    Log.v("API", "Tea API Worked");
                }
            });
            mChocolate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    apiResults.clear();
                    new ApiCallTask().execute("http://api.walmartlabs.com/v1/search?query=chocolate&format=json&apiKey=6c8bqc73q859zx9s3vfzsuhg");
                    Log.v("API", "Chocolate API Worked");
                }
            });

        } else {
            Toast.makeText(MainActivity.this, "No Network Available!", Toast.LENGTH_SHORT).show();
        }

    }

    //GET request
    public void performGetRequest(String myUrl) throws IOException, JSONException {
        InputStream is = null;
        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            is = conn.getInputStream();
            String contentAsString = readIt(is);
            parseJson(contentAsString);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private void parseJson(String contentAsString) throws JSONException {
        JSONObject root = new JSONObject(contentAsString);
        JSONArray array = root.getJSONArray("items");
        for (int i = 0; i < array.length(); i++) {
            JSONObject product = array.getJSONObject(i);//have the object, get the name and add to list
            apiResults.add(product.getString("name"));
        }
    }

    private String readIt(InputStream is) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String read;
        while ((read = reader.readLine()) != null) {
            builder.append(read);
        }
        return builder.toString();
    }

    public class ApiCallTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            try {
                performGetRequest(strings[0]);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return "Unable to retrieve web page. URL may be invalid";
            } catch (JSONException e) {
                e.printStackTrace();
                return "JSON Parsing Issue";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, apiResults);
            mListView.setAdapter(adapter);
        }


    }

}
