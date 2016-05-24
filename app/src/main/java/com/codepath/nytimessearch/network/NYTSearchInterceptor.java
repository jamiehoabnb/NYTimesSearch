package com.codepath.nytimessearch.network;

import com.codepath.nytimessearch.models.Settings;
import com.codepath.nytimessearch.util.NYTSearchContants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NYTSearchInterceptor implements Interceptor {

    private String query;

    private Settings settings;

    private int page;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public NYTSearchInterceptor(String query, int page, Settings settings) {
        this.query = query;
        this.page = page;
        this.settings = settings;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl originalHttpUrl = request.url();

        HttpUrl.Builder builder = originalHttpUrl.newBuilder()
                .addQueryParameter("api-key", NYTSearchContants.API_KEY);

        builder = builder.addQueryParameter("page", String.valueOf(page));

        if (query != null) {
            builder = builder.addQueryParameter("q", query);
        }

        if (settings.getBeginDate() != null) {
            builder = builder.addQueryParameter("begin_date", dateFormat.format(settings.getBeginDate()));
        }

        if (settings.getEndDate() != null) {
            builder = builder.addQueryParameter("end_date", dateFormat.format(settings.getEndDate()));
        }

        if (settings.getSortOrder() != null) {
            builder = builder.addQueryParameter("sort", settings.getSortOrder().name());
        }

        List<Settings.NewsDesk> newsDesks = settings.getNewsDesks();

        if (newsDesks != null && ! settings.getNewsDesks().isEmpty()) {
            StringBuilder newsDeskFilter = new StringBuilder("news_desk:(");
            boolean first = true;
            for (Settings.NewsDesk newsDesk: settings.getNewsDesks()) {
                if (! first) {
                    newsDeskFilter.append(" ");
                }

                newsDeskFilter.append("\"")
                        .append(newsDesk.name())
                        .append("\"");
                first = false;
            }
            newsDeskFilter.append(")");
            builder = builder.addQueryParameter("fq", newsDeskFilter.toString());
        }

        HttpUrl url = builder.build();

        Request.Builder requestBuilder = request.newBuilder().url(url);

        Response response = chain.proceed(requestBuilder.build());
        return response;
    }
}