package com.ostfalia.bs.dartscoring.fragment.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ostfalia.bs.dartscoring.R;

/**
 * Created by lukas on 24.05.2016.
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    public String mBoundString;
    public long mBoundID;

    public final View mView;
    public final TextView mTextView;
    public final ImageView mImageView;
    public final CheckBox mCheckBox;

    //Constructor
    public ViewHolder(View view) {
        super(view);
        mView = view;
        //initialisierung der Elemente in List_item
        mImageView = (ImageView) view.findViewById(R.id.avatar);
        mTextView = (TextView) view.findViewById(android.R.id.text1);
        mCheckBox = (CheckBox) view.findViewById(android.R.id.checkbox);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
