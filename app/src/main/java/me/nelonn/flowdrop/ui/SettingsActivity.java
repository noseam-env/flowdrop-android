/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

import me.nelonn.flowdrop.BuildConfig;
import me.nelonn.flowdrop.R;
import me.nelonn.flowdrop.app.Preferences;
import me.nelonn.flowdrop.app.Util;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences preferences = getSharedPreferences(Preferences.NAME, Context.MODE_PRIVATE);
        boolean isEnabled = preferences.getBoolean(Preferences.RECEIVE_IN_BACKGROUND, false);

        MaterialSwitch materialSwitch = findViewById(R.id.receiveInBgSwitch);
        materialSwitch.setChecked(isEnabled);
        materialSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Preferences.setReceiveInBackground(SettingsActivity.this, isChecked);
        });

        findViewById(R.id.backLayout).setOnClickListener(v -> {
            onBackPressed();
        });

        TextView textAbout = findViewById(R.id.textAbout);

        String fullText = textAbout.getText().toString();
        fullText = fullText.replace("{version}", BuildConfig.VERSION_NAME);
        textAbout.setText(fullText);

        setupClickableText(textAbout);
    }

    private void setupClickableText(TextView textView) {
        String fullText = textView.getText().toString();
        int startPosGPL = fullText.indexOf("GNU GPL");
        int startPosGitHub = fullText.indexOf("GitHub");

        SpannableString spannableString = new SpannableString(fullText);
        ClickableSpan gplClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Util.openUrl(SettingsActivity.this, "https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL");
            }
        };
        spannableString.setSpan(gplClickableSpan, startPosGPL, startPosGPL + 7, 0);

        ClickableSpan gitHubClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Util.openUrl(SettingsActivity.this, "https://github.com/noseam-env/flowdrop-android");
            }
        };
        spannableString.setSpan(gitHubClickableSpan, startPosGitHub, startPosGitHub + 6, 0);

        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}