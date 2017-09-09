package com.example.fho10.flicks.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fho10.flicks.R;
import com.example.fho10.flicks.models.Result;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class FlicksRecyclerViewAdapter extends RecyclerView.Adapter<FlicksRecyclerViewAdapter.FlicksViewHolder>  {
    List<Result> flicks;
    public static String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    public FlicksRecyclerViewAdapter(@NonNull List<Result> flicks) {
        this.flicks = flicks;
    }

    @Override
    public FlicksRecyclerViewAdapter.FlicksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View flickView = inflater.inflate(R.layout.item_movie, parent, false);

        FlicksViewHolder viewHolder = new FlicksViewHolder(flickView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(FlicksViewHolder holder, int position) {
        Result movie = flicks.get(position);
        holder.tvTitle.setText(movie.getTitle());
        holder.tvDescription.setText(movie.getOverview());
        int orientation = holder.context.getResources().getConfiguration().orientation;
        if (orientation == ORIENTATION_PORTRAIT) {
            Glide.with(holder.context).load(IMAGE_BASE_URL + movie.getPosterPath()).into(holder.ivPoster);
        } else {
            Glide.with(holder.context).load(IMAGE_BASE_URL + movie.getBackdropPath()).into(holder.ivPoster);
        }
    }

    @Override
    public int getItemCount() {
        return flicks.size();
    }

    @Override
    public void onViewRecycled(FlicksViewHolder holder) {
        super.onViewRecycled(holder);
        holder.tvTitle.setText("");
        holder.tvDescription.setText("");
        holder.ivPoster.setImageResource(0);
    }

    public class FlicksViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        @BindView(R.id.ivPoster) ImageView ivPoster;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvDescription) TextView tvDescription;


        public FlicksViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            context = itemView.getContext();
        }

    }
}
