package com.coolguy284.cgcryos_money_mod;

import com.coolguy284.cgcryos_money_mod.common.CCMMBankCommand;
import com.coolguy284.cgcryos_money_mod.common.CCMMDebugCommand;
import com.coolguy284.cgcryos_money_mod.common.CCMMItem;
import com.coolguy284.cgcryos_money_mod.client.CCMMItemGroup;
import com.coolguy284.cgcryos_money_mod.common.RegistrationHandler;
import net.minecraft.command.impl.DebugCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("cgcryos_money_mod")
public class CgCryosMoneyMod
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "cgcryos_money_mod";

    public static final CCMMItemGroup CCMMGroup = new CCMMItemGroup(() -> (CCMMItem.itemBill1Dollar)) {};

    public CgCryosMoneyMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        RegistrationHandler.registerItems();
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        //LOGGER.info("HELLO FROM PREINIT");
        //LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        //InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world"; });
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        //LOGGER.info("Got IMC {}", event.getIMCStream().
        //        map(m->m.getMessageSupplier().get()).
        //        collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        //LOGGER.info("HELLO from server starting");
    }

    /*
    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public class RegistrationHandler {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            // register a new item here
            itemRegistryEvent.getRegistry().registerAll(new Item1DollarBill());
        }
    }
    */

    @Mod.EventBusSubscriber(modid = MODID)
    public static class ModEventHandler {
        @SubscribeEvent
        public static void onCommandsRegister(RegisterCommandsEvent event) {
            new CCMMDebugCommand(event.getDispatcher());
            new CCMMBankCommand(event.getDispatcher());

            DebugCommand.register(event.getDispatcher());
        }

        @SubscribeEvent
        public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
            if (!event.getEntity().getCommandSenderWorld().isClientSide()) {
                event.getPlayer().getPersistentData();
            }
        }
    }
}
