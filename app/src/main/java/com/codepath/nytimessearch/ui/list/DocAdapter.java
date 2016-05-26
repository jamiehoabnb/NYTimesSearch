package com.codepath.nytimessearch.ui.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Doc;
import com.codepath.nytimessearch.util.NYTSearchContants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Doc> docs;

    private static final int IMAGE_ARTICLE = 0;
    private static final int TEXT_ONLY_ARTICLE = 1;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }



    public static class ViewHolderTextOnly extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.tvSnippet)
        TextView tvSnippet;

        public ViewHolderTextOnly(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public DocAdapter(List<Doc> docs) {
        this.docs = docs;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;
        View view;

        switch (viewType) {
            case IMAGE_ARTICLE:
                view = inflater.inflate(R.layout.item_article_result, parent, false);
                viewHolder = new ViewHolder(view);
                break;
            default:
                view = inflater.inflate(R.layout.item_article_text_only_result, parent, false);
                viewHolder = new ViewHolderTextOnly(view);
                break;
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case IMAGE_ARTICLE:
                onBindViewHolder((ViewHolder) viewHolder, position);
                break;
            default:
                onBindViewHolder((ViewHolderTextOnly) viewHolder, position);
                break;

        }


    }

    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Doc doc = docs.get(position);
        viewHolder.tvTitle.setText(doc.getHeadline().getMain());

        String url = NYTSearchContants.IMAGE_BASE_URL + doc.getMultimedia().get(0).getUrl();
        Glide.with(viewHolder.ivImage.getContext())
                .load(url)
                //.placeholder(R.drawable.placeholder)
                //.resize(width, 0)
                //.transform(
                        //new RoundedCornersTransformation(10, 10))
                .into(viewHolder.ivImage);
    }

    public void onBindViewHolder(ViewHolderTextOnly viewHolder, int position) {
        Doc doc = docs.get(position);
        viewHolder.tvTitle.setText(doc.getHeadline().getMain());
        viewHolder.tvSnippet.setText(doc.getSnippet());
    }

    @Override
    public int getItemCount() {
        return docs.size();
    }

    @Override
    public int getItemViewType(int position) {
        Doc doc = docs.get(position);

        if (doc.getMultimedia() != null && ! doc.getMultimedia().isEmpty()) {
            return IMAGE_ARTICLE;
        } else {
            return TEXT_ONLY_ARTICLE;
        }
    }
}
