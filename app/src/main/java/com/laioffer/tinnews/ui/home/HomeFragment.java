package com.laioffer.tinnews.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laioffer.tinnews.databinding.FragmentHomeBinding;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.List;


public class HomeFragment extends Fragment implements CardStackListener {
    private HomeViewModel viewModel;
    private FragmentHomeBinding binding;
    private CardStackLayoutManager layoutManager;
    private List<Article> articles;

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    private void swipeCard(Direction direction){
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(direction)
                .setDuration(Duration.Normal.duration)
                .build();
        layoutManager.setSwipeAnimationSetting(setting);
        binding.homeCardStackView.swipe();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NewsRepository repository = new NewsRepository(getContext());
        // Setup CardStackView
        CardSwipeAdapter cardSwipeAdapter = new CardSwipeAdapter();
        layoutManager = new CardStackLayoutManager(requireContext(),this);//this代表HomeFragment，因为inner class的数据属于它的outer class
        layoutManager.setStackFrom(StackFrom.Top);
        binding.homeCardStackView.setLayoutManager(layoutManager);
        binding.homeCardStackView.setAdapter(cardSwipeAdapter);
        // Handle like unlike button clicks , then the layoutManager will ful fill the slide left
        binding.homeLikeButton.setOnClickListener(v -> {
            swipeCard(Direction.Right);
        });
        binding.homeUnlikeButton.setOnClickListener(v -> {
            swipeCard(Direction.Left);
        });


//        viewModel=new HomeViewModel(repository); 用下一行代替相当于书签，可以存储用户上次使用时候的状态
        viewModel = new ViewModelProvider(this,new NewsViewModelFactory(repository)).get(HomeViewModel.class); //相当于一个cache，只创建一次，可以保存之前state的信息
        viewModel.setCountryInput("us");
        viewModel.getTopHeadlines()
                  .observe(getViewLifecycleOwner(),newsResponse -> {
            if(newsResponse != null) {
                articles = newsResponse.articles;
                cardSwipeAdapter.setArticles(articles);
            }
        });
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }
    @Override
    public void onCardSwiped(Direction direction) {
        if (direction == Direction.Left) {
            Log.d("CardStackView", "Unliked " + layoutManager.getTopPosition());
        } else if (direction == Direction.Right) {
            Log.d("CardStackView", "Liked "  + layoutManager.getTopPosition());
            Article article = articles.get(layoutManager.getTopPosition()-1);
            viewModel.setFavoriteArticleInput(article);
        }
    }
    @Override
    public void onCardRewound() {

    }
    @Override
    public void onCardCanceled() {

    }
    @Override
    public void onCardAppeared(View view, int position) {

    }
    @Override
    public void onCardDisappeared(View view, int position) {

    }
}