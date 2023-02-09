package com.example.knowyourage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button findDob_btn,calculate_btn,btnAddData, btnViewData ;
    TextView today_txt,dob_txt,age_txt;
    EditText name_txt;
    String mbirthday,mtoday;
    DatePickerDialog.OnDateSetListener dateSetListener;

    DatabaseHelper AgeDB;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AgeDB = new DatabaseHelper(this);
        name_txt = (EditText) findViewById(R.id.name);
        today_txt= (TextView) findViewById(R.id.dateText);
        dob_txt= (TextView) findViewById(R.id.dobText);
        age_txt= (TextView) findViewById(R.id.ageText);

        findDob_btn= (Button) findViewById(R.id.dobBtn);
        calculate_btn= (Button) findViewById(R.id.calbtn);
        btnAddData = (Button) findViewById(R.id.btnAddData);
        btnViewData =(Button) findViewById(R.id.btnViewData);


        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

       SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
       mtoday=simpleDateFormat.format(Calendar.getInstance().getTime());
       today_txt.setText("Today : "+mtoday);

       findDob_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),dateSetListener,year,month,day);
               datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
               datePickerDialog.show();
           }
       });
       dateSetListener=new DatePickerDialog.OnDateSetListener() {
           @Override
           public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month=month+1;
                mbirthday=dayOfMonth + "/"+month+"/"+year;
                dob_txt.setText("Birthday: "+mbirthday);
           }
       };
       calculate_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (mbirthday==null) {
                   Toast.makeText(getApplicationContext(),"please enter your date of birth",
                           Toast.LENGTH_SHORT).show();
               }else {
                   SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
                   try {
                       Date date1 = simpleDateFormat1.parse(mbirthday);
                       Date date2 = simpleDateFormat1.parse(mtoday);

                       long startDate = date1.getTime();
                       long endDate = date2.getTime();

                       Period period = new Period(startDate, endDate, PeriodType.yearMonthDay());
                       int years = period.getYears();
                       int months = period.getMonths();
                       int days = period.getDays();

                       age_txt.setText(years + "years |" + months + "months |" + days + "days");

                   } catch (ParseException e) {
                       throw new RuntimeException(e);
                   }
               }

           }
       });
        AddData();
        ViewData();


    }
    public void AddData()
    {
        btnAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name =name_txt.getText().toString();
                String age = age_txt.getText().toString();

                boolean insertData = AgeDB.addData(name,age);
                if(insertData==true)
                {
                    Toast.makeText(MainActivity.this, "Data successfully inserted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void ViewData()
    {
        btnViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor data = AgeDB.showData();

                if(data.getCount() == 0)
                {
                    display("Error", "No Data Found");
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while(data.moveToNext())
                {
                    buffer.append("ID: "+data.getString(0)+ "\n");
                    buffer.append("Name: "+ data.getString(1)+"\n");
                    buffer.append("Age: "+ data.getString(2)+"\n");


                }
                display("All stored data: ", buffer.toString());
            }
        });
    }
    public void display(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


    }


