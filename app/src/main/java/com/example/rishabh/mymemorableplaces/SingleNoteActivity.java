package com.example.rishabh.mymemorableplaces;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

public class SingleNoteActivity extends AppCompatActivity {

    //Variables
    ImageView imageView;
    TextView title;
    TextView description;
    TextView folderName;
    CardView button;

    //Database
    SQLiteDatabase myDatabase;

    String noteFetched = "";
    String lat ="";
    String lng = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_note);

        Intent intent = getIntent();
        noteFetched = intent.getStringExtra("noteName");

        init();
        fetchDetails();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String geo = "geo:"+lat+","+lng+"?q="+lat+","+lng+"("+noteFetched+")";
                Uri gmmIntentUri = Uri.parse(geo);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    public void init(){
        imageView = (ImageView) findViewById(R.id.imageView);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        folderName = (TextView) findViewById(R.id.folderName);
        button = (CardView) findViewById(R.id.button);

        myDatabase = this.openOrCreateDatabase("myMemories",MODE_PRIVATE,null);
    }

    public void fetchDetails(){
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS newNote (image BLOB,title VARCHAR PRIMARY KEY, description VARCHAR, folderName VARCHAR, location VARCHAR, date VARCHAR)");
        Cursor c = myDatabase.rawQuery("SELECT * FROM newNote WHERE title='"+noteFetched+"' ",null);
        if(c.moveToFirst()) {
            do{
                title.setText(c.getString(c.getColumnIndex("title")));
                description.setText(c.getString(c.getColumnIndex("description")));
                folderName.setText("Folder : " + c.getString(c.getColumnIndex("folderName")));
                byte[] data = c.getBlob(c.getColumnIndex("image"));
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 200,200, false));
                String[] temp = c.getString(c.getColumnIndex("location")).split(" ");
                lat = temp[0];
                lng = temp[1];
            }while(c.moveToNext());
        }
    }
}
