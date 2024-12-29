package net.abslb.debugstickusage.mixin;

import net.abslb.debugstickusage.Config;
import net.abslb.debugstickusage.Debugstickusage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DebugStickItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DebugStickItem.class)
public class DebugStickMixin {

    @Redirect(
        method = "handleInteraction",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;canUseGameMasterBlocks()Z")
    )
    public boolean NewCanUseDebugStick(Player instance){
        return !instance.isSpectator();
    }

}
