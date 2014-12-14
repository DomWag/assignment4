package com.acertainbookstore.business;

import java.util.concurrent.Callable;

/**
 * CertainBookStoreReplicationTask performs replication to a slave server. It
 * returns the result of the replication on completion using ReplicationResult
 */
public class CertainBookStoreReplicationTask implements
		Callable<ReplicationResult> {
	
	String slaveAdress;
	
	public CertainBookStoreReplicationTask(String s) {
		
		slaveAdress = s;
	}

	@Override
	public ReplicationResult call() throws Exception {
		
		
		
		return null;
	}

}
