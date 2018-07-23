package com.example.rishabh.mymemorableplaces;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Variables for Floating buttons
    FloatingActionButton fab_plus,fab_note,fab_folder;
    Animation fabOpen,fabClose,fabClockwise,fabAnticlockwise;
    boolean isOpen = false;

    //List of Folders
    ArrayList<Folder> folderArrayList;
    CustomFolderAdapter adapter = null;

    //Database
    SQLiteDatabase myDatabase;

    //GridView
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        fetchFolders();

        adapter = new CustomFolderAdapter(this, R.layout.custom_folder_layout, folderArrayList);
        gridView.setAdapter(adapter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen){
                    fab_folder.startAnimation(fabClose);
                    fab_note.startAnimation(fabClose);
                    fab_plus.startAnimation(fabAnticlockwise);
                    fab_note.setClickable(false);
                    fab_folder.setClickable(false);
                    isOpen=false;
                }
                else{
                    fab_folder.startAnimation(fabOpen);
                    fab_note.startAnimation(fabOpen);
                    fab_plus.startAnimation(fabClockwise);
                    fab_note.setClickable(true);
                    fab_folder.setClickable(true);
                    isOpen=true;
                }
            }
        });

        fab_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddFolderActivity.class);
                startActivity(intent);
            }
        });

        fab_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddNoteActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void init(){
        //Initialize the floting buttons
        fab_plus = (FloatingActionButton) findViewById(R.id.fab_plus);
        fab_note = (FloatingActionButton) findViewById(R.id.fab_note);
        fab_folder = (FloatingActionButton) findViewById(R.id.fab_folder);
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        fabClockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        fabAnticlockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);
        folderArrayList = new ArrayList<Folder>();

        myDatabase = this.openOrCreateDatabase("myMemories",MODE_PRIVATE,null);

        gridView = (GridView) findViewById(R.id.gridview);
    }

    public void fetchFolders(){
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS newFolder (folderName VARCHAR PRIMARY KEY, image BLOB, date VARCHAR)");
        Cursor c = myDatabase.rawQuery("SELECT * FROM newFolder ",null);
        if(c.moveToFirst()) {
            do{
                String folderName = c.getString(c.getColumnIndex("folderName"));
                byte[] folderImage = c.getBlob(c.getColumnIndex("image"));
                folderArrayList.add(new Folder(folderImage,folderName));
            }while(c.moveToNext());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        folderArrayList.clear();
        fetchFolders();
        adapter.notifyDataSetChanged();
        if(isOpen){
            fab_folder.startAnimation(fabClose);
            fab_note.startAnimation(fabClose);
            fab_plus.startAnimation(fabAnticlockwise);
            fab_note.setClickable(false);
            fab_folder.setClickable(false);
            isOpen=false;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action

            Intent intent = new Intent(MainActivity.this,ViewNotes.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
