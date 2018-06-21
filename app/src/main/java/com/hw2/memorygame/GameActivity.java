package com.hw2.memorygame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private int numOfElements;
    private GameButton[] buttons;
    private int[] buttonGraphicLocation;
    private int[] buttonGraphics;
    private GameButton selectedButton1;
    private GameButton selectedButton2;
    private TextView textName;

    private boolean isBusy = false;

    private int pairedNum = 0;
    private final Handler handler = new Handler();
    private long time;
    private CountDownTimer timer;
    private CountDownTimer timePunisher;
    private Vibrator vibrator;

    private String name;
    private int age;

    private SensorManager sensorManager;
    private Sensor mSensor;
    private SensorEventListener sensorEventListener;
    private float initialPosition;
    private SensorEvent sensorEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        startVibrate();
        Bundle extrasBundle = this.getIntent().getExtras();
        GridLayout grid = findViewById(R.id.game_grid);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
       // initialPosition = sensorEvent.values[0];

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.values[0] > /*initialPosition +*/ 0.5f || sensorEvent.values[0] < /*initialPosition*/ - 0.5f){
                    timePunisher = new CountDownTimer(3 * 1000, 1000) {

                        @Override
                        public void onTick(long l) {
                            //startVibrate();
                        }

                        @Override
                        public void onFinish() {
                           //stopVibrate();
                            shuffleButtonGraphics();
                        }
                    }.start();
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        int numCols = extrasBundle.getInt("columns");
        int numRows = extrasBundle.getInt("rows");
        time = (long) extrasBundle.getInt("time");
        name = extrasBundle.getString("name");
        age = extrasBundle.getInt("age");


        textName = findViewById(R.id.name_container_text);
        textName.setText(name);

        grid.setColumnCount(numCols);
        grid.setRowCount(numRows);

        numOfElements = numCols * numRows;

        buttons = new GameButton[numOfElements];
        buttonGraphics = new int[numOfElements / 2];

        loadGraphics();

        buttonGraphicLocation = new int[numOfElements];

        shuffleButtonGraphics();

        // fill grid with buttons
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                GameButton tempButton = new GameButton(this, row, col, buttonGraphics[buttonGraphicLocation[(row * numCols) + col]], numOfElements);
                tempButton.setId(View.generateViewId());
                tempButton.setOnClickListener(this);
                buttons[(row * numCols) + col] = tempButton;
                grid.addView(tempButton);
            }
        }

        //start timer
        countDownStart();
    }

    private void countDownStart() {
        timer = new CountDownTimer(time * 1000, 1000) {

            TextView timer = (TextView) findViewById(R.id.seconds_left_text);

            public void onTick(long millisUntilFinished) {
                if (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) == 10)
                    timer.setTextColor(Color.RED);
                timer.setText("" + TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
            }

            public void onFinish() {
                timer.setText("HALT!!!");
                final Animation anim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.bounce);
                Toast.makeText(GameActivity.this, "Out of time, LOSER!\nEnjoy your main menu.", Toast.LENGTH_LONG).show();
                new CountDownTimer(5000,1000) {

                    @Override
                    public void onTick(long arg0) {
                        for(int i = 0; i < buttons.length; i++)
                            buttons[i].startAnimation(anim);
                    }

                    @Override
                    public void onFinish() {
                        returnToMenu();
                    }
                }.start();

            }
        }.start();
    }

    private void returnToMenu() {
        Intent intent = new Intent(GameActivity.this, MenuActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("age", age);
        startActivity(intent);
    }

    private void loadGraphics() {
        buttonGraphics[0] = R.drawable.button_1;
        buttonGraphics[1] = R.drawable.button_2;
        if (numOfElements > 4) {
            buttonGraphics[2] = R.drawable.button_3;
            buttonGraphics[3] = R.drawable.button_4;
            buttonGraphics[4] = R.drawable.button_5;
            buttonGraphics[5] = R.drawable.button_6;
            buttonGraphics[6] = R.drawable.button_7;
            buttonGraphics[7] = R.drawable.button_8;
        }
        if (numOfElements > 16) {
            buttonGraphics[8] = R.drawable.button_9;
            buttonGraphics[9] = R.drawable.button_10;
            buttonGraphics[10] = R.drawable.button_11;
            buttonGraphics[11] = R.drawable.button_12;
        }
    }


    protected void shuffleButtonGraphics() {
        Random rand = new Random();

        for (int i = 0; i < numOfElements; i++) {
            buttonGraphicLocation[i] = i % (numOfElements / 2);
        }

        for (int i = 0; i < numOfElements; i++) {
            int temp = buttonGraphicLocation[i];
            int swapIdx = rand.nextInt(numOfElements);
            buttonGraphicLocation[i] = buttonGraphicLocation[swapIdx];
            buttonGraphicLocation[swapIdx] = temp;
        }

    }


    @Override
    public void onClick(View view) {

        if (isBusy)
            return;

        GameButton button = (GameButton) view;

        if (button.isMatched())
            return;

        if (selectedButton1 == null) {
            selectedButton1 = button;
            selectedButton1.flip();
            return;
        }

        if (selectedButton1.getId() == button.getId())
            return;

        if (selectedButton1.getFrontImageDrawableId() == button.getFrontImageDrawableId()) {
            button.flip();
            selectedButton1.setMatched(true);
            selectedButton1.setEnabled(false);
            button.setEnabled(false);
            selectedButton1 = null;
            pairedNum++;

                checkIfWon();

            return;
        } else {
            selectedButton2 = button;
            selectedButton2.flip();
            isBusy = true;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectedButton2.flip();
                    selectedButton1.flip();
                    selectedButton1 = null;
                    selectedButton2 = null;
                    isBusy = false;
                }
            }, 1000);
        }
    }

    private void checkIfWon()  {
        final Animation anim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.rotate);
        if (pairedNum == buttonGraphics.length) {
            timer.cancel();
            Toast.makeText(this, "You are the mighty Winrar!\nEnjoy your main menu.",Toast.LENGTH_LONG).show();
            new CountDownTimer(5000,1000) {

                @Override
                public void onTick(long arg0) {
                    for(int i = 0; i < buttons.length; i++)
                    buttons[i].startAnimation(anim);
                    }

                @Override
                public void onFinish() {
                    returnToMenu();
                }
            }.start();

        }
    }

    public void startVibrate() {
        long pattern[] = { 0, 100, 200, 300, 400 };
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, 0);
    }
    public void stopVibrate() {
        vibrator.cancel();
    }
    @Override
    public void onBackPressed() {
        timer.cancel();
        returnToMenu();
    }
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener,mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }
}
