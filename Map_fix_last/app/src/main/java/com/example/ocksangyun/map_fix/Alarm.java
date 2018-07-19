package com.example.ocksangyun.map_fix;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import java.util.Calendar;
import java.util.Timer;

public class Alarm extends AppCompatActivity {
    public TextView textView;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        textView = findViewById(R.id.tvBclock);
        ImageButton button = (ImageButton) findViewById(R.id.timer_power);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Thread thread = new Thread() {

                    @Override
                    public void run() {
                        while (!isInterrupted()) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Calendar calendar = Calendar.getInstance();
                                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                    int minute = calendar.get(Calendar.MINUTE);
                                    int second = calendar.get(Calendar.SECOND);
                                    int lastminute = 30;//막차시간을 임의로 설정한 부분임.
                                    int lasthour = 23;

                                    if(lasthour - hour >= 0){
                                        if(lastminute - minute - 1 >= 0)
                                        {
                                            time = ((lasthour - hour) + "시 " +(lastminute - minute - 1) + "분" + (59 - second) + "초 \n");
                                        }
                                        else
                                        {
                                            time = ((lasthour - hour - 1)+"시"+(lastminute + 60 - minute - 1) + "분" + (59-second) + "초 \n");
                                        }
                                    }
                                    else
                                    {
                                        if(lastminute - minute - 1 >= 0)
                                        {
                                            time = ((24 + lasthour - hour) + "시 " +(lastminute - minute - 1) + "분" + (59 - second) + "초 \n");}
                                        else
                                        {
                                            time = ((24 + lasthour - hour - 1)+"시"+(lastminute + 60 - minute - 1) + "분" + (59-second) + "초 \n");
                                        }

                                    }
                                    textView.setText(time);
                                }
                            });

                            try {
                                Thread.sleep(1000);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                thread.start();

            }
        });

    }

}