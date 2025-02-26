package com.mod.interfaces;

import java.math.BigDecimal;

public class StreamingQuoteModeLtp extends StreamingQuote{
	public BigDecimal ltp;
	
	public StreamingQuoteModeLtp() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor
	 * @param time
	 * @param instrumentToken
	 * @param ltp
	 */
	public StreamingQuoteModeLtp(String time, String instrumentToken, BigDecimal ltp) {
		super(time, instrumentToken);
		this.ltp = ltp;
	}

	public BigDecimal getLtp() {
		return ltp;
	}
	public void setLtp(BigDecimal ltp) {
		this.ltp = ltp;
	}

	@Override
	public String toString() {
		return "StreamingQuoteModeLtp [ltp=" + ltp + ", time=" + time + ", instrumentToken=" + instrumentToken + "]";
	}
}
