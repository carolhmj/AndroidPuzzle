package android.puzzletest;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by carol on 10/8/16.
 */

public class PuzzleElement implements Parcelable {
    private Bitmap img;
    private int pos;

    PuzzleElement(Bitmap img, int pos) {
        this.setImg(img);
        this.pos = pos;
    }

    PuzzleElement(Parcel parcel) {
        img = parcel.readParcelable(null);
        pos = parcel.readInt();
    }

    public Bitmap getImg() {
        return img;
    }

    public int getPos() {
        return pos;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(img, flags);
        parcel.writeInt(pos);
    }

    //Creator
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public PuzzleElement createFromParcel(Parcel parcel) {
            return new PuzzleElement(parcel);
        }

        @Override
        public PuzzleElement[] newArray(int size) {
            return new PuzzleElement[size];
        }
    };
}
