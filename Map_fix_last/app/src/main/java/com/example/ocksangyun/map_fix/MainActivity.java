package com.example.ocksangyun.map_fix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ocksangyun.map_fix.R;
import com.google.android.gms.common.api.Api;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button get_result;
    private EditText showResult;
    private ListView listView;
    private DrawerLayout drawerLayout;
    private String start_latitude;
    private String start_longtitude;
    private String end_latitude;
    private  String end_longtitude;
    private GpsInfo gps2;

    String totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        get_result = (Button) findViewById(R.id.bt_api_cal);
        showResult = (EditText) findViewById(R.id.tv_data);

        final String[] items = {"알람 설정", "앱 설정", "기본 경로 설정"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        listView = (ListView) findViewById(R.id.drawer_menulist2);
        listView.setAdapter(adapter);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer2);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Intent alarm_intent = new Intent(getApplicationContext(), Alarm.class);
                        startActivity(alarm_intent);
                        break;
                    case 1:
                        Intent setting_intent = new Intent(getApplicationContext(), Setting.class);
                        startActivity(setting_intent);
                        break;
                    case 2:
                        Intent location_set_intent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(location_set_intent);
                        break;
                }
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        get_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ODsayService oDsayService = ODsayService.init(MainActivity.this, getString(R.string.odsay_key));
                oDsayService.setReadTimeout(5000);
                oDsayService.setConnectionTimeout(5000);

         OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
             @Override
             public void onSuccess(ODsayData oDsayData, API api) {
                 try {
                     if (api == API.SEARCH_PUB_TRANS_PATH) {
                         totalTime = oDsayData.getJson().getJSONObject("result").toString();
                         showResult.setText(totalTime);
                     }
                 }catch (JSONException e){
                     e.printStackTrace();
                 }
             }

             @Override
             public void onError(int i, String s, API api) {
            if(api == API.SEARCH_PUB_TRANS_PATH){
                Toast.makeText(getApplicationContext(),"오류가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
             }
         };

                SharedPreferences preferences = getSharedPreferences("default_location", MODE_PRIVATE);
                end_latitude = preferences.getString("default_lat", "0");
                end_longtitude = preferences.getString("default_lnt", "0");

                gps2 = new GpsInfo(MainActivity.this);
                if (gps2.isGetLocation()) {
                    start_latitude = Double.toString(gps2.getLatitude());
                    start_longtitude = Double.toString(gps2.getLongtitude());
                } else {
                    gps2.showSettingsAlert();
                }
            oDsayService.requestSearchPubTransPath("126.9841925534", "37.5725786462", "126.987124", "37.562474","0", "0", "0", onResultCallbackListener);

            }
        });

    }
    public void openDrawer() {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

}
