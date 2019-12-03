package com.couchbase.travelsample.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.couchbase.travelsample.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FlightSearchAdapter extends RecyclerView.Adapter<FlightSearchAdapter.ViewHolder> {

    private List<List<JSONObject>> mJourneys;
    private OnItemClickListener mOnItemClickListener;
    private int mLayout;

    public interface OnItemClickListener {
        void OnClick(View view, int position);
    }

    public FlightSearchAdapter(List<List<JSONObject>> journeys) {
        this.mJourneys = journeys;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title1;
        TextView subtitle1;
        TextView title2;
        TextView subtitle2;

        ViewHolder(View itemView) {
            super(itemView);
            title1 = itemView.findViewById(R.id.title1);
            subtitle1 = itemView.findViewById(R.id.subtitle1);
            title2 = itemView.findViewById(R.id.title2);
            subtitle2 = itemView.findViewById(R.id.subtitle2);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.flight_search_list_item, parent, false);
        view.setPadding(20, 20, 20, 20);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            holder.title1.setText(mJourneys.get(position).get(0).getString("utc"));
            String subtitle1 = String.format("%s - %s, %s", mJourneys.get(position).get(0).getString("sourceairport")
                , mJourneys.get(position).get(0).getString("destinationairport"), mJourneys.get(position).get(0).getString("name"));
            holder.subtitle1.setText(subtitle1);
            holder.title2.setText(mJourneys.get(position).get(1).getString("utc"));
            String subtitle2 = String.format("%s - %s, %s", mJourneys.get(position).get(1).getString("sourceairport")
                , mJourneys.get(position).get(1).getString("destinationairport"), mJourneys.get(position).get(1).getString("name"));
            holder.subtitle2.setText(subtitle2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.OnClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mJourneys.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}