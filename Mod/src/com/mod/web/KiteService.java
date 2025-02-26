package com.mod.web;

import java.util.Iterator;
import java.util.Map;

import com.mod.interfaces.KiteStockConverter;
import com.mod.objects.GroupPosition;
import com.mod.objects.Position;
import com.mod.order.Order;
import com.mod.process.models.CacheService;
import com.mod.process.models.DashBoard;
import com.mod.process.models.ProcessModelAbstract;
import com.mod.process.models.ProcessingBlock11;
import com.mod.process.models.ProcessingBlock12;
import com.mod.process.models.ProcessingBlock13;
import com.mod.process.models.ProcessingBlock14;
import com.mod.process.models.ProcessingBlock15;
import com.mod.support.ApplicationHelper;

public class KiteService {

	private static final Order orderInterface = new Order();
	
	private static int rangedModelId=11;
	
	public static void orderPE() throws RuntimeException{
		GroupPosition groupPosition = DashBoard.positionMap.get("pmodel10");
		
		double position_val = ApplicationHelper.positionVal("pmodel10");
		int lot_size = ApplicationHelper.lotsize("pmodel10");
		long pe_id = ApplicationHelper.getPositionId("pmodel10", "pe_id");
		double cost = CacheService.PRICE_LIST.get(pe_id);
		
		int size = ApplicationHelper.calculateSize(position_val, cost, lot_size); 
				
				//(int)(Double.valueOf(position_val)/(cost*lot_size));
		size=size*lot_size;
		
		groupPosition.getPePositions().add(new Position("PE", 100.00,cost,size));
		
		orderInterface.orderKiteOption(null);
	}
	
	public static void orderCE() throws RuntimeException{
		GroupPosition groupPosition = DashBoard.positionMap.get("pmodel10");

		double position_val = ApplicationHelper.positionVal("pmodel10");
		int lot_size = ApplicationHelper.lotsize("pmodel10");
		
		long ce_id = ApplicationHelper.getPositionId("pmodel10", "ce_id");
		double cost = CacheService.PRICE_LIST.get(ce_id);

		int size =ApplicationHelper.calculateSize(position_val, cost, lot_size); 
				//(int)(Double.valueOf(position_val)/(cost*lot_size));
		size=size*lot_size;
		
		groupPosition.getCePositions().add(new Position("CE", 100.00,cost,size));
		
		orderInterface.orderKiteOption(null);
	}
	public static void orderBoth() throws RuntimeException{
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				orderPE();
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				orderCE();
			}
		}).start();
	}
	
	public static void orderEquals() throws RuntimeException{
		String model="pmodel"+rangedModelId;
		GroupPosition groupPosition = DashBoard.positionMap.get(model);

		double position_val = ApplicationHelper.positionVal(model);
		int lot_size = ApplicationHelper.lotsize(model);
		
		double[] ranges = ApplicationHelper.getPriceRange(model);
		
		double minPrice = ranges[0];
		double maxPrice= ranges[1];
		
		
		double cost = 0;
		Long key = null;
		int size=0;
		Iterator<Long> keys  = KiteStockConverter.BN_CE_LIST.keySet().iterator();
		
		boolean addModel=false;
		
		while(keys.hasNext()){
			key = keys.next();
			if(CacheService.PRICE_LIST.get(key)>=minPrice && CacheService.PRICE_LIST.get(key)<=maxPrice ){
				cost = CacheService.PRICE_LIST.get(key);
				size =ApplicationHelper.calculateSize(position_val, cost, lot_size);
				size=size*lot_size;
				groupPosition.getCePositions().add(new Position("CE", 100.00,cost,size));
				ApplicationHelper.modeConfig(model).getKeyValueConfigs().put("ce_id", String.valueOf(key.doubleValue()));
				
				addModel=true;
				
			}
		}
		
		cost=0;
		key=null;
		size=0;
		minPrice = ranges[2];
		maxPrice=ranges[3];
		
		if(!addModel) {
			System.out.println("No CE position taken...!!!!!");
			return;
		}else {
			addModel=false;
		}
		
		keys  = KiteStockConverter.BN_PE_LIST.keySet().iterator();
		while(keys.hasNext()){
			key = keys.next();
			if(CacheService.PRICE_LIST.get(key)>=minPrice && CacheService.PRICE_LIST.get(key)<=maxPrice ){
				cost = CacheService.PRICE_LIST.get(key);
				size =ApplicationHelper.calculateSize(position_val, cost, lot_size);
				size=size*lot_size;
				groupPosition.getPePositions().add(new Position("PE", 100.00,cost,size));
				ApplicationHelper.modeConfig(model).getKeyValueConfigs().put("pe_id", String.valueOf(key.doubleValue()));
				addModel=true;
			}
		}

		if(!addModel) {
			System.out.println("No PE position taken....!!!!!");
			return;
		}
		
		boolean modelAdded = false;		//(int)(Double.valueOf(position_val)/(cost*lot_size));
		try {
			ProcessModelAbstract processingModel = null;
			
			if(rangedModelId==11) {
				processingModel = new ProcessingBlock11(CacheService.getInstance());
			}
			if(rangedModelId==12) {
				processingModel = new ProcessingBlock12(CacheService.getInstance());
			}
			if(rangedModelId==13) {
				processingModel = new ProcessingBlock13(CacheService.getInstance());
			}
			if(rangedModelId==14) {
				processingModel = new ProcessingBlock14(CacheService.getInstance());
			}
			
			if(processingModel!=null) {
				DashBoard.kiteWebSocketClient.getProcessingModels().add(processingModel);
				modelAdded=true;
			}
			
			
		}finally {
			if(modelAdded) {
				rangedModelId++;
			}
			
		}
		
		orderInterface.orderKiteOption(null);
	}
	
	public static void orderCombination() {
		Iterator<Long> ce_list_itr = KiteStockConverter.BN_CE_LIST.keySet().iterator();
		Iterator<Long> pe_list_itr =  null;
		
		GroupPosition groupPosition = DashBoard.positionMap.get("pmodel15");

		double position_val = ApplicationHelper.positionVal("pmodel15");
		int lot_size = ApplicationHelper.lotsize("pmodel15");
		
		boolean addModel =  false;
		
		
		long ce_id = 0;
		long pe_id = 0;
		while(ce_list_itr.hasNext()) {
			ce_id = ce_list_itr.next();
			
			pe_list_itr = KiteStockConverter.BN_PE_LIST.keySet().iterator();
			
			while(pe_list_itr.hasNext()) {
				pe_id = pe_list_itr.next();
				
				if(CacheService.PRICE_LIST.containsKey(ce_id) && CacheService.PRICE_LIST.containsKey(pe_id)) {
					//Adding CE 
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
					
					groupPosition.getCePositions().add(pos);
					
					addModel = true;
					
				}else if(!CacheService.PRICE_LIST.containsKey(ce_id)) {
					System.out.println("No price found for ce:"+ce_id);
				}else if(!CacheService.PRICE_LIST.containsKey(pe_id)) {
					System.out.println("No price found for pe:"+pe_id);
				}
				
				
			}
		}
		
		if(addModel) {
			DashBoard.kiteWebSocketClient.getProcessingModels().add(new ProcessingBlock15(CacheService.getInstance()));
		}
		
		
	}
}
