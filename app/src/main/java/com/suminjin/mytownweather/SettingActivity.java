package com.suminjin.mytownweather;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import com.suminjin.appbase.CustomDialog;
import com.suminjin.appbase.CustomProgressDialog;
import com.suminjin.data.LocationItem;

public class SettingActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 0;
    private static final int REQUEST_CODE_SETTINGS = 1;

    private static final String SHARED_PREF_NAME = "location";
    private static final String SHARED_PREF_KEY_TYPE = "location_type";
    private static final String SHARED_PREF_KEY_NAME = "location_name";
    private static final String SHARED_PREF_KEY_X = "location_x";
    private static final String SHARED_PREF_KEY_Y = "location_y";
    private static final int TYPE_SELECT = 0;
    private static final int TYPE_SEARCH = 1;

    private CustomProgressDialog progressDialog;

    private LocationManager locationManager;
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            Log.e("jisunLog", "위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
                    + "\n고도 : " + altitude + "\n정확도 : " + accuracy);

            // 위경도를 x,y로 변환(제공 룰에 따라)
            int[] xy = convertLocation(longitude, latitude);

            // 위치를 찾았으면 메인으로 이동
            goToMain(TYPE_SEARCH, xy[0], xy[1]);
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
    private boolean newSetting = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

//        newSetting = getIntent().getBooleanExtra(MainActivity.INTENT_EXTRA_NEW_SETTING, false);
//        // 메인화면에서 메뉴로 선택해 설정으로 이동했을 경우에는 저장된 값에 상관없이 처리한다.
//        if (!newSetting) {
//            // 저장된 위치 정보가 있으면 바로 메인으로 이동한다.
//            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
//            int x = sharedPreferences.getInt(SHARED_PREF_KEY_X, 0);
//            int y = sharedPreferences.getInt(SHARED_PREF_KEY_Y, 0);
//            if (x > 0 && y > 0) {
//                goToMain(x, y);
//            }
//        }

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        int type = sharedPreferences.getInt(SHARED_PREF_KEY_TYPE, -1);
        switch (type) {
            case TYPE_SELECT:
                // 저장된 위치 정보를 가지고 메인으로 이동한다.
                int x = sharedPreferences.getInt(SHARED_PREF_KEY_X, 0);
                int y = sharedPreferences.getInt(SHARED_PREF_KEY_Y, 0);
                if (x > 0 && y > 0) {
                    goToMain(TYPE_SELECT, x, y);
                }
                break;
            case TYPE_SEARCH:
                onClickSearch(null);
                break;
            default:
        }
    }

    /**
     * 지역 좌표를 저장하고 메인 화면으로 이동한다.
     *
     * @param type
     * @param x
     * @param y
     */
    private void goToMain(int type, int x, int y) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREF_KEY_TYPE, type);
        editor.putInt(SHARED_PREF_KEY_X, x);
        editor.putInt(SHARED_PREF_KEY_Y, y);
        editor.commit();

        if (newSetting) {
            Intent intent = new Intent();
            intent.putExtra(MainActivity.INTENT_EXTRA_X, x);
            intent.putExtra(MainActivity.INTENT_EXTRA_Y, y);
            setResult(RESULT_OK, intent);
        } else {
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            intent.putExtra(MainActivity.INTENT_EXTRA_X, x);
            intent.putExtra(MainActivity.INTENT_EXTRA_Y, y);
            startActivity(intent);
        }
        finish();
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
            public void onSelected(final LocationItem item) {
                StringBuilder sb = new StringBuilder();
                sb.append("[").append(item.addr1).append(" ")
                        .append(item.addr2).append(" ")
                        .append(item.addr3)
                        .append("]으로 이동하시겠습니까?");

                final CustomDialog msgDialog = new CustomDialog(SettingActivity.this, R.string.select_location, sb.toString());
                msgDialog.setOnPositiveBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.dismiss();

                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(SHARED_PREF_KEY_NAME, item.addr1 + " " + item.addr2 + " " + item.addr3);
                        editor.commit();

                        goToMain(TYPE_SELECT, item.x, item.y);
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
     * 현위치 검색하기
     * TODO jisun : 최초 설정에서 위치 검색을 선택했으면 앱 시작할 때마다 현 위치 검색해야 함.
     *
     * @param v
     */
    public void onClickSearch(View v) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setLocationListener();
        } else {
            final CustomDialog dialog = new CustomDialog(this, R.string.seacrh_current_location, R.string.gps_confirm_msg);
            dialog.setOnPositiveBtnClickListener(new View.OnClickListener() {
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
                    locationManager.removeUpdates(mLocationListener);
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
