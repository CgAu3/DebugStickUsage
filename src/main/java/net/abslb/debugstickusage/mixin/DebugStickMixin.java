package net.abslb.debugstickusage.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DebugStickItem;
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

    @Inject(
        method = "canAttackBlock",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true)
    public void OnlyForInstabuild(BlockState state,
                                  Level level,
                                  BlockPos pos,
                                  Player player,
                                  CallbackInfoReturnable<Boolean> cir){
        if(!player.getAbilities().instabuild){
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
