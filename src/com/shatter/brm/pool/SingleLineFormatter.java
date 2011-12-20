package com.shatter.brm.pool;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import com.shatter.brm.pool.PortalContextPool;


public final class SingleLineFormatter extends Formatter {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static SimpleDateFormat FRMT_DATE;

	public SingleLineFormatter() {
		String dateFormat = System.getProperty("java.util.logging.dateFormat",
				"yyyy.MM.dd.HH.mm.ss");
		FRMT_DATE = new SimpleDateFormat(dateFormat);
	}

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();

		// format time
		sb.append(FRMT_DATE.format(record.getMillis()));

		sb.append(" ").append(record.getLevel().getName().charAt(0))
				.append(": ").append(formatMessage(record))
				.append(LINE_SEPARATOR);

		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception ex) {
				// ignore
			}
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		
		Logger logger = Logger.getLogger(SingleLineFormatter.class.getPackage().getName());
		
  	Handler handler = new StreamHandler();
  	Formatter formatter = new SingleLineFormatter();
  	handler.setFormatter(formatter);
  	logger.addHandler(handler);

		// Log a few message at different severity levels
		logger.severe("severe message");
		logger.warning("warning message");
		logger.info("info message");
		logger.config("config message");
		logger.fine("fine message");
		logger.finer("finer message");
		logger.finest("finest message");
	}
}