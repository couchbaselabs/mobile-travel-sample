package com.couchbase.travelsample.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private List<Map<String, Object>> mResultSet;
    private String mField1;
    private String mField2;
    private OnItemClickListener mOnItemClickListener;
    private int mLayout;

    public interface OnItemClickListener {
        void OnClick(View view, int position);
    }

    public ResultAdapter(List<Map<String, Object>> mResultSet, String field1, String field2) {
        this.mResultSet = mResultSet;
        this.mField1 = field1;
        this.mField2 = field2;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            subtitle = itemView.findViewById(android.R.id.text2);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(android.R.layout.two_line_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.title.setText((String) mResultSet.get(position).get(mField1));
        holder.subtitle.setText((String) mResultSet.get(position).get(mField2));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.OnClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResultSet.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}