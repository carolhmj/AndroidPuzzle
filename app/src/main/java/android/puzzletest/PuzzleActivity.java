package android.puzzletest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class PuzzleActivity extends AppCompatActivity {
    static public int EASY = 3;
    static public int MEDIUM = 4;
    static public int HARD = 5;

    static public String cellsArgument = "CELLS";
    static public String imageArgument = "IMAGE";

    static private String elementsSavedId = "SavedPuzzleElements";
    static private String cellsSavedId = "SavedCells";
    static private String emptyElementSavedId = "SavedEmptyElement";

    int cells;
    Bitmap puzzleImage;
    PuzzleElement[] puzzleCells;
    PuzzleLogic puzzleLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        cells = MEDIUM;
        puzzleCells = new PuzzleElement[cells*cells];

        final GridView puzzlegrid = (GridView)findViewById(R.id.gridview);
        puzzlegrid.setNumColumns(cells);

        Bitmap inputImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),  R.drawable.teste);
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.vazio);
        puzzleLogic = PuzzleLogic.makePuzzleFromImage(inputImage, bitmap, cells);

        final PuzzleGridAdapter puzzleGridAdapter = new PuzzleGridAdapter(this, puzzleLogic);
        puzzlegrid.setAdapter(puzzleGridAdapter);

        puzzlegrid.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                puzzleLogic.moveElement(position);
                if (puzzleLogic.isFinished()) {
                    Log.d("listener", "won");
                    TextView winText = (TextView)findViewById(R.id.displaytext);
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
        outState.putInt(cellsSavedId, cells);
        outState.putParcelable(emptyElementSavedId, puzzleLogic.emptyPuzzleElement);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        PuzzleElement[] savedPE = (PuzzleElement[]) savedInstanceState.getParcelableArray(elementsSavedId);
        int savedCells = savedInstanceState.getInt(cellsSavedId);
        PuzzleElement savedEmptyPE = (PuzzleElement) savedInstanceState.getParcelable(emptyElementSavedId);
        puzzleLogic = new PuzzleLogic(savedPE, savedEmptyPE, savedCells);
    }
}


