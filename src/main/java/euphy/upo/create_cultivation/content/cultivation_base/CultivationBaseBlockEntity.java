package euphy.upo.create_cultivation.content.cultivation_base;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlockEntity;
import euphy.upo.create_cultivation.content.recipes.CultivatingRecipe;
import euphy.upo.create_cultivation.content.recipes.StackingCultivatingRecipe;
import euphy.upo.create_cultivation.registry.CCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;

public class CultivationBaseBlockEntity extends KineticBlockEntity {

    private final ItemStackHandler itemHandler = createItemHandler();
    private boolean isHarvesting = false;
    public CultivationBaseBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);

        updateWorkingState();
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
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

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        if (!clientPacket) {
            compound.put("Inventory", itemHandler.serializeNBT(registries));
        }
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (!clientPacket) {
            itemHandler.deserializeNBT(registries, compound.getCompound("Inventory"));
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
        tankBE.getCurrentRecipe().ifPresent(recipeHolder -> {

            if (recipeHolder.value() instanceof CultivatingRecipe recipe) {


                Ingredient seedIngredient = recipe.getIngredients().get(0);
                if (recipe.getHeight() > 1 && tankBE.getHeight() < recipe.getHeight()) {

                    return;
                }

                boolean replanted = false;


                List<ProcessingOutput> results = recipe.getRollableResults();
                List<ItemStack> rolledResults = new ArrayList<>();

                boolean wasWatered = tankBE.isWatered();
                if (wasWatered) {
                    for (ProcessingOutput output : results) {
                        rolledResults.add(output.getStack().copy());
                    }
                }

                for (ProcessingOutput output : results) {
                    ItemStack rolled = output.rollOutput(level.getRandom());
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
        tankBE.getCurrentRecipe().ifPresent(recipeHolder -> {

            if (recipeHolder.value() instanceof StackingCultivatingRecipe recipe) {
                ProcessingOutput result = recipe.getResult();
                int height = tankBE.getCurrentHeight();
                int harvestedAmount = height - 1;

                if (harvestedAmount <= 0) return;

                ItemStack totalResult = result.getStack().copy();
                totalResult.setCount(result.getStack().getCount() * harvestedAmount);

                List<ItemStack> results = List.of(totalResult);

                if (canInsertAll(results)) {
                    insertAll(results);
                    tankBE.onHarvest();
                }
            }
        });
        } finally {
            this.isHarvesting = false;
        }
    }

    private boolean canInsertAll(List<ItemStack> stacks) {
        ItemStackHandler tempHandler = new ItemStackHandler(itemHandler.getSlots());
        tempHandler.deserializeNBT(level.registryAccess(), itemHandler.serializeNBT(level.registryAccess()));
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
