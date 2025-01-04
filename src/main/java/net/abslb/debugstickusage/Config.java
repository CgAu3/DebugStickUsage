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

    private static final ModConfigSpec.BooleanValue ALLOW_TURTLE_EGGS =
        BUILDER.comment("Whether Survival Mode players can debug turtle egg counts")
            .define("AllowTurtleEggs", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean allowTurtuleEggs;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        allowTurtuleEggs = ALLOW_TURTLE_EGGS.get();
    }
}
