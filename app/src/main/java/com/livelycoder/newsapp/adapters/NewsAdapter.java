package com.livelycoder.newsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.livelycoder.newsapp.R;
import com.livelycoder.newsapp.models.News;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<News> mNews;
    private Context context;

    public NewsAdapter(Context context, List<News> news) {
        this.context = context;
        mNews = news;
    }

    public void addAll(List<News> news) {
        mNews.addAll(news);
        notifyDataSetChanged();
    }

    public void clear() {
        mNews.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_list_item,
                viewGroup, false);
        return new ViewHolder(itemView, context);
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
        ImageView newsImage;
        private Context context;

        ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            authorNameTextView = itemView.findViewById(R.id.author_name_text_view);
            datePublishedTextView = itemView.findViewById(R.id.date_published_text_view);
            sectionNameTextView = itemView.findViewById(R.id.section_name_text_view);
            newsImage = itemView.findViewById(R.id.news_image);
            this.context = context;
        }

        void onBind(final News news) {
            titleTextView.setText(news.getTitle());
            sectionNameTextView.setText(news.getSectionName());
            if (!TextUtils.isEmpty(news.getAuthorName()))
                authorNameTextView.setText(news.getAuthorName());
            else
                authorNameTextView.setVisibility(View.GONE);
            String date = news.getPublishedDate().substring(0, 10);
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8, 10));
            Calendar publishDate = Calendar.getInstance();
            publishDate.set(year, month - 1, day);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yy", Locale.getDefault());
            datePublishedTextView.setText(dateFormat.format(publishDate.getTime()));
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
            if (TextUtils.isEmpty(news.getThumbnail())) {
                Glide.with(context).load(R.drawable.ic_no_image).into(newsImage);
            } else {
                Glide.with(context).load(news.getThumbnail()).into(newsImage);
            }

        }
    }
}
