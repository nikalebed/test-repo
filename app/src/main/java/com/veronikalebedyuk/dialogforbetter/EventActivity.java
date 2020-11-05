package com.veronikalebedyuk.dialogforbetter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.veronikalebedyuk.dialogforbetter.App.CHANNEL_1_ID;

public class EventActivity extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        notificationManager = NotificationManagerCompat.from(this);

        long eventDate = getSharedPreferences("prefs",MODE_PRIVATE).getLong("date_value", 0);
        String date = getSharedPreferences("prefs",MODE_PRIVATE).getString("date", "01.01.01");
        String key = getSharedPreferences("prefs",MODE_PRIVATE).getString("key", "");
        String note = getSharedPreferences("prefs",MODE_PRIVATE).getString("note", "");
        TextView textView = findViewById(R.id.note_date);
        textView.setText(date);

        String userID = getSharedPreferences("prefs",MODE_PRIVATE).getString("user", "TestUser");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(userID).child("Events");

        EditText editText = findViewById(R.id.note_text);
        editText.setText(note);
        ImageButton btn = findViewById(R.id.btnAddNote);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Toast.makeText(this, sdf.format(new Date(eventDate)),Toast.LENGTH_LONG).show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(editText.getText().toString().trim().equals("")){
                        myRef.child(key).setValue(null);
                    }
                    else {
                        myRef.child(key).child("note").setValue(editText.getText().toString());
                        Intent intent = new Intent (EventActivity.this,BroadcastNotification.class);
                        PendingIntent pendingIntent =  PendingIntent.getBroadcast(EventActivity.this,0,intent,0);
                        AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                        long currentTIme = System.currentTimeMillis();
                        long oneDay =1000*60*60*24;
                        long oneWeek =1000*60*60*24*7;
                        long oneMonth =1000*60*60*24*30;
                        alarmManager1.set(AlarmManager.RTC_WAKEUP,currentTIme, pendingIntent);

                        intent = new Intent (EventActivity.this,BroadcastReminder.class);
                        pendingIntent =  PendingIntent.getBroadcast(EventActivity.this,0,intent,0);
                        AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);
                        AlarmManager alarmManager3 = (AlarmManager) getSystemService(ALARM_SERVICE);
                        AlarmManager alarmManager4 = (AlarmManager) getSystemService(ALARM_SERVICE);
                        if(eventDate-oneDay>currentTIme)alarmManager2.set(AlarmManager.RTC_WAKEUP,eventDate-oneDay, pendingIntent);
                        if(eventDate-oneWeek>currentTIme)alarmManager3.set(AlarmManager.RTC_WAKEUP,eventDate-oneWeek, pendingIntent);
                        if(eventDate-oneMonth>currentTIme)alarmManager4.set(AlarmManager.RTC_WAKEUP,eventDate-oneMonth, pendingIntent);
                    }
                    Intent i = new Intent(EventActivity.this, CalendarActivity.class);
                    startActivity(i);
            }
        });

    }

}