package com.example.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.net.URL;

public class NewsDetailsActivity extends AppCompatActivity implements AppBarLayout.
        OnOffsetChangedListener{

    private ImageView imageView;
    private TextView AppBarTitle,AppBarSubtitle,date,time,title;
    private boolean isHideToBarView=false;
    private FrameLayout data_behavior;
    private LinearLayout titleAppbar;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private String mUrl,mImg,mTitle,mDate,mSource,mAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout
                =findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        appBarLayout=findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener( this);

        data_behavior=findViewById(R.id.date_behavior);
        titleAppbar=findViewById(R.id.title_appbar);

        imageView=findViewById(R.id.backdrop);

        AppBarTitle=findViewById(R.id.title_on_appbar);
        AppBarSubtitle=findViewById(R.id.subtitle_on_appbar);

        date=findViewById(R.id.date);
        time=findViewById(R.id.time);
        title=findViewById(R.id.title);


      Intent intent=getIntent();

      mUrl=intent.getStringExtra("url");
      mAuthor=intent.getStringExtra("author");
      mTitle=intent.getStringExtra("title");
      mImg=intent.getStringExtra("img");
      mDate=intent.getStringExtra("time");
      mSource=intent.getStringExtra("source");

        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());

        Glide.with(this)
                .load(mUrl)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        AppBarTitle.setText(mSource);
        AppBarSubtitle.setText(mUrl);
        date.setText(Utils.DateFormat(mDate));
        title.setText(mTitle);

        String author = null;

        if (mAuthor !=null || mAuthor !="")
        {
            mAuthor="\u2022"+mAuthor;
        }
        else {
            author="";
        }

     time.setText(mSource+author+"\u2022"+Utils.DateFormat(mDate));

        initwebview(mUrl);
    }

   private void  initwebview(String Url)
   {
       WebView webView=findViewById(R.id.webView);

       webView.getSettings().setLoadsImagesAutomatically(true);
       webView.getSettings().setJavaScriptEnabled(true);
       webView.getSettings().setDomStorageEnabled(true);
       webView.getSettings().setSupportZoom(true);
       webView.getSettings().setDisplayZoomControls(true);
       webView.getSettings().setBuiltInZoomControls(true);
       webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
       webView.setWebViewClient(new WebViewClient());
       webView.loadUrl(Url);

   }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int MaxScroll=appBarLayout.getTotalScrollRange();
        float precantage=(float) Math.abs(i)/(float) MaxScroll;

        if (precantage==1f &&isHideToBarView)
        {
            data_behavior.setVisibility(View.GONE);
            titleAppbar.setVisibility(View.INVISIBLE);
            isHideToBarView=!isHideToBarView;
        }
        else if (precantage<1f &&isHideToBarView)
        {
            data_behavior.setVisibility(View.VISIBLE);
            titleAppbar.setVisibility(View.GONE);
            isHideToBarView=!isHideToBarView;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.share)
        {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plan");
                i.putExtra(Intent.EXTRA_SUBJECT, mSource);
                String body = mTitle + "\n" + mUrl + "\n" + "Share from News App" + "\n";
                i.putExtra(Intent.EXTRA_SUBJECT, body);
                startActivity(Intent.createChooser(i, "Share With :"));
            }
            catch (Exception e)
            {
                Toast.makeText(this,"Sorry,\n cannot be Share",Toast.LENGTH_LONG).
                        show();
            }

        }
        if (id==R.id.web_view)
        {
            Intent i=new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(mUrl));
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
