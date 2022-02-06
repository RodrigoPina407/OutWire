package com.outwire.custom.views;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.outwire.R;

public class OutWireSignInButton extends FrameLayout implements View.OnTouchListener {

    private boolean outside;

    public OutWireSignInButton(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public OutWireSignInButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context, @Nullable AttributeSet set) {

        inflate(context, R.layout.outwire_signin_button, this);

        if (set == null)
            return;

        TypedArray ta = context.obtainStyledAttributes(set, R.styleable.OutWireSignInButton);
        String text = ta.getString(R.styleable.OutWireSignInButton_text);
        int text_size = ta.getDimensionPixelSize(R.styleable.OutWireSignInButton_text_size, getResources().getDimensionPixelSize(R.dimen.sign_button_text));
        int text_color = ta.getColor(R.styleable.OutWireSignInButton_text_color, ContextCompat.getColor(getContext(), R.color.black));
        int logoH = ta.getDimensionPixelSize(R.styleable.OutWireSignInButton_height_logo, getResources().getDimensionPixelSize(R.dimen.sign_logo_height));
        int logoW = ta.getDimensionPixelSize(R.styleable.OutWireSignInButton_width_logo, getResources().getDimensionPixelSize(R.dimen.sign_logo_width));
        Drawable logo_src = ta.getDrawable(R.styleable.OutWireSignInButton_src_logo);
        ta.recycle();

        TextView textView = findViewById(R.id.signin_button_text);
        ImageView imageView = findViewById(R.id.signin_logo);

        float density = getResources().getDisplayMetrics().scaledDensity;


        textView.setText(text);
        textView.setTextSize(text_size / density);
        textView.setTextColor(text_color);


        imageView.setImageDrawable(logo_src);

        imageView.getLayoutParams().width = logoW;
        imageView.getLayoutParams().height = logoH;
        imageView.requestLayout();

        FrameLayout frameLayout = (FrameLayout) getRootView();
        frameLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.sign_in_button));
        frameLayout.setElevation(6 * density);
        frameLayout.setOnTouchListener(this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                performHapticFeedback(1);
                outside = false;
                v.animate()
                        .alpha(0.7f)
                        .setDuration(100);
                v.setTranslationZ(-12);
                break;

            case MotionEvent.ACTION_UP:
                v.animate()
                        .alpha(1f)
                        .setDuration(100);
                v.setTranslationZ(0);
                if (!outside)
                    performClick();
               break;

            case MotionEvent.ACTION_MOVE:

                int x = (int) event.getX();
                int y = (int) event.getY();

                if (x < 0 || x > v.getWidth() || y < 0 || y > v.getHeight()) {
                    v.setAlpha(1f);
                    v.setTranslationZ(0);
                    outside = true;
                } else
                    outside = false;
                break;

        }
        return true;
    }


}
