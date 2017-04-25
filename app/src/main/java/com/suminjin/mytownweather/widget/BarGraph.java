package com.suminjin.mytownweather.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suminjin.mytownweather.ForecastSubItem;
import com.suminjin.mytownweather.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by parkjisun on 2017. 4. 25..
 */

public class BarGraph extends LinearLayout {
    private Context context;

    public BarGraph(Context context) {
        this(context, null);
    }

    public BarGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }


    public BarGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    private void initViews(Context context) {
        this.context = context;

        setBackgroundResource(R.drawable.bg_forecast_item_contents);
        setOrientation(HORIZONTAL);

        if (isInEditMode()) {
            addBar("0800", 30);
        }
    }

    private void addBar(String time, int value) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_bar_graph_item, null);

        float percentage = (float) value / 100;

        View viewRest = rootView.findViewById(R.id.view_rest);
        LinearLayout.LayoutParams p1 = (LayoutParams) viewRest.getLayoutParams();
        p1.weight = 1 - percentage;
        viewRest.setLayoutParams(p1);

        View viewPercentage = rootView.findViewById(R.id.view_percentage);
        LinearLayout.LayoutParams p2 = (LayoutParams) viewPercentage.getLayoutParams();
        p2.weight = percentage;
        viewPercentage.setLayoutParams(p2);

        TextView textValue = (TextView) rootView.findViewById(R.id.text_value);
        textValue.setText(Integer.toString(value));

        TextView textTime = (TextView) rootView.findViewById(R.id.text_time);
        textTime.setText(time);

        addView(rootView);
    }

    public void setData(ArrayList<ForecastSubItem> list) {
        for (ForecastSubItem item : list) {
            addBar(getFormattedTime(item.time), Integer.parseInt(item.value));
        }
    }

    private String getFormattedTime(String time) {
        String result = time;
        if (!time.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("HHmm", Locale.getDefault());
            SimpleDateFormat sdf2 = new SimpleDateFormat("H시", Locale.getDefault());
            try {
                Date date = sdf.parse(time);
                result = sdf2.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
