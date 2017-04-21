package com.suminjin.mytownweather;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.suminjin.appbase.ListDialog;
import com.suminjin.data.LocalLocation;
import com.suminjin.data.LocalLocationItem;

import java.util.ArrayList;

/**
 * FIXME jisun : 지역 선택 방식 좀더 효율적으로 수정하기
 * <p>
 * Created by parkjisun on 2017. 4. 20..
 */

public class LocationListDialog extends ListDialog {
    private ArrayList<String> addrList1;
    private ArrayList<String> addrList2;
    private ArrayList<String> addrList3;
    private int depth = 1;

    private String selectedAddr1;
    private String selectedAddr2;
    private OnSelectListener onSelectListener;

    public LocationListDialog(Context context) {
        super(context);

        final LocalLocation localLocation = new LocalLocation(context);
        addrList1 = localLocation.getFirstAddressList();

        final ListAdapter adapter = new ListAdapter(addrList1, context);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, String data) {
                switch (depth) {
                    case 1:
                        ++depth;
                        selectedAddr1 = data;
                        addrList2 = localLocation.getSecondAddressList(data);
                        adapter.update(addrList2);
                        recyclerView.scrollTo(0, 0);
                        break;
                    case 2:
                        ++depth;
                        selectedAddr2 = data;
                        addrList3 = localLocation.getThirdAddressList(selectedAddr1, data);
                        adapter.update(addrList3);
                        recyclerView.scrollTo(0, 0);
                        break;
                    case 3:
                        LocalLocationItem locationItem = localLocation.getLocationItem(selectedAddr1, selectedAddr2, data);
                        if (onSelectListener != null) {
                            onSelectListener.onSelected(locationItem);
                        }
                        dismiss();
                        break;
                    default:
                }
            }
        });
        recyclerView.setAdapter(adapter);

        // ok 버튼은 '이전' 기능으로
        TextView btnPrev = (TextView) findViewById(com.suminjin.appbase.R.id.btnOk);
        btnPrev.setText("이전");
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (depth) {
                    case 2:
                        --depth;
                        adapter.update(addrList1);
                        recyclerView.scrollTo(0, 0);
                        break;
                    case 3:
                        --depth;
                        adapter.update(addrList2);
                        recyclerView.scrollTo(0, 0);
                        break;
                }

            }
        });
    }

    public void setOnSelectListener(OnSelectListener l) {
        onSelectListener = l;
    }

    /**
     * 지역 선택이 완료되었을 경우 호출되는 listener
     */
    public interface OnSelectListener {
        void onSelected(LocalLocationItem item);
    }

}
