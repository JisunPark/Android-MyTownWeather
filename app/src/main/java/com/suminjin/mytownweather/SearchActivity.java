package com.suminjin.mytownweather;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.suminjin.appbase.BaseConfig;
import com.suminjin.appbase.CustomDialog;
import com.suminjin.appbase.CustomProgressDialog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_PERMISSION = 0;
    private static final int REQUEST_CODE_SETTINGS = 1;

    private CustomProgressDialog progressDialog;

    private LocationManager locationManager;
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자

            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            Log.i(BaseConfig.TAG, "위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
                    + "\n고도 : " + altitude + "\n정확도 : " + accuracy);

            // 위경도에 해당하는 주소 가져오기
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                String addr = "";
                if (null != listAddresses && listAddresses.size() > 0) {
                    addr = listAddresses.get(0).getAddressLine(0);
                }
                Log.i(BaseConfig.TAG, "location name " + addr);
                AppData.put(SearchActivity.this, AppData.KEY_LOCATION_NAME, addr);
            } catch (IOException e) {
                Log.e(BaseConfig.TAG, "IOException] " + e.toString());
            }

            // 위경도를 x,y로 변환(공공 api 제공 룰에 따라)
            int[] xy = convertLocation(longitude, latitude);

            // 위치를 찾았으면 위치값을 가지고 호출한 곳으로 되돌아 가기
            Intent intent = new Intent();
            intent.putExtra(MainActivity.INTENT_EXTRA_X, xy[0]);
            intent.putExtra(MainActivity.INTENT_EXTRA_Y, xy[1]);
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(0, 0);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setLocationListener();
        } else {
            boolean checkedGps = AppData.get(this, AppData.KEY_CHECKED_GPS, false);
            if (checkedGps) {
                setLocationListener();
            } else {
                AppData.put(this, AppData.KEY_CHECKED_GPS, true);

                final CustomDialog dialog = new CustomDialog(this, R.string.seacrh_current_location, R.string.gps_confirm_msg);
                dialog.setPositiveBtn(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //GPS 설정화면으로 이동
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivityForResult(intent, REQUEST_CODE_SETTINGS);
                        dialog.dismiss();
                    }
                });
                dialog.setOnNegativeBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setLocationListener();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SETTINGS:
                // gps 설정 화면 갔다가 돌아왔을 때
                // gps on/off 여부에 관계없이 현위치 검색을 시작한다.
                setLocationListener();
                break;
            default:
        }
    }

    /**
     * 위치 탐색 시작
     */
    private void setLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없으면 사용자에게 허가를 요청한다.
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, REQUEST_CODE_PERMISSION);
            }
        } else {
            // 위치 제공자 listener를 등록한다.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    100, // 통지사이의 최소 시간간격 (milliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    100, // 통지사이의 최소 시간간격 (milliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);

            progressDialog = new CustomProgressDialog(this);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
            progressDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            boolean allowed = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allowed = false;
                    break;
                }
            }
            if (allowed) {
                setLocationListener();
            } else {
                Toast.makeText(this, "위치 정보를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (locationManager != null) {
            locationManager.removeUpdates(mLocationListener);
        }
        super.onDestroy();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native int[] convertLocation(double a, double b);

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

}
