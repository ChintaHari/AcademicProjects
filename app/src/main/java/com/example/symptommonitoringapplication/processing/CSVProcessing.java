package com.example.symptommonitoringapplication.processing;

import static com.example.symptommonitoringapplication.Symptoms.symptomRatings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.symptommonitoringapplication.constants.Constants;
import com.example.symptommonitoringapplication.R;
import com.example.symptommonitoringapplication.calculations.RespiratoryRate;
import io.github.pixee.security.BoundedLineReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVProcessing {
    public static void uploadCSVandCalculateRespiratoryRate(Context context, int requestCode, int resultCode, Intent resultData) {
        ProgressBar spinner = ((Activity) context).findViewById(R.id.spinner);
        Double respiratoryRate;
        TextView resultTextView = ((Activity) context).findViewById(R.id.respiratoryRateResult);
        if (requestCode == Constants.REQUEST_CODE_CSV && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                if (uri != null) {
                    try {
                        List<Float> csvValues = readCSV(context, uri);
                        respiratoryRate = RespiratoryRate.
                                calculateRespiratoryRate(csvValues);
                        symptomRatings.put("Respiratory Rate", respiratoryRate);
                        spinner.setVisibility(View.GONE);
                        resultTextView.setVisibility(View.VISIBLE);
                        resultTextView.setText("Respiratory Rate: "+respiratoryRate.toString()+" BPM");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private static List<Float> readCSV(Context context, Uri uri) throws IOException {
        List<Float> csvValues = new ArrayList<>();
        InputStream csvFile = context.getContentResolver().openInputStream(uri);
        InputStreamReader inputStreamReader = new InputStreamReader(csvFile);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = BoundedLineReader.readLine(bufferedReader, 5_000_000)) != null) {
            try {
                float value = Float.parseFloat(line);
                csvValues.add(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return csvValues;
    }
}
