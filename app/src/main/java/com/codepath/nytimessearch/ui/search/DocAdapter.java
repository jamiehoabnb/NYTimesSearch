package com.codepath.nytimessearch.ui.search;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Doc;
import com.codepath.nytimessearch.util.NYTSearchContants;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class DocAdapter extends RecyclerView.Adapter<DocAdapter.ViewHolder> {

    private List<Doc> docs;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.ivImage)
        ImageView ivImage;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public DocAdapter(List<Doc> docs) {
        this.docs = docs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_article_result, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Doc doc = docs.get(position);

        viewHolder.tvTitle.setText(doc.getHeadline().getMain());

        viewHolder.ivImage.setImageResource(0);

        if (doc.getMultimedia() != null && ! doc.getMultimedia().isEmpty()) {
            String url = NYTSearchContants.IMAGE_BASE_URL + doc.getMultimedia().get(0).getUrl();
            Picasso.with(viewHolder.ivImage.getContext())
                    .load(url)
                    //.placeholder(R.drawable.placeholder)
                    //.resize(width, 0)
                    .transform(
                            new RoundedCornersTransformation(10, 10))
                    .into(viewHolder.ivImage);
        }
    }

    @Override
    public int getItemCount() {
        return docs.size();
    }
}
