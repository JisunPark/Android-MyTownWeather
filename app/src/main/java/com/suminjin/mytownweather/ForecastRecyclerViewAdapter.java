package com.suminjin.mytownweather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by parkjisun on 2017. 4. 19..
 */

public class ForecastRecyclerViewAdapter extends RecyclerView.Adapter<ForecastRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ForecastItem> items;

    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    public ForecastRecyclerViewAdapter(ArrayList<ForecastItem> items, Context mContext) {
        this.items = items;
        context = mContext;
    }

    @Override
    public ForecastRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grib_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ForecastItem item = items.get(position);
        holder.txtIndex.setText(item.index);
        holder.txtCode.setText(item.code);
        holder.txtCodeString.setText(item.codeString);
        holder.txtData.setText(item.data);
        holder.txtDataString.setText(item.dataString);

        setAnimation(holder.txtDataString, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtIndex;
        public TextView txtCode;
        public TextView txtCodeString;
        public TextView txtData;
        public TextView txtDataString;

        public ViewHolder(View view) {
            super(view);
            txtIndex = (TextView) view.findViewById(R.id.txtIndex);
            txtCode = (TextView) view.findViewById(R.id.txtCode);
            txtCodeString = (TextView) view.findViewById(R.id.txtCodeString);
            txtData = (TextView) view.findViewById(R.id.txtData);
            txtDataString = (TextView) view.findViewById(R.id.txtDataString);
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
}
