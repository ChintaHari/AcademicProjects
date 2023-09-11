package com.example.symptommonitoringapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.symptommonitoringapplication.database.DatabaseHelper;
import com.example.symptommonitoringapplication.database.SymptomRating;

import java.util.HashMap;

public class Symptoms extends AppCompatActivity {
    private Spinner symptomsSpinner;
    private RatingBar ratingBar;
    private DatabaseHelper databaseHelper;
    public static HashMap<String, Double> symptomRatings = new HashMap<>();
    private String currentSelectedSymptom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        displaySymptomsDropdownAndRatingBar();
        handleSymptomChangesToHashMap();

        findViewById(R.id.uploadButton).setOnClickListener(view -> updateDBwithSymptomValues());

        findViewById(R.id.deleteAllButton).setOnClickListener(view -> deleteSymptomsData());
    }

    private void deleteSymptomsData() {
        try{
            databaseHelper.deleteAllRows();
        }catch (Exception e){
            Log.e("Exception occurred: ", "Exception", e);
        }finally {
            Toast.makeText(Symptoms.this, "Data Deleted", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDBwithSymptomValues() {
        if (isDataUpdated())
            Toast.makeText(Symptoms.this, "Data Updated", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(Symptoms.this, "Data Not Updated", Toast.LENGTH_SHORT).show();
    }

    private boolean isDataUpdated(){
        SymptomRating symptomRating = new SymptomRating();

        symptomRating.setNausea(symptomRatings.get("Nausea"));
        symptomRating.setHeadache(symptomRatings.get("Headache"));
        symptomRating.setDiarrhea(symptomRatings.get("Diarrhea"));
        symptomRating.setSoreThroat(symptomRatings.get("Sore Throat"));
        symptomRating.setFever(symptomRatings.get("Fever"));
        symptomRating.setMuscleAche(symptomRatings.get("Muscle Ache"));
        symptomRating.setLossOfSmellTaste(symptomRatings.get("Loss of Smell or Taste"));
        symptomRating.setCough(symptomRatings.get("Cough"));
        symptomRating.setBreathDifficulty(symptomRatings.get("Difficulty Breathing"));
        symptomRating.setFeelingTired(symptomRatings.get("Feeling Tired"));

        return databaseHelper.updateSymptomRating(symptomRating);
    }

    private void handleSymptomChangesToHashMap() {
        String[] symptoms = getResources().getStringArray(R.array.symptom_options);
        for (String symptom : symptoms) {
            if (!symptomRatings.containsKey(symptom)) {
                symptomRatings.put(symptom, 0.0);
            }
        }

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (currentSelectedSymptom != null) {
                symptomRatings.put(currentSelectedSymptom, (double) rating);
            }
        });

        symptomsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentSelectedSymptom = parentView.getItemAtPosition(position).toString();
                Double previousRating = symptomRatings.get(currentSelectedSymptom);
                if (previousRating != null) {
                    ratingBar.setRating(previousRating.floatValue());
                } else {
                    ratingBar.setRating(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void displaySymptomsDropdownAndRatingBar() {
        symptomsSpinner = findViewById(R.id.symptomsSpinner);
        ratingBar = findViewById(R.id.ratingBar);
        databaseHelper = new DatabaseHelper(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.symptom_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        symptomsSpinner.setAdapter(adapter);
    }
}