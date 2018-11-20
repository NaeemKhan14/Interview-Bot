package com.interaction.design.naeemkhan.interview_bot;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech TTS;
    private TextView textview;
    private Button startButton, resultsButton;
    private Spinner spinnerList;
    private ArrayAdapter<String> adapter;
    private String[] questionLabels;
    private final String[] questions = {"Hi, what is your name?", "How old are you?", "444444444444444444444444", "QQQQQQQQQQQQQQQQQQQQQ",
    "PEEEEEEEEEEEEEEEEEEEEEEEEEEENIS", "What", "Hi, I'm gay", "Are you gay?", "INDEED, a wise choice", "Go back!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the components of the app and assign them here for use
        textview = findViewById(R.id.text_view);
        startButton = findViewById(R.id.startButton);
        resultsButton = findViewById(R.id.showResults);
        questionLabels = new String[10];

        for(int i = 0; i < questionLabels.length; i++) {
            int count = i + 1;
            questionLabels[i] = "Question #" + count;
        }

        // Initialise Text-To-Speech
        TTS = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // If successfully initialized, set the language to English
                if(status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.ENGLISH);
                }
            }
        });

        // ActionListener for the start
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateList();
                resultsButton.setEnabled(true);
            }
        });

        resultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWebsiteData();
                resultsButton.setEnabled(false); // Disable the button once result is shown
            }
        });
    }

    private void getWebsiteData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<String> result = new ArrayList<>();
                try {

                    // Connect to the link and get the results from specified tag
                    String username = "";
                    String password = "";
                    String login = username + ":" + password;
                    String base64login = new String(Base64.encode(login.getBytes(), Base64.DEFAULT));
                    Document dataSource = Jsoup.connect("http://shootboys.net/interbot/showData.php").header("Authorization", "Basic " + base64login).get();
                    // Get all the data inside <body> tag.
                    Elements data = dataSource.select("body");
                    // Split the results at ### so we know where a new line is
                    String[] dataList = data.text().split("###");

                    // Append each question into the result
                    for(String value : dataList) {
                        result.add(value);
                    }

                } catch (IOException e) {
                    e.getMessage();
                }

                // Once the results' list is populated, push the result into textview
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(String value : result) {
                            textview.append(value + "\n");
                        }

                    }
                });
            }
        }).start();
    }

    private void populateList() {
        // Initialize the spinner and enable it
        spinnerList = findViewById(R.id.spinner);
        spinnerList.setEnabled(true);

        spinnerList.setSelection(0, false);
        // ArrayAdapter will handle the values that goes into spinner
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, questionLabels);
        spinnerList.setAdapter(adapter);

        /*
         * Whenever an item from spinner is selected, this listener will select a question from the
         * question's list based on that select question number
         */
        spinnerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textview.setText(questions[position]);
                TTS.speak(questions[position], TextToSpeech.QUEUE_FLUSH, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    // Force shutdown the TTS when app is closed
    @Override
    protected void onDestroy() {
        if(TTS != null) {
            TTS.stop();
            TTS.shutdown();
        }
        super.onDestroy();
    }
}
