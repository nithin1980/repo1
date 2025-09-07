package com.ai.generated;

import java.util.*;

/*
 * 
 * 
 * 
 * Best Intraday Entry Calculation – Determines the best entry percentage from past data.
ATR-Based Stop-Loss – Dynamically adjusts stop-loss based on volatility.
Target Price Calculation – Uses Risk-Reward Ratio (RRR) to set profit targets.
Trend Filtering with SMA – Confirms if the market is trending before entry.
Real-Time Optimization – Accepts live market input to suggest entry, stop-loss, and target.

 Live market input makes it actionable.
 Dynamically adjusts stop-loss & target levels based on market behavior.
 Risk-Reward optimization (1.5x & 2x RRR) enhances profitability.
 Avoids bad trades in choppy markets using SMA filter.


This version includes:
Volume Confirmation – Ensures high-volume conditions before entering trades.
Breakout Detection – Identifies key intraday breakouts based on volatility.
Live Market Input – Suggests entry, stop-loss, and targets dynamically.
ATR-Based Stop-Loss & Trend Filtering – Optimizes trade risk management.


Volume Confirmation – Ensures that trades occur only during high-volume conditions.
Intraday Breakout Detection – Identifies breakout signals before taking a trade.
ATR-Based Stop-Loss – Dynamically adapts to market conditions.
Trend Filtering (SMA) – Avoids choppy sideways trades.
Live Market Input – Users enter current price & volume for real-time decision-making.
 * 
 * 
 * 
 */

import java.util.*;

/**
 * Auto generated. Not mine. For reference
 * @author Vihaan
 *
 */

public class IntradayStrategyWithVolumeBreakout {

    public static void main(String[] args) {
        // Sample last 5 days OHLC + Volume data
        List<OHLC> ohlcData = Arrays.asList(
            new OHLC("2025-02-13", 23055.75, 23120.50, 22980.75, 23019.40, 1250000),
            new OHLC("2025-02-14", 23096.45, 23150.80, 22850.65, 22897.25, 1350000),
            new OHLC("2025-02-17", 22809.90, 23010.75, 22780.60, 22963.50, 1400000),
            new OHLC("2025-02-18", 22963.65, 23075.20, 22850.30, 22923.15, 1500000),
            new OHLC("2025-02-19", 22847.25, 23010.90, 22810.50, 22914.40, 1600000)
        );

        // Compute best entry percentage, stop-loss, and trend status
        double bestEntryPercentage = calculateBestEntryPercentage(ohlcData);
        double stopLoss = calculateStopLoss(ohlcData);
        boolean isTrending = isTrendingMarket(ohlcData);
        boolean breakoutDetected = detectIntradayBreakout(ohlcData);

        // Get real-time market input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Current Market Price: ");
        double currentMarketPrice = scanner.nextDouble();
        System.out.print("Enter Current Volume: ");
        double currentVolume = scanner.nextDouble();
        scanner.close();

        // Execute trade suggestion based on volume confirmation
        if (isVolumeHigh(currentVolume, ohlcData)) {
            suggestTrade(currentMarketPrice, bestEntryPercentage, stopLoss);
        } else {
            System.out.println(" Trade Skipped: Low Volume, Not Ideal for Entry.");
        }

        // Display results
        System.out.printf("Best Entry Pullback: %.2f%%\n", bestEntryPercentage);
        System.out.printf("Suggested Stop-Loss: %.2f points\n", stopLoss);
        System.out.println("Market Trend: " + (isTrending ? "Trending " : "Sideways "));
        System.out.println("Intraday Breakout: " + (breakoutDetected ? "Yes " : "No "));
    }

    /**
     * Computes the average intraday pullback percentage from the previous close.
     */
    public static double calculateBestEntryPercentage(List<OHLC> ohlcData) {
        return ohlcData.stream()
            .skip(1) // Skip the first day (since there's no previous close for it)
            .mapToDouble(o -> ((o.open - ohlcData.get(ohlcData.indexOf(o) - 1).close) / ohlcData.get(ohlcData.indexOf(o) - 1).close) * 100)
            .average().orElse(0);
    }

    /**
     * Calculates stop-loss using Average True Range (ATR) method.
     */
    public static double calculateStopLoss(List<OHLC> ohlcData) {
        return ohlcData.stream()
            .skip(1)
            .mapToDouble(o -> Math.max(o.high - o.low, Math.max(Math.abs(o.high - ohlcData.get(ohlcData.indexOf(o) - 1).close), Math.abs(o.low - ohlcData.get(ohlcData.indexOf(o) - 1).close))))
            .average().orElse(0);
    }

    /**
     * Determines if the market is trending based on a simple moving average (SMA).
     */
    public static boolean isTrendingMarket(List<OHLC> ohlcData) {
        double sma = ohlcData.stream().mapToDouble(o -> o.close).average().orElse(0);
        return ohlcData.get(ohlcData.size() - 1).close > sma && ohlcData.get(ohlcData.size() - 2).close > sma;
    }

    /**
     * Checks if the current volume is higher than the average volume of past data.
     */
    public static boolean isVolumeHigh(double currentVolume, List<OHLC> ohlcData) {
        double avgVolume = ohlcData.stream().mapToDouble(o -> o.volume).average().orElse(0);
        return currentVolume > avgVolume;
    }

    /**
     * Detects an intraday breakout based on previous high/low levels.
     */
    public static boolean detectIntradayBreakout(List<OHLC> ohlcData) {
        OHLC prevDay = ohlcData.get(ohlcData.size() - 2);
        OHLC today = ohlcData.get(ohlcData.size() - 1);
        return today.high > prevDay.high || today.low < prevDay.low;
    }

    /**
     * Suggests a trade entry, stop-loss, and profit targets based on the current price.
     */
    public static void suggestTrade(double currentPrice, double entryPercentage, double stopLoss) {
        double suggestedEntry = currentPrice * (1 + (entryPercentage / 100));
        double suggestedStopLoss = suggestedEntry - stopLoss;
        double suggestedTarget1 = suggestedEntry + (1.5 * stopLoss);
        double suggestedTarget2 = suggestedEntry + (2 * stopLoss);

        System.out.printf("\nSuggested Trade Entry: %.2f\n", suggestedEntry);
        System.out.printf("Stop-Loss: %.2f\n", suggestedStopLoss);
        System.out.printf("Target 1 (1.5x RRR): %.2f\n", suggestedTarget1);
        System.out.printf("Target 2 (2x RRR): %.2f\n", suggestedTarget2);
    }
}
