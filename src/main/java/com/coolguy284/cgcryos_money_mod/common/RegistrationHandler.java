package com.coolguy284.cgcryos_money_mod.common;

import com.coolguy284.cgcryos_money_mod.CgCryosMoneyMod;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistrationHandler {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CgCryosMoneyMod.MODID);

    public static final RegistryObject<Item> ITEM_COIN_1_MILLE = ITEMS.register(CCMMItem.itemCoin1Mille.REGISTRY_NAME, () -> CCMMItem.itemCoin1Mille);
    public static final RegistryObject<Item> ITEM_COIN_2_MILLE = ITEMS.register(CCMMItem.itemCoin2Mille.REGISTRY_NAME, () -> CCMMItem.itemCoin2Mille);
    public static final RegistryObject<Item> ITEM_COIN_5_MILLE = ITEMS.register(CCMMItem.itemCoin5Mille.REGISTRY_NAME, () -> CCMMItem.itemCoin5Mille);
    public static final RegistryObject<Item> ITEM_COIN_1_CENT = ITEMS.register(CCMMItem.itemCoin1Cent.REGISTRY_NAME, () -> CCMMItem.itemCoin1Cent);
    public static final RegistryObject<Item> ITEM_COIN_5_CENT = ITEMS.register(CCMMItem.itemCoin5Cent.REGISTRY_NAME, () -> CCMMItem.itemCoin5Cent);
    public static final RegistryObject<Item> ITEM_COIN_10_CENT = ITEMS.register(CCMMItem.itemCoin10Cent.REGISTRY_NAME, () -> CCMMItem.itemCoin10Cent);
    public static final RegistryObject<Item> ITEM_COIN_25_CENT = ITEMS.register(CCMMItem.itemCoin25Cent.REGISTRY_NAME, () -> CCMMItem.itemCoin25Cent);
    public static final RegistryObject<Item> ITEM_COIN_50_CENT = ITEMS.register(CCMMItem.itemCoin50Cent.REGISTRY_NAME, () -> CCMMItem.itemCoin50Cent);
    public static final RegistryObject<Item> ITEM_COIN_1_DOLLAR = ITEMS.register(CCMMItem.itemCoin1Dollar.REGISTRY_NAME, () -> CCMMItem.itemCoin1Dollar);
    public static final RegistryObject<Item> ITEM_COIN_5_DOLLAR = ITEMS.register(CCMMItem.itemCoin5Dollar.REGISTRY_NAME, () -> CCMMItem.itemCoin5Dollar);
    public static final RegistryObject<Item> ITEM_COIN_10_DOLLAR = ITEMS.register(CCMMItem.itemCoin10Dollar.REGISTRY_NAME, () -> CCMMItem.itemCoin10Dollar);
    public static final RegistryObject<Item> ITEM_COIN_20_DOLLAR = ITEMS.register(CCMMItem.itemCoin20Dollar.REGISTRY_NAME, () -> CCMMItem.itemCoin20Dollar);

    public static final RegistryObject<Item> ITEM_BILL_1_DOLLAR = ITEMS.register(CCMMItem.itemBill1Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill1Dollar);
    public static final RegistryObject<Item> ITEM_BILL_5_DOLLAR = ITEMS.register(CCMMItem.itemBill5Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill5Dollar);
    public static final RegistryObject<Item> ITEM_BILL_10_DOLLAR = ITEMS.register(CCMMItem.itemBill10Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill10Dollar);
    public static final RegistryObject<Item> ITEM_BILL_20_DOLLAR = ITEMS.register(CCMMItem.itemBill20Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill20Dollar);
    public static final RegistryObject<Item> ITEM_BILL_50_DOLLAR = ITEMS.register(CCMMItem.itemBill50Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill50Dollar);
    public static final RegistryObject<Item> ITEM_BILL_100_DOLLAR = ITEMS.register(CCMMItem.itemBill100Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill100Dollar);
    public static final RegistryObject<Item> ITEM_BILL_200_DOLLAR = ITEMS.register(CCMMItem.itemBill200Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill200Dollar);
    public static final RegistryObject<Item> ITEM_BILL_500_DOLLAR = ITEMS.register(CCMMItem.itemBill500Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill500Dollar);
    public static final RegistryObject<Item> ITEM_BILL_1000_DOLLAR = ITEMS.register(CCMMItem.itemBill1000Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill1000Dollar);
    public static final RegistryObject<Item> ITEM_BILL_2000_DOLLAR = ITEMS.register(CCMMItem.itemBill2000Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill2000Dollar);
    public static final RegistryObject<Item> ITEM_BILL_5000_DOLLAR = ITEMS.register(CCMMItem.itemBill5000Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill5000Dollar);
    public static final RegistryObject<Item> ITEM_BILL_10000_DOLLAR = ITEMS.register(CCMMItem.itemBill10000Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill10000Dollar);
    public static final RegistryObject<Item> ITEM_BILL_20000_DOLLAR = ITEMS.register(CCMMItem.itemBill20000Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill20000Dollar);
    public static final RegistryObject<Item> ITEM_BILL_50000_DOLLAR = ITEMS.register(CCMMItem.itemBill50000Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill50000Dollar);
    public static final RegistryObject<Item> ITEM_BILL_100000_DOLLAR = ITEMS.register(CCMMItem.itemBill100000Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill100000Dollar);
    public static final RegistryObject<Item> ITEM_BILL_200000_DOLLAR = ITEMS.register(CCMMItem.itemBill200000Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill200000Dollar);
    public static final RegistryObject<Item> ITEM_BILL_500000_DOLLAR = ITEMS.register(CCMMItem.itemBill500000Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill500000Dollar);
    public static final RegistryObject<Item> ITEM_BILL_1000000_DOLLAR = ITEMS.register(CCMMItem.itemBill1000000Dollar.REGISTRY_NAME, () -> CCMMItem.itemBill1000000Dollar);

    public static void registerItems() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
