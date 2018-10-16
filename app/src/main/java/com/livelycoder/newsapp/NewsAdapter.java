package com.livelycoder.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<News> mNews;

    NewsAdapter(List<News> news) {
        mNews = news;
    }

    void addAll(List<News> news) {
        mNews.addAll(news);
        notifyDataSetChanged();
    }

    void clear() {
        mNews.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_list_item,
                viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.onBind(mNews.get(position));
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView authorNameTextView;
        TextView datePublishedTextView;
        TextView sectionNameTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            authorNameTextView = itemView.findViewById(R.id.author_name_text_view);
            datePublishedTextView = itemView.findViewById(R.id.date_published_text_view);
            sectionNameTextView = itemView.findViewById(R.id.section_name_text_view);
        }

        void onBind(final News news) {
            titleTextView.setText(news.getTitle());
            sectionNameTextView.setText(news.getSectionName());
            authorNameTextView.setText(news.getAuthorName());
            datePublishedTextView.setText(news.getPublishedDate().substring(0, 10));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = news.getWebUrl();
                    Context context = view.getContext();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (intent.resolveActivity(context.getPackageManager()) != null)
                        context.startActivity(intent);
                }
            });
        }
    }
}
