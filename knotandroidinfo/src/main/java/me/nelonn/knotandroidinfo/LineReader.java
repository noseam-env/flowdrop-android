/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.knotandroidinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class LineReader extends BufferedReader {
    public LineReader(Reader in) {
        super(in);
    }

    @Override
    public String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = read()) != -1) {
            if (c == '\r') {
                continue;
            } else if (c == '\n') {
                return sb.toString();
            } else {
                sb.append((char) c);
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }
}