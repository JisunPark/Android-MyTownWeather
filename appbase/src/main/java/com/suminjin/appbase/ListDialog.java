package com.suminjin.appbase;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by parkjisun on 2017. 4. 20..
 */

public class ListDialog extends BaseDialog {
    protected RecyclerView recyclerView;
    OnItemClickListener listener;

    public ListDialog(Context context) {
        super(context);
        initViews();
    }

    protected void initViews() {
        initWindowFeatures();
        setContentView(R.layout.dialog_list);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        findViewById(R.id.btnCancel).setOnClickListener(defaultClickListener);
        findViewById(R.id.btnOk).setOnClickListener(defaultClickListener);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String data);
    }

    protected class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private Context context;
        private ArrayList<String> items;

        // Allows to remember the last item shown on screen
        private int lastPosition = -1;

        public ListAdapter(ArrayList<String> items, Context mContext) {
            this.items = items;
            context = mContext;
        }

        // 필수로 Generate 되어야 하는 메소드 1 : 새로운 뷰 생성
        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // 새로운 뷰를 만든다
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_dialog_item, parent, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            String data = items.get(position);
            holder.textView.setText(data);
            holder.textView.setTag(data);
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(position, (String) v.getTag());
                    }
                }
            });

            setAnimation(holder.textView, position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;

            public ViewHolder(View view) {
                super(view);
                textView = (TextView) view.findViewById(R.id.text);
            }
        }

        private void setAnimation(View viewToAnimate, int position) {
            // 새로 보여지는 뷰라면 애니메이션을 해줍니다
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        public void update(ArrayList<String> list) {
            items = list;
            notifyDataSetChanged();
        }
    }

}
