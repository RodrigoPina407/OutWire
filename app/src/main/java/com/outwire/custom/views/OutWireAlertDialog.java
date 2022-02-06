package com.outwire.custom.views;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.outwire.R;

public abstract class OutWireAlertDialog extends Dialog implements View.OnTouchListener {

    private TextView titleView;
    private TextView messageView;
    private TextView buttonViewLeft;
    private TextView buttonViewRight;

    private String title;
    private String message;
    private String textButtonLeft;
    private String textButtonRight;

    private boolean leftButtonVisibility = false;
    private boolean rightButtonVisibility = false;
    private boolean outside = false;

    private ValueAnimator animatorLeft;
    private ValueAnimator animatorRight;

    public final void setLeftButtonVisibility(boolean leftButtonVisibility) {
        this.leftButtonVisibility = leftButtonVisibility;
    }

    public final void setRightButtonVisibility(boolean rightButtonVisibility) {
        this.rightButtonVisibility = rightButtonVisibility;
    }


    public final void setTextButtonRight(String textButtonRight) {
        this.textButtonRight = textButtonRight;

    }

    public final void setTextButtonLeft(String textButtonLeft) {
        this.textButtonLeft = textButtonLeft;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final void setMessage(String message) {
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.outwire_alert_dialog);

        getWindow().setBackgroundDrawableResource(R.color.transparent);

        buttonViewLeft = findViewById(R.id.alertButton);
        buttonViewRight = findViewById(R.id.alertButton2);


        titleView = findViewById(R.id.alertTitle);
        messageView = findViewById(R.id.alertMessage);


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onStart() {
        super.onStart();

        buttonViewLeft.setOnTouchListener(this);
        buttonViewRight.setOnTouchListener(this);

        titleView.setText(title);
        messageView.setText(message);

        buttonViewLeft.setText(textButtonLeft);
        buttonViewRight.setText(textButtonRight);

        setVisibility(buttonViewLeft, leftButtonVisibility);
        setVisibility(buttonViewRight, rightButtonVisibility);
        colorAnimation();

        animatorLeft.addUpdateListener(animator -> buttonViewLeft.setBackgroundColor((int) animator.getAnimatedValue()));

        animatorRight.addUpdateListener(animator -> buttonViewRight.setBackgroundColor((int) animator.getAnimatedValue()));

    }

    private void setVisibility(TextView b, boolean s) {

        if (s)
            b.setVisibility(View.VISIBLE);
        else
            b.setVisibility(View.GONE);
    }


    public void leftButtonOnClick() {

    }

    public void rightButtonOnClick() {
    }

    public OutWireAlertDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (v.getId() == R.id.alertButton)
                animatorLeft.start();
            else if (v.getId() == R.id.alertButton2)
                animatorRight.start();

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            v.setOnClickListener(v1 -> {

                if (v1.getId() == R.id.alertButton)
                    leftButtonOnClick();
                else if (v1.getId() == R.id.alertButton2)
                    rightButtonOnClick();
            });

            if (!outside)
                v.performClick();

            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            if (x < 0 || x > v.getWidth() || y < 0 || y > v.getHeight()) {
                if (v.getId() == R.id.alertButton) {
                    animatorLeft.start();
                    animatorLeft.cancel();
                } else if (v.getId() == R.id.alertButton2) {
                    animatorRight.start();
                    animatorRight.cancel();
                }
                outside = true;
            } else
                outside = false;


            return true;
        }
        return false;
    }

    private void colorAnimation() {

        int colorFrom = ContextCompat.getColor(getContext(), R.color.alertBackground);
        int colorTo = ContextCompat.getColor(getContext(), R.color.press_grey);
        this.animatorLeft = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        this.animatorLeft.setDuration(300);
        this.animatorRight = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        this.animatorRight.setDuration(300);

    }


}
