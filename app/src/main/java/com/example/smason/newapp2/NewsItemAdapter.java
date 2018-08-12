package com.example.smason.newapp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;


public class NewsItemAdapter extends ArrayAdapter<NewsItem> {

    public NewsItemAdapter(Context context, List<NewsItem> newstories) {
        super(context, 0, newstories);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //check if a view exists to recycle, if not create a new one
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_layout, parent, false);
        }

        //Find the news at the given position in the list of articles
        NewsItem currentNewsItem = getItem(position);

        TextView dateView = listItemView.findViewById(R.id.date);
        dateView.setText(currentNewsItem.getDate());

        TextView titleView = listItemView.findViewById(R.id.title);
        titleView.setText(currentNewsItem.getTitle());

        TextView sectionView = listItemView.findViewById(R.id.section);
        sectionView.setText(currentNewsItem.getSection());

        TextView authorView = listItemView.findViewById(R.id.author);
        authorView.setText(currentNewsItem.getAuthor());

        return listItemView;
    }
}