package com.suminjin.appbase;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by jspark on 2016-03-14.
 */
public abstract class BaseDialog extends Dialog {
    protected View.OnClickListener defaultClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };
    protected Context context;

    public BaseDialog(Context context) {
        super(context);
        this.context = context;
    }

    protected void initWindowFeatures() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void setPositiveBtn(int resId, View.OnClickListener listener) {
        TextView btnOk = (TextView) findViewById(R.id.btnOk);
        btnOk.setText(resId);
        btnOk.setOnClickListener(listener);
    }

    public void setOnNegativeBtnClickListener(View.OnClickListener listener) {
        View view = findViewById(R.id.btnCancel);
        view.setOnClickListener(listener);
        view.setVisibility(View.VISIBLE);
    }
}
