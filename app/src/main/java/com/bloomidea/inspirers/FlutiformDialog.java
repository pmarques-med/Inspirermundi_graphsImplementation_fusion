package com.bloomidea.inspirers;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class FlutiformDialog extends DialogFragment {
    private View rootDialog;
    private int boxWidth;
    private int ypos;

    private DialogInterface.OnDismissListener dismissListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        rootDialog = inflater.inflate(R.layout.dialog_flutiform, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(rootDialog);
        dialog.setCancelable(false);

        TextView boxFlu = (TextView) rootDialog.findViewById(R.id.aux_textView);

        ConstraintLayout.LayoutParams params =  (ConstraintLayout.LayoutParams) boxFlu.getLayoutParams();
        params.width = boxWidth;



        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;



        params.topMargin = ypos-statusBarHeight;

        boxFlu.setLayoutParams(params);


        //boxFlu.setY(ypos);

        rootDialog.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        rootDialog.findViewById(R.id.aux_textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialog;
    }

    public void setBoxValues(int boxWidth, int ypos, DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
        this.boxWidth = boxWidth;
        this.ypos = ypos;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if(dismissListener!=null)
            dismissListener.onDismiss(dialog);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}