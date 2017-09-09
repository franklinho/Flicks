package com.example.fho10.flicks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.fho10.flicks.adapters.FlicksRecyclerViewAdapter;
import com.example.fho10.flicks.models.Data;
import com.example.fho10.flicks.models.Result;
import com.example.fho10.flicks.networking.FlicksClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieActivity extends AppCompatActivity {
    private static String TAG = "MovieActivity";
    FlicksClient.FlicksService client;
    List<Result> flicks = new ArrayList<>();
    FlicksRecyclerViewAdapter flicksRecyclerViewAdapter;
    @BindView(R.id.rvFlicks) RecyclerView rvFlicks;
    int currentPage = 1;
    LinearLayoutManager linearLayoutManager;
    boolean isLoading = false;
    boolean isLastPage = false;
    private static int PAGE_SIZE = 20;

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

            if (!isLoading && flicksRecyclerViewAdapter.getItemCount() - linearLayoutManager.findLastVisibleItemPosition() < PAGE_SIZE) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    currentPage += 1;
                    requestFlicks(currentPage);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);

        if (client == null) {
            client = FlicksClient.getService();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        flicksRecyclerViewAdapter = new FlicksRecyclerViewAdapter(flicks);
        rvFlicks.setAdapter(flicksRecyclerViewAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        rvFlicks.setLayoutManager(linearLayoutManager);
        rvFlicks.setOnScrollListener(recyclerViewOnScrollListener);

        if (flicks == null || flicks.isEmpty()) {
            requestFlicks(currentPage);
        } else {
            flicksRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void requestFlicks(int page) {
        if (isLoading) {
            return;
        }
        Log.d(TAG, "Requesting flicks");
        if (page <= 1) {
            flicks.clear();
        }
        Call<Data> request = client.listNowPlaying(page);
        isLoading = true;
        request.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if (response != null && response.body() != null && !response.body().getResults().isEmpty()) {
                    List<Result> results = response.body().getResults();
                    int currentSize = flicks.size();
                    flicks.addAll(results);
                    flicksRecyclerViewAdapter.notifyItemRangeChanged(currentSize, flicks.size());
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Log.d(TAG, "Error requesting flicks");
                isLoading = false;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("Flicks", (Serializable) flicks);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        List<Result> results = (List<Result>) savedInstanceState.getSerializable("Flicks");
        flicks = results != null ? results : new ArrayList<Result>();
    }
}
