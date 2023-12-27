package com.example.hubo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    Button meet;

    Button delivery;

    boolean flag = true;

    VideoView video;

    Button openDialogButton



    private SpeechRecognizer speechRecognizer;

    private static final int RECORD_AUDIO_PERMISSION_CODE = 1;

    private LinearLayout submitLayout;

    private TextView responseTextView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meet = findViewById(R.id.meet);
        video = findViewById(R.id.video);
        delivery=findViewById(R.id.delivery);
        Button openDialogButton = findViewById(R.layout.);



        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.welcome;
        playVideo(videoPath);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_CODE);
        } else {
            initializeSpeechRecognizer();
        }



        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                startSpeechRecognition();
            }
        });

        meet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.meet;
                playVideo(videoPath);
                flag=true;
                video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                        if (flag) {
                            showPersonListBottomSheet();
                            flag = false;
                        }
                    }
                });
            }
        });

        openDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showYesNoDialog();
            }
        });

        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.delivery;
                playVideo(videoPath);
            }
        });
    }

    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // Called when the speech recognition service is ready to listen.
                Toast.makeText(MainActivity.this, "Started Listening", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                // Called when the user starts speaking.
                Toast.makeText(MainActivity.this, "Started speech", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRmsChanged(float rmsdB) {

                // Called when the RMS changes.
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Toast.makeText(MainActivity.this, "onbuffer" , Toast.LENGTH_SHORT).show();
                // Called when partial recognition results are available.
            }

            @Override
            public void onEndOfSpeech() {
                Toast.makeText(MainActivity.this, "Speech ended " , Toast.LENGTH_SHORT).show();
                // Called when the user stops speaking.
            }

            @Override
            public void onError(int error) {
                Log.e("SpeechRecognition", "Error: " + error);
                // or use Toast to display an error message
                Toast.makeText(MainActivity.this, "Speech recognition error: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                // Called when recognition results are ready.
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && matches.size() > 0) {
                    String result = matches.get(0);
                    findPerson(result);
                    Toast.makeText(MainActivity.this, "Result generated " , Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Toast.makeText(MainActivity.this, "Partial result " , Toast.LENGTH_SHORT).show();
                // Called when partial recognition results are available.
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Reserved for future use.
            }
        });
    }

    private void startSpeechRecognition() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");


            speechRecognizer.startListening(intent);
        } else {
            Toast.makeText(this, "Microphone permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopSpeechRecognition() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initialize SpeechRecognizer
                initializeSpeechRecognizer();
            } else {
                // Permission denied, inform the user
                Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    private void showPersonListBottomSheet() {
        // Sample list of persons
        String[] persons = {"dana", "fatima", "harish", "jovian", "ritin", "shezad", "sukesh", "vivek"};

        // Create a bottom sheet dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.persons_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Set up the ListView with the list of persons
        ListView listView = bottomSheetView.findViewById(R.id.listViewPersons);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, persons);
        listView.setAdapter(adapter);

        // Set item click listener for the ListView
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedPerson = persons[position];
            findPerson(selectedPerson);
            bottomSheetDialog.dismiss();
        });

        // Show the bottom sheet
        bottomSheetDialog.show();


    }

    private void showYesNoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to proceed?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                        // Add your code to handle the positive response
                        showSubmitButton();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked No button
                        // Add your code to handle the negative response
                        handleNegativeResponse();

                    }
                });

        // Create the AlertDialog object and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSubmitButton() {

        // Show or enable the submit button
        submitLayout.setVisibility(View.VISIBLE); // Assuming submitLayout is initially set to View.GONE

        // Inflate the form layout
        View formView = LayoutInflater.from(this).inflate(R.layout.form_layout, null);

        // Create and configure the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Could you please fill the form")
                .setView(formView)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle form submission here
                        handleFormSubmission(formView);
                    }
                });

        // Show the AlertDialog with the form
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void handleFormSubmission(View formView) {
        // Handle the form submission here
        EditText editTextName = formView.findViewById(R.id.editTextName);
        EditText editText = formView.findViewById(R.id.editText);

        String name = editTextName.getText().toString();
        String text = editText.getText().toString();

        // Do something with the user's input, e.g., send it to a server or process it locally
    }

    private void handleNegativeResponse() {
        // Display a text or perform some action for the negative response
        meet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.meet;
                playVideo(videoPath);
                flag=true;
                video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                        if (flag) {
                            showPersonListBottomSheet();
                            flag = false;
                        }
                    }
                }); 
            }
        });
    }


    public void findPerson(String name)
    {
        if(name.toLowerCase().equals("meet"))
        {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.meet;
            playVideo(videoPath);
            flag=true;
        }


        if(name.toLowerCase().equals("dana"))
        {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.dana;
            playVideo(videoPath);
        }
        if(name.toLowerCase().equals("fatima"))
        {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.fatima;
            playVideo(videoPath);
        }
        if(name.toLowerCase().equals("harish"))
        {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.harish;
            playVideo(videoPath);
        }
        if(name.toLowerCase().equals("jovian"))
        {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.jovian;
            playVideo(videoPath);
        }
        if(name.toLowerCase().equals("ritin"))
        {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.ritin;
            playVideo(videoPath);
        }
        if(name.toLowerCase().equals("shezad"))
        {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.shezad;
            playVideo(videoPath);
        }
        if(name.toLowerCase().equals("sukesh"))
        {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.sukesh;
            playVideo(videoPath);
        }
        if(name.toLowerCase().equals("vivek"))
        {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.vivek;
            playVideo(videoPath);
        }

    }

    public void playVideo(String path)
    {
        video.setVideoURI(Uri.parse(path));
        stopSpeechRecognition();
        video.start();
    }

}