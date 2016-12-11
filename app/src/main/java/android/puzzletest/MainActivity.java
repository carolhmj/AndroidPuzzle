package android.puzzletest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startPuzzle(View view) {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            String uriPath = uri.getPath();

            Intent callPuzzle = new Intent(this, PuzzleActivity.class);
            callPuzzle.putExtra(PuzzleActivity.difficultyIntent, PuzzleActivity.Difficulty.EASY.getCode());
            callPuzzle.putExtra(PuzzleActivity.imageIntent, uriPath);
//            startActivity(callPuzzle);
            startActivityForResult(callPuzzle, PuzzleActivity.requestCode);
        }

        if (requestCode == PuzzleActivity.requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("puzzle result", "ok");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("puzzle result", "canceled");
            } else {
                Log.d("puzzle result", "wtf");
            }
        }
    }
}
