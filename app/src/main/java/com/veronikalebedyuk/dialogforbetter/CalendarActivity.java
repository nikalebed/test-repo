package com.veronikalebedyuk.dialogforbetter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.applandeo.materialcalendarview.CalendarUtils;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.veronikalebedyuk.dialogforbetter.adapters.MessageAdapter;
import com.veronikalebedyuk.dialogforbetter.classes.Message;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private RecyclerView rv;
    private CalendarView calendarView;
    private List<Message> messages;
    private MessageAdapter adapter;
    private List<EventNote> eventNotes;
    private List<EventDay> events;
    private List<String> keys;
    private ImageButton btn;
    private boolean CAN_EDIT;
    private boolean IS_EVENT_NEW;
    private int SELECTED_DATE_POSITION;
    EventDay eventPicked;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.YY");

        CAN_EDIT =false;
        eventNotes = new ArrayList<>();
        events =  new ArrayList<>();
        keys =  new ArrayList<>();
        calendarView = (CalendarView)findViewById(R.id.datePickCalendar);
        String userID = getSharedPreferences("prefs",MODE_PRIVATE).getString("user", "TestUser");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(userID).child("Events");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Calendar calendar = Calendar.getInstance();
                    keys.add(ds.getKey());
                    int d, m, y;
                    d = ds.child("calendar").child("time").child("date").getValue(int.class);
                    m = ds.child("calendar").child("time").child("month").getValue(int.class);
                    y = ds.child("calendar").child("time").child("year").getValue(int.class);
                    calendar.set(y+1900, m, d);
                    //EventDay event = new EventDay(calendar,R.drawable.event_note);
                    String note = ds.child("note").getValue(String.class);
                    events.add(new EventDay(calendar,R.drawable.event_note));
                    eventNotes.add(new EventNote(calendar,note));
                    calendarView.setEvents(events);
                    Toast.makeText(CalendarActivity.this,ds.getKey(),Toast.LENGTH_LONG);
                }
                calendarView.setEvents(events);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        messages = new ArrayList<>();
        rv = (RecyclerView) findViewById(R.id.calendar_helper_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        adapter = new MessageAdapter(this, messages);
        rv.setAdapter(adapter);
        messages.add(new Message("DIAlog", "выберите дату", "string answer"));
        calendarView = (CalendarView)findViewById(R.id.datePickCalendar);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                eventPicked = eventDay;
                int cnt=0;
                for(int i = 0; i<events.size(); i++){
                    if(events.get(i).equals(eventDay)){
                        messages.add( new Message("DIAlog",
                                "вы выбрали дату " + dateFormat.format(eventDay.getCalendar().getTime()) +"\n\nСОБЫТИЕ: " + eventNotes.get(i).getNote() +
                                        "\nнажмите на кнопку в правом нижнем углу экрана, чтобы изменить",
                                "string answer"));
                        IS_EVENT_NEW = false;
                        SELECTED_DATE_POSITION = i;
                        break;
                    }
                    cnt++;
                }
                 if(cnt==events.size()) {
                    messages.add(new Message("DIAlog",
                            "вы выбрали дату " + dateFormat.format(eventDay.getCalendar().getTime())+"\n\nНЕТ СОБЫТИЙ,\nнажмите на кнопку внизу экрана, чтобы добавить",
                            "string answer"));
                    IS_EVENT_NEW = true;
                }
                adapter.notifyDataSetChanged();
                linearLayoutManager.smoothScrollToPosition(rv,null,adapter.getItemCount());
                CAN_EDIT = true;
            }
        });

        btn = (ImageButton)findViewById(R.id.btnEditCalendar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CAN_EDIT){
                    //events.add(eventPicked);
                    Intent i = new Intent(CalendarActivity.this, EventActivity.class);
                    SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
                    String s = dateFormat.format(eventPicked.getCalendar().getTime());
                    prefs.edit().putLong("date_value",eventPicked.getCalendar().getTime().getTime()).apply();
                    prefs.edit().putString("date", s).apply();

                    if(IS_EVENT_NEW){
                        String key = myRef.push().getKey();
                        myRef.child(key).setValue(new EventNote(eventPicked.getCalendar(),"tempNote"));
                        prefs.edit().putString("key",key).apply();
                        prefs.edit().putString("note","").apply();
                    }
                    else{
                        prefs.edit().putString("key", keys.get(SELECTED_DATE_POSITION)).apply();
                        prefs.edit().putString("note", eventNotes.get(SELECTED_DATE_POSITION).note).apply();
                    }
                    prefs.edit().putLong("long date",eventPicked.getCalendar().getTimeInMillis()).apply();
                    startActivity(i);
                }
                else{
                    messages.add(new Message("DIAlog", "пожалуйста, выберите дату :)", "string answer"));
                    adapter.notifyDataSetChanged();
                    linearLayoutManager.smoothScrollToPosition(rv,null,messages.size());
                }
            }
        });

        ImageButton btnBack = findViewById(R.id.backToChat);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //database.getReference("Users").child(userID).child("Messages").push().setValue(new Message("DIAlog","в календарь внесены изменения","string answer"));
                Intent i = new Intent(CalendarActivity.this,MainActivity.class);
                startActivity(i);
            }
        });


    }

    class EventNote{
        private Calendar calendar;
        private String note;

        public EventNote(Calendar calendar, String note) {
            this.calendar = calendar;
            this.note = note;
        }

        public Calendar getCalendar() {
            return calendar;
        }

        public String getNote() {
            return note;
        }
    }

}