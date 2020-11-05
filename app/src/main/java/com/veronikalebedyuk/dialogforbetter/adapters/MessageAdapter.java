package com.veronikalebedyuk.dialogforbetter.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.veronikalebedyuk.dialogforbetter.DatabaseHelper;
import com.veronikalebedyuk.dialogforbetter.FoodDatabaseHelper;
import com.veronikalebedyuk.dialogforbetter.R;
import com.veronikalebedyuk.dialogforbetter.UnwantedFoodDatabaseHelper;
import com.veronikalebedyuk.dialogforbetter.classes.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class MessageAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 0;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 1;
    private static final int VIEW_TYPE_CALENDAR_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_GRAPH_MESSAGE_RECEIVED = 3;
    private static final int VIEW_TYPE_CHOICE_MESSAGE_RECEIVED = 4;

    private boolean enable = true;
    private int visibility = 0;

    private List<Message> messages;
    private Context context;
    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messages = messageList;
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        String key;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.message_text);
            timeText = (TextView) itemView.findViewById(R.id.message_time);
            nameText = (TextView) itemView.findViewById(R.id.message_user);
        }

        void bind(Message message) {
            messageText.setText(message.getMessageText());
            timeText.setText(DateFormat.format("HH:mm", message.getMessageTime()));
            nameText.setText(message.getUsername());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.message_text_s);
            timeText = (TextView) itemView.findViewById(R.id.message_time_s);
            nameText = (TextView) itemView.findViewById(R.id.message_user_s);
        }

        void bind(Message message) {
            messageText.setText(message.getMessageText());
            timeText.setText(DateFormat.format("HH:mm", message.getMessageTime()));
            nameText.setText(message.getUsername());

        }
    }
    private class ChoiceMessageHolder extends RecyclerView.ViewHolder {
        TextView timeText, nameText;
        ListView messageText;
        Button btn;

        ChoiceMessageHolder(View itemView) {
            super(itemView);
            btn =(Button)itemView.findViewById(R.id.sbmBtn);
            messageText = (ListView) itemView.findViewById(R.id.message_choice);
            timeText = (TextView) itemView.findViewById(R.id.message_time_s);
            nameText = (TextView) itemView.findViewById(R.id.message_user_s);
        }

        void bind(Message message) {
            btn.setVisibility(visibility);
            btn.setEnabled(enable);
            messageText.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            List<String>foodOptions = new ArrayList<>();
            FoodDatabaseHelper foodDatabaseHelper = new FoodDatabaseHelper(context);
            UnwantedFoodDatabaseHelper unwantedFoodDatabaseHelper =new UnwantedFoodDatabaseHelper(context);
            int cnt = 0;
            Cursor cursor = foodDatabaseHelper.getData();
            while(cursor.moveToNext()){
                if(cursor.getString(1).toLowerCase().contains(message.messageText.toLowerCase())){
                    foodOptions.add(cursor.getString(1));
                    cnt++;
                }
            }
                String[] fo = new String[foodOptions.size()];
                foodOptions.toArray(fo);

            ArrayAdapter<String>adapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_multiple_choice, fo);
            messageText.setAdapter(adapter);
            timeText.setText(DateFormat.format("HH:mm", message.getMessageTime()));
            nameText.setText(message.getUsername());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SparseBooleanArray sbArray = messageText.getCheckedItemPositions();
                    for (int i = 0; i < sbArray.size(); i++) {
                        int key = sbArray.keyAt(i);
                        if (sbArray.get(key)){
                            Integer d = foodDatabaseHelper.deleteData(fo[key]);
                            unwantedFoodDatabaseHelper.addData(fo[key],-1);
                        }
                    }
                    btn.setVisibility(View.INVISIBLE);
                    btn.setEnabled(false);
                }
            });
        }
    }

    private class GraphMessageHolder extends RecyclerView.ViewHolder {
        TextView timeText, nameText;
        LineChart messageText;
        GraphMessageHolder(View itemView) {
            super(itemView);
            messageText = (LineChart) itemView.findViewById(R.id.message_graph);
            timeText = (TextView) itemView.findViewById(R.id.message_time_s);
            nameText = (TextView) itemView.findViewById(R.id.message_user_s);
        }



        void bind(Message message) {
            String criteria = message.getMessageText();

            long period_begin;
            long currentTIme = message.getMessageTime();
            long oneWeek =1000*60*60*24*7;
            long oneMonth =1000*60*60*24*30;
            long threeMonths =1000*60*60*24*30*3;
            long oneYear =1000*60*60*24*365;

            if(message.function.equalsIgnoreCase("graph0")){
                period_begin = currentTIme - oneWeek;
            }
            else if(message.function.equalsIgnoreCase("graph1")){
                period_begin = currentTIme - oneMonth;
            }
            else if(message.function.equalsIgnoreCase("graph2")){
                period_begin = currentTIme-threeMonths;
            }
            else{
                period_begin = currentTIme -oneYear;
            }
            List<Entry> entries = new ArrayList<Entry>();
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            Cursor cursor = databaseHelper.getData();
            while(cursor.moveToNext()){
                long date = cursor.getLong(1);
               if(cursor.getString(3).equalsIgnoreCase(criteria) && period_begin<=date && date <= currentTIme){
                   entries.add( new Entry(cursor.getLong(1),cursor.getLong(2)));
               }
            }
            LineDataSet dataSet = new LineDataSet(entries,message.messageText);
            dataSet.setColor(Color.BLACK);
            dataSet.setDrawCircles(true);

            LineData lineData = new LineData(dataSet);
            messageText.getXAxis().setDrawLabels(false);
            messageText.getXAxis().setDrawGridLines(false);
            messageText.setData(lineData);
            messageText.setDescription(null);
            messageText.setAutoScaleMinMaxEnabled(true);
            messageText.invalidate();
            timeText.setText(DateFormat.format("HH:mm", message.getMessageTime()));
            nameText.setText(message.getUsername());

        }
    }


    private class CalendarMessageHolder extends RecyclerView.ViewHolder {
        TextView timeText, nameText;
        com.applandeo.materialcalendarview.CalendarView messageText;
        CalendarMessageHolder(View itemView) {
            super(itemView);
            messageText = (com.applandeo.materialcalendarview.CalendarView) itemView.findViewById(R.id.calendar_message);
            timeText = (TextView) itemView.findViewById(R.id.calendar_message_time);
            nameText = (TextView) itemView.findViewById(R.id.calendar_user);
        }

        void bind(Message message) {
            timeText.setText(DateFormat.format("HH:mm", message.getMessageTime()));
            nameText.setText(message.getUsername());
            List<EventDay> events =  new ArrayList<>();
            List<String> notes =  new ArrayList<>();
            String key = context.getSharedPreferences("prefs",MODE_PRIVATE).getString("user", "TestUser");
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users").child(key).child("Events");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Calendar calendar = Calendar.getInstance();
                        int d, m, y;
                        d = ds.child("calendar").child("time").child("date").getValue(int.class);
                        m = ds.child("calendar").child("time").child("month").getValue(int.class);
                        y = ds.child("calendar").child("time").child("year").getValue(int.class);
                        calendar.set(y+1900, m, d);
                        //EventDay event = new EventDay(calendar,R.drawable.event_note);
                        String note = ds.child("note").getValue(String.class);
                        events.add(new EventDay(calendar,R.drawable.event_note));
                        notes.add(note);
                        messageText.setEvents(events);
                        Toast.makeText(context,ds.getKey(),Toast.LENGTH_LONG);
                    }
                    messageText.setEvents(events);
                    messageText.setOnDayClickListener(new OnDayClickListener() {
                        @Override
                        public void onDayClick(EventDay eventDay) {
                            String s = "нет событий";
                            for(int i = 0; i<events.size();i++)if(events.get(i).equals(eventDay))s=notes.get(i);
                            Toast t = Toast.makeText(context, s,Toast.LENGTH_SHORT);
                            t.setGravity(Gravity.CENTER, 0, 0);
                            t.show();
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            messageText.setEvents(events);

        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = (Message) messages.get(position);

        if (message.function.equals("request")) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else if (message.function.equals("string answer")){
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
        else if (message.function.equals("calendar answer")){
            // If some other user sent the message
            return VIEW_TYPE_CALENDAR_MESSAGE_RECEIVED;
        }
        else if (message.function.contains("graph")){
            // If some other user sent the message
            return VIEW_TYPE_GRAPH_MESSAGE_RECEIVED;
        }
        else if (message.function.contains("choice")){
            // If some other user sent the message
            if(position!=messages.size()-1){
                enable=false;
                visibility=View.INVISIBLE;
            }
            else{
                enable=true;
                visibility=View.VISIBLE;
            }
            return VIEW_TYPE_CHOICE_MESSAGE_RECEIVED;
        }

        return 0;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_message, parent, false);
            return new MessageAdapter.SentMessageHolder(view);
        }
        else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.system_message, parent, false);
            return new MessageAdapter.ReceivedMessageHolder(view);
        }
        else if (viewType == VIEW_TYPE_CALENDAR_MESSAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.system_calendar_message, parent, false);
            return new MessageAdapter.CalendarMessageHolder(view);
        }
        else if (viewType == VIEW_TYPE_GRAPH_MESSAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.system_graph_message, parent, false);
            return new MessageAdapter.GraphMessageHolder(view);
        }
        else if (viewType == VIEW_TYPE_CHOICE_MESSAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.system_choice_message, parent, false);
            return new MessageAdapter.ChoiceMessageHolder(view);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((MessageAdapter.SentMessageHolder) holder).bind(messages.get(position));
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((MessageAdapter.ReceivedMessageHolder) holder).bind(messages.get(position));
                break;
            case VIEW_TYPE_CALENDAR_MESSAGE_RECEIVED:
                ((MessageAdapter.CalendarMessageHolder) holder).bind(messages.get(position));
                break;
            case VIEW_TYPE_GRAPH_MESSAGE_RECEIVED:
                ((MessageAdapter.GraphMessageHolder) holder).bind(messages.get(position));
                break;
            case VIEW_TYPE_CHOICE_MESSAGE_RECEIVED:
                ((MessageAdapter.ChoiceMessageHolder) holder).bind(messages.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
