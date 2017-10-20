package com.example.mancoi.news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by mancoi on 23/08/2017.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(@NonNull Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }

        News currentNews = getItem(position);

        // Find the TextView in the news_list_item.xml layout with the ID title_tv
        TextView titleTextView = listItemView.findViewById(R.id.title_tv);
        // Display the title of the current News in that TextView
        titleTextView.setText(currentNews.getTitle());

        // Find the TextView in the news_list_item.xml layout with the ID author_tv
        TextView authorTextView = listItemView.findViewById(R.id.author_tv);
        // Display the author of the current News in that TextView
        authorTextView.setText(currentNews.getAuthor());

        // Find the TextView in the news_list_item.xml layout with the ID date_tv
        TextView dateTextView = listItemView.findViewById(R.id.date_tv);

        //Set the input format time
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateObject;
        String dateFormatted = null;
        try {
            //Create the dateObject with the input format time
            dateObject = inputFormat.parse(currentNews.getDate());
            //Format that input to take the date
            dateFormatted = formatDate(dateObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Display the date of the current News in that TextView
        dateTextView.setText(dateFormatted);

        // Find the TextView in the news_list_item.xml layout with the ID section_tv
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section_tv);
        // Display the title of the current News in that TextView
        sectionTextView.setText(currentNews.getSection());

        // Find the ImageView in the news_list_item.xml layout with the ID imgView
        ImageView imgView = (ImageView) listItemView.findViewById(R.id.imgView);

        String imgUrl = currentNews.getImgUrl();

        //Check if there is an image or not, it not, leave it as default
        //Not show broken image here because it's not an error
        if (!TextUtils.isEmpty(imgUrl)) {
            // Display the thumbnail of the current News in that TextView
            Picasso.with(getContext())
                    .load(currentNews.getImgUrl())
                    .error(R.drawable.img_broken)
                    .placeholder(R.drawable.img_holder)
                    .into(imgView);
        } else {
            imgView.setImageResource(R.drawable.img_no_image);
        }

        return listItemView;
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy, HH:mm");
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(dateObject);
    }
}
