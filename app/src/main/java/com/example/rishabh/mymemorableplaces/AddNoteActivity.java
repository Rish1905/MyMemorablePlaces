package com.example.rishabh.mymemorableplaces;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class AddNoteActivity extends AppCompatActivity {

    //Variables
    ImageView noteImage;
    TextInputLayout title;
    TextInputLayout description;
    Spinner spinner;
    TextView locationText;
    ImageButton locationButton;
    CardView button;

    //Database
    SQLiteDatabase myDatabase;

    ArrayList<String> folderNames;
    ArrayAdapter<String> adapter;

    //Lat and Langs
    String lat = "";
    String lng = "";

    //Current Location
    private LocationManager locationManager;
    private LocationListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        init();
        fillSpinners();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,folderNames);
        spinner.setAdapter(adapter);

        configure_button();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationText.setText("Location Known");
                lat = Double.toString(location.getLatitude());
                lng = Double.toString(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
    }

    public void init(){
        noteImage = (ImageView) findViewById(R.id.noteImage);
        title = (TextInputLayout) findViewById(R.id.text_input_note_name);
        description = (TextInputLayout) findViewById(R.id.text_input_note_description);
        spinner = (Spinner) findViewById(R.id.spinner);
        locationText = (TextView) findViewById(R.id.locationText);
        locationText.setText("Location unknown");
        locationButton = (ImageButton) findViewById(R.id.locationButton);
        button = (CardView) findViewById(R.id.button);

        myDatabase = this.openOrCreateDatabase("myMemories",MODE_PRIVATE,null);

        folderNames = new ArrayList<String>();
        folderNames.add("General");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    public void fillSpinners(){
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS newFolder (folderName VARCHAR PRIMARY KEY, image BLOB, date VARCHAR)");
        Cursor c = myDatabase.rawQuery("SELECT folderName FROM newFolder ",null);
        if(c.moveToFirst()) {
            do{
                String folderName = c.getString(c.getColumnIndex("folderName"));
                folderNames.add(folderName);
            }while(c.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        locationButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates("gps", 5000, 0, listener);
            }
        });
    }
}
