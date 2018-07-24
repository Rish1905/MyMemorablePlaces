package com.example.rishabh.mymemorableplaces;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewNotes extends AppCompatActivity implements android.support.v7.widget.SearchView.OnQueryTextListener {

    //List of Folders
    ArrayList<Note> noteArrayList;
    CustomNoteAdapter adapter = null;

    //Database
    SQLiteDatabase myDatabase;

    //GridView
    GridView gridView;

    String fold = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

        Intent intent = getIntent();
        fold = intent.getStringExtra("folderName");
        init();

        fetchNotes();

        adapter = new CustomNoteAdapter(this, R.layout.custom_note_layout, noteArrayList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewNotes.this,SingleNoteActivity.class);
                intent.putExtra("noteName",noteArrayList.get(position).getTitle());
                startActivity(intent);
            }
        });

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(fold.equals("General"))
            getMenuInflater().inflate(R.menu.search_add, menu);
        else
            getMenuInflater().inflate(R.menu.delete_search_add,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.add:
                Intent intent = new Intent(ViewNotes.this,AddNoteActivity.class);
                intent.putExtra("Folder",fold);
                intent.putExtra("noteFetched","");
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
                .setMessage("Are you sure to delete Folder and its notes!")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDatabase.execSQL("DELETE FROM newFolder WHERE folderName='"+fold+"'");
                        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS newFolder (folderName VARCHAR PRIMARY KEY, image BLOB, date VARCHAR)");
                        Cursor c = myDatabase.rawQuery("SELECT title FROM newNote WHERE folderName ='"+fold+"'",null);
                        if(c.moveToFirst()) {
                            do{
                                String noteName = c.getString(c.getColumnIndex("title"));
                                myDatabase.execSQL("DELETE FROM newNote WHERE title='"+noteName+"'");
                            }while(c.moveToNext());
                        }
                        Toast.makeText(getApplicationContext(), "Folder Deleted", Toast.LENGTH_SHORT).show();
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
        noteArrayList = new ArrayList<Note>();

        myDatabase = this.openOrCreateDatabase("myMemories",MODE_PRIVATE,null);

        gridView = (GridView) findViewById(R.id.gridview);
    }

    public void fetchNotes(){
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS newNote (image BLOB,title VARCHAR PRIMARY KEY, description VARCHAR, folderName VARCHAR, location VARCHAR, date VARCHAR)");
        Cursor c = null;
        if(fold.equals("General")){
            c = myDatabase.rawQuery("SELECT * FROM newNote ",null);
        }
        else{
            c = myDatabase.rawQuery("SELECT * FROM newNote where folderName='"+fold+"'",null);
        }
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
    }

}
