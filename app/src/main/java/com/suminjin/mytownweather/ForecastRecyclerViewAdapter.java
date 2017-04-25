package com.suminjin.mytownweather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suminjin.data.DataCode;
import com.suminjin.mytownweather.widget.BarGraph;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(getLayout(viewType), parent, false);
        ViewHolder holder = new ViewHolder(v, viewType);
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).viewType;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        ForecastItem item = items.get(position);

        // 공통 view들
        holder.txtCode.setText("[" + item.code + "]");
        StringBuilder sb = new StringBuilder();
        sb.append(item.dataCode.dataName);
        holder.txtCodeString.setText(sb.toString());

        switch (getDataCode(viewType)) {
            case POP:
            case REH:
                holder.txtCode.setVisibility(View.GONE);
                holder.barGraph.setData(item.list);
                if (!item.dataCode.unit.isEmpty()) {
                    sb.append("(").append(item.dataCode.unit).append(")");
                }
                holder.txtCodeString.setText(sb.toString());
                break;
            default:
                holder.layoutContents.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(context);
                for (int i = 0; i < item.list.size(); i++) {
                    LinearLayout layoutRow = (LinearLayout) inflater.inflate(R.layout.layout_forecast_item_contents, null, false);
                    TextView txtTime = (TextView) layoutRow.findViewById(R.id.txtTime);
                    TextView txtData = (TextView) layoutRow.findViewById(R.id.txtData);
                    TextView txtDataString = (TextView) layoutRow.findViewById(R.id.txtDataString);

                    ForecastSubItem subItem = item.list.get(i);
                    if (!subItem.date.isEmpty() && !subItem.time.isEmpty()) {
                        txtTime.setText(subItem.getFormattedDateString() +
                                " " + subItem.getFormattedTimeString());
                    }
                    txtData.setText("(" + subItem.value + ")");
                    txtDataString.setText(subItem.dataString);

                    holder.layoutContents.addView(layoutRow);
                }

                setAnimation(holder.layoutBase, position);
        }
    }

    protected int getLayout(int viewType) {
        int res;
        switch (getDataCode(viewType)) {
            case POP: // 강수확률
            case REH: // 습도
                res = R.layout.layout_forecast_graph;
                break;
            default:
                res = R.layout.layout_forecast_item;
        }
        return res;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View layoutBase;
        public TextView txtCode;
        public TextView txtCodeString;
        public LinearLayout layoutContents;
        public BarGraph barGraph;

        public ViewHolder(View view, int viewType) {
            super(view);

            // 공통
            txtCode = (TextView) view.findViewById(R.id.txtCode);
            txtCodeString = (TextView) view.findViewById(R.id.txtCodeString);

            switch (getDataCode(viewType)) {
                case POP:
                case REH:
                    barGraph = (BarGraph) view.findViewById(R.id.bar_graph);
                    break;
                default:
                    layoutBase = view.findViewById(R.id.layoutBase);
                    layoutContents = (LinearLayout) view.findViewById(R.id.layoutContents);
            }
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

    /**
     * viewType에 따른 dataCode 가져오기
     *
     * @param viewType
     * @return
     */
    private DataCode getDataCode(int viewType) {
        return DataCode.values()[viewType];
    }
}
