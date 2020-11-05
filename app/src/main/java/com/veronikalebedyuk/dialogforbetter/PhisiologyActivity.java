package com.veronikalebedyuk.dialogforbetter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.veronikalebedyuk.dialogforbetter.classes.MedicalCritera;
import com.veronikalebedyuk.dialogforbetter.classes.Message;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

public class PhisiologyActivity extends AppCompatActivity {
    EditText height;
    EditText weight;
    EditText sdi;
    ListView insulineTypes;
    DatePicker dateOfB;
    Toolbar toolbar;
    CollapsingToolbarLayout toolBarLayout;
    DatabaseReference r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phisiology);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        setSupportActionBar(toolbar);
        toolBarLayout.setMaxLines(4);
        toolBarLayout.setTitle(getTitle());
        String[] hints = getResources().getStringArray(R.array.MediaclCriteriasHints);
        insulineTypes = findViewById(R.id.insulineTypeList);
        insulineTypes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                PhisiologyActivity.this, R.array.InsulineType, android.R.layout.simple_list_item_multiple_choice);
        insulineTypes.setAdapter(adapter1);

        ListView lv = findViewById(R.id.medCriteriaChoice);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.AdditionalMediaclCriterias, android.R.layout.simple_list_item_multiple_choice);
        lv.setAdapter(adapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Toast t = Toast.makeText(PhisiologyActivity.this,hints[position],Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                return true;
            }
        });


        String key = getSharedPreferences("prefs",MODE_PRIVATE).getString("user", "TestUser");
        r = FirebaseDatabase.getInstance().getReference("Users").child(key);
        height =findViewById(R.id.enterHeight);
        weight =findViewById(R.id.enterWeight);
        sdi = findViewById(R.id.enterSDI);
        dateOfB =findViewById(R.id.dateOfBirthPicker);
        Button btn = findViewById(R.id.btnContinue);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast t = Toast.makeText(PhisiologyActivity.this, "пожалуйста, все поля", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                if(noEmpties()) {
                    addToDatabase();
                    MedicalCritera[]criterias = getCriteria();
                    SparseBooleanArray sbArray = lv.getCheckedItemPositions();
                    for (int i = 0; i < sbArray.size(); i++) {
                        int key = sbArray.keyAt(i);
                        if (sbArray.get(key)) {
                            r.child("MedCriterias").push().setValue(criterias[key]);
                        }
                    }

                    int type;
                    if (insulineTypes.getCheckedItemCount() == 2) type = 2;
                    else type = insulineTypes.getCheckedItemPosition();

                    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    prefs.edit().putInt("height", Integer.parseInt(height.getText().toString())).apply();
                    prefs.edit().putInt("weight",  Integer.parseInt(weight.getText().toString())).apply();
                    prefs.edit().putInt("insulineType", type).apply();
                    prefs.edit().putInt("SDI", Integer.parseInt(sdi.getText().toString())).apply();
                    Intent i = new Intent(PhisiologyActivity.this, MainActivity.class);
                    SharedPreferences firstOpenPrefs = getSharedPreferences("prefs",MODE_PRIVATE);
                    firstOpenPrefs.edit().putBoolean("first open", false).apply();
                    FirebaseDatabase.getInstance().getReference("Users").child(key).child("Messages").push().setValue(
                            new Message("DIAlog", getResources().getString(R.string.first_message), "string answer"));
                    startActivity(i);
                }else t.show();
            }
        });
    }

    private void addToDatabase() {
        r.child("height").setValue(Integer.parseInt(height.getText().toString()));
        r.child("weight").setValue(Integer.parseInt(weight.getText().toString()));
        r.child("dateOfBirth").child("day").setValue(dateOfB.getDayOfMonth());
        r.child("dateOfBirth").child("month").setValue(dateOfB.getMonth());
        r.child("dateOfBirth").child("year").setValue(dateOfB.getYear());
        r.child("MedCriterias").push().setValue(new MedicalCritera("сахар", 7, 2, 3, 11));

    }

    public boolean noEmpties(){
        if(height.getText().toString().trim().equals("") ||
                weight.getText().toString().trim().equals("")||
                sdi.getText().toString().trim().equals("")
        )return false;
        return true;
    }
    public MedicalCritera[] getCriteria(){
        double h = Integer.parseInt(height.getText().toString()) * 0.01;
        return new MedicalCritera[]{
        new MedicalCritera("систолическое давление ",130,10,80,170),
                new MedicalCritera("диастолическое давление ",80,3,70,170),
                new MedicalCritera("холестерин",3,1,2,6),
                new MedicalCritera("вес",22 * h*h,10,18 * h*h,50 * h*h),
                new MedicalCritera("острота зрения",1, 0.2,0.09,100)};

    }
}