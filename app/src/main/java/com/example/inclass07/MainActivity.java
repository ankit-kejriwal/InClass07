package com.example.inclass07;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    EditText editTextName;
    TextView textViewlimit;
    SeekBar seekBarLimit;
    Button buttonSearch;
    RadioButton radioButtonTrackRating;
    RadioButton radioButtonArtistRating;
    RadioGroup radioGroup;
    ListView listViewSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.editTextName);
        textViewlimit = findViewById(R.id.textViewlimit);
        seekBarLimit = findViewById(R.id.seekBarLimit);
        buttonSearch = findViewById(R.id.buttonSearch);
        radioButtonTrackRating = findViewById(R.id.radioButtonTrackRating);
        radioButtonArtistRating = findViewById(R.id.radioButtonArtistRating);
        radioGroup = findViewById(R.id.radioGroup);
        listViewSource = findViewById(R.id.ListViewSource);
        seekBarLimit.setMin(5);
        seekBarLimit.setMax(25);
        textViewlimit.setText(5+"");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                String songname  ="q="+ editTextName.getText().toString();
                String apikey = "apikey="+ "87b16d1acd389a73b3ab2fb3bfb48ddf";
                String psize = "page_size" + seekBarLimit.getProgress();
                String tRating = "s_track_rating=desc";
                String url ="";
                switch (index) {
                    case 0: // first button
                        url = "http://api.musixmatch.com/ws/1.1/track.search?"+ songname+"&" +psize+"&"+apikey+"&"+tRating;
                        new GetNewsAsync().execute(url);
                        break;
                    case 1: // secondbutton
                        tRating = "s_artist_rating=desc";
                        url = "http://api.musixmatch.com/ws/1.1/track.search?"+ songname+"&" +psize+"&"+apikey+"&"+tRating;
                        new GetNewsAsync().execute(url);
                        break;
                }
            }
        });

        seekBarLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewlimit.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()) {
                    String songname  ="q="+ editTextName.getText().toString();
                    String apikey = "apikey="+ "87b16d1acd389a73b3ab2fb3bfb48ddf";
                    String psize = "page_size" + seekBarLimit.getProgress();
                    String tRating = "s_track_rating=desc";
                    String url = "http://api.musixmatch.com/ws/1.1/track.search?"+ songname+"&" +psize+"&"+apikey+"&"+tRating;
                    Log.d("demo",url);
                    new GetNewsAsync().execute(url);

                } else {
                    Toast.makeText(MainActivity.this, "No Active connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }


    private class GetNewsAsync extends AsyncTask<String ,Void, ArrayList<Album>> {

        @Override
        protected ArrayList<Album> doInBackground(String... strings) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            ArrayList<Album> result = new ArrayList<Album>();
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    String json = stringBuilder.toString();
                    JSONObject root = new JSONObject(json);
                    JSONObject rootMessage  = root.getJSONObject("message");
                    JSONObject rootBody  = rootMessage.getJSONObject("body");
                    JSONArray songs = rootBody.getJSONArray("track_list");
                    for(int i=0;i<songs.length();i++){
                        JSONObject songJSON = songs.getJSONObject(i);
                        JSONObject newSong = songJSON.getJSONObject("track");
                        Log.d("demo",newSong+"");
                        Album song = new Album();
                        song.track_name = newSong.getString("track_name");
                        song.track_share_url = newSong.getString("track_share_url");
                        song.album_name = newSong.getString("album_name");
                        song.artist_name = newSong.getString("artist_name");
                        song.updated_time = newSong.getString("updated_time");
                        result.add(song);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading");
            progressDialog.setProgress(0);
            progressDialog.setMax(20);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<Album> albums) {
            AlbumAdapter albumAdapter = new AlbumAdapter(MainActivity.this,R.layout.track,albums);
            listViewSource.setAdapter(albumAdapter);
            progressDialog.dismiss();
        }
    }
}
