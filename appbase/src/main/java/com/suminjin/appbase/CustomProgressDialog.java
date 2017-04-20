package com.suminjin.appbase;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by p90447 on 2015-11-18.
 */
public class CustomProgressDialog extends BaseDialog {

    public CustomProgressDialog(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        initWindowFeatures();
        setContentView(R.layout.layout_custom_progress_dialog);
        setCancelable(true);
    }
}
