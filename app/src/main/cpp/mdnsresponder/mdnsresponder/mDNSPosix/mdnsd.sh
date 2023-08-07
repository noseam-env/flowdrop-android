#!/bin/sh
#
# This file is part of FlowDrop Android.
#
# For license and copyright information please follow this link:
# https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
#

if [ -r /usr/sbin/mdnsd ]; then
    DAEMON=/usr/sbin/mdnsd
else
    DAEMON=/usr/local/sbin/mdnsd
fi

test -r $DAEMON || exit 0

# Some systems have start-stop-daemon, some don't. 
if [ -r /sbin/start-stop-daemon ]; then
	START="start-stop-daemon --start --quiet --exec"
	# Suse Linux doesn't work with symbolic signal names, but we really don't need
	# to specify "-s TERM" since SIGTERM (15) is the default stop signal anway
	# STOP="start-stop-daemon --stop -s TERM --quiet --oknodo --exec"
	STOP="start-stop-daemon --stop --quiet --oknodo --exec"
else
	killmdnsd() {
		kill -TERM `cat /var/run/mdnsd.pid`
	}
	START=
	STOP=killmdnsd
fi

case "$1" in
    start)
	echo -n "Starting Apple Darwin Multicast DNS / DNS Service Discovery daemon:"
	echo -n " mdnsd"
        $START $DAEMON
	echo "."
	;;
    stop)
        echo -n "Stopping Apple Darwin Multicast DNS / DNS Service Discovery daemon:"
        echo -n " mdnsd" ; $STOP $DAEMON
        echo "."
	;;
    reload|restart|force-reload)
		echo -n "Restarting Apple Darwin Multicast DNS / DNS Service Discovery daemon:"
		$STOP $DAEMON
		sleep 1
		$START $DAEMON
		echo -n " mdnsd"
	;;
    *)
	echo "Usage: /etc/init.d/mDNS {start|stop|reload|restart}"
	exit 1
	;;
esac

exit 0
