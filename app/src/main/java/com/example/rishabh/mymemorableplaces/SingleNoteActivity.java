package com.example.rishabh.mymemorableplaces;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SweepGradient;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

    //Menu
    private Menu mainMenu;

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

        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.edit:
                Intent intent = new Intent();
                intent.putExtra("noteFetched",noteFetched);
                startActivity(intent);
                break;
            case R.id.delete:
                deleteNote();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void deleteNote(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete the note!")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDatabase.execSQL("DELETE FROM newNote WHERE title='"+noteFetched+"'");
                        Toast.makeText(getApplicationContext(), "Note Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.delete_edit_menu,menu);
        mainMenu = menu;
        return true;
    }
}
