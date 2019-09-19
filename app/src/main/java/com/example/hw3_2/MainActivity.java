package com.example.hw3_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    TextView min;
    TextView max;
    TextView avg;
    TextView count;
    SeekBar drag;
    Button generate;
    ProgressDialog progressDialog;
    int step = 1;
    int maxvalue = 10;
    int minValue = 0;
    int complexityValue = 0;
    double minDisplay = 0;
    double maxDisplay = 0;
    double avgDisplay = 0;
    ArrayList<Double> arrList;
    ExecutorService threadPool;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        min = findViewById(R.id.textViewMinDisplay);
        max = findViewById(R.id.textViewMaxDisplay);
        avg = findViewById(R.id.textViewAvgDisplay);
        count = findViewById(R.id.textViewCount);
        drag = findViewById(R.id.seekBar);
        drag.setMax((maxvalue - minValue) / step);
        generate = findViewById(R.id.buttonGenerate);
        threadPool = Executors.newFixedThreadPool(2);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                minDisplay = message.getData().getDouble(DoWork.FINAL_MIN);
                maxDisplay = message.getData().getDouble(DoWork.FINAL_MAX);
                avgDisplay = message.getData().getDouble(DoWork.FINAL_AVG);
                Log.d("demo",minDisplay+"");
                Log.d("demo",maxDisplay+"");
                Log.d("demo",avgDisplay+"");
                return false;
            }
        });
        drag.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                complexityValue = minValue + (i * 1);
                count.setText(complexityValue + " Times");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("demo", complexityValue + "");
                arrList = HeavyWork.getArrayNumbers(complexityValue);
                if (complexityValue > 0) {
                    threadPool.execute(new DoWork());
                } else {
                    Toast.makeText(MainActivity.this, "Please drag seekbar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class DoWork implements Runnable {
        static final  String FINAL_MIN = "MIN";
        static final  String FINAL_MAX = "MAX";
        static final  String FINAL_AVG = "AVG";
        @Override
        public void run() {
            Message message = new Message();
            arrList = HeavyWork.getArrayNumbers(complexityValue);
            double min = Collections.min(arrList);
            double max = Collections.max(arrList);

            double sum =0;
            for(int i=0 ;i< arrList.size();i++) {
//                publishProgress(i);
                sum += arrList.get(i);
            }
            double avg = sum /arrList.size();
            Bundle bundle = new Bundle();
            bundle.putDouble(FINAL_MIN, min);
            bundle.putDouble(FINAL_MAX, max);
            bundle.putDouble(FINAL_AVG, avg);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
}
