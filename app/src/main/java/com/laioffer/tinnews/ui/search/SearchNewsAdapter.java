package com.laioffer.tinnews.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.SearchNewsItemBinding;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.ui.save.SavedNewsAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchNewsAdapter extends RecyclerView.Adapter<SearchNewsAdapter.SearchNewsViewHolder> {
    interface ItemCallback{
        //此处定义方法，在search Fragment里implement
        void onOpenDetails(Article article);
    }
    private SearchNewsAdapter.ItemCallback itemCallback;
    public void setItemCallback(SearchNewsAdapter.ItemCallback itemCallback) {
        this.itemCallback = itemCallback;
    }


    // 1. Supporting data:
    private List<Article> articles = new ArrayList<>();
    public void setArticles(List<Article> newsList) {
        articles.clear();
        articles.addAll(newsList);
        notifyDataSetChanged();
    }


    // 2. Adapter overrides
    @NonNull
    @Override
    public SearchNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_news_item, parent, false);
        return new SearchNewsViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SearchNewsViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);
        holder.itemTitleTextView.setText(article.title);
        Picasso.get().load(article.urlToImage).into(holder.itemImageView);
        holder.itemView.setOnClickListener(v-> itemCallback.onOpenDetails(article));
    }
    @Override
    public int getItemCount() {
        return articles.size();
    }


    // 3. SearchNewsViewHolder:
    public static class SearchNewsViewHolder extends RecyclerView.ViewHolder {
        ImageView favoriteImageView;
        ImageView itemImageView;
        TextView itemTitleTextView;
        public SearchNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            //bind是一个比较heavy的操作，把它放在onCreateViewHolder的时候创建，只需要创建有限次数满足屏幕尺寸的内容就可；
            //而不是在onBindViewHolder里创建，因为onBindViewHolder这个过程是无限次的，只要滑动就会触发
            SearchNewsItemBinding binding = SearchNewsItemBinding.bind(itemView);
            favoriteImageView = binding.searchItemFavorite;
            itemImageView = binding.searchItemImage;
            itemTitleTextView = binding.searchItemTitle;
        }
    }

    }
