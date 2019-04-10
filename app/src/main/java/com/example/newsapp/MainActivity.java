package com.example.newsapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsapp.Api.ApiClient;
import com.example.newsapp.Api.ApiInterface;
import com.example.newsapp.models.Articles;
import com.example.newsapp.models.NewsModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String Api_Key="a964cae8989245d5a13b4c51ae3e0ff7";
    private RecyclerView recyclerView;
    private Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Articles> articles=new ArrayList<>();
    private String TAG=MainActivity.class.getSimpleName();
    TextView TopHeadlines;
    SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout relativeLayout;
    private ImageView errorimage;
    private TextView errortitle,errormessage;
    private Button btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout=findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        TopHeadlines=findViewById(R.id.top_text);

        recyclerView=findViewById(R.id.recycle);
        layoutManager=new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

      OnSwipeLoadedRefrech("");

      relativeLayout=findViewById(R.id.my_Relative_Layout);
      errorimage=findViewById(R.id.error_image);
      errortitle=findViewById(R.id.error_title);
      errormessage=findViewById(R.id.error_message);
      btnRetry=findViewById(R.id.retry);


    }

    public void LoadJson(final  String Keyword)
    {
        TopHeadlines.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        relativeLayout.setVisibility(View.GONE);

        ApiInterface apiInterface= ApiClient.GetApiClient().create(ApiInterface.class);

        String country=Utils.getCountry();
        String language=Utils.getLanguage();
        Call<NewsModel> call;

        if (Keyword.length()>0)
        {
            call=apiInterface.GetSearchNews(Keyword,language,"PublishedAt",Api_Key);
        }
        else {
            call = apiInterface.getnews(country, Api_Key);
        }

        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                if (response.isSuccessful()&&response.body().getArticles() !=null)
                {
                    if (!articles.isEmpty())
                    {
                        articles.clear();
                    }
                    articles=response.body().getArticles();
                    adapter=new Adapter(articles,MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    initlistener();

                    TopHeadlines.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);

                }
                else
                {
                    TopHeadlines.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);

                    String errorCode;

                    switch (response.code())
                    {
                        case 404:
                            errorCode="404 not found";
                            break;
                        case 500:
                            errorCode="500 server broken";
                            break;
                        default:
                                errorCode="Unknown Error";
                                break;

                    }

                    ShowErrorMessage(R.drawable.no_result,
                            "No Result",
                            "Please Try Again "+errorCode);
                }
            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                relativeLayout.setVisibility(View.VISIBLE);
                ShowErrorMessage(R.drawable.no_result,
                        "Please Check your Connection",
                        "Please Try Again "+t.toString());

            }
        });
    }

    private void initlistener()
    {
        adapter.OnItemClicklistener(new Adapter.OnItemClickListener() {
            @Override
            public void OnItemclick(View view, int Position) {

                ImageView imageView=view.findViewById(R.id.img);

                Intent intent=new Intent(MainActivity.this,NewsDetailsActivity.class);

                Articles article=articles.get(Position);

                intent.putExtra("url",article.getUrl());
                intent.putExtra("title",article.getTitle());
                intent.putExtra("author",article.getAuthor());
                intent.putExtra("source",article.getSource().getName());
                intent.putExtra("img",article.getUrlToImage());
                intent.putExtra("time",article.getPublishedAt());

               android.support.v4.util.Pair pair=
                        Pair.create((View)imageView, ViewCompat.getTransitionName(imageView));
                ActivityOptionsCompat optionsCompat=
                        ActivityOptionsCompat.
                                makeSceneTransitionAnimation(MainActivity.this,pair);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(intent, optionsCompat.toBundle());
                }
                else {
                    startActivity(intent);
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);

        SearchManager searchManager= (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView= (SearchView) menu.findItem(R.id.search).getActionView();
        MenuItem searchMenuItem=menu.findItem(R.id.search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest News....");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length()>2)
                {
                    OnSwipeLoadedRefrech("");
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchMenuItem.getIcon().setVisible(false,false);
        return true;
    }

    @Override
    public void onRefresh() {

        LoadJson("");
    }

    private void OnSwipeLoadedRefrech(final String Keyword)
    {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                LoadJson(Keyword);
            }
        });
    }

    private void ShowErrorMessage(int imageview,String tit,String message)
    {
        if (relativeLayout.getVisibility()==View.GONE)
        {
            relativeLayout.setVisibility(View.VISIBLE);
        }

        errorimage.setImageResource(imageview);
        errortitle.setText(tit);
        errormessage.setText(message);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSwipeLoadedRefrech("");
            }
        });
    }
}
