package com.codepath.nytimessearch.network;

import com.codepath.nytimessearch.models.SearchAPIResponse;
import com.codepath.nytimessearch.models.SearchResponse;
import com.codepath.nytimessearch.models.Settings;
import com.codepath.nytimessearch.util.NYTSearchContants;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NYTAPI {

    public interface SearchResponseListener {
        void onSearchResponse(SearchResponse searchResponse);
    }

    private static NYTService getNYTService(Interceptor interceptor) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(interceptor);

        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(NYTSearchContants.API_BASE_URL)
                .build();

        NYTService service = retrofit.create(NYTService.class);
        return service;
    }

    public static void search(final String query, final Settings settings, final SearchResponseListener listener) {
        NYTSearchInterceptor interceptor = new NYTSearchInterceptor(query, settings);
        NYTService service = getNYTService(interceptor);

        Call<SearchAPIResponse> call = service.search();
        call.enqueue(new Callback<SearchAPIResponse>() {
            @Override
            public void onResponse(Call<SearchAPIResponse> call, Response<SearchAPIResponse> response) {
                SearchAPIResponse resp = response.body();
                listener.onSearchResponse(resp.getResponse());
            }

            @Override
            public void onFailure(Call<SearchAPIResponse> call, Throwable t) {
                //XXX Add snackbar here.
                t.printStackTrace();
            }
        });
    }
}
