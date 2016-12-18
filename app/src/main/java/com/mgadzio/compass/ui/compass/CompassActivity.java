package com.mgadzio.compass.ui.compass;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.mgadzio.compass.R;
import com.mgadzio.compass.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompassActivity extends BaseActivity implements CompassPresenter.ViewCompass {

    CompassPresenter presenter;
    ViewHolder viewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        viewHolder = new ViewHolder(this);
        initPresenter();
        initPointer();
        initButtons();
    }

    @Override
    protected void initPresenter() {
        presenter = new CompassPresenter();
        presenter.bindView(this);
    }

    protected void initButtons() {
        viewHolder.bLatitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onButtonLatitudeClicked();
            }
        });
        viewHolder.bLongitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onButtonLongitudeClicked();
            }
        });
    }

    protected void initPointer() {
        viewHolder.ivPointer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void rotateCompass(float currentDegree, float targetDegree) {
        viewHolder.ivCompass.startAnimation(initRotateAnimation(currentDegree, targetDegree));
    }

    @Override
    public void rotatePointer(float currentDegree, float targetDegree) {
        viewHolder.ivPointer.setVisibility(View.VISIBLE);
        viewHolder.ivPointer.startAnimation(initRotateAnimation(currentDegree, targetDegree));
    }

    @Override
    public void updateButtons(@Nullable String latitude, @Nullable String longitude) {
        if (latitude != null) {
            viewHolder.bLatitude.setText(latitude);
        }
        if (longitude != null) {
            viewHolder.bLongitude.setText(longitude);
        }
    }

    @Override
    public void showDialogLatitude() {
        new CoordinateDialog(this, CoordinateDialog.Type.latitude, new CoordinateDialog.CoordinateDialogListener() {
            @Override
            public void onCoordinatePassed(double coordinate) {
                presenter.onLatitudePassed(coordinate);
            }
        }).show();
    }

    @Override
    public void showDialogLongitude() {
        new CoordinateDialog(this, CoordinateDialog.Type.longitude, new CoordinateDialog.CoordinateDialogListener() {
            @Override
            public void onCoordinatePassed(double coordinate) {
                presenter.onLongitudePassed(coordinate);
            }
        }).show();
    }

    @Override
    public void showMsgAppClosing() {
        Toast.makeText(this, R.string.compass_permission_location_not_granted, Toast.LENGTH_SHORT).show();
    }

    protected RotateAnimation initRotateAnimation(float currentDegree, float targetDegree) {
        RotateAnimation rotateAnimation = new RotateAnimation(
                -currentDegree,
                -targetDegree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        rotateAnimation.setDuration(210);
        rotateAnimation.setFillAfter(true);
        return rotateAnimation;
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    static class ViewHolder {

        public ViewHolder(Activity activity) {
            ButterKnife.bind(this, activity);
        }

        @BindView(R.id.compass_iv_compass)
        ImageView ivCompass;

        @BindView(R.id.compass_iv_pointer)
        ImageView ivPointer;

        @BindView(R.id.compass_b_latitude)
        Button bLatitude;

        @BindView(R.id.compass_b_longitude)
        Button bLongitude;
    }
}
