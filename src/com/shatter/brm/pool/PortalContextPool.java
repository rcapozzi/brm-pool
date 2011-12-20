package com.shatter.brm.pool;

import com.portal.pcm.EBufException;
import com.portal.pcm.FList;
import com.portal.pcm.PortalContext;
//import com.portal.pcm.fields.FldPoid;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * 
 * @author rcapozzi
 */
public class PortalContextPool extends GenericObjectPool {
	private final static Logger log = Logger.getLogger(PortalContextPool.class.getPackage().getName());

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * Returns singleton instance of this class. This is a thread safe call.
	 * 
	 * @return PCMConnectionFactory object
	 */
	public static PortalContextPool getInstance() {
		return ConnectionPoolHolder.INSTANCE;
	}

	private static class ConnectionPoolHolder {
		private static final PortalContextPool INSTANCE = new PortalContextPool();
	}

	/**
	 * Call <tt>opcode</tt> using the pool.
	 * 
	 * @param opcode
	 * @param inFlist
	 * @return
	 * @throws Exception
	 */
	public static FList opcode(int opcode, FList inFlist) throws Exception {
		FList outFlist = null;
		PortalContext portalContext = null;
		PortalContextPool pool = null;

		try {
			pool = PortalContextPool.getInstance();
			portalContext = pool.borrowContext();
			outFlist = portalContext.opcode(opcode, inFlist);
		} catch (EBufException e) {
			if (log.isLoggable(Level.WARNING)) {
				log.warning("Exception calling " + opcode + " with " + inFlist);
			}
			throw e;
		} finally {
			pool.returnObject(portalContext);
		}
		return outFlist;
	}

	/**
	 * Private constructor. Use the getInstance() method instead.
	 */
	private PortalContextPool() {
		super(new PortalContextPoolFactory());
		//log.log(Level.FINE, "Making pool: {0}", this);
		// pool = new GenericObjectPool();
	}

	/**
	 * Borrows a BRM Connection from the pool. It needs to be returned. The pool
	 * creates Objects on demand and up to the configured limit.
	 * 
	 * @return com.portal.pcm.PortalContext
	 * @throws Exception
	 *           if any problems borrowing object form the pool
	 */
	public PortalContext borrowContext() throws Exception {
		//log.log(Level.FINE, "borrowConnection()");
		return (PortalContext) super.borrowObject();
	}

	public Object borrowObject() throws Exception {
		return borrowContext();
	}

	/**
	 * Returns the connection to the pool.
	 * 
	 * @param portalContext of type com.portal.pcm.PortalContext
	 * @throws Exception
	 *           if any problems with returning object to the pool
	 */
	public void returnContext(PortalContext portalContext) throws Exception {
		//log.log(Level.INFO, "returnConnection(...)");
		super.returnObject(portalContext);
	}

	public void returnObject(Object object) throws Exception {
		returnContext((PortalContext) object);
	}

	public String status() throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("active objects: ").append(this.getNumActive());
		sb.append("\nidle objects:").append(getNumIdle());
		String str = sb.toString();
		log.log(Level.INFO, str);
		return str;
	}

	public static void main(String[] args) throws Exception {

		PortalContextPool pool = PortalContextPool.getInstance();
		Logger logger = Logger.getLogger("main");
		logger.setLevel(Level.FINE);
		logger.fine("Starting");

		for (int i = 0; i < 5; i++) {
			logger.log(Level.INFO, "borrow() {0}", i);
			PortalContext ctx = pool.borrowContext();
			// ctx.transactionOpen(0);
			pool.returnContext(null);
		}
	}

}
