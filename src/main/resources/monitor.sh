#!/bin/bash
while(true)
do

	time1=`date`
	`echo  $time1 '开始监控应用...' >> /root/monitor//monitor.log`
	#检测应用是否启动
	pid=`ps -ef|grep java|grep 'apache-tomcat-8.5.32/conf'|awk '{print $2}'`
	if [ -z "$pid" ]
	then
			`echo $time '应用未启动，开始启动应用...' >> /root/monitor.log`
			pid=`ps -ef|grep java|grep 'apache-tomcat-8.5.32/conf'|awk '{print $2}'`
			echo $pid
			kill -9 $pid
			/root/apache-tomcat-8.5.32/bin/shutdown.sh
			/root/apache-tomcat-8.5.32/bin/startup.sh
			`echo $time '应用启动完成...' >> /root/monitor/monitor.log`
			sleep 60s
			continue
	fi
	#检测程序是否宕机
	curl --connect-timeout 3 -m 3 http://127.0.0.1
	resultstatus=$?
	if [ $resultstatus -ne 0 ]
	then
			`echo $time '应用宕机，状态为' $resultstatus '开始重新启动应用...' >> /root/monitor.log`
			pid=`ps -ef|grep java|grep 'apache-tomcat-8.5.32/conf'|awk '{print $2}'`
			echo $pid
			kill -9 $pid
			/root/apache-tomcat-8.5.32/bin/shutdown.sh
			/root/apache-tomcat-8.5.32/bin/startup.sh
			`echo $time '应用重新启动完成...' >> /root/monitor//monitor.log`
			sleep 60s
			continue
	fi
	
	time2=`date`
	`echo  $time2 '结束监控应用...' >> /root/monitor//monitor.log`
	sleep 10s
done