package com.acertainbookstore.business;

import java.util.concurrent.Callable;

import com.acertainbookstore.client.ClientProxyForReplicateCall;

/**
 * CertainBookStoreReplicationTask performs replication to a slave server. It
 * returns the result of the replication on completion using ReplicationResult
 */
public class CertainBookStoreReplicationTask implements
		Callable<ReplicationResult> {
	
	String slaveAdress;
	ReplicationRequest rr;
	

	

	public CertainBookStoreReplicationTask(String slave, ReplicationRequest request) {
		slaveAdress = slave;
		rr = request;
		}




	@Override
	public ReplicationResult call() throws Exception {
		ClientProxyForReplicateCall clients = new ClientProxyForReplicateCall();
		clients.forwardRequest(slaveAdress, rr);
		
		
		return ;
	}

}
