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

public class MainActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener {

    double myLongitude;
    double myLatitude;

    ViewGroup treeInfoLayout;
    Button btnGetLocation;
    Button btnRename;
    TextView txtTreeName, txtTreeLatitude, txtTreeLongitude, txtTreeAuthor;
    MapView mapView;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Layout fields
        treeInfoLayout = findViewById(R.id.treeInfoLayout);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnRename = findViewById(R.id.btnRename);
        txtTreeName = findViewById(R.id.txtTreeName);
        txtTreeLatitude = findViewById(R.id.txtTreeLatitude);
        txtTreeLongitude = findViewById(R.id.txtTreeLongitude);
        txtTreeAuthor = findViewById(R.id.txtAuthor);

        // Location Manager: 현재 위치 정보를 받아오기 위한 개체.
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 현재 위치 정보(위도와 경도)를 받아온다. myLongitude, myLatitude를 찾는다.
        getLocation(locationManager);

        // 버튼 이벤트: 위치 정보 받기.
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation(locationManager);
            }
        });
        // 버튼 이벤트: 이름 짓기: 이름 짓기 Activity로 넘어간다.
        //           TODO:: 임시로 현재 위도와 경도를 참조하여 임의의 name, author를 생성하여 넘겨줌.
        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RenameActivity.class);
                intent.putExtra("treeName", txtTreeName.getText());
                intent.putExtra("treeAuthor", txtTreeAuthor.getText());
                startActivity(intent);
            }
        });

        // TODO:: DEBUGGING
        getHashKey();

        // 맵뷰 생성.
        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        // 마커 클릭시 이벤트.
        mapView.setPOIItemEventListener(this);
        // 지도 클릭시 이벤트.
        mapView.setMapViewEventListener(this);
        // 현재 위치로 맵 중심 이동.
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(myLatitude, myLongitude), true);
        // 현재 위치에 마커 찍기.
        mapMarkerPoint(myLatitude, myLongitude, "현재 위치", MapPOIItem.MarkerType.BluePin, MapPOIItem.MarkerType.RedPin);
        // 100개 랜덤 마커 찍기.
        for (int i=0;i<100;i++)temp(myLatitude, myLongitude);
    }

    // getLocation: 경도, 위도, 고도를 받아온다.
    private void getLocation(LocationManager locationManager) {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( MainActivity.this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
        }
        else{
            // 가장 최근 위치정보 가져오기
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                // 위치 정보 받기.
                myLongitude = location.getLongitude();
                myLatitude = location.getLatitude();
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

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // LocationListener는 위치 정보를 전달할 때 호출되므로 onLocationChanged() 메소드 안에 위치 정보 처리 작업을 구현한다.
            String provider = location.getProvider();  // 위치정보
            double longitude = location.getLongitude(); // 위도
            double latitude = location.getLatitude(); // 경도
            double altitude = location.getAltitude(); // 고도
        } public void onStatusChanged(String provider, int status, Bundle extras) {

        } public void onProviderEnabled(String provider) {

        } public void onProviderDisabled(String provider) {

        }
    };

    // mapMarkerPoint: 해당 위도와 경도에 마커를 찍는다.
    private void mapMarkerPoint(double latitude, double longitude, String markerName, MapPOIItem.MarkerType markerType, MapPOIItem.MarkerType markerTypeOnClick) {
        // #1. 좌표 설정.
        //     카카오 맵은 좌표 기준이 여러 개라 new MapPoint()로 생성할 수 없고, 이렇게 생성해야 함.
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(markerName);
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(markerType);
        marker.setSelectedMarkerType(markerTypeOnClick); // 마커를 클릭했을때

        mapView.addPOIItem(marker);
    }
    private void temp(double latitude, double longitude) {
        int count = (int)(100 * Math.random());
        for (int i = 0; i < count; i++) {
            int a = (int)(10 * Math.random()); // 0 ~ 10
            if (a < 2) {
                latitude += 0.001;
            }
            else if (a < 5) {
                latitude -= 0.001;
            }
            else if (a < 8) {
                longitude += 0.001;
            }
            else {
                longitude -= 0.001;
            }
        }
        mapMarkerPoint(latitude, longitude, getTreeName(latitude, longitude), MapPOIItem.MarkerType.YellowPin, MapPOIItem.MarkerType.RedPin);
    }








    // setTreeInfoLayout: 위도와 경도를 참조하여 가로수 정보 레이아웃을 채운다.
    private void setTreeInfoLayout(double latitude, double longitude) {
        txtTreeName.setText(getTreeName(latitude, longitude));
        txtTreeLatitude.setText(Double.toString(latitude));
        txtTreeLongitude.setText(Double.toString(longitude));
        txtTreeAuthor.setText(getAuthor(latitude, longitude));
    }
    private String getTreeName(double latitude, double longitude) {
        int a = (int)(latitude * 1000000) - (int)(latitude * 10000) * 100;
        int b = (int)(longitude * 1000000) - (int)(longitude * 10000) * 100;
        return "가로수 " + Integer.toString(a) + "_" + Integer.toString(b);
    }
    private String getAuthor(double latitude, double longitude) {
        return getTreeName(latitude, longitude) + "의 주인";
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

    // ====== MapView.MapViewEventListener ======
    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        treeInfoLayout.setVisibility(View.GONE);
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
    // ====== MapView.MapViewEventListener ======

    // ====== MapView.POIItemEventListener ======
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        double latitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude;
        double longitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;
        setTreeInfoLayout(latitude, longitude);
        treeInfoLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
    // ====== MapView.POIItemEventListener ======
}