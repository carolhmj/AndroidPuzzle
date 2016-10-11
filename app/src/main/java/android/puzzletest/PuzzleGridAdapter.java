package android.puzzletest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by carol on 10/9/16.
 */

public class PuzzleGridAdapter extends BaseAdapter {
    Context context;
    PuzzleLogic puzzleLogic;

    PuzzleGridAdapter(Context context, PuzzleLogic puzzleLogic) {
        this.context = context;
        this.puzzleLogic = puzzleLogic;
    }

    @Override
    public int getCount() {
        return puzzleLogic.getNumElements();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        final PuzzleElement puzzleElement = puzzleLogic.getPuzzleElementForDisplay(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.puzzleelement_layout, null);

            final ImageView imageElementView = (ImageView)convertView.findViewById(R.id.imageelement);
            final TextView positionElementView = (TextView)convertView.findViewById(R.id.positionelement);

            final ViewHolder viewHolder = new ViewHolder(imageElementView, positionElementView);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        Bitmap bmap = puzzleElement.getImg();
        viewHolder.imageElementView.setImageBitmap(bmap);
        viewHolder.positionElementView.setText(String.valueOf(puzzleElement.getPos()));

        return convertView;
    }

    private class ViewHolder {
        private final ImageView imageElementView;
        private final TextView positionElementView;

        public ViewHolder(ImageView imageElementView, TextView positionElementView) {
            this.imageElementView = imageElementView;
            this.positionElementView = positionElementView;
        }
    }
}
