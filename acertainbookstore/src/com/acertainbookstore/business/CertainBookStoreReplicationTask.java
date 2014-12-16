package com.acertainbookstore.business;

import java.util.concurrent.Callable;

import com.acertainbookstore.client.ClientProxyForReplicateCall;
import com.acertainbookstore.utils.BookStoreException;
import com.acertainbookstore.utils.BookStoreResult;

/**
 * CertainBookStoreReplicationTask performs replication to a slave server. It
 * returns the result of the replication on completion using ReplicationResult
 */
public class CertainBookStoreReplicationTask implements
		Callable<ReplicationResult> {
	
	String slaveAdress;
	ReplicationRequest rr;
	ClientProxyForReplicateCall clients;

	

	public CertainBookStoreReplicationTask(String slave, ReplicationRequest request) throws Exception {
		slaveAdress = slave;
		rr = request;
		clients = new ClientProxyForReplicateCall();
		}




	@Override
	public ReplicationResult call() throws Exception  {
		ReplicationResult rrs;
		try{
			BookStoreResult rs = clients.forwardRequest(slaveAdress, rr);
			rrs = new ReplicationResult(slaveAdress, true);
		} catch (BookStoreException bse){
			rrs = new ReplicationResult(slaveAdress, false);

		}
		
		return rrs;
	}

}
