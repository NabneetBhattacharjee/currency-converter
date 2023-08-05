package com.example.nabneetutilityapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    Handler handler;
    private final int DELAY_TIME = 1000;
    double convertedAmount;
    private ExchangeRateTask rateTask;
    //private ShowWeatherInfo weatherinfo;
    String[] fromCurrencies;
    String[] toCurrencies;
    //String[] cities;
    int index = 0;
    //int city_index = 0;
    //ArrayList<String> timezones = new ArrayList<String>();
    //ArrayList<String> temperatures = new ArrayList<String>();
    //ArrayList<String> weathers = new ArrayList<String>();
    private Boolean got_Data=false;

    @SuppressLint("StaticFieldLeak")
    class ExchangeRateTask extends AsyncTask<String,Void,Boolean>{
        @Override
        protected Boolean doInBackground(String... strings) {
            fromCurrencies = getResources().getStringArray(R.array.currencies);
            String rateApiUrl=MyUtilityModel.RATE_API_CORE + fromCurrencies[index];
            String rateInfo=Helper.getInfo(rateApiUrl);
            if(rateInfo!=null){
                try {
                    JSONObject jsonObject = new JSONObject(rateInfo);
                    JSONObject rateObject = jsonObject.getJSONObject("rates");
                    for(int i=0; i<MyUtilityModel.rates.length; i++){
                        MyUtilityModel.rates[i]=rateObject.getDouble(toCurrencies[i]);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean gotData) {
            //updating the screen
            if(gotData) {
                for (int i=0;i<MyUtilityModel.rates.length;i++) {
                    Log.i("rates", String.valueOf(MyUtilityModel.rates[i]));
                }
                got_Data = gotData;
            }

            super.onPostExecute(gotData);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode==RESULT_OK){
                if(data!=null){
                    Bundle bundle = data.getExtras();
                    if(bundle!=null){
                        index = bundle.getInt("position",0);
                        fromCurrencies = getResources().getStringArray(R.array.currencies);
                        rateTask = (ExchangeRateTask)new ExchangeRateTask().execute(fromCurrencies);
                    }
                }
            }
        }
    }
   /* class ShowWeatherInfo extends AsyncTask<String,Void,Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
                    cities = getResources().getStringArray(R.array.cities);
                    String weatherApiUrl = MyUtilityModel.WEATHER_API_CORE + cities[city_index];
                    String weatherInfo = Helper.getInfo(weatherApiUrl);
                    if (weatherInfo != null) {
                        try {
                            JSONObject weatherObject = new JSONObject(weatherInfo);
                            JSONObject Object = weatherObject.getJSONObject("location");
                            JSONArray weatherArray = Object.getJSONArray("observation");
                            for (int i = 0; i < weatherArray.length(); i++) {
                                JSONObject info = weatherArray.getJSONObject(i);
                                String timeZone=info.getString("utcName").toString();
                                timezones.add(timeZone);
                                String temp = info.getString("temperature").toString();
                                temperatures.add(temp);
                                String weather = info.getString("temperatureDesc").toString();
                                weathers.add(weather);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean got_info) {
            super.onPostExecute(got_info);
        }
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toCurrencies = getResources().getStringArray(R.array.currencies);
        EditText fromAmount = findViewById(R.id.fromAmount);
        TextView toAmount=findViewById(R.id.toAmount);
        Spinner to_menu = (Spinner)findViewById(R.id.toMenu);
        //Spinner citySelector = (Spinner)findViewById(R.id.citySelector);
        //TextView timeZoneView=(TextView)findViewById(R.id.timeZoneView);
        //TextView tempView = (TextView)findViewById(R.id.tempView);
        //TextView weatherView = (TextView)findViewById(R.id.weatherView);

        to_menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String fromAmountString = fromAmount.getText().toString();
                if(fromAmountString.length() == 0){
                    Toast.makeText(MainActivity.this,"Enter a value",Toast.LENGTH_SHORT).show();
                }
                else{
                    int toCurrencyIndex = (int)to_menu.getSelectedItemId();
                    if(got_Data){
                        double fromAmountDouble = Double.parseDouble(fromAmount.getText().toString());
                        convertedAmount = fromAmountDouble * MyUtilityModel.rates[toCurrencyIndex];
                        toAmount.setText(Double.toString(convertedAmount));
                    }
                    }
                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        /*citySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String timeZone = timezones.get(position);
                timeZoneView.setText(timeZone);
                String temp = temperatures.get(position);
                tempView.setText(temp);
                String weather = weathers.get(position);
                weatherView.setText(weather);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        handler = new Handler();
        //display current time
        updateTime();
        rateTask = (ExchangeRateTask)new ExchangeRateTask().execute(fromCurrencies);
        //weatherinfo = (ShowWeatherInfo)new ShowWeatherInfo().execute(cities);
    }

    private void updateTime() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                String currentTime = Helper.getCurrentTime();
                TextView timeView = findViewById(R.id.timeView);
                timeView.setText(currentTime);
                handler.postDelayed(this,DELAY_TIME);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.setting:
                Intent intent = new Intent(this,Setting.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}