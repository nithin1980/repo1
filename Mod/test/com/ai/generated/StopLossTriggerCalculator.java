package com.ai.generated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.test.KiteBNFSellingCandle;

public class StopLossTriggerCalculator {

	
    public static void calculateStopLossTrigger(List<List<OHLC>> data) {
        Map<Integer, Integer> stopLossEffectiveness = new HashMap<>();
        Map<Integer, Integer> entryPointEffectiveness = new HashMap<>();
        Map<Integer, List<Integer>> slTrendOverDays = new HashMap<>();

        // Print CSV header to console
        System.out.println("Previous Close,Stop Loss,Entry Point X,Entry Point Long,Entry Point Short,Long SL,Short SL,Triggered (Yes/No)");

        // Iterate through the data to check stop loss triggers
        for (int dayIndex = 1; dayIndex < data.size(); dayIndex++) {
            double previousClose = data.get(dayIndex - 1).get(data.get(dayIndex - 1).size() - 1).close;
            List<OHLC> dailyData = data.get(dayIndex);
            

            if (dailyData.size() < 2) continue; // Ensure at least two OHLCs exist

            // Iterate over Stop Loss values (e.g., 30 to 150 in steps of 30)
            for (int stopLoss = 30; stopLoss <= 150; stopLoss += 30) {
                int totalTriggersForSL = 0;

                // Iterate over Entry Points (e.g., 30 to 300 in steps of 10)
                for (int x = 30; x <= 300; x += 10) {
                    double entryPointLong = 0, entryPointShort = 0;

                    // Offset logic (±50)
                    double offset = x;

                    // Logic to check if a Long or Short position should be opened
                    for (OHLC ohlc : dailyData) {
                        // Check if the high price of the current OHLC is greater than previousClose + offset (for Long position)
                        if ((ohlc.high > previousClose + offset) || (ohlc.low < previousClose - offset)) {
                            entryPointLong = ohlc.close;  // Create long position    
                            entryPointShort = ohlc.close;  // Create short position
                        }


                        // If either position is created, break out of the loop
                        if (entryPointLong > 0 || entryPointShort > 0) {
                            break;
                        }
                    }

                    // If no entry point was triggered, continue to the next loop
                    if (entryPointLong == 0 && entryPointShort == 0) {
                        continue;
                    }

                    // Define the Stop Loss for Long and Short positions
                    double longSL = entryPointLong - stopLoss;
                    double shortSL = entryPointShort + stopLoss;

                    int totalBothTriggered = 0;
                    List<Integer> dailyTriggers = new ArrayList<>();

                    boolean longTriggered = false, shortTriggered = false;

                    // After position is taken, check only subsequent OHLC data for stop loss
                    for (int i = 1; i < dailyData.size(); i++) { // Start from the second OHLC
                        OHLC ohlc = dailyData.get(i); // Skip the first OHLC where position was taken
                        
                        if (entryPointLong > 0 && !longTriggered && ohlc.low < longSL) {
                            longTriggered = true;
                        }
                        
                        if (entryPointShort > 0 && !shortTriggered && ohlc.high > shortSL) {
                            shortTriggered = true;
                        }
                        
                        // If both stop losses are triggered, break out of the loop
                        if (longTriggered && shortTriggered) {
                            break;
                        }
                    }

                    int dayTrigger = 0;
                    // For each OHLC in daily data, check for stop loss trigger
                    for (int i = 1; i < dailyData.size(); i++) { // Ensure we skip the first OHLC
                        OHLC ohlc = dailyData.get(i);
                        if (entryPointLong > 0 && !longTriggered && ohlc.low < longSL) longTriggered = true;
                        if (entryPointShort > 0 && !shortTriggered && ohlc.high > shortSL) shortTriggered = true;

                        if (longTriggered && shortTriggered) {
                            totalBothTriggered++;
                            dayTrigger = 1;
                            break;
                        }
                    }
                    dailyTriggers.add(dayTrigger);

                    totalTriggersForSL += totalBothTriggered;
                    entryPointEffectiveness.put(x, entryPointEffectiveness.getOrDefault(x, 0) + totalBothTriggered);

                    // Track the trend of SL triggers over days
                    slTrendOverDays.putIfAbsent(x, new ArrayList<>(Collections.nCopies(10, 0)));
                    List<Integer> trendList = slTrendOverDays.get(x);
                    for (int i = 0; i < dailyTriggers.size(); i++) {
                        trendList.set(i, trendList.get(i) + dailyTriggers.get(i));
                    }

                    // Print data to console (instead of CSV)
                    System.out.println(previousClose + "," + stopLoss + "," + x + "," + entryPointLong + "," + entryPointShort + "," + longSL + "," + shortSL + ","+longTriggered+","+shortTriggered+","+ totalBothTriggered);
                }
                stopLossEffectiveness.put(stopLoss, totalTriggersForSL);
            }
        }

        // Visualize results
        visualizeStopLossEffectiveness(stopLossEffectiveness);
        visualizeEntryPointEffectiveness(entryPointEffectiveness);
        visualizeSLTrend(slTrendOverDays);
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

    public static void visualizeStopLossEffectiveness(Map<Integer, Integer> stopLossEffectiveness) {
        SwingUtilities.invokeLater(() -> createBarChart("Stop-Loss Effectiveness", "Stop-Loss Value", "SL Triggers", stopLossEffectiveness));
    }

    public static void visualizeEntryPointEffectiveness(Map<Integer, Integer> entryPointEffectiveness) {
        SwingUtilities.invokeLater(() -> createBarChart("Best Entry Points", "Entry Point X", "SL Triggers", entryPointEffectiveness));
    }

    public static void visualizeSLTrend(Map<Integer, List<Integer>> slTrendOverDays) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("SL Trigger Trends Over 10 Days");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            XYSeriesCollection dataset = new XYSeriesCollection();
            for (Map.Entry<Integer, List<Integer>> entry : slTrendOverDays.entrySet()) {
                XYSeries series = new XYSeries("EntryX: " + entry.getKey());
                for (int day = 0; day < entry.getValue().size(); day++) {
                    series.add(day + 1, entry.getValue().get(day));
                }
                dataset.addSeries(series);
            }

            JFreeChart lineChart = ChartFactory.createXYLineChart(
                    "SL Trigger Trends Over 10 Days",
                    "Days",
                    "SL Triggers",
                    dataset
            );

            frame.setContentPane(new ChartPanel(lineChart));
            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        // Initialize the list of KiteBNFSellingCandle
        List<KiteBNFSellingCandle> bnfdataList = new ArrayList<KiteBNFSellingCandle>();
        
        // Define the start and end date for the data collection
        String startDate = "2025-02-10T09:15:00+0530";
        String endDate = "2025-02-25T15:29:00+0530";
        
        // Collect data from the file and populate the bnfdataList
        bnfdataList = ApplicationHelper.collectData(bnfdataList, startDate, endDate, "C:/data/testdata.txt");
        
        // Initialize variables for candle data
        Candle candle = null;
        List<Candle> candles = null;

        // Prepare data in OHLC format for the calculation
        List<List<OHLC>> data = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            
            // Get the list of candles for each KiteBNFSellingCandle entry
            candles = bnfdataList.get(i).getCandles();
            
            // Prepare daily data list for OHLC
            List<OHLC> dailyData = new ArrayList<>();
            
            // Populate OHLC data from the candles
            for (int j = 0; j < 375; j++) {
                candle = candles.get(j);
                dailyData.add(new OHLC(candle.getTime(),
                        candle.getOpen(),
                        candle.getHigh(),
                        candle.getLow(),
                        candle.getClose(),
                        candle.getVol()));
            }
            // Add daily data to the overall data
            data.add(dailyData);
        }
        
        // Call the method to calculate Stop Loss Trigger
        calculateStopLossTrigger(data);
    }
}
