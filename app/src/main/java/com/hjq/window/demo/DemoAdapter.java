package com.hjq.window.demo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hjq.window.demo.DemoAdapter.DemoViewHolder;
import java.util.List;

public class DemoAdapter extends RecyclerView.Adapter<DemoViewHolder> {

    private final List<String> mDataList;

    /** 条目点击监听器 */
    @Nullable
    private OnItemClickListener mItemClickListener;

    /** 条目长按监听器 */
    @Nullable
    private OnItemLongClickListener mItemLongClickListener;

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

    public class DemoViewHolder extends RecyclerView.ViewHolder
                                implements OnClickListener, OnLongClickListener {

        private final TextView mTextview;

        public DemoViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextview = itemView.findViewById(R.id.tv_window_list_item_text);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        /**
         * {@link View.OnClickListener}
         */

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            if (position < 0 || position >= getItemCount()) {
                return;
            }

            if (view == getItemView()) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(view, position);
                }
            }
        }

        /**
         * {@link View.OnLongClickListener}
         */
        @Override
        public boolean onLongClick(View view) {
            int position = getLayoutPosition();
            if (position < 0 || position >= getItemCount()) {
                return false;
            }

            if (view == getItemView()) {
                if (mItemLongClickListener != null) {
                    return mItemLongClickListener.onItemLongClick(view, position);
                }
                return false;
            }

            return false;
        }


        public final View getItemView() {
            return itemView;
        }
    }

    /**
     * 设置 RecyclerView 条目点击监听
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * 设置 RecyclerView 条目长按监听
     */
    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    /**
     * RecyclerView 条目点击监听类
     */
    public interface OnItemClickListener{

        /**
         * 当 RecyclerView 某个条目被点击时回调
         *
         * @param itemView          被点击的条目对象
         * @param position          被点击的条目位置
         */
        void onItemClick(View itemView, int position);
    }

    /**
     * RecyclerView 条目长按监听类
     */
    public interface OnItemLongClickListener {

        /**
         * 当 RecyclerView 某个条目被长按时回调
         *
         * @param itemView          被点击的条目对象
         * @param position          被点击的条目位置
         * @return                  是否拦截事件
         */
        boolean onItemLongClick(View itemView, int position);
    }
}