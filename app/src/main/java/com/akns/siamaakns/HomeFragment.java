package com.akns.siamaakns;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

/**
 * Created by Affan Mohammad on 17/04/2016.
 */
public class HomeFragment extends Fragment {
    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private static long SLIDE_MILIS = 1000;
    private static long SLIDE_DELAY = 10000;
    private static int COUNTER = 0;
    private static boolean SLIDE = true;
    private int[] imgSlideIds;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_home, container, false);
        imgSlideIds = new int[]{R.drawable.slide_a, R.drawable.slide_b, R.drawable.slide_c, R.drawable.slide_d, R.drawable.slide_e, R.drawable.slide_f};
        final ImageView imgSlide = (ImageView) rootView.findViewById(R.id.img_slide);
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SlideShow(imgSlide);
            }
        };
        handler.post(runnable);
        imgSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SLIDE) {
                    SLIDE = false;
                    handler.removeCallbacks(runnable);
                } else {
                    SLIDE = true;
                    handler.post(runnable);
                }
            }
        });
        return rootView;
    }

    private void SlideShow(final ImageView imageView) {
        if (imageView.getVisibility() == View.INVISIBLE) {
            imageView.setVisibility(View.VISIBLE);
        }
        imageView.setImageResource(imgSlideIds[COUNTER]);
        COUNTER++;
        if (COUNTER >= imgSlideIds.length) {
            COUNTER = 0;
        }
        YoYo.with(Techniques.SlideInLeft).duration(SLIDE_MILIS).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (SLIDE) {
                    YoYo.with(Techniques.SlideOutRight).duration(SLIDE_MILIS).delay(SLIDE_DELAY).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            SlideShow(imageView);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    }).playOn(imageView);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).playOn(imageView);
    }
}
