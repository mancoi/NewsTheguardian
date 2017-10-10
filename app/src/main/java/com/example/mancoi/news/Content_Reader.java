package com.example.mancoi.news;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by mancoi on 25/09/2017.
 */

public class Content_Reader extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.read_content);

        //Set the toolbar to support actionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Article");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        final String webUrl = intent.getStringExtra("apiUrl");

        //Get the title and set it to headline_tv
        TextView title = (TextView) findViewById(R.id.headline_tv);
        title.setText(intent.getStringExtra("headline"));

        //Get the author and set it to byline_tv
        TextView byline = (TextView) findViewById(R.id.byline_tv);
        String author = intent.getStringExtra("byline");
        //If there are no author(s) present, hide this TextView
        if (!TextUtils.isEmpty(author))
        {
            byline.setText(author);
        }
        else
        {
            byline.setVisibility(View.GONE);
        }

        //Get the trailText and set it to trailText_tv
        TextView trailText = (TextView) findViewById(R.id.trailText_tv);
        trailText.setText(QueryUtils.fromHtml(intent.getStringExtra("trailText")));

        //Get the thumbnail's Url, load it and set it to thumbnail_imgView
        ImageView thumbnail = (ImageView) findViewById(R.id.thumbnail_imgView);
        String thumbnailUrl = intent.getStringExtra("thumbnail");
        //Check if there is an image or not, it not, leave it as default
        //Not show broken image here because it's not an error
        if (!TextUtils.isEmpty(thumbnailUrl)) {

            // Display the thumbnail of the current News in that TextView
            Picasso.with(this)
                    .load(thumbnailUrl)
                    .into(thumbnail);

        } else {

            thumbnail.setVisibility(View.GONE);

        }

        Button butReadmore = (Button) findViewById(R.id.read_more_but);
        butReadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
                // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
                // and launch the desired Url with CustomTabsIntent.launchUrl()
                String url = webUrl;
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(Content_Reader.this, Uri.parse(url));
            }
        });

    }

}
