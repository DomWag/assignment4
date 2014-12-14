/**
 * 
 */
package com.acertainbookstore.server;

import com.acertainbookstore.business.ReplicationRequest;
import com.acertainbookstore.business.SlaveCertainBookStore;
import com.acertainbookstore.interfaces.Replication;
import com.acertainbookstore.utils.BookStoreConstants;

/**
 * Starts the slave bookstore HTTP server.
 */
public class SlaveBookStoreHTTPServer implements Replication {

	/**
	 * @param args
	 *            Not being used now
	 */
	
	SlaveCertainBookStore bookStore;
	
	public static void main(String[] args) {
		SlaveCertainBookStore bookStore = new SlaveCertainBookStore();
		int listen_on_port = 8081;
		SlaveBookStoreHTTPMessageHandler handler = new SlaveBookStoreHTTPMessageHandler(
				bookStore);
		String server_port_string = System
				.getProperty(BookStoreConstants.PROPERTY_KEY_SERVER_PORT);
		if (server_port_string != null) {
			try {
				listen_on_port = Integer.parseInt(server_port_string);
			} catch (NumberFormatException ex) {
				System.err.println(ex);
			}
		}
		if (BookStoreHTTPServerUtility.createServer(listen_on_port, handler)) {
			;
		}
	}

	@Override
	public void replicate(ReplicationRequest req) {
		
		
		
	}
}
