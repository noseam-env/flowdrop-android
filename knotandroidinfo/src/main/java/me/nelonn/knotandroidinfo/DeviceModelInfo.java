/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.knotandroidinfo;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class DeviceModelInfo {
    public final String manufacturer;
    @SerializedName("market_name")
    public final String marketName;
    public final String codename;
    public final String model;

    public DeviceModelInfo(String manufacturer, String marketName, String codename, String model) {
        this.manufacturer = manufacturer;
        this.marketName = marketName;
        this.codename = codename;
        this.model = model;
    }

    /**
     * @return the consumer friendly name of the manufacturer.
     */
    public String getManufacturer() {
        return !TextUtils.isEmpty(manufacturer) ? manufacturer : null;
    }

    /**
     * @return the consumer friendly name of the device.
     */
    public String getName() {
        return !TextUtils.isEmpty(marketName) ? marketName :
                !TextUtils.isEmpty(model) ? Util.capitalize(model) :
                        null;
    }
}
