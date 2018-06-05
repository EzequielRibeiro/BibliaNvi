/**
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at http://aws.amazon.com/apache2.0/
 * or in the "license" file accompanying this file.
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.projeto.biblianvi.biblianvi;

import com.amazon.device.ads.*;
import android.content.Context;
import android.util.Log;

/**
 * This is a simple app for testing interstitial ad loading capabilities of the Amazon Mobile Ads API.
 * This app demonstrates how to load and show an interstitial ad on the screen.
 * 
 * Buttons labeled "Load Ad" and "Show Ad" will become enabled or disabled depending on whether the
 * corresponding actions are valid at different points in the ad's lifecycle.
 */
public class InterstitialAdAmazon{

    private String APP_KEY;
    private static final String LOG_TAG = "InterstitialAdSample"; // Tag used to prefix all log messages.
    private InterstitialAd interstitialAd;

    public InterstitialAdAmazon(Context context) {

        APP_KEY = context.getResources().getString(R.string.interstitial_ad_unit_amazon);
        // For debugging purposes enable logging, but disable for production builds.
        AdRegistration.enableLogging(false);
        // For debugging purposes flag all ad requests as tests, but set to false for production builds.
        AdRegistration.enableTesting(false);
        
        this.interstitialAd = new InterstitialAd(context);
        this.interstitialAd.setListener(new SampleAdListener());
        
        try {
            AdRegistration.setAppKey(APP_KEY);
        } catch (final IllegalArgumentException e) {
            Log.e(LOG_TAG, "IllegalArgumentException thrown: " + e.toString());
            return;
        }

               // loadAd();
              //  showAd();

    }
    
    /**
     * Load a new interstitial ad in the background.
     * This action is generally recommended during the activity's start, after a previous ad has been shown, 
     * after a previous ad has failed to show, or thirty seconds after a previous ad has failed to load.
     */
    public void loadAd() {
        // Load an ad with default ad targeting.
        if (this.interstitialAd.loadAd()) {
            // Once an ad has started to load, new ads cannot load until the loading ad's lifecycle is complete.
            // We disable the load button here and do not re-enable it until the load action becomes available again. 

        } else {
            Log.w(LOG_TAG, "The ad could not be loaded. Check the logcat for more information.");
        }

        // Note: You can choose to provide additional targeting information to modify how your ads
        // are targeted to your users. This is done via an AdTargetingOptions parameter that's passed
        // to the loadAd call. See an example below:
        //
        //    final AdTargetingOptions adOptions = new AdTargetingOptions();
        //    adOptions.enableGeoLocation(true);
        //    if (this.interstitialAd.loadAd(adOptions)) ...
    }

    /**
     * Display the previously-loaded interstitial ad on the screen.
     * This action is generally recommended during a natural transition point in your app.
     */
    public void showAd() {
        // Once an ad has shown, a new ad must be loaded instead of showing the same ad.

        
        if (!this.interstitialAd.showAd()) {
            Log.w(LOG_TAG, "The ad was not shown. Check the logcat for more information.");
            
            // If the show action fails, a new ad must be loaded before another ad can be shown.

        }
    }
    
    /**
     * This class is for an event listener that tracks ad lifecycle events.
     * It extends DefaultAdListener, so you can override only the methods that you need.
     * In this case, there is no need to override methods specific to expandable ads.
     */
    class SampleAdListener extends DefaultAdListener {
        /**
         * This event is called once an ad loads successfully.
         */
        @Override
        public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
            Log.i(LOG_TAG, adProperties.getAdType().toString() + " ad loaded successfully.");
    
            // Once an interstitial ad has been loaded, it can then be shown.

        }
    
        /**
         * This event is called if an ad fails to load.
         */
        @Override
        public void onAdFailedToLoad(final Ad view, final AdError error) {
            Log.w(LOG_TAG, "Ad failed to load. Code: " + error.getCode() + ", Message: " + error.getMessage());
            
            // A new load action may be attempted once the previous one has returned a failure callback.

        }
        
        /**
         * This event is called when an interstitial ad has been dismissed by the user.
         */
        @Override
        public void onAdDismissed(final Ad ad) {
            Log.i(LOG_TAG, "Ad has been dismissed by the user.");
            
            // Once the shown ad is dismissed, its lifecycle is complete and a new ad can be loaded.

        }
    }
}
