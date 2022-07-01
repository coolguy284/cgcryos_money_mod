package com.coolguy284.cgcryos_money_mod.client;

import com.coolguy284.cgcryos_money_mod.CgCryosMoneyMod;
import com.coolguy284.cgcryos_money_mod.common.CCMMItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CCMMItemGroup extends ItemGroup {
    private IItemProvider icon;

    public CCMMItemGroup(IItemProvider icon) {
        super(CgCryosMoneyMod.MODID);
        this.icon = icon;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemStack makeIcon() {
        return new ItemStack(icon);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void fillItemList(NonNullList<ItemStack> itemStackList) {
        Item[] itemOrder = {
                CCMMItem.itemCoin1Mille,
                CCMMItem.itemCoin2Mille,
                CCMMItem.itemCoin5Mille,
                CCMMItem.itemCoin1Cent,
                CCMMItem.itemCoin5Cent,
                CCMMItem.itemCoin10Cent,
                CCMMItem.itemCoin25Cent,
                CCMMItem.itemCoin50Cent,
                CCMMItem.itemCoin1Dollar,
                CCMMItem.itemCoin5Dollar,
                CCMMItem.itemCoin10Dollar,
                CCMMItem.itemCoin20Dollar,
                CCMMItem.itemBill1Dollar,
                CCMMItem.itemBill5Dollar,
                CCMMItem.itemBill10Dollar,
                CCMMItem.itemBill20Dollar,
                CCMMItem.itemBill50Dollar,
                CCMMItem.itemBill100Dollar,
                CCMMItem.itemBill200Dollar,
                CCMMItem.itemBill500Dollar,
                CCMMItem.itemBill1000Dollar,
                CCMMItem.itemBill2000Dollar,
                CCMMItem.itemBill5000Dollar,
                CCMMItem.itemBill10000Dollar,
                CCMMItem.itemBill20000Dollar,
                CCMMItem.itemBill50000Dollar,
                CCMMItem.itemBill100000Dollar,
                CCMMItem.itemBill200000Dollar,
                CCMMItem.itemBill500000Dollar,
                CCMMItem.itemBill1000000Dollar,
        };

        for (Item item : itemOrder) {
            item.fillItemCategory(this, itemStackList);
        }
    }
}
