
# Vertx Best Practices

**Background**

Eclipse Vert.x is event driven and non blocking. This means apps can handle a lot of concurrency using a small number of kernel threads. Vert.x lets your app scale with minimal hardware. This document gentle introduction of the best practises to be fallowed in case if support team need to add/update any vertx stages.

The target users are -
Application support team

**Below are the various aspects considered while implementing Vertx*

The key solution considerations are -
1.	Event Loop Instances
	Vertx is single threaded event loop which delivers events to all handlers as they arrive. Vertx instance maintains several event loops, number of event loop depends on the number of cores available.
	
	If you have a single event loop, and you want to handle 10000 http requests per second, then it’s clear that each request can’t take more than 0.1 ms to process, so you can’t block for any more time than that.
	
	The best way to handle blocking code which could be opertations like long lived database operation and waiting for a result, complex calculations which take significant amount of time etc. is allow this code to execute in another thread. Vertx provides API to handle such blocking code which can be done by using executeBlocking as shown below:
	
2.	Running blocking code
	There could be scenerio where single threaded event loop could be blocked in which case stages will grind to a complete halt!

3.  Threading

4. 	Scheduler

5.  Deployment