package com.coolguy284.cgcryos_money_mod.common;

import java.util.ArrayList;
import java.util.Hashtable;

public class CCMMItem {
    public static ItemMoney itemCoin1Mille = new ItemMoney(false, 1);
    public static ItemMoney itemCoin2Mille = new ItemMoney(false, 2);
    public static ItemMoney itemCoin5Mille = new ItemMoney(false, 5);
    public static ItemMoney itemCoin1Cent = new ItemMoney(false, 10);
    public static ItemMoney itemCoin5Cent = new ItemMoney(false, 50);
    public static ItemMoney itemCoin10Cent = new ItemMoney(false, 100);
    public static ItemMoney itemCoin25Cent = new ItemMoney(false, 250);
    public static ItemMoney itemCoin50Cent = new ItemMoney(false, 500);
    public static ItemMoney itemCoin1Dollar = new ItemMoney(false, 1000);
    public static ItemMoney itemCoin5Dollar = new ItemMoney(false, 5000);
    public static ItemMoney itemCoin10Dollar = new ItemMoney(false, 10000);
    public static ItemMoney itemCoin20Dollar = new ItemMoney(false, 20000);

    public static ItemMoney itemBill1Dollar = new ItemMoney(true, 1000);
    public static ItemMoney itemBill5Dollar = new ItemMoney(true, 5000);
    public static ItemMoney itemBill10Dollar = new ItemMoney(true, 10000);
    public static ItemMoney itemBill20Dollar = new ItemMoney(true, 20000);
    public static ItemMoney itemBill50Dollar = new ItemMoney(true, 50000);
    public static ItemMoney itemBill100Dollar = new ItemMoney(true, 100000);
    public static ItemMoney itemBill200Dollar = new ItemMoney(true, 200000);
    public static ItemMoney itemBill500Dollar = new ItemMoney(true, 500000);
    public static ItemMoney itemBill1000Dollar = new ItemMoney(true, 1000000);
    public static ItemMoney itemBill2000Dollar = new ItemMoney(true, 2000000);
    public static ItemMoney itemBill5000Dollar = new ItemMoney(true, 5000000);
    public static ItemMoney itemBill10000Dollar = new ItemMoney(true, 10000000);
    public static ItemMoney itemBill20000Dollar = new ItemMoney(true, 20000000);
    public static ItemMoney itemBill50000Dollar = new ItemMoney(true, 50000000);
    public static ItemMoney itemBill100000Dollar = new ItemMoney(true, 100000000);
    public static ItemMoney itemBill200000Dollar = new ItemMoney(true, 200000000);
    public static ItemMoney itemBill500000Dollar = new ItemMoney(true, 500000000);
    public static ItemMoney itemBill1000000Dollar = new ItemMoney(true, 1000000000);

    public static long[] _coinValues = {20000, 10000, 5000, 1000, 500, 250, 100, 50, 10, 5, 2, 1};
    public static long[] _billValues = {1000000000, 500000000, 200000000, 100000000, 50000000, 20000000, 10000000, 5000000, 2000000, 1000000, 500000, 200000, 100000, 50000, 20000, 10000, 5000, 1000};

    public static Hashtable<Long, ItemMoney> _coins = new Hashtable<>();
    public static Hashtable<Long, ItemMoney> _bills = new Hashtable<>();

    static {
        _coins.put(20000L, itemCoin20Dollar);
        _coins.put(10000L, itemCoin10Dollar);
        _coins.put(5000L, itemCoin5Dollar);
        _coins.put(1000L, itemCoin1Dollar);
        _coins.put(500L, itemCoin50Cent);
        _coins.put(250L, itemCoin25Cent);
        _coins.put(100L, itemCoin10Cent);
        _coins.put(50L, itemCoin5Cent);
        _coins.put(10L, itemCoin1Cent);
        _coins.put(5L, itemCoin5Mille);
        _coins.put(2L, itemCoin2Mille);
        _coins.put(1L, itemCoin1Mille);

        _bills.put(1000000000L, itemBill1000000Dollar);
        _bills.put(500000000L, itemBill500000Dollar);
        _bills.put(200000000L, itemBill200000Dollar);
        _bills.put(100000000L, itemBill100000Dollar);
        _bills.put(50000000L, itemBill50000Dollar);
        _bills.put(20000000L, itemBill20000Dollar);
        _bills.put(10000000L, itemBill10000Dollar);
        _bills.put(5000000L, itemBill5000Dollar);
        _bills.put(2000000L, itemBill2000Dollar);
        _bills.put(1000000L, itemBill1000Dollar);
        _bills.put(500000L, itemBill500Dollar);
        _bills.put(200000L, itemBill200Dollar);
        _bills.put(100000L, itemBill100Dollar);
        _bills.put(50000L, itemBill50Dollar);
        _bills.put(20000L, itemBill20Dollar);
        _bills.put(10000L, itemBill10Dollar);
        _bills.put(5000L, itemBill5Dollar);
        _bills.put(1000L, itemBill1Dollar);
    }

    public static ItemMoney getItemMoneySingleton(boolean isBill, long tenthCentAmount) throws NullPointerException {
        if (isBill) {
            return _bills.get(tenthCentAmount);
        } else {
            return _coins.get(tenthCentAmount);
        }
    }

    public static class MoneyWithCount {
        public long count;
        public ItemMoney money;

        public MoneyWithCount(long count, ItemMoney money) {
            this.count = count;
            this.money = money;
        }
    }

    public static ArrayList<MoneyWithCount> getItemMoneysArbitrary(long tenthCentAmount, long coinBillCutoff) {
        ArrayList<MoneyWithCount> moneyWithCounts = new ArrayList<>();

        for (long moneyValue : _billValues) {
            if (moneyValue < coinBillCutoff) break;
            long count = tenthCentAmount / moneyValue;
            if (count < 1) continue;
            moneyWithCounts.add(new MoneyWithCount(count, getItemMoneySingleton(true, moneyValue)));
            tenthCentAmount -= count * moneyValue;
        }

        for (long moneyValue : _coinValues) {
            if (moneyValue >= coinBillCutoff) continue;
            long count = tenthCentAmount / moneyValue;
            if (count < 1) continue;
            moneyWithCounts.add(new MoneyWithCount(count, getItemMoneySingleton(false, moneyValue)));
            tenthCentAmount -= count * moneyValue;
        }

        return moneyWithCounts;
    }
}
