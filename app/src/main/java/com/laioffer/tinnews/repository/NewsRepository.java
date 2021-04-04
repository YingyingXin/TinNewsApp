package com.laioffer.tinnews.repository;

import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.laioffer.tinnews.TinNewsApplication;
import com.laioffer.tinnews.database.TinNewsDatabase;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.network.NewsApi;
import com.laioffer.tinnews.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {
    private final NewsApi newsApi;
    private final TinNewsDatabase database;

    //aysnc write the data to database
    private static class FavoriteAsyncTask extends AsyncTask<Article, Void, Boolean> {
        private final TinNewsDatabase database;
        private final MutableLiveData<Boolean> liveData;
        private FavoriteAsyncTask(TinNewsDatabase database, MutableLiveData<Boolean> liveData) {
            this.database = database;
            this.liveData = liveData;
        }
        @Override
        protected Boolean doInBackground(Article... articles) {
            Article article = articles[0];
            try {
                database.articleDao().saveArticle(article);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean success) {
            liveData.setValue(success);
        }
    }
    public LiveData<Boolean> favoriteArticle(Article article) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        new FavoriteAsyncTask(database, resultLiveData).execute(article);
        return resultLiveData;
    }
    public LiveData<List<Article>> getAllSavedArticles(){
        //LiveData 做了threadhandling的工作，且不需要onPostExecute,所以不需要AsyncTask
        return database.articleDao().getAllArticles();
    }
    public void deleteSavedArticle(Article article){
        AsyncTask.execute(()->database.articleDao().deleteArticle(article));
    }


    //public NewsRepository(Context context) {// newsApi = RetrofitClient.newInstance(context).create(NewsApi.class);//    }
    public NewsRepository(Context context) {
        newsApi = RetrofitClient.newInstance().create(NewsApi.class);
        database = ((TinNewsApplication) context.getApplicationContext()).getDatabase();
        //The database instance is provided by casting the application context into TinNewsApplication.

    }
    public LiveData<NewsResponse> getTopHeadlines(String country) {
        MutableLiveData<NewsResponse> topHeadlinesLiveData = new MutableLiveData<>();
        newsApi.getTopHeadinglines(country).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful()) {
                    topHeadlinesLiveData.setValue(response.body());
                } else {
                    topHeadlinesLiveData.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                topHeadlinesLiveData.setValue(null);
            }
        });
        return topHeadlinesLiveData;
    }

    public LiveData<NewsResponse> searchNews(String query) {
        MutableLiveData<NewsResponse> everythingLiveData = new MutableLiveData<>();
        newsApi.getEverything(query, 40).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful()) {
                    everythingLiveData.setValue(response.body());
                } else {
                    everythingLiveData.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                everythingLiveData.setValue(null);
            }
        });
        return everythingLiveData;
    }



}