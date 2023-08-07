/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.jimpl;

import android.content.Context;

import me.nelonn.jflowdrop.DNSSD;

public interface DNSSDFactory {
    DNSSD create(Context context);
}
