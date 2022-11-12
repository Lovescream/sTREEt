package com.jyoon.hackathon2022_test3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    double myLongitude;
    double myLatitude;
    double myAltitude;


    Button btnGetLocation;
    Button btnRename;
    TextView txtResult;
    MapView mapView;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnRename = findViewById(R.id.btnRename);
        txtResult = findViewById(R.id.txtResult);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        getLocation(locationManager);

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation(locationManager);
            }
        });
        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RenameActivity.class);
                startActivity(intent);
            }
        });
        getHashKey();

        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(myLatitude, myLongitude), true);
        MapMarkerPoint(myLatitude, myLongitude);
    }
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // 위치 리스너는 위치정보를 전달할 때 호출되므로 onLocationChanged()메소드 안에 위지청보를 처리를 작업을 구현 해야합니다.
            String provider = location.getProvider();  // 위치정보
            double longitude = location.getLongitude(); // 위도
            double latitude = location.getLatitude(); // 경도
            double altitude = location.getAltitude(); // 고도
            txtResult.setText("위치정보 : " + provider + "\n" + "위도 : " + longitude + "\n" + "경도 : " + latitude + "\n" + "고도 : " + altitude);
        } public void onStatusChanged(String provider, int status, Bundle extras) {

        } public void onProviderEnabled(String provider) {

        } public void onProviderDisabled(String provider) {

        }
    };

    // getLocation: 경도, 위도, 고도를 받아온다.
    private void getLocation(LocationManager locationManager) {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( MainActivity.this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
        }
        else{
            // 가장최근 위치정보 가져오기
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                String provider = location.getProvider();
                myLongitude = location.getLongitude();
                myLatitude = location.getLatitude();
                myAltitude = location.getAltitude();

                txtResult.setText("위치정보 : " + provider + "\n" +
                        "위도 : " + myLongitude + "\n" +
                        "경도 : " + myLatitude + "\n" +
                        "고도  : " + myAltitude);
            }

            // 위치정보를 원하는 시간, 거리마다 갱신해준다.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000,
                    1,
                    gpsLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000,
                    1,
                    gpsLocationListener);
        }
    }

    //
    private void MapMarkerPoint(double latitude, double longitude) {
        // 카카오 맵은 좌표 기준이 여러 개라 new MapPoint()로 생성할 수 없고, 이렇게 생성해야 함.
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);
    }





    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }



}