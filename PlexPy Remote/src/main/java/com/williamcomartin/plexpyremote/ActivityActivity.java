package com.williamcomartin.plexpyremote;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.williamcomartin.plexpyremote.Adapters.ActivityAdapter;
import com.williamcomartin.plexpyremote.Helpers.EmptyRecyclerView;
import com.williamcomartin.plexpyremote.Helpers.GsonRequest;
import com.williamcomartin.plexpyremote.Helpers.UrlHelpers;
import com.williamcomartin.plexpyremote.Models.ActivityModels;

public class ActivityActivity extends NavBaseActivity {

    private EmptyRecyclerView rvActivities;

    private SharedPreferences SP;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);
        setupActionBar();

        refreshItems();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutActivities);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });
    }

    private void refreshItems() {
        SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        rvActivities = (EmptyRecyclerView) findViewById(R.id.rvActivities);

        String url = UrlHelpers.getHostPlusAPIKey() + "&cmd=get_activity";

        GsonRequest<ActivityModels> request = new GsonRequest<>(
                url,
                ActivityModels.class,
                null,
                requestListener(),
                errorListener()
        );

        ApplicationController.getInstance().addToRequestQueue(request);

        rvActivities.setLayoutManager(new LinearLayoutManager(this));

        View emptyView = findViewById(R.id.emptyRvActivities);
        rvActivities.setEmptyView(emptyView);
    }

    private void onItemsLoadComplete() {
        if(mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private Response.ErrorListener errorListener() {
        onItemsLoadComplete();
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TextView text = (TextView) findViewById(R.id.emptyTextView);
                text.setText(error.getMessage());
                text.setTextColor(Color.RED);
            }
        };
    }

    private Response.Listener<ActivityModels> requestListener() {
        return new Response.Listener<ActivityModels>() {
            @Override
            public void onResponse(ActivityModels response) {
                ActivityAdapter adapter = new ActivityAdapter(response.response.data.sessions);

                rvActivities.setAdapter(adapter);
                onItemsLoadComplete();
            }
        };
    }



    protected void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.activity);
    }

}