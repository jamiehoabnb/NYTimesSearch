package com.codepath.nytimessearch.adapters;

import android.content.Context;
import android.graphics.Movie;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    static class ViewHolder {
        @Nullable
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.ivImage)
        ImageView ivImage;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_article_result, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(article.getHeadline());

        viewHolder.ivImage.setImageResource(0);

        if (article.getThumbNail() != null) {
            Picasso.with(getContext())
                    .load(article.getThumbNail())
                    //.placeholder(R.drawable.placeholder)
                    //.resize(width, 0)
                    .transform(
                            new RoundedCornersTransformation(10, 10))
                    .into(viewHolder.ivImage);
        }
        return convertView;
    }
}
