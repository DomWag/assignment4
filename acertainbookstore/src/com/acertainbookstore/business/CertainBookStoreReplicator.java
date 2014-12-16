package com.acertainbookstore.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.acertainbookstore.interfaces.Replicator;

/**
 * CertainBookStoreReplicator is used to replicate updates to slaves
 * concurrently.
 */
public class CertainBookStoreReplicator implements Replicator {
	
	private final ExecutorService pool ;
	
	public CertainBookStoreReplicator(int maxReplicatorThreads) {
		
		pool = Executors.newFixedThreadPool(maxReplicatorThreads);
	}

	public List<Future<ReplicationResult>> replicate(Set<String> slaveServers,
			ReplicationRequest request) {
		
		// Future holds the future Replication Result
		List<Future<ReplicationResult>> future = new ArrayList<Future<ReplicationResult>>();
		
		for(String s: slaveServers){
			
			// Creates a replication 
			CertainBookStoreReplicationTask task;
			try {
				task = new CertainBookStoreReplicationTask(s, request);
				// submit returns the Future<Replication Result> which is added to the return list
				future.add(pool.submit(task));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return future;
	}
}
