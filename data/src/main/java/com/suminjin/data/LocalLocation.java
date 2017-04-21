package com.suminjin.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by parkjisun on 2017. 4. 20..
 */

public class LocalLocation {
    public static ArrayList<LocalLocationItem> items;

    public LocalLocation(Context context) {
        if (items == null) {
            items = new ArrayList<>();

            InputStream raw = context.getResources().openRawResource(R.raw.location);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            int i;
            try {
                i = raw.read();
                while (i != -1) {
                    byteArrayOutputStream.write(i);
                    i = raw.read();
                }
                raw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String rawText = byteArrayOutputStream.toString();
            parseJson(rawText);
        }
    }

    private void parseJson(String rawText) {
        try {
            JSONArray jsonArray = new JSONArray(rawText);
            Log.e("jisunLog", "jsonArray length " + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray subArray = (JSONArray) jsonArray.get(i);
                LocalLocationItem locationItem = new LocalLocationItem();
                for (int j = 0; j < subArray.length(); j++) {
                    // 0,1,2 : address string
                    // 3,4 : x,y integer
                    switch (j) {
                        case LocalLocationItem.ADDR1:
                            if (subArray.get(j) instanceof String) {
                                String s = (String) subArray.get(j);
                                locationItem.addr1 = s;
                            }
                            break;
                        case LocalLocationItem.ADDR2:
                            if (subArray.get(j) instanceof String) {
                                String s = (String) subArray.get(j);
                                locationItem.addr2 = s;
                            }
                            break;
                        case LocalLocationItem.ADDR3:
                            if (subArray.get(j) instanceof String) {
                                String s = (String) subArray.get(j);
                                locationItem.addr3 = s;
                            }
                            break;
                        case LocalLocationItem.X:
                            if (subArray.get(j) instanceof Integer) {
                                locationItem.x = (int) subArray.get(j);
                            }
                            break;
                        case LocalLocationItem.Y:
                            if (subArray.get(j) instanceof Integer) {
                                locationItem.y = (int) subArray.get(j);
                            }
                            break;
                        default:
                    }
                }
                items.add(locationItem);
            }
            Log.e("jisunLog", "items length " + items.size());
        } catch (JSONException e) {
            Log.e(AppConfig.TAG, "JSONException] " + e.toString());
        }
    }

    public ArrayList<String> getFirstAddressList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            String s = items.get(i).addr1;
            if (!list.contains(s)) {
                list.add(s);
            }
        }
        return list;
    }

    public ArrayList<String> getSecondAddressList(String addr1) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            String s1 = items.get(i).addr1;
            String s2 = items.get(i).addr2;
            if (s1.equals(addr1) && !list.contains(s2) && !s2.isEmpty()) {
                list.add(s2);
            }
        }
        return list;
    }

    public ArrayList<String> getThirdAddressList(String addr1, String addr2) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            LocalLocationItem item = items.get(i);
            String s1 = item.addr1;
            String s2 = item.addr2;
            String s3 = item.addr3;
            if (s1.equals(addr1) && s2.equals(addr2) && !list.contains(s3) && !s3.isEmpty()) {
                list.add(s3);
            }
        }
        return list;
    }

    public LocalLocationItem getLocationItem(String addr1, String addr2, String addr3) {
        LocalLocationItem result = null;
        for (int i = 0; i < items.size(); i++) {
            LocalLocationItem item = items.get(i);
            if (item.addr1.equals(addr1) && item.addr2.equals(addr2) && item.addr3.equals(addr3)) {
                result = item;
                break;
            }
        }
        return result;
    }
}
