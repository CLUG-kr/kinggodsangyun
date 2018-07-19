package com.example.ocksangyun.map_fix;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class Setting extends PreferenceActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preference);

        final List<String> ListItems = new ArrayList<>();
        ListItems.add("5분");
        ListItems.add("10분");
        ListItems.add("20분");
        ListItems.add("사용자 설정");

    }

}