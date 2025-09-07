package com.ai.generated.working;

import java.util.*;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.test.KiteBNFSellingCandle;

/**
 * To find out what is the best stop loss
 * 
 * for Long & Short Future entry.
 * Working one.
 * @author Vihaan
 *
 */

public class StopLossTriggerCalculator {
	
    public static void calculateStopLossTrigger(List<List<OHLC>> data) {
        Map<Integer, Integer> stopLossEffectiveness = new HashMap<>();
        Map<Integer, Integer> entryPointEffectiveness = new HashMap<>();
        Map<Integer, Integer> entryPointCounts = new HashMap<>();

        System.out.println("Previous Close,Stop Loss,Entry Point X,Entry Point Long,Entry Point Short,Long SL,Short SL,Day Close,Long Triggered,Short Triggered,HighestHigh,LowestLow,LongProfit,ShortProfit,HigheshHighPos,LowesLowPos,LongSLPos,ShortSLPos,validrecord, TotalProfit");

        List<int[]> allEntries = new ArrayList<>();
        int maxTriggers = 0;
        int[] maxCombination = null;

        for (int dayIndex = 1; dayIndex < data.size(); dayIndex++) {
            double previousClose = data.get(dayIndex - 1).get(data.get(dayIndex - 1).size() - 1).close;
            List<OHLC> dailyData = data.get(dayIndex);
            if (dailyData.size() < 2) continue;

            
            System.out.println("Previous Close,Stop Loss,Entry Point X,Entry Point Long,Entry Point Short,Long SL,Short SL,Day Close,Long Triggered,Short Triggered,HighestHigh,LowestLow,LongProfit,ShortProfit,HigheshHighPos,LowesLowPos,LongSLPos,ShortSLPos,validrecord, 0");
            System.out.println("Previous Close,Stop Loss,Entry Point X,Entry Point Long,Entry Point Short,Long SL,Short SL,Day Close,Long Triggered,Short Triggered,HighestHigh,LowestLow,LongProfit,ShortProfit,HigheshHighPos,LowesLowPos,LongSLPos,ShortSLPos,validrecord, 300");

            for (int stopLoss = 30; stopLoss <= 150; stopLoss += 30) {
                for (int x = 30; x <= 300; x += 10) {
                    double entryPointLong = 0, entryPointShort = 0;
                    double offset = x;
                    int entryIndex = -1;

                    for (int i = 0; i < dailyData.size(); i++) {
                        OHLC ohlc = dailyData.get(i);
                        if (ohlc.high > (previousClose + offset)) {
                        	
                        	if((previousClose + offset)>ohlc.low) {
                                entryPointLong = entryPointShort = ohlc.close;
                                entryIndex = i;
                                break;                        		
                        	}

                        }else if(ohlc.low < (previousClose - offset)) {
                        	
                        	if(ohlc.high>(previousClose - offset)) {
                                entryPointLong = entryPointShort = ohlc.close;
                                entryIndex = i;
                                break;                        		
                        	}

                        }
                    }
                    if (entryPointLong == 0 && entryPointShort == 0) continue;

                    entryPointCounts.put(x, entryPointCounts.getOrDefault(x, 0) + 1);

                    double longSL = entryPointLong - stopLoss;
                    double shortSL = entryPointShort + stopLoss;
                    int totalTriggersForSL = 0;
                    boolean longTriggered = false, shortTriggered = false;
                    
                    double highestHigh=entryPointLong;
                    double lowestLow=entryPointShort;
                    boolean longSLReset=false,shortSLReset=false;
                    int highestHighPos=0, lowestLowPos=0, longSLPos=0, shortSLPos=0, validRecord=0;
                    
                    

                    for (int i = entryIndex + 1; i < dailyData.size(); i++) {
                        OHLC ohlc = dailyData.get(i);
                        
                        if(ohlc.high>highestHigh) {
                        	highestHigh=ohlc.high;
                        	highestHighPos = i;
                        }
                        if(ohlc.low<lowestLow) {
                        	lowestLow=ohlc.low;
                        	lowestLowPos = i;
                        }
                        
                        if (!longTriggered && ohlc.low < longSL) {
                            longTriggered = true;
                            longSLPos = i;
                            totalTriggersForSL++;
                        }
                        if (!shortTriggered && ohlc.high > shortSL) {
                            shortTriggered = true;
                            shortSLPos = i;
                            totalTriggersForSL++;
                        }
                        
                        if(offset==100 && stopLoss==60) {
//                            if(longTriggered & !shortSLReset & !longSLReset) {
//                            	shortSL=ohlc.open;
//                            	shortSLReset=true;
//                            }
//                            
//                            if(shortTriggered & !longSLReset && !shortSLReset) {
//                            	longSL=ohlc.open;
//                            	longSLReset=true;
//                            	
//                            }                        	
                        }
                        

                        
                        
                        if (longTriggered && shortTriggered) break;
                    }
                    
                    shortSLReset=false;
                    longSLReset=false;
                    
                    double longProfit,shortProfit,totalProfit=0;
                    
                    if(longTriggered) {
                    	longProfit=longSL-entryPointLong;
                    }else {
                    	longProfit=highestHigh-entryPointLong;
                    	longProfit=longProfit*.75;
                    }
                    if(shortTriggered) {
                    	shortProfit=entryPointShort-shortSL;
                    }else {
                    	shortProfit=entryPointShort-lowestLow;
                    	shortProfit=shortProfit*.75;
                    }
                    
                    if(!longTriggered && !shortTriggered) {
                    	totalProfit=-4;
                    }else {
                    	totalProfit=longProfit+shortProfit;
                    }
                    

                    if(longTriggered && shortTriggered) {
                    	validRecord=1;
                    }else if(longTriggered && !shortTriggered) {
                    	//the long SL should be triggered before the lowest of the short for a valid record.
                    	// if the lowest is before the long SL, then the best profit is the lowest after the long SL.
                    	if(longSLPos<=lowestLowPos) {
                    		validRecord=1;
                    	}
                    }else if(shortTriggered && !longTriggered) {
                    	//the short sl should be before the hgihgest high, otherwise the only the highest high after the  short sl can be considered.
                    	if(shortSLPos<=highestHighPos) {
                    		validRecord=1;
                    	}
                    }else if(!longTriggered && !shortTriggered) {
                    	validRecord=1;
                    }

                    System.out.println(previousClose + "," + stopLoss + "," + x + "," + entryPointLong 
                    		+ "," + entryPointShort + "," + longSL + "," + shortSL + "," + dailyData.get(dailyData.size()-1).close+"," 
                    		+ (longTriggered ? 1 : 0) + "," + (shortTriggered ? 1 : 0)+ "," + highestHigh+ "," + lowestLow+ "," 
                    		+ (int)longProfit+ "," + (int)shortProfit+ ","+ highestHighPos+ ","+ lowestLowPos+ ","+ longSLPos+ ","+ shortSLPos+ "," + validRecord+ ","+ (int)totalProfit);

                    if (totalTriggersForSL > 0) {
                        entryPointEffectiveness.put(x, entryPointEffectiveness.getOrDefault(x, 0) + totalTriggersForSL);
                        stopLossEffectiveness.put(stopLoss, stopLossEffectiveness.getOrDefault(stopLoss, 0) + totalTriggersForSL);
                        allEntries.add(new int[]{x, totalTriggersForSL, stopLoss});

                        if (totalTriggersForSL > maxTriggers) {
                            maxTriggers = totalTriggersForSL;
                            maxCombination = new int[]{x, totalTriggersForSL, stopLoss};
                        }
                    }
                }
            }
        }

//        System.out.println("\nAll Entry Points with Stop-Loss Triggers:");
//        allEntries.sort(Comparator.comparingInt(a -> a[1]));
//        for (int[] result : allEntries) {
//            System.out.println("Entry Point: " + result[0] + " | Stop-Loss: " + result[2] + " | Stop-Loss Triggers: " + result[1]);
//        }
//
//        System.out.println("\nEntry Points and the Number of Times a Position Was Taken:");
//        for (Map.Entry<Integer, Integer> entry : entryPointCounts.entrySet()) {
//            System.out.println("Entry Point: " + entry.getKey() + " | Positions Taken: " + entry.getValue());
//        }

      //  visualizeStopLossEffectiveness(stopLossEffectiveness);
      //  visualizeEntryPointEffectiveness(entryPointEffectiveness);
    }
   
    

 
    
    /**
     * Enter only when one of the leg SL is triggered.
     * @param data
     */
    public static void calculateStopLossTriggerWithFakeEntry(List<List<OHLC>> data) {
        Map<Integer, Integer> stopLossEffectiveness = new HashMap<>();
        Map<Integer, Integer> entryPointEffectiveness = new HashMap<>();
        Map<Integer, Integer> entryPointCounts = new HashMap<>();

        System.out.println("Previous Close,Stop Loss,Entry Point X,Entry Point Long,Entry Point Short,Long SL,Short SL,Day Close,Long Triggered,Short Triggered,HighestHigh,LowestLow,LongProfit,ShortProfit,HigheshHighPos,LowesLowPos,LongSLPos,ShortSLPos,validrecord, TotalProfit");

        List<int[]> allEntries = new ArrayList<>();
        int maxTriggers = 0;
        int[] maxCombination = null;

        for (int dayIndex = 1; dayIndex < data.size(); dayIndex++) {
            double previousClose = data.get(dayIndex - 1).get(data.get(dayIndex - 1).size() - 1).close;
            List<OHLC> dailyData = data.get(dayIndex);
            if (dailyData.size() < 2) continue;

            for (int stopLoss = 30; stopLoss <= 150; stopLoss += 30) {
                for (int x = 30; x <= 300; x += 10) {
                    double entryPointLong = 0, entryPointShort = 0;
                    double offset = x;
                    int entryIndex = -1;

                    for (int i = 0; i < dailyData.size(); i++) {
                        OHLC ohlc = dailyData.get(i);
                        if (ohlc.high > (previousClose + offset)) {
                        	
                        	if((previousClose + offset)>ohlc.low) {
                                entryPointLong = entryPointShort = ohlc.close;
                                entryIndex = i;
                                break;                        		
                        	}

                        }else if(ohlc.low < (previousClose - offset)) {
                        	
                        	
                        	if(ohlc.high>(previousClose - offset)) {
                                entryPointLong = entryPointShort = ohlc.close;
                                entryIndex = i;
                                break;                        		
                        	}

                        }
                    }
                    if (entryPointLong == 0 && entryPointShort == 0) continue;

                    entryPointCounts.put(x, entryPointCounts.getOrDefault(x, 0) + 1);

                    double longSL = entryPointLong - stopLoss;
                    double shortSL = entryPointShort + stopLoss;
                    int totalTriggersForSL = 0;
                    boolean longTriggered = false, shortTriggered = false;
                    
                    double highestHigh=entryPointLong;
                    double lowestLow=entryPointShort;
                    boolean longSLReset=false,shortSLReset=false;
                    int highestHighPos=0, lowestLowPos=0, longSLPos=0, shortSLPos=0, validRecord=0;
                    
                    double secondShortPos = 0,secondLongPos = 0,secondShortSL = 0,secondLongSL = 0;
                    boolean secondLongTriggered = false, secondShortTriggered = false;
                    

                    for (int i = entryIndex + 1; i < dailyData.size(); i++) {
                    	
                        OHLC ohlc = dailyData.get(i);
                        
                        if(ohlc.high>highestHigh) {
                        	highestHigh=ohlc.high;
                        	highestHighPos = i;
                        }
                        if(ohlc.low<lowestLow) {
                        	lowestLow=ohlc.low;
                        	lowestLowPos = i;
                        }
                        
                        if (!longTriggered && ohlc.low < longSL) {
                            longTriggered = true;
                            longSLPos = i;
                            totalTriggersForSL++;
                            
                            if(secondLongPos==0 && secondShortPos==0) {
                            	secondShortPos=longSL;
                            	secondShortSL=secondShortPos+30;
                            }
                        }
                        //stop if the long SL has been triggered.
//                        if(longTriggered) {
//                        	break;
//                        }
                        
                        if (!shortTriggered && ohlc.high > shortSL) {
                            shortTriggered = true;
                            shortSLPos = i;
                            totalTriggersForSL++;
                            
                            if(secondLongPos==0 && secondShortPos==0) {
                            	secondLongPos=shortSL;
                            	secondLongSL=secondLongPos-30;
                            }
                        }
                        
//                        if(shortTriggered) {
//                        	break;
//                        }
                        

                        
                        if(offset==100 && stopLoss==60) {
//                            if(longTriggered & !shortSLReset & !longSLReset) {
//                            	shortSL=ohlc.open;
//                            	shortSLReset=true;
//                            }
//                            
//                            if(shortTriggered & !longSLReset && !shortSLReset) {
//                            	longSL=ohlc.open;
//                            	longSLReset=true;
//                            	
//                            }                        	
                        }
                        

                        
                        
                        if (longTriggered && shortTriggered) break;
                    }
                    
                    double secondHighestHigh=0; double secondLowestLow=0;
                    double secondLongProfit=0,secondShortProfit=0;
                    if(longTriggered) {
                    	secondHighestHigh=secondShortPos;
                    	secondLowestLow=secondShortPos;

                    	
                    	 for (int i = longSLPos; i < dailyData.size(); i++) {
                    		 
                    		 OHLC ohlc = dailyData.get(i);
                    		 
                             if(ohlc.high>secondHighestHigh) {
                             	secondHighestHigh=ohlc.high;
                             	
                             }
                             if(ohlc.low<secondLowestLow) {
                             	secondLowestLow=ohlc.low;
                             	
                             }
                             
                             if(secondShortPos!=0 && secondShortSL!=0 && !secondShortTriggered) {
                             	if(ohlc.high>secondShortSL) {
                             		secondShortTriggered=true;
                             	}

                             }
                             
                             
                             if(secondShortTriggered) break;
                    	 }
                    	 
                         if(secondShortTriggered) {
                         	secondShortProfit=secondShortPos-secondShortSL;
                         }else {
                        	 secondShortProfit=secondShortPos-secondLowestLow;
                        	 secondShortProfit=secondShortProfit*.75;
                         }
                    	 
                    	 
                    }
                    
                    
                    if(shortTriggered) {
                    	secondHighestHigh=secondLongPos;
                    	secondLowestLow=secondLongPos;

                    	
                    	 for (int i = shortSLPos; i < dailyData.size(); i++) {
                    		 
                    		 OHLC ohlc = dailyData.get(i);
                    		 
                             if(ohlc.high>secondHighestHigh) {
                             	secondHighestHigh=ohlc.high;
                             	
                             }
                             if(ohlc.low<secondLowestLow) {
                             	secondLowestLow=ohlc.low;
                             	
                             }
                             
                             if(secondLongPos!=0 && secondLongSL!=0 && !secondLongTriggered) {
                             	if(ohlc.high>secondLongSL) {
                             		secondLongTriggered=true;
                             	}

                             }
                             
                             
                             if(secondLongTriggered) break;
                    	 }
                    	 
                    	 
                    	 
                    	 
                         if(secondLongTriggered) {
                         	secondLongProfit=secondLongSL-secondLongPos;
                         }else {
                        	 secondLongProfit=secondHighestHigh-secondLongPos;
                        	 secondLongProfit=secondLongProfit*.75;
                         }
                    }
                    
                    
                    shortSLReset=false;
                    longSLReset=false;
                    
                    double longProfit,shortProfit,totalProfit=0;
                    
                    
                    if(longTriggered) {
                    	longProfit=longSL-entryPointLong;
                    }else {
                    	longProfit=highestHigh-entryPointLong;
                    	longProfit=longProfit*.50;
                    }
                    if(shortTriggered) {
                    	shortProfit=entryPointShort-shortSL;
                    }else {
                    	shortProfit=entryPointShort-lowestLow;
                    	shortProfit=shortProfit*.50;
                    }
                    
                    if(!longTriggered && !shortTriggered) {
                    	totalProfit=-4;
                    }else {
                    	totalProfit=longProfit+shortProfit;
                    }
                    
                    
                    
                    
                    

                    /**
                     * Check if there was a SL after the high.....
                     */
                    if(longTriggered && shortTriggered) {
                    	validRecord=1;
                    }else if(longTriggered && !shortTriggered) {
                    	//the long SL should be triggered before the lowest of the short for a valid record.
                    	// if the lowest is before the long SL, then the best profit is the lowest after the long SL.
                    	if(longSLPos<=lowestLowPos) {
                    		validRecord=1;
                    	}
                    }else if(shortTriggered && !longTriggered) {
                    	//the short sl should be before the hgihgest high, otherwise the only the highest high after the  short sl can be considered.
                    	if(shortSLPos<=highestHighPos) {
                    		validRecord=1;
                    	}
                    }else if(!longTriggered && !shortTriggered) {
                    	validRecord=1;
                    }
                    
                    
                    
                    
                    
                    

                    System.out.println(previousClose + "," + stopLoss + "," + x + "," + entryPointLong 
                    		+ "," + entryPointShort + "," + longSL + "," + shortSL + "," + dailyData.get(dailyData.size()-1).close+"," 
                    		+ (longTriggered ? 1 : 0) + "," + (shortTriggered ? 1 : 0)+ "," + highestHigh+ "," + lowestLow+ "," 
                    		+ (int)longProfit+ "," + (int)shortProfit+ ","+ highestHighPos+ ","+ lowestLowPos+ ","+ longSLPos+ ","+ shortSLPos+ "," + validRecord+ ","+ (int)totalProfit+","+secondLongProfit+","+secondShortProfit);

//                    if (totalTriggersForSL > 0) {
//                        entryPointEffectiveness.put(x, entryPointEffectiveness.getOrDefault(x, 0) + totalTriggersForSL);
//                        stopLossEffectiveness.put(stopLoss, stopLossEffectiveness.getOrDefault(stopLoss, 0) + totalTriggersForSL);
//                        allEntries.add(new int[]{x, totalTriggersForSL, stopLoss});
//
//                        if (totalTriggersForSL > maxTriggers) {
//                            maxTriggers = totalTriggersForSL;
//                            maxCombination = new int[]{x, totalTriggersForSL, stopLoss};
//                        }
//                    }
                }
            }
        }

//        System.out.println("\nAll Entry Points with Stop-Loss Triggers:");
//        allEntries.sort(Comparator.comparingInt(a -> a[1]));
//        for (int[] result : allEntries) {
//            System.out.println("Entry Point: " + result[0] + " | Stop-Loss: " + result[2] + " | Stop-Loss Triggers: " + result[1]);
//        }
//
//        System.out.println("\nEntry Points and the Number of Times a Position Was Taken:");
//        for (Map.Entry<Integer, Integer> entry : entryPointCounts.entrySet()) {
//            System.out.println("Entry Point: " + entry.getKey() + " | Positions Taken: " + entry.getValue());
//        }

      //  visualizeStopLossEffectiveness(stopLossEffectiveness);
      //  visualizeEntryPointEffectiveness(entryPointEffectiveness);
    }
   
    
    
    private static String trimTimeStramp(String timest) {
    	
    	if(timest==null || timest.length()<11) {
    		return timest;
    				
    	}
    	
    	return timest.substring(11);
    }
    
    
    public static void visualizeStopLossEffectiveness(Map<Integer, Integer> stopLossEffectiveness) {
        SwingUtilities.invokeLater(() -> createBarChart("Stop-Loss Effectiveness", "Stop-Loss Value", "SL Triggers", stopLossEffectiveness));
    }

    public static void visualizeEntryPointEffectiveness(Map<Integer, Integer> entryPointEffectiveness) {
        SwingUtilities.invokeLater(() -> createBarChart("Best Entry Points", "Entry Point X", "SL Triggers", entryPointEffectiveness));
    }

    private static void createBarChart(String title, String xAxis, String yAxis, Map<Integer, Integer> data) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<Integer, Integer> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "SL Triggers", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createBarChart(title, xAxis, yAxis, dataset, PlotOrientation.VERTICAL, true, true, false);
        frame.setContentPane(new ChartPanel(chart));
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        List<KiteBNFSellingCandle> bnfdataList = new ArrayList<>();
        String startDate = "2025-05-19T09:15:00+0530";
        String endDate = "2025-07-18T15:29:00+0530";

        bnfdataList = ApplicationHelper.collectData(bnfdataList, startDate, endDate, "C:/data/testdata.txt");

        List<List<OHLC>> data = new ArrayList<>();
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
            data.add(dailyData);
        }
        
        calculateStopLossTriggerWithFakeEntry(data);

       // calculateStopLossTrigger(data);
    }
}
