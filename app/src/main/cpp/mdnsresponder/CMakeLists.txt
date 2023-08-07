# This file is part of FlowDrop Android.
#
# For license and copyright information please follow this link:
# https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL

include_directories("${CMAKE_CURRENT_SOURCE_DIR}/mdnsresponder/mDNSShared")

set(commonFlags
        -O2 -g
        -fno-strict-aliasing
        -D_GNU_SOURCE
        -DHAVE_IPV6
        -DHAVE_LINUX
        -DNOT_HAVE_SA_LEN
        -DPLATFORM_NO_RLIMIT
        -DTARGET_OS_LINUX
        -DUSES_NETLINK
        -DMDNS_DEBUGMSGS=0
        -DMDNS_UDS_SERVERPATH=\"/dev/socket/mdnsd\"
        -DMDNS_USERNAME=\"mdnsr\"
        -W
        -Wall
        -Wextra
        -Wno-array-bounds
        -Wno-pointer-sign
        -Wno-unused
        -Wno-unused-parameter
        -Werror=implicit-function-declaration
        )

set(commonIncludes
        ${CMAKE_CURRENT_SOURCE_DIR}/mdnsresponder/mDNSPosix
        ${CMAKE_CURRENT_SOURCE_DIR}/mdnsresponder/mDNSCore
        ${CMAKE_CURRENT_SOURCE_DIR}/mdnsresponder/mDNSShared
        ${CMAKE_CURRENT_SOURCE_DIR}/external/mdnsresponder/mDNSShared
        )

set(commonSources
        mdnsresponder/mDNSShared/dnssd_clientlib.c
        mdnsresponder/mDNSShared/dnssd_clientstub.c
        mdnsresponder/mDNSShared/dnssd_ipc.c
        )

set(embeddedSources
        mdnsresponder/mDNSCore/mDNS.c
        mdnsresponder/mDNSCore/DNSDigest.c
        mdnsresponder/mDNSCore/uDNS.c
        mdnsresponder/mDNSPosix/mDNSPosix.c
        mdnsresponder/mDNSPosix/mDNSUNP.c
        mdnsresponder/mDNSShared/mDNSDebug.c
        mdnsresponder/mDNSShared/dnssd_clientlib.c
        mdnsresponder/mDNSShared/dnssd_clientshim.c
        mdnsresponder/mDNSShared/dnssd_ipc.c
        mdnsresponder/mDNSShared/GenLinkedList.c
        mdnsresponder/mDNSShared/PlatformCommon.c
        mdnsresponder/mDNSCore/DNSCommon.c
        mdnsresponder/mDNSPosix/PosixDaemon.c
        mdnsresponder/mDNSShared/uds_daemon.c
        )

# Add the daemon library target
# hihihi its SHARED before me :)
add_library(my_mdnsresponder STATIC ${commonSources})
target_include_directories(my_mdnsresponder PRIVATE ${commonIncludes})
target_compile_options(my_mdnsresponder PRIVATE ${commonFlags})
target_link_libraries(my_mdnsresponder log)

# Add the embedded library target
# hihihi its SHARED before me :)
add_library(my_mdnsresponder_embedded STATIC ${embeddedSources})
target_include_directories(my_mdnsresponder_embedded PRIVATE ${commonIncludes})
target_compile_options(my_mdnsresponder_embedded PRIVATE ${commonFlags} -UMDNS_DEBUGMSGS -DMDNS_DEBUGMSGS=0 -DSO_REUSEADDR -DUNICAST_DISABLED -DMDNS_VERSIONSTR_NODTS=1 -DAUTO_CALLBACKS=1 -DEMBEDDED)
target_link_libraries(my_mdnsresponder_embedded log)
