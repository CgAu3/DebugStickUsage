package net.abslb.debugstickusage;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DebugStickItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DebugStickState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;

import dev.dubhe.anvilcraft.block.AbstractMultiplePartBlock;

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
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
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

    @SubscribeEvent
    public void onPlayerRightClickJudgeBlackList(PlayerInteractEvent.RightClickBlock event){
        if(event.getItemStack().getItem() instanceof DebugStickItem
            && !event.getEntity().canUseGameMasterBlocks()){
            BlockState state = event.getLevel().getBlockState(event.getPos());
            DebugStickState debugstickstate =
                (DebugStickState) event.getItemStack().get(DataComponents.DEBUG_STICK_STATE);
            Holder<Block> holder = state.getBlockHolder();
            if (debugstickstate != null) {
                Property<?> property = debugstickstate.properties().get(holder);
                if(!Config.allowTurtuleEggs){
                    if( (state.is(Blocks.TURTLE_EGG) && property == null)
                        ||(property ==  BlockStateProperties.EGGS) ){
                        event.setCanceled(true);
                        return;
                    }
                }
                if (state.getBlock() instanceof AbstractMultiplePartBlock){
                    if(property == null || property.getName().toLowerCase().matches("half")
                        || property.getName().toLowerCase().matches("cube")){
                        event.setCanceled(true);
                        return;
                    }
                }
                int x = 1;
                x += 1;
            }
        }
    }

}
