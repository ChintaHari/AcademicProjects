package com.example.symptommonitoringapplication.processing;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.symptommonitoringapplication.R;
import com.example.symptommonitoringapplication.calculations.HeartRate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VideoProcessing {
    public static Uri URI;
    private static boolean isVideoInProcess =false;
    public static Double uploadVideoAndCalculateHearRate(Context context){
        InputStream inputStream = context.getResources().openRawResource(R.raw.input);
        String fileName = "heart_rate.mp4";
        File location = new File(context.getFilesDir(), fileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(location);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        URI = Uri.fromFile(location);

        if (isVideoInProcess) {
            Toast.makeText(context, "Heart rate is getting caluclated...",
                    Toast.LENGTH_SHORT).show();
            return (double) -1;
        } else if (URI != null) {
            isVideoInProcess = true;
            return HeartRate.calculateHeartRate(extractFramesFromVideo(context, URI));
        } else {
            Toast.makeText(context, "There's a problem in the video or it hasn't recorded yet", Toast.LENGTH_SHORT).show();
            return (double) -1;
        }
    }

    private static List<Bitmap> extractFramesFromVideo(Context context, Uri videoUri) {
        List<Bitmap> frames = new ArrayList<>();

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, videoUri);

            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
            Log.i("duration",duration);

            int aduration = duration != null ? Integer.parseInt(duration) : 0;
            int i = 10;
            while (i < aduration) {
                Bitmap bitmap = retriever.getFrameAtIndex(i);
                if (bitmap != null) {
                    frames.add(bitmap);
                }
                i += 5;
            }
            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("total no of frames", "frames: "+frames.size());
        return frames;
    }

}

