package com.example.symptommonitoringapplication;

import static com.example.symptommonitoringapplication.Symptoms.symptomRatings;
import static com.example.symptommonitoringapplication.constants.Constants.REQUEST_CODE_CSV;
import static com.example.symptommonitoringapplication.constants.Constants.LATEST_ROW_ID;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.symptommonitoringapplication.database.DatabaseHelper;
import com.example.symptommonitoringapplication.processing.CSVProcessing;
import com.example.symptommonitoringapplication.processing.VideoProcessing;

public class MainActivity extends AppCompatActivity {

    public DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar respiratoryRateSpinner = findViewById(R.id.spinner);
        TextView respiratoryRateTextView = findViewById(R.id.respiratoryRateResult);

        ProgressBar heartRateSpinner = findViewById(R.id.heartRateSpinner);
        TextView heartRateTextView = findViewById(R.id.heartRateResult);

        databaseHelper = new DatabaseHelper(this);

        findViewById(R.id.measureRespiratoryRate).setOnClickListener(view -> {
            Toast.makeText(MainActivity.this,"Respiratory rate calculation in progress....", Toast.LENGTH_LONG).show();
            uploadCSVandCalculateRespiratoryRate(respiratoryRateSpinner, respiratoryRateTextView);
        });

        findViewById(R.id.measureHeartRate).setOnClickListener(view -> {
            Toast.makeText(MainActivity.this,"Heart rate calculation in progress. Please wait", Toast.LENGTH_LONG).show();
            uploadVideoAndCalculateHeartRate(heartRateSpinner, heartRateTextView);
        });


        findViewById(R.id.symptoms).setOnClickListener(view -> {
            if ((LATEST_ROW_ID > 0) && (symptomRatings.containsKey("Heart Rate")
                    || symptomRatings.containsKey("Respiratory Rate"))) {
                Intent intent = new Intent(view.getContext(), Symptoms.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(view.getContext(), "Please upload values for Heart Rate or Respiratory Rate to DB first.", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.uploadSigns).setOnClickListener(view -> {
            if((symptomRatings.containsKey("Heart Rate") || symptomRatings.containsKey("Respiratory Rate"))){
                LATEST_ROW_ID = (int) databaseHelper.insertRRandHRValues();
                if (LATEST_ROW_ID != -1)
                    Toast.makeText(MainActivity.this, "Data Stored", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "Data Not Stored", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(MainActivity.this, "Please select to compute either Heart Rate or Respiratory Rate first", Toast.LENGTH_LONG).show();
        });
    }

    private void uploadCSVandCalculateRespiratoryRate(ProgressBar spinner, TextView resultTextView) {
        spinner.setVisibility(View.VISIBLE);
        resultTextView.setVisibility(View.GONE);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");

        startActivityForResult(intent, REQUEST_CODE_CSV);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        CSVProcessing.uploadCSVandCalculateRespiratoryRate(this, requestCode, resultCode, resultData);
    }

    private void uploadVideoAndCalculateHeartRate(ProgressBar heartRateSpinner, TextView heartRateTextView) {
        Double hearRate = VideoProcessing.uploadVideoAndCalculateHearRate(this);

        if (hearRate == -1) {
            Toast.makeText(MainActivity.this,"Something went wrong while calculating. Please try again", Toast.LENGTH_LONG).show();
        }
        else {
            symptomRatings.put("Heart Rate", hearRate);
            heartRateTextView.setVisibility(View.VISIBLE);
            heartRateTextView.setText("Heart Rate: " + hearRate + " BPM");
        }
    }
}
