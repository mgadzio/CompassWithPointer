package com.mgadzio.compass.ui.compass;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.InputType;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mgadzio.compass.R;

public class CoordinateDialog {

    public interface CoordinateDialogListener {
        void onCoordinatePassed(double coordinate);
    }

    public enum Type {
        latitude, longitude
    }

    private Type type;
    private Context context;
    private CoordinateDialogListener listener;

    public CoordinateDialog(Context context, Type type, CoordinateDialogListener listener) {
        this.context = context;
        this.type = type;
        this.listener = listener;
    }

    @StringRes
    protected int getTextHint() {
        return type == Type.latitude ? R.string.coordinate_dialog_hint_latitude : R.string.coordinate_dialog_hint_longitude;
    }

    public CoordinateDialog show() {

        new MaterialDialog.Builder(context)
                .title(R.string.coordinate_dialog_title)
                .inputType(InputType.TYPE_NUMBER_FLAG_SIGNED)
                .autoDismiss(false)
                .input(getTextHint(), R.string.coordinate_dialog_prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        double value;
                        try {
                            value = Double.valueOf(input.toString());
                            listener.onCoordinatePassed(value);
                            dialog.dismiss();
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, R.string.coordinate_dialog_invalid_coordinate, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();

        return this;
    }

}
