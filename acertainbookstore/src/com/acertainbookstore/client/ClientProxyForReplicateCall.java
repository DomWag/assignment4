package com.acertainbookstore.client;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.acertainbookstore.business.ReplicationRequest;
import com.acertainbookstore.business.ReplicationResult;
import com.acertainbookstore.utils.BookStoreException;
import com.acertainbookstore.utils.BookStoreMessageTag;
import com.acertainbookstore.utils.BookStoreResult;
import com.acertainbookstore.utils.BookStoreUtility;

public class ClientProxyForReplicateCall {
	private HttpClient client;

	
	public ClientProxyForReplicateCall() throws Exception {
	client = new HttpClient();
	client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
	client.setMaxConnectionsPerAddress(BookStoreClientConstants.CLIENT_MAX_CONNECTION_ADDRESS); // max
																								// concurrent
																								// connections
																								// to
																								// every
																								// address
	client.setThreadPool(new QueuedThreadPool(
			BookStoreClientConstants.CLIENT_MAX_THREADSPOOL_THREADS)); // max
																		// threads
	client.setTimeout(BookStoreClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS); // seconds
																				// timeout;
																				// if
																				// no
																				// server
																				// reply,
																				// the
																				// request
																				// expires
	client.start();
	}
	
	
	public ReplicationResult forwardRequest(String slaveAdress, ReplicationRequest rr) throws BookStoreException{
		ReplicationResult result = null;
		String listISBNsxmlString = BookStoreUtility
				.serializeObjectToXMLString(rr);
		Buffer requestContent = new ByteArrayBuffer(listISBNsxmlString);
		String urlString = slaveAdress
				+ BookStoreMessageTag.REPLICATIONREQUEST;
		ContentExchange exchange = new ContentExchange();

		exchange.setMethod("POST");
		exchange.setURL(urlString);
		exchange.setRequestContent(requestContent);
		result = BookStoreUtility.SendAndRecv2(this.client, exchange);
		return result;
	}
}
