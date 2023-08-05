package com.example.nabneetutilityapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class Setting extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Spinner selector = (Spinner)findViewById(R.id.selector);
        selector.setSelection(0,false);
        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                position = selector.getSelectedItemPosition();
                intent.putExtra("position",position);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}