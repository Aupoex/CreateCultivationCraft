package euphy.upo.create_cultivation.mixin;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlock;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SpoutBlockEntity.class, remap = false)
public abstract class SpoutBlockEntityMixin {

    @Shadow public int processingTicks;
    @Shadow protected SmartFluidTankBehaviour tank;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        SpoutBlockEntity spout = (SpoutBlockEntity) (Object) this;
        Level level = spout.getLevel();

        if (level == null || level.isClientSide() || this.processingTicks != -1) {
            return;
        }

        BlockPos posBelowSpout = spout.getBlockPos().below();
        BlockEntity beBelow = level.getBlockEntity(posBelowSpout);

        if (beBelow instanceof CultivationTankBlockEntity tankBE) {

            CultivationTankBlockEntity controller = tankBE.getControllerBE();
            if (controller == null) {
                return;
            }

            BlockState controllerState = controller.getBlockState();
            if (controllerState.getValue(CultivationTankBlock.PLANTED)) {

                FluidStack fluidInTank = this.tank.getPrimaryHandler().getFluid();
                if (FluidHelper.isWater(fluidInTank.getFluid()) && fluidInTank.getAmount() >= 100) {

                    this.tank.getPrimaryHandler().drain(100, IFluidHandler.FluidAction.EXECUTE);

                    controller.setWatered(true);

                    this.processingTicks = SpoutBlockEntity.FILLING_TIME;
                    spout.notifyUpdate();
                    AllSoundEvents.SPOUTING.playOnServer(level, spout.getBlockPos(), 0.5f, 1.25f);

                    ci.cancel();
                }
            }
        }

    }
}