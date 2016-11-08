package android.puzzletest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class PuzzleActivity extends AppCompatActivity {

    static public int SOLVE_EASY = 1;
    static public int SOLVE_MEDIUM = 2;

    public enum Difficulty {
        EASY (3, 5*60*1000),
        HARD (4, 3*60*1000);

        private final int code;
        private final int numCells;
        private final long resolutionTime;

        Difficulty(int numCells, long resolutionTime) {
            this.code = ordinal();
            this.numCells = numCells;
            this.resolutionTime = resolutionTime;
        }


        public final int getCode() {
            return code;
        }

        public final int getNumCells() {
            return numCells;
        }

        public final long getResolutionTime() {
            return resolutionTime;
        }

        public String getResolutionTimeAsString() {
            long minutes = TimeUnit.MINUTES.convert(resolutionTime, TimeUnit.MILLISECONDS);
            long seconds = TimeUnit.SECONDS.convert(resolutionTime, TimeUnit.MILLISECONDS) - 60*minutes;
            return String.format("%1$02d:%2$02d", minutes, seconds);
        }

    }

    Difficulty difficulty;

    static public String difficultyIntent = "DIFFICULTY";
    static public String imageIntent = "IMAGE";

    static private String elementsSavedId = "SavedPuzzleElements";
    static private String difficultySavedId = "SavedDifficulty";
    static private String emptyElementSavedId = "SavedEmptyElement";
    static private String timeSavedId = "SavedTime";

    long startTimer;
    Chronometer timer;

    PuzzleLogic puzzleLogic;
    Handler timeHandler = new Handler();
//    private class TimeRunnable implements Runnable {
//        long startTime, maxTime;
//        public TimeRunnable(long startTime, long maxTime) {
//            this.startTime = startTime;
//            this.maxTime = maxTime;
//        }
//        @Override
//        public void run() {
//            final long deltaTime = SystemClock.elapsedRealtime() - startTime;
//
//            timeHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    TextView timerView = (TextView) findViewById(R.id.timerText);
//                    timerView.setText("Time: " + String.valueOf(deltaTime));
//                }
//            });
//
//            if (deltaTime > maxTime) {
//                timeHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        TextView timerView = (TextView) findViewById(R.id.displaytext);
//                        timerView.setText("You lost!!!!");
//                    }
//                });
//            }
//
//            timeHandler.postDelayed(this, 500);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        Intent callingIntent = getIntent();
        int selectedDifficulty = callingIntent.getIntExtra(difficultyIntent, Difficulty.EASY.getCode());

        if (selectedDifficulty == Difficulty.EASY.getCode()) {
            difficulty = Difficulty.EASY;
        } else if (selectedDifficulty == Difficulty.HARD.getCode()) {
            difficulty = Difficulty.HARD;
        }

        final GridView puzzlegrid = (GridView)findViewById(R.id.gridview);
        assert puzzlegrid != null;
        puzzlegrid.setNumColumns(difficulty.getNumCells());

        Bitmap inputImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.teste_de_imagem_puzzle);
        puzzleLogic = PuzzleLogic.makePuzzleFromImage(inputImage, difficulty.getNumCells());

        final PuzzleGridAdapter puzzleGridAdapter = new PuzzleGridAdapter(this, puzzleLogic);
        puzzlegrid.setAdapter(puzzleGridAdapter);

        TextView resolutionTime = (TextView) findViewById(R.id.resolutionTime);
        resolutionTime.setText("Tempo disponível para resolução é: " + difficulty.getResolutionTimeAsString());

        if (savedInstanceState == null) {
            startTimer = SystemClock.elapsedRealtime();
        } else {
            startTimer = savedInstanceState.getLong(timeSavedId);
        }
        Log.d("startTimer", String.valueOf(startTimer));
//        timeHandler.postDelayed(new TimeRunnable(startTimer, MEDIUMTIME), 0);

        timer = (Chronometer) findViewById(R.id.timer);
        timer.setBase(startTimer);
        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - timer.getBase();
//                Log.d("timerTime", String.valueOf(elapsedMillis));
                if (elapsedMillis > difficulty.getResolutionTime()) {
                    TextView loseText = (TextView) findViewById(R.id.displaytext);
                    loseText.setText("You lost!!!!!!!");
                    puzzlegrid.setClickable(false);
                }
            }
        });
        timer.start();

        puzzlegrid.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                puzzleLogic.moveElement(position);
                if (puzzleLogic.isFinished()) {
                    Log.d("listener", "won");
                    TextView winText = (TextView) findViewById(R.id.displaytext);
                    winText.setText("You won!!!!!!!");
                }

            puzzleGridAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArray(elementsSavedId, puzzleLogic.getPuzzleElements());
        outState.putInt(difficultySavedId, difficulty.getCode());
        outState.putParcelable(emptyElementSavedId, puzzleLogic.emptyPuzzleElement);
        outState.putLong(timeSavedId, startTimer);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        PuzzleElement[] savedPE = (PuzzleElement[]) savedInstanceState.getParcelableArray(elementsSavedId);

        int selectedDifficulty = savedInstanceState.getInt(difficultySavedId);
        if (selectedDifficulty == Difficulty.EASY.getCode()) {
            difficulty = Difficulty.EASY;
        } else if (selectedDifficulty == Difficulty.HARD.getCode()) {
            difficulty = Difficulty.HARD;
        }

        PuzzleElement savedEmptyPE = (PuzzleElement) savedInstanceState.getParcelable(emptyElementSavedId);
        puzzleLogic = new PuzzleLogic(savedPE, savedEmptyPE, difficulty.getNumCells());

        long savedStartTime = savedInstanceState.getLong(timeSavedId);
        Log.d("timeSaved", String.valueOf(savedStartTime));
        timer.setBase(savedStartTime);
//        timeHandler.postDelayed(new TimeRunnable(savedStartTime, MEDIUMTIME), 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}


