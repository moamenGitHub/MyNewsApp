package com.example.newsapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.newsapp.models.Articles;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private  List<Articles> articles;
    private  Context context;
    private OnItemClickListener onItemClickListener;

    public Adapter(List<Articles> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(context).
                inflate(R.layout.item,viewGroup,false);

        return new MyViewHolder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {

        final MyViewHolder holder=viewHolder;
        Articles model=articles.get(i);
        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(model.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                     holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);

                holder.Title.setText(model.getTitle());
                holder.Description.setText(model.getDescription());
                holder.Source.setText(model.getSource().getName());
                holder.Time.setText("\u2022"+Utils.DateToTimeFormat(model.getPublishedAt()));
                holder.publishedAt.setText(Utils.DateToTimeFormat(model.getPublishedAt()));
                holder.Author.setText(model.getAuthor());

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void OnItemClicklistener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener=onItemClickListener;
    }

    public interface OnItemClickListener{
        void OnItemclick(View view,int Position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView Title,Description,Author,Source,Time,publishedAt;
        ProgressBar progressBar;
        ImageView imageView;
        OnItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View itemView,OnItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);

            Title=itemView.findViewById(R.id.title);
            Description=itemView.findViewById(R.id.description);
            Author=itemView.findViewById(R.id.author);
            Source=itemView.findViewById(R.id.Source);
            Time=itemView.findViewById(R.id.time);
            publishedAt=itemView.findViewById(R.id.publishedAt);

            progressBar=itemView.findViewById(R.id.my_progress_bar);
            imageView=itemView.findViewById(R.id.img);

            this.onItemClickListener=onItemClickListener;


        }

        @Override
        public void onClick(View v) {

            onItemClickListener.OnItemclick(v,getAdapterPosition());
        }
    }
}
