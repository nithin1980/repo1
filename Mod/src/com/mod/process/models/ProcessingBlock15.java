package com.mod.process.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mod.objects.GroupPosition;
import com.mod.objects.Position;
import com.mod.support.ApplicationHelper;

public class ProcessingBlock15 extends ProcessModelAbstract {

	
    private String openingState;
    
    private int counter;
    
    private static Map<Long, Double> PREVIOUS_lTP = new HashMap<Long, Double>(); 
    
    
	public ProcessingBlock15(CacheService cacheService) {
		super();
		setCacheService(cacheService);
	}    
    
	@Override
	public String modelid() {
		// TODO Auto-generated method stub
		return "pmodel15";
	}
	
	
	@Override
	public void processNow() {
		// TODO Auto-generated method stub

		completedProcess=false;
		
		try {
			if(counter==0) {
				System.out.println("Staring Model 15 counter");
			}
			//boolean freekPrice = validateFrekPriceChange();
			//System.out.println("freek price:"+freekPrice);
			boolean freekPrice=false;
			if(counter<15 && !freekPrice) {
				counter++;
			}else if(!freekPrice){
				GroupPosition groupPosition = DashBoard.positionMap.get(modelid());
				
				Iterator<Position> itr = groupPosition.getCePositions().iterator();
				

				
				double ce_id=0;
				double pe_id=0;
				Position pos = null;
				Position reverse = null;
				double profitPer = 0;
				int index=0;
				System.out.println("Time:"+new Date());
				
				/***
				 *  New code to record just the price.
				 */
				printPrices();
				
				
//				while(itr.hasNext()) {
//					pos = itr.next();
//					reverse = pos.getReversePosition();
//					pos.setSell(getCacheService().PRICE_LIST.get(pos.getId()));
//					reverse.setSell(getCacheService().PRICE_LIST.get(reverse.getId()));
//					profitPer = profitPercen(pos, reverse);
//					//System.out.println(pos.getId()+","+reverse.getId()+","+pos.getBuy()+","+reverse.getBuy()+","+pos.getSell()+","+reverse.getSell()+","+profit(pos, reverse)+","+profitPer+","+pos.profitPercentage()+","+reverse.profitPercentage());
//					
////					if(profitPer<-10) {
////						addNewPosition(pos, pos.getId(), reverse.getId());
////						groupPosition.getCePositions().set(counter, pos);
////						System.out.println("Position Added:"+pos.getId()+","+reverse.getId());
////					}
//					
//					index++;
//					
//				}
				
				counter=0;
				
			}
		} finally {
			// TODO Auto-generated catch block
			completedProcess=true;
		}
		
		completedProcess=true;
	}
	
	private boolean validateFrekPriceChange() {
		
		Iterator<Long> itr = getCacheService().PRICE_LIST.keySet().iterator();
		Long key =  null;
		
		if(PREVIOUS_lTP.size()==0) {
			while(itr.hasNext()) {
				key = itr.next();
				PREVIOUS_lTP.put(key, getCacheService().PRICE_LIST.get(key));
				//System.out.println("R_C,"+key.doubleValue()+","+getCacheService().PRICE_LIST.get(key).doubleValue());
			}

		}
		
		Iterator<Long> previousitr = PREVIOUS_lTP.keySet().iterator();
		double currentVal = 0;
		double previousVal = 0;
		double percenChange = 0;
		while(previousitr.hasNext()) {
			key = previousitr.next();
			currentVal = getCacheService().PRICE_LIST.get(key).doubleValue();
			previousVal = PREVIOUS_lTP.get(key).doubleValue();
			percenChange = ((currentVal-previousVal)/previousVal)*100;
			if(percenChange>25) {
				System.out.println("Freek Price");
				printPrices();
			}
			return true;
		}
		
		return false;
	}
	
	
	private void printPrices() {
		
		Iterator<Long> itr = getCacheService().PRICE_LIST.keySet().iterator();
		Long key =  null;
		while(itr.hasNext()) {
			key = itr.next();
			System.out.println("R_C,"+key.doubleValue()+","+getCacheService().PRICE_LIST.get(key).doubleValue());
			PREVIOUS_lTP.put(key, getCacheService().PRICE_LIST.get(key));
		}
		
		
	}
	
	private void addNewPosition(Position currenPosition,long ce_id,long pe_id) {
		double position_val = ApplicationHelper.positionVal("pmodel15");
		int lot_size = ApplicationHelper.lotsize("pmodel15");

		double cost = CacheService.PRICE_LIST.get(ce_id);

		int size =ApplicationHelper.calculateSize(position_val, cost, lot_size); 
		size=size*lot_size;
		Position pos = new Position(ce_id,"CE", 100.00,cost,size);
		
		//clear
		size=0;
		cost=0;
		
		//Adding PE on reverse
		cost = CacheService.PRICE_LIST.get(pe_id);
		size =ApplicationHelper.calculateSize(position_val, cost, lot_size); 
		size=size*lot_size;
		pos.setReversePosition(new Position(pe_id,"PE", 100.00,cost,size)); 
		
		currenPosition.addNewAndAveragePosition(pos);
		currenPosition.getReversePosition().addNewAndAveragePosition(pos.getReversePosition());
		
		
		
	}
	/**
	 * Check the opening value and see if 
	 * open is really high, flat or really low.
	 * @param current
	 */
	private double profit(Position pos,Position reverse) {
		double profit = pos.getProfit()+reverse.getProfit();
		return profit;
	}
	
	private double profitPercen(Position pos,Position reverse) {
		double totalCost = pos.cost()+reverse.cost();
		double profit = profit(pos,reverse);
		
		return (profit/totalCost)*100;
	}
//	private void stateCheck(){
//		
//		double bnifty = CacheService.PRICE_LIST.get(260105.0);
//		if(openingState==null){
//			if(bnifty-prev_nifty>0.5 || bnifty-prev_nifty<0.5 ){
//				double openPercentage = percen(bnifty, prev_nifty);
//				if(openPercentage>0.2){
//					openingState= ObservedState.UP_HIGH;
//				}else if(openPercentage>0 && openPercentage<=0.2){
//					openingState= ObservedState.UP_FLAT;
//				}else if(openPercentage<-0.2){
//					openingState= ObservedState.DOWN_LOW;
//				}else if(openPercentage<0 && openPercentage>=-0.2){
//					openingState= ObservedState.DOWN_FLAT;
//				}
//			}
//		}
//		
//		/**
//		 * Remove this..only for testing...should not be null during normal ops.
//		 */
//		openingState=null;
//	}
	private boolean entry(double prevDay,double prevVal,double currentVal){
		if(prevVal==0){
			prevVal=currentVal;
		}
		
		double diff = currentVal-prevVal;
		if(diff !=0 && diff>.1){
			if(percen(currentVal, prevVal)<-25){
				System.out.println("b loop:"+currentVal);
				return true;
			}
			
		}else if(percen(currentVal, prevVal)<-38){
			System.out.println("b:"+currentVal);
			return true;
		}
		
		return false;
	}
	
	private static double percen(double current,double prev){
		return ((current-prev)/prev)*100;
	}	
	

}
