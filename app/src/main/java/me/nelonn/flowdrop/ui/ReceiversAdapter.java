/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.nelonn.flowdrop.R;
import me.nelonn.flowdrop.app.Receiver;
import me.nelonn.jflowdrop.DeviceInfo;

public class ReceiversAdapter extends RecyclerView.Adapter<ReceiversAdapter.ReceiverViewHolder> {
    private Context context;
    private ArrayList<Receiver> items;
    private SparseBooleanArray selectedItems;
    private Delegate delegate;
    private boolean isSelectionMode = false;

    public ReceiversAdapter(Context context, ArrayList<Receiver> items) {
        this.context = context;
        this.items = items;
        this.selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public ReceiverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receiver, parent, false);
        return new ReceiverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiverViewHolder holder, int position) {
        Receiver item = items.get(position);
        DeviceInfo deviceInfo = item.getDeviceInfo();
        int drawableId = R.drawable.round_laptop_24;
        String platform;
        if (deviceInfo.getPlatform().isPresent()) {
            platform = deviceInfo.getPlatform().get();

            if (platform.equalsIgnoreCase("android")) {
                drawableId = R.drawable.round_phone_android_24;
            } else if (platform.equalsIgnoreCase("ios")) {
                drawableId = R.drawable.round_phone_iphone_24;
            } else if (platform.toLowerCase().contains("macos")) {
                drawableId = R.drawable.round_laptop_mac_24;
            }

            if (deviceInfo.getSystemVersion().isPresent()) {
                platform = platform + " " + deviceInfo.getSystemVersion().get();
            }
        } else {
            platform = deviceInfo.getId();
        }
        Drawable icon = AppCompatResources.getDrawable(context, drawableId);
        assert icon != null;
        Drawable wrappedDrawable = DrawableCompat.wrap(icon);
        DrawableCompat.setTint(wrappedDrawable, context.getColor(R.color.oneui_text1));
        holder.imageView.setImageDrawable(wrappedDrawable);
        holder.deviceFirstLine.setText(deviceInfo.getName().orElse(deviceInfo.getModel().orElse(deviceInfo.getId())));
        holder.deviceSecondLine.setText(platform);

        if (isSelected(position)) {
            holder.selectionIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.selectionIndicator.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void startSelectionMode(int position) {
        isSelectionMode = true;
        toggleItemSelection(position);
        //delegate.changeMultiSelectButtonVisiblity(true);
    }

    private void toggleItemSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
        if (isSelectionMode) {
            delegate.changeMultiSelectButtonVisiblity(!getSelectedReceivers().isEmpty());
        }
    }

    public void exitSelectionMode() {
        isSelectionMode = false;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public ArrayList<Receiver> getSelectedReceivers() {
        ArrayList<Receiver> selectedReceivers = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (selectedItems.get(i, false)) {
                selectedReceivers.add(items.get(i));
            }
        }
        return selectedReceivers;
    }

    public boolean isSelected(int position) {
        return selectedItems.get(position, false);
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView deviceFirstLine;
        private final TextView deviceSecondLine;
        private final ImageView selectionIndicator;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            deviceFirstLine = itemView.findViewById(R.id.deviceFirstLine);
            deviceSecondLine = itemView.findViewById(R.id.deviceSecondLine);
            selectionIndicator = itemView.findViewById(R.id.selectionIndicator);
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (isSelectionMode) {
                    toggleItemSelection(position);
                } else if (delegate != null) {
                    delegate.singleSend(items.get(position).getDeviceInfo());
                }
            });
            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (!isSelectionMode) {
                    startSelectionMode(position);
                    return true;
                }
                return false;
            });
        }
    }

    public interface Delegate {
        void singleSend(DeviceInfo receiver);

        void changeMultiSelectButtonVisiblity(boolean visible);
    }
}
