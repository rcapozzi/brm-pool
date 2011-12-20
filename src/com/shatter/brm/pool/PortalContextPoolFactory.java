/**
 * Creates PCM connections to BRM.
 *
 * Must have Infranet.properties in the CLASSPATH so that the PortalContext
 * can be created properly.
 *
 * On destruction of the object pool, the destroyObject method will be called
 * for all PortalContext objects in the pool. Any open transactions will be ignored
 * during the closing. It is the responsibility of the caller to handle opening
 * and closing of any transactions.
 *
 */
package com.shatter.brm.pool;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.pool.BasePoolableObjectFactory;
import com.portal.pcm.EBufException;
import com.portal.pcm.PortalContext;
import com.portal.pcm.fields.FldOpCorrelationId;

class PortalContextPoolFactory extends BasePoolableObjectFactory {

	private final static Logger log = Logger.getLogger("ConnectionPoolFactory");
	static public boolean doConnect = true;

	/**
	 * Creates an instance that can be served by the pool. Instances returned from
	 * this method should be in the same state as if they had been activated. They
	 * will not be activated before being served by the pool.
	 * 
	 * Uses PortalContext.connect() method which assumes an Infranet.properties
	 * file is in the CLASSPATH
	 * 
      Infranet.connect    = Connection_string
      Infranet.login.type = 1 # If no cm proxy
      
      For JRuby, the properties can be in the cwd.
	 * 
	 * @return com.portal.pcm.PortalContext
	 * @throws Exception
	 *           if we have problems connecting to BRM
	 */
	@Override
	public Object makeObject() throws Exception{
		PortalContext.setAppName("ContextPool");
		FldOpCorrelationId.getInst();
		PortalContext portalContext = null;
		if (PortalContextPoolFactory.doConnect){
			portalContext = new PortalContext();
			portalContext.connect();
		}
		if (log.isLoggable(Level.INFO)) {
			log.info("makeObject()");
		}
		return portalContext;
	}

	/**
	 * No-Op method that is called when a connection is borrowed from the pool. In other words, when you borrow, we activate. In the case of BRM, there is nothing to do.
	 * 
	 * TODO: Reset a connection that is in a transaction.
	 * 
	 * @param obj
	 *          of type com.portal.pcm.PortalContext
	 * @throws Exception
	 */
	@Override
	public void activateObject(Object obj) {
		//log.log(Level.FINE, "activateObject: {0}", obj);
	}

	/**
	 * Un-initialize an instance to be returned to the idle object pool.
	 * 
	 * @param obj
	 *          of type com.portal.pcm.PortalContext
	 * @throws Exception
	 */
	@Override
	public void passivateObject(Object obj) {
		//		if (log.isLoggable(Level.FINE)) {
		//			log.fine("passivateObject: closing portalContext : " + obj);
		//		}
	}

	/**
	 * Ensure the PortalContext is closed.
	 * 
	 * @param obj
	 *          of type com.portal.pcm.PortalContext
	 */
	@Override
	public void destroyObject(Object obj) {
		if (log.isLoggable(Level.FINE)) {
			log.fine("destroyObject: Closing PortalContext " + obj);
		}

		try {
			PortalContext portalContext = (PortalContext) obj;
			portalContext.close(true);
			super.destroyObject(obj);
		} catch (EBufException e) {
			log.severe("Exception closing PortalContext: " + e.getStackTrace());
		} catch (Exception e) {
			log.severe("Exception finalizing PortalContext: " + e.getStackTrace());
		}
	}
}
