package com.suminjin.mytownweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.suminjin.appbase.CustomDialog;
import com.suminjin.data.LocalLocationItem;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    /**
     * 주소 선택하는 다이얼로그를 이용해 직접 지역 선택하기
     *
     * @param v
     */
    public void onClickLocal(View v) {
        LocationListDialog dialog = new LocationListDialog(this);
        dialog.setOnSelectListener(new LocationListDialog.OnSelectListener() {
            @Override
            public void onSelected(final LocalLocationItem item) {
                StringBuilder sb = new StringBuilder();
                sb.append("[").append(item.addr1).append(" ")
                        .append(item.addr2).append(" ")
                        .append(item.addr3)
                        .append("]으로 이동하시겠습니까?");

                final CustomDialog msgDialog = new CustomDialog(SettingActivity.this, R.string.select_location, sb.toString());
                msgDialog.setPositiveBtn(R.string.confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.dismiss();

                        AppData.put(SettingActivity.this, AppData.KEY_LOCATION_NAME, item.addr1 + " " + item.addr2 + " " + item.addr3);
                        goToMain(AppData.LOCATION_TYPE_SELECT, item.x, item.y);
                    }
                });
                msgDialog.setOnNegativeBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.dismiss();
                    }
                });
                msgDialog.show();
            }
        });
        dialog.show();
    }

    /**
     * 현위치 검색하기 선택
     *
     * @param v
     */
    public void onClickSearch(View v) {
        // 메인으로 이동해서 검색시작함
        goToMain(AppData.LOCATION_TYPE_SEARCH, 0, 0);
    }

    /**
     * 지역 좌표를 저장하고 메인 화면으로 이동한다.
     *
     * @param type
     * @param x
     * @param y
     */
    private void goToMain(int type, int x, int y) {
        AppData.put(this, AppData.KEY_LOCATION_TYPE, type);
        AppData.put(this, AppData.KEY_X, x);
        AppData.put(this, AppData.KEY_Y, y);
        Intent intent = new Intent();
        intent.putExtra(MainActivity.INTENT_EXTRA_X, x);
        intent.putExtra(MainActivity.INTENT_EXTRA_Y, y);
        setResult(RESULT_OK, intent);
        finish();
    }
}
