package com.ai.generated;

/**
 * How to create trailing stoploss. 
 * @author Vihaan
 *
 */
public class TrailingStopLoss {
    private double buyPrice;
    private double sellPrice;
    private double trailingPercentage;
    private double stopLoss;
    private double stopGain;
    private boolean isLongTrade; // true for buy, false for sell

    public TrailingStopLoss(double price, double trailingPercentage, boolean isLongTrade) {
        this.isLongTrade = isLongTrade;
        this.trailingPercentage = trailingPercentage;
        
        if (isLongTrade) {
            this.buyPrice = price;
            this.stopLoss = buyPrice * (1 - trailingPercentage / 100);
        } else {
            this.sellPrice = price;
            this.stopGain = sellPrice * (1 + trailingPercentage / 100);
        }
    }

    public void updatePrice(double currentPrice) {
        if (isLongTrade) {
            // Adjust the stop loss if the price rises
            if (currentPrice > buyPrice) {
                buyPrice = currentPrice;
                stopLoss = buyPrice * (1 - trailingPercentage / 100);
            }
            // Check if stop loss is hit
            if (currentPrice <= stopLoss) {
                System.out.println("Stop loss triggered! Selling at: " + currentPrice);
            } else {
                System.out.println("Current price: " + currentPrice + ", Stop loss: " + stopLoss);
            }
        } else {
            // Adjust the stop gain if the price falls
            if (currentPrice < sellPrice) {
                sellPrice = currentPrice;
                stopGain = sellPrice * (1 + trailingPercentage / 100);
            }
            // Check if stop gain is hit
            if (currentPrice >= stopGain) {
                System.out.println("Stop gain triggered! Buying at: " + currentPrice);
            } else {
                System.out.println("Current price: " + currentPrice + ", Stop gain: " + stopGain);
            }
        }
    }

    public static void main(String[] args) {
        double entryPrice = 100.0;
        double trailingPercentage = 5.0;

        System.out.println("Simulating Long Trade:");
        TrailingStopLoss longTrade = new TrailingStopLoss(entryPrice, trailingPercentage, true);
        double[] longPriceChanges = {102, 105, 110, 108, 112, 107, 101, 95};
        for (double price : longPriceChanges) {
            longTrade.updatePrice(price);
        }

        System.out.println("\nSimulating Short Trade:");
        TrailingStopLoss shortTrade = new TrailingStopLoss(entryPrice, trailingPercentage, false);
        double[] shortPriceChanges = {98, 95, 93, 97, 90, 88, 92, 99};
        for (double price : shortPriceChanges) {
            shortTrade.updatePrice(price);
        }
    }
}

