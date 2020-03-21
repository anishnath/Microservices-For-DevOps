#!/bin/sh
redis-server --daemonize yes --port 6379
exec /opt/tomcat/bin/catalina.sh run