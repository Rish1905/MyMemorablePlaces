package com.example.rishabh.mymemorableplaces;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;

public class ViewNotes extends AppCompatActivity {

    //List of Folders
    ArrayList<Note> noteArrayList;
    CustomNoteAdapter adapter = null;

    //Database
    SQLiteDatabase myDatabase;

    //GridView
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

        init();

        fetchNotes();

        adapter = new CustomNoteAdapter(this, R.layout.custom_note_layout, noteArrayList);
        gridView.setAdapter(adapter);
    }

    public void init(){
        noteArrayList = new ArrayList<Note>();

        myDatabase = this.openOrCreateDatabase("myMemories",MODE_PRIVATE,null);

        gridView = (GridView) findViewById(R.id.gridview);
    }

    public void fetchNotes(){
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS newNote (image BLOB,title VARCHAR PRIMARY KEY, description VARCHAR, folderName VARCHAR, location VARCHAR, date VARCHAR)");
        Cursor c = myDatabase.rawQuery("SELECT * FROM newNote ",null);
        if(c.moveToFirst()) {
            do{
                String noteName = c.getString(c.getColumnIndex("title"));
                byte[] NoteImage = c.getBlob(c.getColumnIndex("image"));
                noteArrayList.add(new Note(noteName,NoteImage));
            }while(c.moveToNext());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteArrayList.clear();
        fetchNotes();
        adapter.notifyDataSetChanged();
    }

}
