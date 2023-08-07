/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import me.nelonn.flowdrop.AppController;
import me.nelonn.flowdrop.R;
import me.nelonn.flowdrop.ShareActivity;
import me.nelonn.flowdrop.background.BackgroundSendingService;
import me.nelonn.flowdrop.jimpl.AndroidFile;
import me.nelonn.jflowdrop.DeviceInfo;
import me.nelonn.jflowdrop.JFlowDrop;

public class SelectDevicesFragment extends SuperBottomSheetFragment implements ReceiversAdapter.Delegate {
    private final ArrayList<AndroidFile> files;
    private ArrayList<Receiver> receivers;
    private ReceiversAdapter receiversAdapter;
    private AtomicBoolean isStopped = new AtomicBoolean(false);
    private Button sendButton;

    public SelectDevicesFragment() {
        this.files = new ArrayList<>();
        this.receivers = new ArrayList<>();
    }

    public SelectDevicesFragment(List<AndroidFile> files) {
        super();
        this.files = new ArrayList<>(files);
        this.receivers = new ArrayList<>();

        for (AndroidFile androidFile : files) {
            Log.i("FILE", "META: " + androidFile.getRelativePath() + " " + androidFile.getSize() + " " + androidFile.getCreatedTime() + " " + androidFile.getModifiedTime());
        }
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_select_devices_sheet, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.devicesView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        receiversAdapter = new ReceiversAdapter(getContext(), receivers);
        receiversAdapter.setDelegate(this);
        recyclerView.setAdapter(receiversAdapter);

        new Thread(() -> {
            final String selfId = AppController.getInstance().getDeviceInfo().getId();
            JFlowDrop.getInstance().discover(deviceInfo -> {
                // removing the current device from the list
                if (deviceInfo.getId().equals(selfId)) return;
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        receivers.add(new Receiver(deviceInfo));
                        receiversAdapter.notifyDataSetChanged();
                    }
                });
            }, () -> {
                return isStopped.get();
            });
        }).start();

        sendButton = view.findViewById(R.id.sendButton);
        sendButton.setVisibility(View.INVISIBLE);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Receiver> selected = receiversAdapter.getSelectedReceivers();
                onSend_before();
                selected.forEach(receiver -> sendTo(receiver.getDeviceInfo()));
                onSend_after();
            }
        });

        return view;
    }

    public void onSend_before() {
        files.forEach(AndroidFile::open);
    }

    public void onSend_after() {
        Toast.makeText(getContext(), "Sending files ...", Toast.LENGTH_LONG).show();
        dismiss();
    }

    @Override
    public void singleSend(DeviceInfo receiver) {
        onSend_before();
        sendTo(receiver);
        onSend_after();
    }

    public void sendTo(DeviceInfo receiver) {
        Context context = getContext();
        if (context == null) return;
        Intent intent = new Intent(context, BackgroundSendingService.class);
        intent.putExtra("receiverId", receiver.getId());
        intent.putParcelableArrayListExtra("files", new ArrayList<>(files.stream().map(AndroidFile::getUri).collect(Collectors.toList())));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void changeMultiSelectButtonVisiblity(boolean visible) {
        sendButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        Activity activity = getActivity();
        if (activity instanceof ShareActivity) {
            activity.finish();
        }

        isStopped.set(true);

        for (AndroidFile androidFile : files) {
            androidFile.close();
        }
    }

    @Override
    public float getCornerRadius() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        float dpValue = 26.0f;
        return dpValue * density + 0.5f;
    }

    /*@Override
    public float getCornerRadius() {
        return 80.0f;
    }*/

    @Override
    public int getBackgroundColor() {
        return getResources().getColor(R.color.oneui_bg2);
    }
}
