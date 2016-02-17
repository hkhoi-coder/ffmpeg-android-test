package com.example.hkhoi.ffmpeg_demo_1;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EditText cmdTextView;
    private TextView consoleTextView;
    private final String LOG_TAG = "debug_MainActivity";
    private FFmpeg ffmpeg;
    private FFmpegLoadBinaryResponseHandler fFmpegLoadBinaryResponseHandler;
    private FFmpegExecuteResponseHandler fFmpegExecuteResponseHandler;
    private String workingPath;
    private String command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        try {
            initializeFFmpeg();
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "---Something went wrong at initializeFFmpeg(): " + e.getMessage());
        }


    }

    private void executeFFmpeg(String cmd) {
        try {
            ffmpeg.execute(cmd, fFmpegExecuteResponseHandler);
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "---ERROR: FFmpeg is running" + e.getMessage());
        }
    }

    private void initializeFFmpeg() throws FFmpegNotSupportedException {
        ffmpeg.loadBinary(fFmpegLoadBinaryResponseHandler);
    }

    private void initComponents() {
        ffmpeg = FFmpeg.getInstance(this);
        workingPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        fFmpegExecuteResponseHandler = new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
                consoleTextView.append(message + "\n");
                Log.d(LOG_TAG, "---Execute: Success!");
            }

            @Override
            public void onProgress(String message) {
                consoleTextView.append(message + "\n");
                Log.d(LOG_TAG, "---Execute: Progress: " + message);
            }

            @Override
            public void onFailure(String message) {
                consoleTextView.append(message + "\n");
                Log.d(LOG_TAG, "---Execute: FAILED: " + message);
            }

            @Override
            public void onStart() {
                Log.d(LOG_TAG, "---Execute: Starting");
            }

            @Override
            public void onFinish() {
                Log.d(LOG_TAG, "---ExecuteL Finishing");
            }
        };
        fFmpegLoadBinaryResponseHandler = new FFmpegLoadBinaryResponseHandler() {
            @Override
            public void onFailure() {
                Log.d(LOG_TAG, "---Response: FAILED");
            }

            @Override
            public void onSuccess() {
                Log.d(LOG_TAG, "---Response: Success");
            }

            @Override
            public void onStart() {
                Log.d(LOG_TAG, "---Response: Starting");
            }

            @Override
            public void onFinish() {
                Log.d(LOG_TAG, "---Response: Finishing");
            }
        };

        String audio = workingPath.concat("/audio.mp3");
        String video = workingPath.concat("/video.mp4");
        String output = workingPath.concat("/output.mp4");

        StringBuilder cmdBuilder = new StringBuilder();
        cmdBuilder.append("-i ")
                .append(video)
                .append(" -i ")
                .append(audio)
                .append(" -c:v copy -c:a aac -strict experimental -map 0:v:0 -map 1:a:0 -c copy ")
                .append(output);

        command = cmdBuilder.toString();

        cmdTextView = (EditText) findViewById(R.id.activityMain_textView_comand);
        cmdTextView.setText(command);

        consoleTextView = (TextView) findViewById(R.id.activityMain_textView_console);
        consoleTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    public void onClick(View view) {
        consoleTextView.setText("");
        executeFFmpeg(cmdTextView.getText().toString());
    }
}
