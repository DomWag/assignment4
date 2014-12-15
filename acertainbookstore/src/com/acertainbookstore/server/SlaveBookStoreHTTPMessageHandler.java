/**
 * 
 */
package com.acertainbookstore.server;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.acertainbookstore.business.BookCopy;
import com.acertainbookstore.business.BookEditorPick;
import com.acertainbookstore.business.ReplicationRequest;
import com.acertainbookstore.business.ReplicationResult;
import com.acertainbookstore.business.SlaveCertainBookStore;
import com.acertainbookstore.business.StockBook;
import com.acertainbookstore.utils.BookStoreConstants;
import com.acertainbookstore.utils.BookStoreException;
import com.acertainbookstore.utils.BookStoreMessageTag;
import com.acertainbookstore.utils.BookStoreResponse;
import com.acertainbookstore.utils.BookStoreUtility;

/**
 * 
 * SlaveBookStoreHTTPMessageHandler implements the message handler class which
 * is invoked to handle messages received by the slave book store HTTP server It
 * decodes the HTTP message and invokes the SlaveCertainBookStore API
 * 
 */
public class SlaveBookStoreHTTPMessageHandler extends AbstractHandler {
	private SlaveCertainBookStore myBookStore = null;

	public SlaveBookStoreHTTPMessageHandler(SlaveCertainBookStore bookStore) {
		myBookStore = bookStore;
	}

	@SuppressWarnings("unchecked")
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		BookStoreMessageTag messageTag;
		String numBooksString = null;
		int numBooks = -1;
		String requestURI;
		BookStoreResponse bookStoreResponse = null;
		ReplicationResult replicationResult = null;

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		requestURI = request.getRequestURI();

		// Need to do request multi-plexing
		if (!BookStoreUtility.isEmpty(requestURI)
				&& requestURI.toLowerCase().startsWith("/stock")) {
			messageTag = BookStoreUtility.convertURItoMessageTag(requestURI
					.substring(6)); // the request is from store
			// manager, more
			// sophisticated security
			// features could be added
			// here
		} else {
			messageTag = BookStoreUtility.convertURItoMessageTag(requestURI);
		}
		// the RequestURI before the switch
		if (messageTag == null) {
			System.out.println("Unknown message tag");
		} else {

			// Write requests should not be handled
			switch (messageTag) {

			case LISTBOOKS:
				bookStoreResponse = new BookStoreResponse();
				try {
					bookStoreResponse.setResult(myBookStore.getBooks());
				} catch (BookStoreException ex) {
					bookStoreResponse.setException(ex);
				}
				response.getWriter().println(
						BookStoreUtility
								.serializeObjectToXMLString(bookStoreResponse));
				break;

			case GETBOOKS:
				String xml = BookStoreUtility
						.extractPOSTDataFromRequest(request);
				Set<Integer> isbnSet = (Set<Integer>) BookStoreUtility
						.deserializeXMLStringToObject(xml);

				bookStoreResponse = new BookStoreResponse();
				try {
					bookStoreResponse.setResult(myBookStore.getBooks(isbnSet));
				} catch (BookStoreException ex) {
					bookStoreResponse.setException(ex);
				}
				response.getWriter().println(
						BookStoreUtility
								.serializeObjectToXMLString(bookStoreResponse));
				break;

			case EDITORPICKS:
				numBooksString = URLDecoder
						.decode(request
								.getParameter(BookStoreConstants.BOOK_NUM_PARAM),
								"UTF-8");
				bookStoreResponse = new BookStoreResponse();
				try {
					numBooks = BookStoreUtility
							.convertStringToInt(numBooksString);
					bookStoreResponse.setResult(myBookStore
							.getEditorPicks(numBooks));
				} catch (BookStoreException ex) {
					bookStoreResponse.setException(ex);
				}
				response.getWriter().println(
						BookStoreUtility
								.serializeObjectToXMLString(bookStoreResponse));
				break;

			case GETSTOCKBOOKSBYISBN:
				xml = BookStoreUtility.extractPOSTDataFromRequest(request);
				isbnSet = (Set<Integer>) BookStoreUtility
						.deserializeXMLStringToObject(xml);

				bookStoreResponse = new BookStoreResponse();
				try {
					bookStoreResponse.setResult(myBookStore
							.getBooksByISBN(isbnSet));
				} catch (BookStoreException ex) {
					bookStoreResponse.setException(ex);
				}
				response.getWriter().println(
						BookStoreUtility
								.serializeObjectToXMLString(bookStoreResponse));
				break;
			case REPLICATIONREQUEST:
				String bla = BookStoreUtility
						.extractPOSTDataFromRequest(request);
				ReplicationRequest rr = (ReplicationRequest) BookStoreUtility
						.deserializeXMLStringToObject(bla);
				replicationResult = null;
				BookStoreMessageTag messageTags = rr.getMessageType();
				switch (messageTags) {
				case ADDBOOKS:
					Set<StockBook> newBooks = (Set<StockBook>) rr.getDataSet();
					try {
						replicationResult = new ReplicationResult(requestURI,
								true);
						myBookStore.addBooks(newBooks);

					} catch (BookStoreException ex) {
						replicationResult = new ReplicationResult(requestURI,
								false);
					}
					String listBooksxmlString = BookStoreUtility
							.serializeObjectToXMLString(replicationResult);

					response.getWriter().println(listBooksxmlString);

					break;
				case ADDCOPIES:
					Set<BookCopy> listBookCopies = (Set<BookCopy>) rr
							.getDataSet();
					try {
						replicationResult = new ReplicationResult(requestURI,
								true);
						myBookStore.addCopies(listBookCopies);
					} catch (BookStoreException ex) {
						replicationResult = new ReplicationResult(requestURI,
								false);
					}
					String listBooksxmlString2 = BookStoreUtility
							.serializeObjectToXMLString(replicationResult);

					response.getWriter().println(listBooksxmlString2);
					break;

				case BUYBOOKS:
					Set<BookCopy> bookCopiesToBuy = (Set<BookCopy>) rr
							.getDataSet();
					try {
						replicationResult = new ReplicationResult(requestURI,
								true);
						myBookStore.buyBooks(bookCopiesToBuy);
					} catch (BookStoreException ex) {
						replicationResult = new ReplicationResult(requestURI,
								false);
					}

					String listBooksxmlString3 = BookStoreUtility
							.serializeObjectToXMLString(replicationResult);

					response.getWriter().println(listBooksxmlString3);
					break;

				case UPDATEEDITORPICKS:
					Set<BookEditorPick> mapEditorPicksValues = (Set<BookEditorPick>) rr
							.getDataSet();
					try {
						replicationResult = new ReplicationResult(requestURI,
								true);
						myBookStore.updateEditorPicks(mapEditorPicksValues);
					} catch (BookStoreException e) {
						replicationResult = new ReplicationResult(requestURI,
								false);
					}
					
					String listBooksxmlString4 = BookStoreUtility
							.serializeObjectToXMLString(replicationResult);

					response.getWriter().println(listBooksxmlString4);
					break;
				case REMOVEALLBOOKS:
					try {
						replicationResult = new ReplicationResult(requestURI,
								true);
						myBookStore.removeAllBooks();
					} catch (BookStoreException e) {
						replicationResult = new ReplicationResult(requestURI,
								false);
					}
					
					String listBooksxmlString5 = BookStoreUtility
							.serializeObjectToXMLString(replicationResult);

					response.getWriter().println(listBooksxmlString5);
					break;
					
				case REMOVEBOOKS:
					Set<Integer> bookSet = (Set<Integer>) rr.getDataSet();
					try {
						replicationResult = new ReplicationResult(requestURI,
								true);
						myBookStore.removeBooks(bookSet);
					} catch (BookStoreException e) {
						replicationResult = new ReplicationResult(requestURI,
								false);
					}
					
					String listBooksxmlString6 = BookStoreUtility
							.serializeObjectToXMLString(replicationResult);

					response.getWriter().println(listBooksxmlString6);
					break;

				default:
					break;
				}

			default:
				System.out.println("Unhandled message tag");
				break;
			}
		}
		// Mark the request as handled so that the HTTP response can be sent
		baseRequest.setHandled(true);

	}
}
