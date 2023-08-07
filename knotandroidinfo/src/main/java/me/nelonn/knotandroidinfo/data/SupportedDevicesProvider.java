/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.knotandroidinfo.data;

import java.io.BufferedReader;
import java.io.IOException;

public interface SupportedDevicesProvider extends AutoCloseable {

    void open() throws IOException;

    BufferedReader reader() throws IOException;

}
