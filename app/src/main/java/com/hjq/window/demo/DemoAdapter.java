package com.hjq.window.demo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hjq.window.demo.DemoAdapter.DemoViewHolder;
import java.util.List;

public class DemoAdapter extends RecyclerView.Adapter<DemoViewHolder> {

    private final List<String> mDataList;

    public DemoAdapter(List<String> dataList) {
        mDataList = dataList;
    }

    @NonNull
    @Override
    public DemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.window_list_item, parent, false);
        return new DemoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DemoViewHolder holder, int position) {
        String data = mDataList.get(position);
        holder.mTextview.setText(data);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class DemoViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextview;

        public DemoViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextview = itemView.findViewById(R.id.tv_window_list_item_text);
        }
    }
}