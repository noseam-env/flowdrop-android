/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        BatteryOptimizationMode batteryOptimizationMode = BatteryOptimizationMode.get(this);
        if (batteryOptimizationMode == BatteryOptimizationMode.Unrestricted) {
            Preferences.setReceiveInBackground(InstructionsActivity.this, true);
            finish();
            return;
        }

        findViewById(R.id.skipBtn).setOnClickListener(view -> finish());
        findViewById(R.id.openAppSettingsBtn).setOnClickListener(view -> openAppSettings());
    }

    private void openAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        BatteryOptimizationMode batteryOptimizationMode = BatteryOptimizationMode.get(this);
        if (batteryOptimizationMode == BatteryOptimizationMode.Unrestricted) {
            Preferences.setReceiveInBackground(InstructionsActivity.this, true);
            finish();
        }
    }
}