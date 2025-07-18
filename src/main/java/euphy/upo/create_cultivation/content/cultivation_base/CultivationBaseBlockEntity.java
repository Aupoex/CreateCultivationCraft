package euphy.upo.create_cultivation.content.cultivation_base;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlockEntity;
import euphy.upo.create_cultivation.content.recipes.CultivatingRecipe;
import euphy.upo.create_cultivation.content.recipes.StackingCultivatingRecipe;
import euphy.upo.create_cultivation.registry.CCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CultivationBaseBlockEntity extends KineticBlockEntity {

    private final ItemStackHandler itemHandler = createItemHandler();
    private LazyOptional<IItemHandler> itemHandlerCapability = LazyOptional.empty();
    private boolean isHarvesting = false;

    public CultivationBaseBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        updateWorkingState();
    }

    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(8) {
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (!isHarvesting) {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void initialize() {
        super.initialize();
        this.itemHandlerCapability = LazyOptional.of(() -> this.itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerCapability.invalidate();
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        if (!clientPacket) {
            compound.put("Inventory", itemHandler.serializeNBT());
        }
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if (!clientPacket) {
            itemHandler.deserializeNBT(compound.getCompound("Inventory"));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide) {
            return;
        }

        if (lazyTickCounter-- < 0) {
            lazyTickCounter = 20;
            updateWorkingState();
        }

        if (getBlockState().getValue(CultivationBaseBlock.WORKING)) {
            tryHarvest();
        }
    }

    private void tryHarvest() {
        if(level == null) return;
        BlockEntity be = level.getBlockEntity(worldPosition.above());
        if (!(be instanceof CultivationTankBlockEntity tankBE)) {
            return;
        }

        if (!tankBE.isReadyForHarvest()) {
            return;
        }

        switch (tankBE.getRecipeMode()) {
            case STAGE_BASED -> harvestStageBased(tankBE);
            case STACK_BASED -> harvestStackBased(tankBE);
        }
    }

    private void harvestStageBased(CultivationTankBlockEntity tankBE) {
        this.isHarvesting = true;
        try {
            tankBE.getCurrentRecipe().ifPresent(recipe -> {
                if (recipe instanceof CultivatingRecipe cultivatingRecipe) {
                    Ingredient seedIngredient = cultivatingRecipe.getIngredients().get(0);
                    if (cultivatingRecipe.getHeight() > 1 && tankBE.getHeight() < cultivatingRecipe.getHeight()) return;

                    boolean replanted = false;
                    List<ProcessingOutput> results = cultivatingRecipe.getRollableResults();
                    List<ItemStack> rolledResults = new ArrayList<>();

                    if (tankBE.isWatered()) {
                        for (ProcessingOutput output : results) {
                            rolledResults.add(output.getStack().copy());
                        }
                    }

                    for (ProcessingOutput output : results) {
                        ItemStack rolled = output.rollOutput();
                        if (!rolled.isEmpty()) {
                            rolledResults.add(rolled);
                        }
                    }

                    for (int i = 0; i < rolledResults.size(); i++) {
                        ItemStack potentialSeed = rolledResults.get(i);
                        if (seedIngredient.test(potentialSeed)) {
                            potentialSeed.shrink(1);
                            replanted = true;
                            if (potentialSeed.isEmpty()) {
                                rolledResults.remove(i);
                            }
                            break;
                        }
                    }

                    if (canInsertAll(rolledResults)) {
                        insertAll(rolledResults);
                        tankBE.onHarvest(replanted);
                    }
                }
            });
        } finally {
            this.isHarvesting = false;
        }
    }

    private void harvestStackBased(CultivationTankBlockEntity tankBE) {
        this.isHarvesting = true;
        try {
            tankBE.getCurrentRecipe().ifPresent(recipe -> {
                if (recipe instanceof StackingCultivatingRecipe stackingRecipe) {
                    ProcessingOutput result = stackingRecipe.getResult();
                    int height = tankBE.getCurrentHeight();
                    int harvestedAmount = height - 1;

                    if (harvestedAmount <= 0) return;

                    ItemStack totalResult = result.getStack().copy();
                    totalResult.setCount(result.getStack().getCount() * harvestedAmount);

                    if (canInsertAll(List.of(totalResult))) {
                        insertAll(List.of(totalResult));
                        tankBE.onHarvest(true);
                    }
                }
            });
        } finally {
            this.isHarvesting = false;
        }
    }

    private boolean canInsertAll(List<ItemStack> stacks) {
        ItemStackHandler tempHandler = new ItemStackHandler(itemHandler.getSlots());
        tempHandler.deserializeNBT(itemHandler.serializeNBT());
        for (ItemStack stack : stacks) {
            if (!ItemHandlerHelper.insertItemStacked(tempHandler, stack, true).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void insertAll(List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            ItemHandlerHelper.insertItemStacked(itemHandler, stack, false);
        }
    }

    public void updateWorkingState() {
        if (level == null) return;
        boolean currentState = getBlockState().getValue(CultivationBaseBlock.WORKING);
        boolean shouldBeWorking = false;

        boolean hasPower = getSpeed() != 0;
        boolean hasTankAbove = level.getBlockState(worldPosition.above()).is(CCBlocks.CULTIVATION_TANK.get());

        if (hasPower && hasTankAbove) {
            shouldBeWorking = true;
        }

        if (currentState != shouldBeWorking) {
            level.setBlock(worldPosition, getBlockState().setValue(CultivationBaseBlock.WORKING, shouldBeWorking), 3);
        }
    }
}