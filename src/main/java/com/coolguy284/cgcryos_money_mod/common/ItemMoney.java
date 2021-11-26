package com.coolguy284.cgcryos_money_mod.common;

import com.coolguy284.cgcryos_money_mod.CgCryosMoneyMod;
import net.minecraft.item.Item;

public class ItemMoney extends Item {
    public String REGISTRY_NAME;
    public boolean isBill = true;
    public long tenthCentAmount;

    public ItemMoney(boolean isBill, long tenthCentAmount) {
        super(new Item.Properties()
                .tab(CgCryosMoneyMod.CCMMGroup)
                .durability(0)
                .stacksTo(64));

        this.tenthCentAmount = tenthCentAmount;

        String prefix = isBill ? "bill_" : "coin_";

        if (tenthCentAmount % 1000 == 0)
            this.REGISTRY_NAME = prefix + (tenthCentAmount / 1000) + "_dollar";
        else if (tenthCentAmount % 10 == 0)
            this.REGISTRY_NAME = prefix + (tenthCentAmount / 10) + "_cent";
        else
            this.REGISTRY_NAME = prefix + tenthCentAmount + "_mille";
    }
}
