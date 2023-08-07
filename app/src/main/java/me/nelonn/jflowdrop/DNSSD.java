/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

public interface DNSSD {

    void registerService(String serviceName,
                         String regType,
                         String domain,
                         int port,
                         Map<String, String> txt,
                         IsStoppedFunction isStoppedFunction);

    class DiscoverReply {
        private final String serviceName;
        private final String regType;
        private final String replyDomain;

        public DiscoverReply(String serviceName, String regType, String replyDomain) {
            this.serviceName = serviceName;
            this.regType = regType;
            this.replyDomain = replyDomain;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getRegType() {
            return regType;
        }

        public String getReplyDomain() {
            return replyDomain;
        }
    }

    void discoverServices(String regType,
                          String domain,
                          Consumer<DiscoverReply> callback,
                          IsStoppedFunction isStoppedFunction);

    enum IPType {
        IPv6,
        IPv4
    }

    class IPAddress {
        public final IPType type;
        public final String value;

        public IPAddress(IPType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    class ResolveReply {
        @Nullable
        public final String hostName;
        @Nullable
        public final IPAddress ip;
        public final int port;
        public final Map<String, String> txt;

        public ResolveReply(@Nullable String hostName, @Nullable IPAddress ip, int port, Map<String, String> txt) {
            this.hostName = hostName;
            this.ip = ip;
            this.port = port;
            this.txt = Collections.unmodifiableMap(txt);
        }
    }

    void resolveService(String serviceName,
                        String regType,
                        String domain,
                        Consumer<ResolveReply> callback);

    void queryIPv6Address(String hostName,
                          Consumer<String> callback);

    void queryIPv4Address(String hostName,
                          Consumer<String> callback);

}
