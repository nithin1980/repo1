package com.kite.objects;

/**
 * 
 * @author nkumar
 * Holding information on the position
 * Like profit/loss for PE/CE trade.
 * This can be used to influence trade if the position is at loss.
 * 
 *
 */
public class Position {
	
	private double buy=0.0;
	private double sell=0.0;
	private double profit=0.0;
	
	private double expense;
	
	private String name;
	
	private int size=150;
	
	private int buyRecord;
	private int sellRecord;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{name:"+name+",buy:"+buy+",sell:"+sell+",profit:"+profit+",buyRecord:"+buyRecord+",sellRecord:"+sellRecord+"}";
	}
	
	public Position(double expense) {
		// TODO Auto-generated constructor stub
		setExpense(expense);
	}
	public Position(String name,double expense,double buy) {
		// TODO Auto-generated constructor stub
		setExpense(expense);
		setBuy(buy);
		setName(name);
	}
	public Position(String name,double expense,double buy,int size) {
		// TODO Auto-generated constructor stub
		setExpense(expense);
		setBuy(buy);
		setSize(size);
		setName(name);
	}
	
	public double getProfit() {
		if(profit==0.0){
			if(buy>0.0 && sell>0.0){
				profit = sell-buy;
				profit = (profit*size)-expense;
			}
		}
		return profit;
	}
	
	
	
	public int getBuyRecord() {
		return buyRecord;
	}

	public void setBuyRecord(int buyRecord) {
		this.buyRecord = buyRecord;
	}

	public int getSellRecord() {
		return sellRecord;
	}

	public void setSellRecord(int sellRecord) {
		this.sellRecord = sellRecord;
	}

	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public void setProfit(double profit) {
		this.profit = profit;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getBuy() {
		return buy;
	}
	public void setBuy(double buy) {
		this.buy = buy;
	}
	public double getSell() {
		return sell;
	}
	public void setSell(double sell) {
		this.sell = sell;
	}
	public double getExpense() {
		return expense;
	}
	public void setExpense(double expense) {
		this.expense = expense;
	}
	
	

}