package com.example.rishabh.mymemorableplaces;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class FAQActivity extends AppCompatActivity {

    ArrayList<String> questions = new ArrayList<String>();
    ArrayList<String> answers = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        ListView listView = (ListView) findViewById(R.id.listView);

        questions.add("What is this app all about ?");
        questions.add("How to add a Note ?");
        questions.add("How to add a Folder ?");
        questions.add("How to Insert an Image ?");
        questions.add("How to add a Location ?");
        questions.add("Why I can't change the title of Note ?");
        questions.add("What happen after delete of a folder ?");

        answers.add("This app is use to store your most memorable places and their location in easy and efficient way.");
        answers.add("To add a Note you have two options, first you can click the location button of your bottom right " +
                "corner of your home screen and second you can click the add button on the top right corner of the specific " +
                " folder");
        answers.add("To add a folder, you have to click the floating button in the bottom right corner of the home screen");
        answers.add("You can Insert an image for your folder and notes. To insert an image click the default image and choose" +
                " the image from your phone gallery or click by camera.");
        answers.add("To add a Location, click on blue tracking button on the Add note form and search for the specific location" +
                " on the Google maps. For the efficiency of coordinates you can long press the map to select the specific location");
        answers.add("You can't change title of the Note as it is the prime method to distinguish between one note from other" +
                ". Delete the note and try to save same location with the new note.");
        answers.add("Deleting a folder will result in delete of folder and all its specific notes.");


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, questions);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               createDialog(i);
            }
        });

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("FAQ");
        }
    }

    void createDialog(int i){
        new AlertDialog.Builder(this)
                .setTitle(questions.get(i).toString())
                .setMessage(answers.get(i).toString())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }
}
