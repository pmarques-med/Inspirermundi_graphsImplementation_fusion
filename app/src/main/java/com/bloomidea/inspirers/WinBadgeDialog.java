package com.bloomidea.inspirers;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.bloomidea.inspirers.model.Badge;
import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.ConfettoGenerator;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;
import com.github.jinatonic.confetti.confetto.Confetto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WinBadgeDialog extends DialogFragment {
    private View rootDialog;
    private Badge badge;

    private ConfettiManager confettiManager;

    private WinBadgeDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        rootDialog = inflater.inflate(R.layout.layout_win_badge, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(rootDialog);
        dialog.setCancelable(false);

        ((ImageView) rootDialog.findViewById(R.id.badge_icon)).setImageResource(badge.getImgResourceId());

        ((TextView) rootDialog.findViewById(R.id.achievement_name_textView)).setText(badge.getName());
        ((TextView) rootDialog.findViewById(R.id.achievement_desc_textView)).setText(badge.getDesc());

        rootDialog.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CommonConfetti.rainingConfetti((ViewGroup) rootDialog.findViewById(R.id.emiter_top_center), new int[] { Color.BLUE }).infinite();

                if(confettiManager!=null)
                    confettiManager.terminate();
                dismiss();

                if(listener!=null)
                    listener.onDismiss();
            }
        });

        ViewTreeObserver vto = rootDialog.findViewById(R.id.emiter_top_center).getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootDialog.findViewById(R.id.emiter_top_center).getViewTreeObserver().removeGlobalOnLayoutListener(this);

                final List<Bitmap> allPossibleConfetti = new ArrayList<>();
                allPossibleConfetti.add(BitmapFactory.decodeResource(getResources(), R.drawable.confetti));
                allPossibleConfetti.add(BitmapFactory.decodeResource(getResources(), R.drawable.confetti1));
                allPossibleConfetti.add(BitmapFactory.decodeResource(getResources(), R.drawable.confetti2));
                allPossibleConfetti.add(BitmapFactory.decodeResource(getResources(), R.drawable.confetti3));
                allPossibleConfetti.add(BitmapFactory.decodeResource(getResources(), R.drawable.confetti4));

                final int numConfetti = allPossibleConfetti.size();
                final ConfettoGenerator confettoGenerator = new ConfettoGenerator() {
                    @Override
                    public Confetto generateConfetto(Random random) {
                        final Bitmap bitmap = allPossibleConfetti.get(random.nextInt(numConfetti));
                        return new BitmapConfetto(bitmap);
                    }
                };

                final int containerMiddleX = rootDialog.findViewById(R.id.emiter_top_center).getWidth() / 2;
                final int containerMiddleY = 0;
                final ConfettiSource confettiSource = new ConfettiSource(0, 0,rootDialog.findViewById(R.id.emiter_top_center).getWidth(),0);

                confettiManager = new ConfettiManager(getContext(), confettoGenerator, confettiSource, (ViewGroup) rootDialog.findViewById(R.id.emiter_top_center))
                        .setVelocityX(0, getResources().getDimensionPixelOffset(R.dimen.conffety_velocity_slow))
                        .setVelocityY(getResources().getDimensionPixelOffset(R.dimen.conffety_velocity_normal), getResources().getDimensionPixelOffset(R.dimen.conffety_velocity_slow))
                        .setInitialRotation(180, 180)
                        .setRotationalAcceleration(360, 180)
                        .setTargetRotationalVelocity(360)
                        .setNumInitialCount(0)
                        .setEmissionDuration(ConfettiManager.INFINITE_DURATION)
                        .setEmissionRate(50)
                        .animate();

                //CommonConfetti.rainingConfetti((ViewGroup) rootDialog.findViewById(R.id.emiter_top_center), new int[] { Color.BLUE }).infinite();
            }
        });


        return dialog;
    }



    public void setBadge(Badge badge, WinBadgeDialogListener listener) {
        this.badge = badge;
        this.listener = listener;
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

    public interface WinBadgeDialogListener{
        void onDismiss();
    }
}