/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.nelonn.flowdrop.app.jimpl.AndroidFile;
import me.nelonn.flowdrop.ui.SelectDevicesFragment;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SelectDevicesFragment modal = new SelectDevicesFragment();
        //modal.show(getSupportFragmentManager(), "SelectDevicesFragment");

        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction()) || Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())) {
            handleSharedFiles(intent);
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEND.equals(intent.getAction()) || Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())) {
            handleSharedFiles(intent);
        }
    }*/

    private void handleSharedFiles(Intent intent) {
        List<Uri> filesUris = new ArrayList<>();
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            filesUris.add(intent.getParcelableExtra(Intent.EXTRA_STREAM));
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())) {
            List<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            if (uris != null) {
                filesUris.addAll(uris);
            }
        }
        if (filesUris.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        List<AndroidFile> files = filesUris.stream().map(uri -> new AndroidFile(this, uri)).collect(Collectors.toList());
        SelectDevicesFragment modal = new SelectDevicesFragment(files);
        modal.show(getSupportFragmentManager(), "SelectDevicesFragment");
    }
}