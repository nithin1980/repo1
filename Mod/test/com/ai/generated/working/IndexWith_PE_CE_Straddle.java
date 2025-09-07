package com.ai.generated.working;

import java.util.ArrayList;
import java.util.List;


import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.test.KiteBNFSellingCandle;


/**
 * 
 * @author Vihaan
 * 
 * Missing: NIFTY2532022500CE.txt
 * 
 * This is standard 9.20 wala Straddle. 
 *
 */

public class IndexWith_PE_CE_Straddle {

    static String startDate = "2025-04-07T09:15:00+0530";
    static String endDate = "2025-04-09T15:29:00+0530";
    
    static double[] slPercentages = {15.00,17.00,20.00, 25.00};
    
    final static String fileExt="NIFTY25409";
    
    public static void main(String[] args) {
        // Sample test data for 5 days with minute-level OHLC including Date Timestamp
        List<List<OHLC>> futureData = new ArrayList<>();
       // List<List<OHLC>> peData = new ArrayList<>();
        
        
        
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
       
        
        for(double slpercent:slPercentages) {
	        for (int entryVar : entryVars) {
	            System.out.println("Entry Variable: " + entryVar);
	            processTradingStrategy(futureData, entryVar,slpercent);
	        }        	
        }
        

    }
    
    public static void processTradingStrategy(List<List<OHLC>> futureData, int entryVar, double slPercentage) {
    	
    	
    	
        for (int day = 1; day < futureData.size(); day++) {
            List<OHLC> futureDay = futureData.get(day);
            List<OHLC> ceDay = new ArrayList<OHLC>();
            List<OHLC> peDay = new ArrayList<OHLC>();
            double prevClose = futureData.get(day - 1).get(futureData.get(day - 1).size() - 1).close;
            boolean positionTaken = false;
            double futureBuyPt = 0, ceBuyPt = 0, peBuyPt=0;
            double ceSL=0,peSL=0;
            boolean ceSLHit=false,peSLHit=false;
            
            

            
            
            for (int i = 1; i < futureDay.size(); i++) {
                OHLC futureOHLC = futureDay.get(i);
                
                
                
                if (!positionTaken) {
                    if (futureOHLC.high > prevClose + entryVar && futureOHLC.low < prevClose + entryVar) {
                    	futureBuyPt = futureOHLC.close;
                    	ceDay = assignCE( futureOHLC, ceDay,futureBuyPt,day);
                    	peDay = assignPE( futureOHLC, peDay,futureBuyPt,day);
                    	if(futureOHLC.timestamp.equalsIgnoreCase(ceDay.get(i).timestamp) && futureOHLC.timestamp.equalsIgnoreCase(peDay.get(i).timestamp)) {
                        	ceBuyPt = ceDay.get(i).close;
                        	peBuyPt = peDay.get(i).close;
                        	ceSL= ApplicationHelper.givePercenValue(ceBuyPt, 100+slPercentage);
                        	peSL= ApplicationHelper.givePercenValue(peBuyPt, 100+slPercentage);
                            positionTaken = true;                    		
                    	}else {
                    		throw new RuntimeException("Timestamps don't match:"+futureOHLC.timestamp+" "+ceDay.get(i).timestamp+" "+peDay.get(i).timestamp);
                    	}

                    } else if (futureOHLC.low < prevClose - entryVar && futureOHLC.high > prevClose - entryVar) {
                    	futureBuyPt = futureOHLC.close;
                    	ceDay = assignCE( futureOHLC, ceDay,futureBuyPt,day);
                    	peDay = assignPE( futureOHLC, peDay,futureBuyPt,day);
                    	if(futureOHLC.timestamp.equalsIgnoreCase(ceDay.get(i).timestamp) && futureOHLC.timestamp.equalsIgnoreCase(peDay.get(i).timestamp)) {
                        	ceBuyPt = ceDay.get(i).close;
                        	peBuyPt = peDay.get(i).close;
                        	ceSL= ApplicationHelper.givePercenValue(ceBuyPt, 100+slPercentage);
                        	peSL= ApplicationHelper.givePercenValue(peBuyPt, 100+slPercentage);
                            positionTaken = true;                    		
                    	}else {
                    		throw new RuntimeException("Timestamps don't match:"+futureOHLC.timestamp+" "+ceDay.get(i).timestamp+" "+peDay.get(i).timestamp);
                    	}

                    }
                }
                
                if (positionTaken) {
                	OHLC ceOHLC = ceDay.get(i);
                	OHLC peOHLC = peDay.get(i);
                	
                	if(!ceSLHit && ceOHLC.high>=ceSL) {
                		ceSLHit=true;
                	}
                	if(!peSLHit && peOHLC.high>=peSL) {
                		peSLHit=true;
                	}               	
                	
                	double ceProfit=0.0, peProfit=0.0;
                	
                	if(!ceSLHit) {
                		ceProfit = ceBuyPt - ceOHLC.close;
                	}else {
                		ceProfit=ceBuyPt-ceSL;
                	}
                	
                	if(!peSLHit) {
                		peProfit = peBuyPt - peOHLC.close;
                	}else {
                		peProfit=peBuyPt-ceSL;
                	}                	
                	/**
                	 * Need to change this.
                	 */

                    double profit = ceProfit+peProfit;
                    //System.out.println(futureOHLC.timestamp + ", Entry: " + entryVar+ ", percent: " + slPercentage+ ", Future Buy: " + futureBuyPt + ", PE Buy: " + peBuyPt + ", CE Buy: " + ceBuyPt + ", Profit: " + (int)profit+ ", CE: " + ceOHLC.close+ ", PE: " + peOHLC.close+ ", CE Pr: " + (int)ceProfit+ ", PE Pr: " + (int)peProfit  );
                    System.out.println(futureOHLC.timestamp + ", Entry: " + entryVar+ ", percent: " + slPercentage+ ", Future: " + futureBuyPt + ", PE: " + peBuyPt + ", CE: " + ceBuyPt + ", P: " + (int)profit);
                }
            }
            
            
            
        }
    }
    
    
    private static List<OHLC> assignCE(OHLC futureOHLC,List<OHLC> ceDay, double futureBuyPt, int day) {
        
        int strikePrice =  ApplicationHelper.findNthItmCe(50, (int) futureBuyPt, 3);
        System.out.println("CE strike:"+strikePrice);
        
 
        List<List<OHLC>> ceData = new ArrayList<>();
        
        String filename = fileExt+strikePrice+"CE.txt";
        
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
            ceData.add(dailyData);
        }        
        ceDay =  ceData.get(day);
        
        
        return ceDay;
    	
    }
    
    private static List<OHLC> assignPE(OHLC futureOHLC,List<OHLC> peDay, double futureBuyPt, int day) {
        
        int strikePrice =  ApplicationHelper.findNthItmPe(50, (int) futureBuyPt, 3);
        System.out.println("PE strike:"+strikePrice);
        
 
        List<List<OHLC>> peData = new ArrayList<>();
        
        String filename = fileExt+strikePrice+"PE.txt";
        
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
