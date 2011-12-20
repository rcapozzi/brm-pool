package com.shatter.brm.pool;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.shatter.brm.pool.PortalContextPool;

public class TestPool {
	public static void main(String[] args) throws Exception {
		PortalContextPool pool = PortalContextPool.getInstance();
		Logger logger = Logger.getLogger("main");
		logger.fine("Starting");

		for (int i = 0; i < 10; i++) {
			logger.log(Level.FINE, "borrow() {0}", i);
			pool.borrowContext();
		}
	}

}
