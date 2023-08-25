package com.add.vpn;


import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


public class AdManager {
    private final FragmentActivity activity;
    private InterstitialAd mInterstitialAd;

    public AdManager(FragmentActivity activity) {
        this.activity = activity;
    }

    public void loadBannerAd() {
            AdView adView = activity.findViewById(R.id.adView);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> loadBannerAd(), 60_000);
                    super.onAdFailedToLoad(loadAdError);
                }
            });
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
    }

    public void loadInterstitialAd() {
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(activity, activity.getString(R.string.interstitial_ad_unit_id), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(AdManager.this::loadInterstitialAd,60_000);
                        }
                    });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mInterstitialAd = null;
                }
            });
        }

    public void showInterstitialAd() {
        if (mInterstitialAd != null && activity != null) {
            mInterstitialAd.show(activity);
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(this::showInterstitialAd, 60_000);
        }
    }
}
