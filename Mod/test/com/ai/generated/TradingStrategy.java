package com.ai.generated;

import java.util.*;

import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.test.KiteBNFSellingCandle;

/**
 * 
 * @author Vihaan
 * 
 * Future long with protective PE
 * ABility to pick PE across ITM/OTM
 * 
 * Doesn't work.
 *
 */

public class TradingStrategy {
	
    static String startDate = "2025-02-21T09:15:00+0530";
    static String endDate = "2025-02-27T15:29:00+0530";
    
    public static void main(String[] args) {
        // Sample test data for 5 days with minute-level OHLC including Date Timestamp
        List<List<OHLC>> futureData = new ArrayList<>();
        List<List<OHLC>> peData = new ArrayList<>();
        
        
        
        List<KiteBNFSellingCandle> bnfdataList = new ArrayList<>();


        bnfdataList = ApplicationHelper.collectData(bnfdataList, startDate, endDate, "C:/data/testdata.txt");

       // List<List<OHLC>> data = new ArrayList<>();
        for (KiteBNFSellingCandle kiteBNFCandle : bnfdataList) {
            List<OHLC> dailyData = new ArrayList<>();
            for (Candle candle : kiteBNFCandle.getCandles()) {
                dailyData.add(new OHLC(candle.getTime(),
                        candle.getOpen(),
                        candle.getHigh(),
                        candle.getLow(),
                        candle.getClose(),
                        candle.getVol()));
            }
            futureData.add(dailyData);
        }
        


        int[] entryVars = {50, 100,150,200,250};
        
        for (int entryVar : entryVars) {
            System.out.println("Entry Variable: " + entryVar);
            processTradingStrategy(futureData, peData, entryVar);
        }
    }
    
    public static void processTradingStrategy(List<List<OHLC>> futureData, List<List<OHLC>> peData, int entryVar) {
        for (int day = 1; day < futureData.size(); day++) {
            List<OHLC> futureDay = futureData.get(day);
            List<OHLC> peDay = new ArrayList<OHLC>();
            double prevClose = futureData.get(day - 1).get(futureData.get(day - 1).size() - 1).close;
            boolean positionTaken = false;
            double futureBuyPt = 0, peBuyPt = 0;
            
            
            
            
            for (int i = 1; i < futureDay.size(); i++) {
                OHLC futureOHLC = futureDay.get(i);
                
                
                if (!positionTaken) {
                    if (futureOHLC.high > prevClose + entryVar && futureOHLC.low < prevClose + entryVar) {
                    	futureBuyPt = futureOHLC.close;
                    	peDay = assign( futureOHLC, peDay,futureBuyPt,day);
                    	peBuyPt = peDay.get(i).close;
//                        futureBuyPt = futureOHLC.close;
//                        strikePrice =  ApplicationHelper.findNthItmPe(50, (int) futureBuyPt, 1);
//                        System.out.println("strike:"+strikePrice);
//                        peBuyPt = peOHLC.close;
                        positionTaken = true;
                    } else if (futureOHLC.low < prevClose - entryVar && futureOHLC.high > prevClose - entryVar) {
                    	futureBuyPt = futureOHLC.close;
                    	peDay = assign( futureOHLC, peDay,futureBuyPt,day);
                    	peBuyPt = peDay.get(i).close;
                        positionTaken = true;
                    }
                }
                
                if (positionTaken) {
                	OHLC peOHLC = peDay.get(i);
                    double profit = (futureOHLC.close - futureBuyPt) + (peOHLC.close - peBuyPt);
                    System.out.println(futureOHLC.timestamp + ", Future Buy: " + futureBuyPt + ", PE Buy: " + peBuyPt + ", Profit:" + (int)profit);
                }
            }
        }
    }
    
    
    private static List<OHLC> assign(OHLC futureOHLC,List<OHLC> peDay, double futureBuyPt, int day) {
        
        int strikePrice =  ApplicationHelper.findNthItmPe(50, (int) futureBuyPt, 3);
        System.out.println("strike:"+strikePrice);
        
 
        List<List<OHLC>> peData = new ArrayList<>();
        
        String filename = "NIFTY25FEB"+strikePrice+"PE.txt";
        
        List<KiteBNFSellingCandle> bnfdataList = new ArrayList<>();


        bnfdataList = ApplicationHelper.collectData(bnfdataList, startDate, endDate, "C:/data/Nifty Minute Data/Option data/"+filename);

       // List<List<OHLC>> data = new ArrayList<>();
        for (KiteBNFSellingCandle kiteBNFCandle : bnfdataList) {
            List<OHLC> dailyData = new ArrayList<>();
            for (Candle candle : kiteBNFCandle.getCandles()) {
                dailyData.add(new OHLC(candle.getTime(),
                        candle.getOpen(),
                        candle.getHigh(),
                        candle.getLow(),
                        candle.getClose(),
                        candle.getVol()));
            }
            peData.add(dailyData);
        }        
        peDay =  peData.get(day);
        
        
        return peDay;
    	
    }
}
