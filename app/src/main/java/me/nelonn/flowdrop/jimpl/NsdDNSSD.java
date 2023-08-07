/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.jimpl;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import me.nelonn.jflowdrop.DNSSD;
import me.nelonn.jflowdrop.IsStoppedFunction;

public class NsdDNSSD implements DNSSD {
    public static final DNSSDFactory FACTORY = NsdDNSSD::new;

    private final NsdManager nsdManager;

    public NsdDNSSD(Context context) {
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    @Override
    public void registerService(String serviceName, String regType, String domain, int port, Map<String, String> txt, IsStoppedFunction isStoppedFunction) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(serviceName);
        serviceInfo.setServiceType(regType);
        serviceInfo.setPort(port);
        for (Map.Entry<String, String> line : txt.entrySet()) {
            serviceInfo.setAttribute(line.getKey(), line.getValue());
        }
        NsdManager.RegistrationListener listener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {

            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {

            }

            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {

            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {

            }
        };
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, listener);
        if (isStoppedFunction != null) {
            while (true) {
                if (isStoppedFunction.isStopped()) {
                    nsdManager.unregisterService(listener);
                    break;
                }
            }
        }
    }

    @Override
    public void discoverServices(String regType, String domain, Consumer<DiscoverReply> callback, IsStoppedFunction isStoppedFunction) {
        NsdManager.DiscoveryListener listener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String s, int i) {

            }

            @Override
            public void onStopDiscoveryFailed(String s, int i) {

            }

            @Override
            public void onDiscoveryStarted(String s) {
            }

            @Override
            public void onDiscoveryStopped(String s) {

            }

            @Override
            public void onServiceFound(NsdServiceInfo nsdServiceInfo) {
                Log.i("NsdDNSSD", "Found service: " + nsdServiceInfo.getServiceName() + ", " + nsdServiceInfo.getServiceType());
                DiscoverReply discoverReply = new DiscoverReply(nsdServiceInfo.getServiceName(), nsdServiceInfo.getServiceType(), "local.");
                callback.accept(discoverReply);
            }

            @Override
            public void onServiceLost(NsdServiceInfo nsdServiceInfo) {

            }
        };
        nsdManager.discoverServices(regType, NsdManager.PROTOCOL_DNS_SD, listener);
        if (isStoppedFunction != null) {
            while (true) {
                if (isStoppedFunction.isStopped()) {
                    nsdManager.stopServiceDiscovery(listener);
                    break;
                }
            }
        }
    }

    @Override
    public void resolveService(String serviceName, String regType, String domain, Consumer<ResolveReply> callback) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(serviceName);
        serviceInfo.setServiceType(regType);
        nsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Log.i("NsdDNSSD", "Failed resolve service: " + nsdServiceInfo.getServiceName() + ", " + nsdServiceInfo.getServiceType());
            }

            @Override
            public void onServiceResolved(NsdServiceInfo service) {
                Log.i("NsdDNSSD", "Fully resolved service: " + service.getServiceName() + ", " + service.getServiceType() + ", " + service.getHost().getHostAddress() + ", " + service.getPort());
                Map<String, String> txt = new HashMap<>();
                for (Map.Entry<String, byte[]> entry : service.getAttributes().entrySet()) {
                    txt.put(entry.getKey(), new String(entry.getValue()));
                }
                IPAddress ipAddress = new IPAddress(IPType.IPv4, service.getHost().getHostAddress());
                ResolveReply resolveReply = new ResolveReply(null, ipAddress, service.getPort(), txt);
                callback.accept(resolveReply);
            }
        });
    }

    @Override
    public void queryIPv6Address(String hostName, Consumer<String> callback) {

    }

    @Override
    public void queryIPv4Address(String hostName, Consumer<String> callback) {

    }
}
