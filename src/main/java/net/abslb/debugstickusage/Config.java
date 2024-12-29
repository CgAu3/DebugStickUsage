package net.abslb.debugstickusage;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = Debugstickusage.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /*private static final ModConfigSpec.BooleanValue ALLOW_ADVENTRUE_MODE =
        BUILDER.comment("Whether Adventure Mode players can use debug stick")
            .define("AllowAdventureMode", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean allowAdventureMode;
    */
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        //allowAdventureMode = ALLOW_ADVENTRUE_MODE.get();
    }
}
