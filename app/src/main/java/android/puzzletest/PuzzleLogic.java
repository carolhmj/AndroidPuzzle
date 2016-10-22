package android.puzzletest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by carol on 10/9/16.
 */

//Representa a lógica do puzzle, tem um conjunto de elementos e a quantidade de células dele
public class PuzzleLogic {
    private PuzzleElement[] puzzleElements;
    PuzzleElement emptyPuzzleElement;
    private int ncells;
    private int emptyPosition;
    private int ncorrectCells;
    private boolean finished = false;

    PuzzleLogic(PuzzleElement[] puzzleElements, PuzzleElement emptyPuzzleElement, int ncells) {
        for (int i = 0; i < puzzleElements.length; i++) {
            //Elemento tá na posição correta
            if (puzzleElements[i].getPos() == i) {
                ncorrectCells++;
            }
        }
        this.puzzleElements = puzzleElements;
        this.ncells = ncells;
        emptyPosition = emptyPuzzleElement.getPos();
        this.emptyPuzzleElement = emptyPuzzleElement;
    }

    PuzzleLogic(PuzzleElement[] puzzleElements, PuzzleElement emptyPuzzleElement, int emptyPuzzleElementCurrPos, int ncells) {
        for (int i = 0; i < puzzleElements.length; i++) {
            //Elemento tá na posição correta
            if (puzzleElements[i].getPos() == i) {
                ncorrectCells++;
            }
        }
        Log.d("ncorrectcells", String.valueOf(ncorrectCells));
        this.puzzleElements = puzzleElements;
        this.ncells = ncells;
        emptyPosition = emptyPuzzleElementCurrPos;
        this.emptyPuzzleElement = emptyPuzzleElement;
    }

    static PuzzleLogic makePuzzleFromImage(Bitmap completeImage, int ncells) {

        int inputImgW = completeImage.getWidth(), inputImgH = completeImage.getHeight();
        if (inputImgW != inputImgH) {
            int minDimension = (inputImgW < inputImgH)? inputImgW : inputImgH;
            completeImage = Bitmap.createScaledBitmap(completeImage, minDimension, minDimension, false);
        }

        PuzzleElement[] generatedElements = new PuzzleElement[ncells*ncells];
        int imgDimension = completeImage.getWidth()/ncells;
        Bitmap emptyElementBitmap = Bitmap.createBitmap(imgDimension, imgDimension, Bitmap.Config.ARGB_8888);
        emptyElementBitmap.eraseColor(Color.TRANSPARENT);

        for (int i = 0; i < ncells; i++) {
            for (int j = 0; j < ncells; j++){
                int pos = getPosFromTwoDimPos(i,j,ncells);
                generatedElements[pos] = new PuzzleElement(Bitmap.createBitmap(completeImage, j*imgDimension, i*imgDimension, imgDimension, imgDimension), pos);
            }
        }

        int emptyPos = ncells*ncells-1;
        Random rand = new Random();
        for (int i = 0; i < 30*ncells; i++) {
            int direction = rand.nextInt(5);
            int slidePosX = getTwoDimXPos(emptyPos, ncells);
            int slidePosY = getTwoDimYPos(emptyPos, ncells);
            int slidePos = emptyPos;

            if (direction == 0 && slidePosX > 0) {
                slidePos = getPosFromTwoDimPos(slidePosX-1,slidePosY,ncells);
            } else if (direction == 0 && slidePosY > 0) {
                slidePos = getPosFromTwoDimPos(slidePosX,slidePosY-1,ncells);
            } else if (direction == 0 && slidePosX < ncells-1) {
                slidePos = getPosFromTwoDimPos(slidePosX+1,slidePosY,ncells);
            } else if (direction == 0 && slidePosY < ncells-1) {
                slidePos = getPosFromTwoDimPos(slidePosX,slidePosY+1,ncells);
            }
            PuzzleElement aux = generatedElements[emptyPos];
            generatedElements[emptyPos] = generatedElements[slidePos];
            generatedElements[slidePos] = aux;
            emptyPos = slidePos;
        }

        return new PuzzleLogic(generatedElements, new PuzzleElement(emptyElementBitmap, ncells*ncells-1), emptyPos, ncells);
    }


    //Mesma coisa, só que recebe o bitmap da imagem vazia
    static PuzzleLogic makePuzzleFromImage(Bitmap completeImage, Bitmap emptyImage, int ncells) {

        int inputImgW = completeImage.getWidth(), inputImgH = completeImage.getHeight();
        if (inputImgW != inputImgH) {
            int minDimension = (inputImgW < inputImgH)? inputImgW : inputImgH;
            completeImage = Bitmap.createScaledBitmap(completeImage, minDimension, minDimension, false);
        }

        PuzzleElement[] generatedElements = new PuzzleElement[ncells*ncells];
        int imgDimension = completeImage.getWidth()/ncells;

        for (int i = 0; i < ncells; i++) {
            for (int j = 0; j < ncells; j++){
                int pos = getPosFromTwoDimPos(i,j,ncells);
                generatedElements[pos] = new PuzzleElement(Bitmap.createBitmap(completeImage, j*imgDimension, i*imgDimension, imgDimension, imgDimension), pos);
            }
        }

        int emptyPos = ncells*ncells-1;
        Random rand = new Random();
        for (int i = 0; i < 30*ncells; i++) {
            int direction = rand.nextInt(5);
            int slidePosX = getTwoDimXPos(emptyPos, ncells);
            int slidePosY = getTwoDimYPos(emptyPos, ncells);
            int slidePos = emptyPos;

            if (direction == 0 && slidePosX > 0) {
                slidePos = getPosFromTwoDimPos(slidePosX-1,slidePosY,ncells);
            } else if (direction == 0 && slidePosY > 0) {
                slidePos = getPosFromTwoDimPos(slidePosX,slidePosY-1,ncells);
            } else if (direction == 0 && slidePosX < ncells-1) {
                slidePos = getPosFromTwoDimPos(slidePosX+1,slidePosY,ncells);
            } else if (direction == 0 && slidePosY < ncells-1) {
                slidePos = getPosFromTwoDimPos(slidePosX,slidePosY+1,ncells);
            }
            PuzzleElement aux = generatedElements[emptyPos];
            generatedElements[emptyPos] = generatedElements[slidePos];
            generatedElements[slidePos] = aux;
            emptyPos = slidePos;
        }

        return new PuzzleLogic(generatedElements, new PuzzleElement(emptyImage, ncells*ncells-1), emptyPos, ncells);
    }

    public PuzzleElement[] getPuzzleElements() {
        return puzzleElements;
    }

    public int getNcells() {
        return ncells;
    }

    public PuzzleElement getPuzzleElement(int i) {
        return puzzleElements[i];
    }

    public PuzzleElement getPuzzleElementForDisplay(int i) {
        if (i != emptyPosition) {
            return puzzleElements[i];
        } else if (finished) {
            return puzzleElements[i];
        } else {
            return emptyPuzzleElement;
        }
    }

    public void swapPuzzleElements(int i, int j) {
        Log.d("swapPuzzleElements", "called");
        PuzzleElement aux = puzzleElements[i];
        puzzleElements[i] = puzzleElements[j];
        puzzleElements[j] = aux;
    }

    public int getNumElements() {
        return ncells*ncells;
    }

    public int getTwoDimXPos(int pos) {
        return pos/ncells;
    }

    static public int getTwoDimXPos(int pos, int ncells) {
        return pos/ncells;
    }

    public int getTwoDimYPos(int pos) {
        return pos%ncells;
    }

    static public int getTwoDimYPos(int pos, int ncells) {
        return pos%ncells;
    }

    static public int getPosFromTwoDimPos(int x, int y, int ncells) {
        return x*ncells + y;
    }

    public boolean isTopNeighbour(int pos1, int pos2) {
        return ((getTwoDimXPos(pos2) == getTwoDimXPos(pos1)-1) && (getTwoDimYPos(pos2) == getTwoDimYPos(pos1)));
    }

    public boolean isBottomNeighbour(int pos1, int pos2) {
        return ((getTwoDimXPos(pos2) == getTwoDimXPos(pos1)+1) && (getTwoDimYPos(pos2) == getTwoDimYPos(pos1)));
    }

    public boolean isLeftNeighbour(int pos1, int pos2) {
        return ((getTwoDimXPos(pos2) == getTwoDimXPos(pos1)) && (getTwoDimYPos(pos2) == getTwoDimYPos(pos1)-1));
    }

    public boolean isRightNeighbour(int pos1, int pos2) {
        return ((getTwoDimXPos(pos2) == getTwoDimXPos(pos1)) && (getTwoDimYPos(pos2) == getTwoDimYPos(pos1)+1));
    }

    public void moveElement(int position) {
        Log.d("moveElement", "position: " + String.valueOf(position) + " emptyPostition: " + String.valueOf(emptyPosition));
        Log.d("position", String.valueOf(getTwoDimXPos(position))+ " " + String.valueOf(getTwoDimYPos(position)));
        Log.d("emptypos", String.valueOf(getTwoDimXPos(emptyPosition))+ " " + String.valueOf(getTwoDimYPos(emptyPosition)));
        if (isTopNeighbour(position, emptyPosition) || isBottomNeighbour(position, emptyPosition) || isLeftNeighbour(position, emptyPosition) || isRightNeighbour(position, emptyPosition)){
            //Checa se o elemento está indo para a posição correta dele
            Log.d("correct position", String.valueOf(puzzleElements[position].getPos()));
            if (emptyPosition == puzzleElements[position].getPos()) {
                ncorrectCells++;
            } else {
                ncorrectCells--;
            }

            Log.d("nCorrectCells", String.valueOf(ncorrectCells));
            if (ncorrectCells == ncells*ncells-1) {
                finished = true;
            }

            swapPuzzleElements(position, emptyPosition);
            emptyPosition = position;
        }
    }

    public boolean isFinished() {
        return finished;
    }
}
