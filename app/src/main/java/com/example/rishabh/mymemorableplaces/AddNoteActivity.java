package com.example.rishabh.mymemorableplaces;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.GoogleMap;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

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

    //Selected Spinner
    String selectedSpinner = "General";

    ArrayList<String> folderNames;
    ArrayAdapter<String> adapter;

    //Lat and Langs
    static public String lat = "";
    static public String lng = "";

    //Current Location
    private LocationManager locationManager;
    private LocationListener listener;

    //Images flags
    private static final int SELECT_PHOTO = 1;
    private static final int CAPTURE_PHOTO = 2;

    //Image load progress bar
    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarbHandler = new Handler();
    private boolean hasImageChanged = false;

    Bitmap thumbnail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        init();
        fillSpinners();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,folderNames);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Check Camera permissions
        if (ContextCompat.checkSelfPermission(AddNoteActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            noteImage.setEnabled(false);
            ActivityCompat.requestPermissions(AddNoteActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        } else {
            noteImage.setEnabled(true);
        }

        noteImage.setOnClickListener(this);

        configure_button();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationText.setText("Location Saved");
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(!validateTitle() || !validateDescription())){
                    insertToDB();
                }
            }
        });
    }

    public void insertToDB(){
        String noteName = title.getEditText().getText().toString().trim();
        String noteDescription = description.getEditText().getText().toString().trim();

        if(lat.equals("") && lng.equals("")){
            Toast.makeText(this, "Location Unknown", Toast.LENGTH_SHORT).show();
            return;
        }

        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS newNote (image BLOB,title VARCHAR PRIMARY KEY, description VARCHAR, folderName VARCHAR, location VARCHAR, date VARCHAR)");
        Cursor c = myDatabase.rawQuery("SELECT title FROM newNote ",null);
        if(c.moveToFirst()) {
            do{
                String name = c.getString(c.getColumnIndex("title"));
                if(noteName.toUpperCase().equals(name.toUpperCase())) {
                    Toast.makeText(this, "Note Name Already exsist. Try Again!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }while(c.moveToNext());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        noteImage.setDrawingCacheEnabled(true);
        noteImage.buildDrawingCache();
        Bitmap bitmap = noteImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS newNote (image BLOB,title VARCHAR PRIMARY KEY, description VARCHAR, folderName VARCHAR, location VARCHAR, date VARCHAR)");
        String sql = "INSERT INTO newNote VALUES (?,?,?,?,?,?)";
        SQLiteStatement statement = myDatabase.compileStatement(sql);
        statement.clearBindings();
        statement.bindBlob(1,data);
        statement.bindString(2,noteName);
        statement.bindString(3,noteDescription);
        statement.bindString(4,selectedSpinner);
        statement.bindString(5,lat+" "+lng);
        statement.bindString(6,currentDateandTime);
        statement.executeInsert();

        Toast.makeText(this, "Note Added", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateTitle(){
        String NoteTitle = title.getEditText().getText().toString().trim();
        if(NoteTitle.isEmpty()){
            title.setError("Folder name can't be empty");
            return false;
        }
        else if(NoteTitle.length() > 25){
            title.setError("Name size exceeds the limit 25");
            return false;
        }
        else{
            title.setError(null);
            return true;
        }
    }

    private boolean validateDescription(){
        String des = description.getEditText().getText().toString().trim();
        if(des.length() > 100){
            description.setError("Name size exceeds the limit 100");
            return false;
        }
        else{
            description.setError(null);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
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
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    noteImage.setEnabled(true);
                }
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
                Intent intent = new Intent(AddNoteActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!lat.equals("") && !lng.equals("")){
            locationText.setText("Location Saved");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.noteImage:
                new MaterialDialog.Builder(this)
                        .title(R.string.uploadImages)
                        .items(R.array.uploadImages)
                        .itemsIds(R.array.itemIds)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which){
                                    case 0:
                                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                        photoPickerIntent.setType("image/*");
                                        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                                        break;
                                    case 1:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(intent, CAPTURE_PHOTO);
                                        break;
                                    case 2:
                                        noteImage.setImageResource(R.drawable.folder_default);
                                        break;
                                }
                            }
                        })
                        .show();
                break;
        }
    }

    public void setProgressBar(){
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Please wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressBarStatus < 100){
                    progressBarStatus += 30;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    progressBarbHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }
                if (progressBarStatus >= 100) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBar.dismiss();
                }

            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PHOTO){
            if(resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    //set Progress Bar
                    setProgressBar();
                    //set profile picture form gallery
                    noteImage.setImageBitmap(selectedImage);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }else if(requestCode == CAPTURE_PHOTO){
            if(resultCode == RESULT_OK) {
                onCaptureImageResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");

        //set Progress Bar
        setProgressBar();
        //set profile picture form camera
        noteImage.setMaxWidth(200);
        noteImage.setImageBitmap(thumbnail);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSpinner = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
