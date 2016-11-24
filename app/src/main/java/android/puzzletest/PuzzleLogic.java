package android.puzzletest;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

import static java.util.Collections.shuffle;

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

        this.puzzleElements = puzzleElements;
        this.ncells = ncells;
        emptyPosition = emptyPuzzleElement.getPos();
        this.emptyPuzzleElement = emptyPuzzleElement;
    }

    PuzzleLogic(PuzzleElement[] puzzleElements, PuzzleElement emptyPuzzleElement, int emptyPuzzleElementCurrPos, int ncells) {

        this.puzzleElements = puzzleElements;
        this.ncells = ncells;
        emptyPosition = emptyPuzzleElementCurrPos;
        Log.d("emptyPos", String.valueOf(emptyPosition));
        this.emptyPuzzleElement = emptyPuzzleElement;
    }

    static int countInversions(PuzzleElement[] list, int i) {
        int inversions = 0;
        for (int j = i+1; j < list.length; j++) {
            if (list[i].getPos() > list[j].getPos()) {
                inversions++;
            }
        }
        return inversions;
    }

    static int sumInversions(PuzzleElement[] list, int emptyPosition) {
        int totInversions = 0;
        for (int i = 0; i < list.length; i++) {
            if (i != emptyPosition)
                totInversions += countInversions(list, i);
        }
        return totInversions;
    }

    static void swapIndices(PuzzleElement[] list, int i, int j) {
        PuzzleElement aux = list[i];
        list[i] = list[j];
        list[j] = aux;
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
        int nelements = ncells*ncells;
        int emptyPos = nelements-1;

        ArrayList<Integer> randomPositions = new ArrayList<>();
        for (int i = 0; i < nelements; i++) {
            randomPositions.add(i,i);
        }
        int newEmptyPos = emptyPos;
        shuffle(randomPositions);
        Log.d("puzzle positions", randomPositions.toString());
        PuzzleElement[] randomPosGeneratedElements = new PuzzleElement[ncells*ncells];
        for (int i = 0; i < nelements; i++) {
            Log.d("i", String.valueOf(i));
            Log.d("randompos", String.valueOf(randomPositions.get(i)));
            Log.d("getPos", String.valueOf(generatedElements[randomPositions.get(i)].getPos()));
            Log.d("comp", String.valueOf(generatedElements[randomPositions.get(i)].getPos() == emptyPos));
            if (generatedElements[randomPositions.get(i)].getPos() == emptyPos) {
                newEmptyPos = i;
            }

            randomPosGeneratedElements[i] = generatedElements[randomPositions.get(i)];
        }
        emptyPos = newEmptyPos;

        Log.d("emptyPos", String.valueOf(emptyPos));
        String pz = "";
        for (int i = 0; i < nelements; i++) {
            pz = pz + "i :" + String.valueOf(i) + " element: " + randomPosGeneratedElements[i].toString() + "\n";

        }
        Log.d("positions", pz);

        int inversions = sumInversions(randomPosGeneratedElements, emptyPos);
        Log.d("inversions", String.valueOf(inversions));
        boolean solvable;
        if (ncells % 2 == 1) {
            solvable = (inversions % 2 == 0);
        } else {
            solvable = ((inversions + getTwoDimXPos(emptyPos, ncells)) % 2 == 1);
        }
        Log.d("solvable", String.valueOf(solvable));
        if (!solvable) {
            if (emptyPos <= 1) {
                swapIndices(randomPosGeneratedElements, nelements-1, nelements-2);
            } else {
                swapIndices(randomPosGeneratedElements, 0, 1);
            }
        }

        if (ncells % 2 == 1) {
            Log.d("suminv: ", String.valueOf(sumInversions(randomPosGeneratedElements, emptyPos)));
            if (sumInversions(randomPosGeneratedElements, emptyPos) % 2 != 0)
                throw new AssertionError();
        } else {
            if ((sumInversions(randomPosGeneratedElements, emptyPos) + getTwoDimXPos(emptyPos, ncells)) % 2 != 1) {
                throw new AssertionError();
            }
        }
        String pz2 = "";
        for (int i = 0; i < nelements; i++) {
            pz2 = pz2 + "i :" + String.valueOf(i) + " element: " + randomPosGeneratedElements[i].toString() + "\n";

        }
        Log.d("positions", pz2);
        return new PuzzleLogic(randomPosGeneratedElements, new PuzzleElement(emptyElementBitmap, ncells*ncells-1), emptyPos, ncells);
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

    public int numCorrectCells() {
        int numCorrectCells = 0;
        for (int i = 0; i < puzzleElements.length; i++) {
          if (puzzleElements[i].getPos() == i) {
              numCorrectCells++;
          }
        }
        return numCorrectCells;
    }

    public void moveElement(int position) {
        Log.d("moveElement", "position: " + String.valueOf(position) + " emptyPostition: " + String.valueOf(emptyPosition));
        Log.d("position", String.valueOf(getTwoDimXPos(position))+ " " + String.valueOf(getTwoDimYPos(position)));
        Log.d("emptypos", String.valueOf(getTwoDimXPos(emptyPosition))+ " " + String.valueOf(getTwoDimYPos(emptyPosition)));
        if (isTopNeighbour(position, emptyPosition) || isBottomNeighbour(position, emptyPosition) || isLeftNeighbour(position, emptyPosition) || isRightNeighbour(position, emptyPosition)) {

            swapPuzzleElements(position, emptyPosition);
            emptyPosition = position;

            //Checa se os elementos estão em suas posições corretas
            ncorrectCells = numCorrectCells();

            Log.d("nCorrectCells", String.valueOf(ncorrectCells));
            if (ncorrectCells >= ncells*ncells) {
                finished = true;
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }
}
