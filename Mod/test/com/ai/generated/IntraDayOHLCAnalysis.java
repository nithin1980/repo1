package com.ai.generated;


import java.time.LocalDateTime;
import java.util.*;

import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.test.KiteBNFSellingCandle;

/**
 * Simple protective put strategy. Doesn't work well. 
 * @author Vihaan
 *
 */

public class IntraDayOHLCAnalysis {
	
	static List<KiteBNFSellingCandle> bnfdataList = new ArrayList<KiteBNFSellingCandle>();
    
    public static void main(String[] args) {
    	
    	String startDate= "2025-02-14T09:15:00+0530";
    	String endDate="2025-02-14T15:29:00+0530";
    	
    	bnfdataList = ApplicationHelper.collectData(bnfdataList,startDate,endDate,"C:/data/testdata.txt");
    	
        // Sample OHLC data
        List<OHLC> A = new ArrayList<>();
        
        Iterator<Candle> candles = bnfdataList.get(0).getCandles().iterator();
        
        while(candles.hasNext()) {
        	Candle candle = candles.next();
        	A.add(new OHLC("",candle.getOpen(), candle.getHigh(),candle.getLow(), candle.getClose(),0.0));
        }
        

        
        double A_Buy = 480.20; // Sample Buy Price
        
        System.out.println("High_Diff,Close_Diff,Low_Diff,Sell_High_Diff,Sell_Low_Diff,Sell_Close_Diff");
        calculateAndPrint(A, A_Buy);
        
        
    }
    
    public static void calculateAndPrint(List<OHLC> A, double A_Buy) {
        for (OHLC ohlc : A) {
        	
            double highDiff = ohlc.high - A_Buy;
            double closeDiff = ohlc.close - A_Buy;
            double lowDiff = ohlc.low - A_Buy;
            double sellHighDiff = A_Buy - ohlc.high;
            double sellLowDiff = A_Buy - ohlc.low;
            double sellCloseDiff = A_Buy - ohlc.close;
            
            System.out.printf("%.2f,%.2f,%.2f,%.2f,%.2f,%.2f\n", highDiff, closeDiff,lowDiff,sellHighDiff, sellLowDiff, sellCloseDiff);
        }

    }
    
    
    
    
    
    
    
    
    
    
    
}

