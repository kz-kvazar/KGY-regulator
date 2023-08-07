package com.add.vpn;

import android.app.Activity;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.add.vpn.holders.ContextHolder;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


public class AdManager {
    private static InterstitialAd mInterstitialAd;

    public static void loadBannerAd() {
        Activity activity = ContextHolder.getActivity();
        if (activity != null) {
            AdView adView = activity.findViewById(R.id.adView);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Handler handler = new Handler();
                    handler.postDelayed(AdManager::loadBannerAd, 60_000);
                    super.onAdFailedToLoad(loadAdError);
                }
            });
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }

    public static void loadInterstitialAd() {
        Activity activity = ContextHolder.getActivity();
        if (activity != null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(activity, activity.getString(R.string.interstitial_ad_unit_id), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            Handler handler = new Handler();
                            handler.postDelayed(AdManager::loadInterstitialAd,60_000);
                        }
                    });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mInterstitialAd = null;
                }
            });
        }
    }

    public static void showInterstitialAd() {
        Activity activity = ContextHolder.getActivity();
        if (mInterstitialAd != null && activity != null) {
            mInterstitialAd.show(activity);
        } else {
            Handler handler = new Handler();
            loadInterstitialAd();
            handler.postDelayed(AdManager::showInterstitialAd, 60_000);
        }
    }
}
