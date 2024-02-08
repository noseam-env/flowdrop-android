/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.app.jimpl;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.github.druk.dnssd.DNSSD;
import com.github.druk.dnssd.DNSSDEmbedded;
import com.github.druk.dnssd.DNSSDRegistration;
import com.github.druk.dnssd.DNSSDService;
import com.github.druk.dnssd.NSClass;
import com.github.druk.dnssd.NSType;
import com.github.druk.dnssd.QueryListener;
import com.github.druk.dnssd.RegisterListener;
import com.github.druk.dnssd.ResolveListener;
import com.github.druk.dnssd.TXTRecord;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.function.Consumer;

import me.nelonn.jflowdrop.IsStoppedFunction;

public class DrukDNSSD implements me.nelonn.jflowdrop.DNSSD {
    public static final DNSSDFactory FACTORY = DrukDNSSD::new;

    private final DNSSD dnssd;

    public DrukDNSSD(Context context) {
        this.dnssd = new DNSSDEmbedded(context);
    }

    @Override
    public void registerService(String serviceName, String regType, String domain, int port, Map<String, String> txt, @Nullable IsStoppedFunction isStoppedFunction) {
        TXTRecord txtRecord = new TXTRecord();
        for (Map.Entry<String, String> line : txt.entrySet()) {
            txtRecord.set(line.getKey(), line.getValue());
        }
        try {
            dnssd.register(0, 0, serviceName, regType, domain, null, port, txtRecord, new RegisterListener() {
                @Override
                public void serviceRegistered(DNSSDRegistration registration, int flags, String serviceName, String regType, String domain) {
                    Log.i("DrukDNSSD", "serviceRegistered");
                }

                @Override
                public void operationFailed(DNSSDService service, int errorCode) {
                    Log.i("DrukDNSSD", "operationFailed: " + errorCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("DrukDNSSD", "dnssdException: " + e.getMessage());
        }
    }

    @Override
    public void discoverServices(String regType, String domain, Consumer<DiscoverReply> callback, IsStoppedFunction isStoppedFunction) {

    }

    @Override
    public void resolveService(String serviceName, String regType, String domain, Consumer<ResolveReply> callback) {
        try {
            dnssd.resolve(0, 0, serviceName, regType, domain, new ResolveListener() {
                @Override
                public void serviceResolved(DNSSDService resolver, int flags, int ifIndex, String fullName, String hostName, int port, Map<String, String> txtRecord) {
                    ResolveReply reply = new ResolveReply(hostName, null, port, txtRecord);
                    callback.accept(reply);
                }

                @Override
                public void operationFailed(DNSSDService service, int errorCode) {
                    Log.i("DrukDNSSD", "resolve operationFailed: " + errorCode);
                }
            });
        } catch (Exception e) {
            Log.i("DrukDNSSD", "resolve failed: " + e.getMessage());
        }
    }

    @Override
    public void queryIPv6Address(String hostName, Consumer<String> callback) {
        try {
            dnssd.queryRecord(0, 0, hostName, NSType.AAAA, NSClass.IN, true, new QueryListener() {
                @Override
                public void queryAnswered(DNSSDService query, int flags, int ifIndex, String fullName, int rrtype, int rrclass, byte[] rdata, int ttl) {
                    if (rrtype != NSType.AAAA) return;
                    try {
                        InetAddress inetAddress = InetAddress.getByAddress(rdata);
                        Log.i("DrukDNSSD", "Fully resolved service: " + inetAddress.getHostAddress());
                        callback.accept(inetAddress.getHostAddress());
                    } catch (UnknownHostException e) {

                    }
                }

                @Override
                public void operationFailed(DNSSDService service, int errorCode) {
                    Log.i("DrukDNSSD", "query operationFailed: " + errorCode);
                }
            });
        } catch (Exception e) {
            Log.i("DrukDNSSD", "queryRecord failed: " + e.getMessage());
        }
    }

    @Override
    public void queryIPv4Address(String hostName, Consumer<String> callback) {
        try {
            dnssd.queryRecord(0, 0, hostName, NSType.A, NSClass.IN, true, new QueryListener() {
                @Override
                public void queryAnswered(DNSSDService query, int flags, int ifIndex, String fullName, int rrtype, int rrclass, byte[] rdata, int ttl) {
                    if (rrtype != NSType.A) return;
                    try {
                        InetAddress inetAddress = InetAddress.getByAddress(rdata);
                        Log.i("DrukDNSSD", "Fully resolved service: " + inetAddress.getHostAddress());
                        callback.accept(inetAddress.getHostAddress());
                    } catch (UnknownHostException e) {

                    }
                }

                @Override
                public void operationFailed(DNSSDService service, int errorCode) {
                    Log.i("DrukDNSSD", "query operationFailed: " + errorCode);
                }
            });
        } catch (Exception e) {
            Log.i("DrukDNSSD", "queryRecord failed: " + e.getMessage());
        }
    }
}
