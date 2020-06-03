package org.smartregister.addo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.smartregister.addo.R;

public class EmptystateView extends LinearLayout {

    public EmptystateView(Context context) {
        this(context, null);
    }

    public EmptystateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptystateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.emptystate_view, this, false);
        addView(view);

        ImageView ivEmptyStateImage = view.findViewById(R.id.emptystate_image);
        TextView tvTitle = view.findViewById(R.id.emptystate_title);
        TextView tvMessage = view.findViewById(R.id.emptystate_text);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.EmptystateView);

        ivEmptyStateImage.setImageDrawable(attributes.getDrawable(R.styleable.EmptystateView_emptystate_image));
        tvTitle.setText(attributes.getString(R.styleable.EmptystateView_emptystate_title));
        tvMessage.setText(attributes.getString(R.styleable.EmptystateView_emptystate_text));

        attributes.recycle();

    }

}
