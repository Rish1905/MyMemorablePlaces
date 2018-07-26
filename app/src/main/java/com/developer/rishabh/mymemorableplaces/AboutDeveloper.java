package com.developer.rishabh.mymemorableplaces;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutDeveloper extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_developer);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("About Developer");
        }

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("My name is Rishabh Agrawal. I am pursing my Master's degree in Computer and Information" +
                " Sciences from Syracuse University USA. My passion and skills are to develop world class software" +
                " products. This product is one of my best projects. Thank you for using the software." +
                " - Contact Me :- ragrawal@syr.edu");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }
}
