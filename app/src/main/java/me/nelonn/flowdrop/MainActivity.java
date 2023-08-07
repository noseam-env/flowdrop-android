/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.nelonn.flowdrop.background.BackgroundServerService;
import me.nelonn.flowdrop.jimpl.AndroidFile;
import me.nelonn.flowdrop.ui.SelectDevicesFragment;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_FILES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ImageView iconImageView = findViewById(R.id.iconImageView);
        Drawable iconDrawable = AppCompatResources.getDrawable(this, R.drawable.flowdrop);
        assert iconDrawable != null;
        Drawable wrappedDrawable = DrawableCompat.wrap(iconDrawable);
        DrawableCompat.setTint(wrappedDrawable, getColor(R.color.oneui_text3));
        iconImageView.setImageDrawable(wrappedDrawable);

        Button donateButton = findViewById(R.id.donateButton);
        donateButton.setOnClickListener(v -> {
            Util.openUrl(MainActivity.this, "https://flowdrop.com/donate");
        });

        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        Button shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v -> openFilePicker());

        SharedPreferences preferences = getSharedPreferences(Preferences.NAME, Context.MODE_PRIVATE);
        boolean instructionsViewed = preferences.getBoolean(Preferences.INSTRUCTIONS_VIEWED, false);
        if (!instructionsViewed) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Preferences.INSTRUCTIONS_VIEWED, true);
            editor.apply();

            Intent intent = new Intent(this, InstructionsActivity.class);
            startActivity(intent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        startService(new Intent(this, BackgroundServerService.class));
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_CODE_PICK_FILES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_FILES && resultCode == RESULT_OK) {
            if (data != null) {
                List<Uri> filesUris = new ArrayList<>();
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        filesUris.add(uri);
                    }
                } else if (data.getData() != null) {
                    filesUris.add(data.getData());
                }
                if (filesUris.isEmpty()) {
                    return;
                }
                List<AndroidFile> files = filesUris.stream().map(uri -> new AndroidFile(this, uri)).collect(Collectors.toList());
                SelectDevicesFragment modal = new SelectDevicesFragment(files);
                modal.show(getSupportFragmentManager(), "SelectDevicesFragment");
            }
        }
    }
}