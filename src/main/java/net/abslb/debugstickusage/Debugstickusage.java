package net.abslb.debugstickusage;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DebugStickItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Debugstickusage.MODID)
public class Debugstickusage {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "debugstickusage";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<UUID, Boolean> playerTriggerMap = new HashMap<>();
    private static final Map<UUID, Long> playerLastTriggerTimeMap = new HashMap<>();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Debugstickusage(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Debugstickusage) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        //modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("Detected Server Started. Note that Debug Sticks will no longer require Permission Level to use.");
    }

    @SubscribeEvent
    public void onAttackBlock(PlayerInteractEvent.LeftClickBlock event){
        if(event.getItemStack().getItem() instanceof DebugStickItem){
            UUID playerUUID = event.getEntity().getUUID();
            if(!playerTriggerMap.getOrDefault(playerUUID, false)
                && !event.getLevel().isClientSide
                && !event.isCanceled()){
                Class<? extends Item> RDebugStickItem = event.getItemStack().getItem().getClass();
                try{
                    Method RhandleInteraction = RDebugStickItem.getDeclaredMethod(
                        "handleInteraction",
                        Player.class,
                        BlockState.class,
                        LevelAccessor.class,
                        BlockPos.class,
                        boolean.class,
                        ItemStack.class
                        );
                    RhandleInteraction.setAccessible(true);
                    RhandleInteraction.invoke(
                        event.getItemStack().getItem(), //object
                        event.getEntity(), //player
                        event.getLevel().getBlockState(event.getPos()), //blockState
                        (LevelAccessor)event.getLevel(), //level
                        event.getPos(), //pos
                        false,
                        event.getEntity().getItemInHand(InteractionHand.MAIN_HAND)
                    );
                }
                catch (Exception e){
                    LOGGER.error("Unable to run Debug Stick handleInteraction.", e);
                }
                playerTriggerMap.put(playerUUID, true);
                playerLastTriggerTimeMap.put(playerUUID, System.currentTimeMillis());
                event.setCanceled(true);
            }
        }


    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event){
        UUID playerUUID = event.getEntity().getUUID();
        if(playerTriggerMap.containsKey(playerUUID)){
            if(playerLastTriggerTimeMap.containsKey(playerUUID)){
                if(System.currentTimeMillis() - playerLastTriggerTimeMap.get(playerUUID) < 500)
                    return;
            }
            playerTriggerMap.remove(playerUUID);
            playerLastTriggerTimeMap.remove(playerUUID);
        }
    }


}
