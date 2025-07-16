package euphy.upo.create_cultivation.content.cultivation_tank;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import euphy.upo.create_cultivation.content.cultivation_base.CultivationBaseBlock;
import euphy.upo.create_cultivation.content.recipes.CultivatingRecipe;
import euphy.upo.create_cultivation.content.recipes.StackingCultivatingRecipe;
import euphy.upo.create_cultivation.registry.CCRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class CultivationTankBlockEntity extends SmartBlockEntity implements IMultiBlockEntityContainer {

    private Optional<RecipeHolder<?>> currentRecipe = Optional.empty();
    private ResourceLocation recipeToLoad = null;
    private RecipeMode recipeMode = RecipeMode.NONE;

    private boolean isWatered;
    private int wateredTickCounter;

    private int progress = 0;
    private int processingDuration = 10;
    private int harvestCooldown = 0;
    private static final int MATURE_DISPLAY_TICKS = 6;

    private static final int TOTAL_GROWTH_STAGES = 4;

    private int currentHeight = 0;
    private int maxHeight = 3;

    private float growthAccumulator = 0.0f;

    private BlockPos controller;
    private BlockPos lastKnownPos;
    private boolean updateConnectivity;
    private int height;
    private int width;

    public enum RecipeMode { NONE, STAGE_BASED, STACK_BASED }

    public CultivationTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.height = 1;
        this.width = 1;
        this.updateConnectivity = false;
    }

    @Override
    public void initialize() {
        super.initialize();
        this.sendData();
        if (this.level.isClientSide) {
            this.invalidateRenderBoundingBox();
        }
    }

    public boolean plant(ItemStack seedStack) {
        if (level == null) return false;

        RecipeInput inventoryWrapper = new RecipeInput() {
            @Override
            public ItemStack getItem(int slot) { return slot == 0 ? seedStack : ItemStack.EMPTY; }
            @Override
            public int size() { return 1; }
        };

        Optional<RecipeHolder<CultivatingRecipe>> stageRecipe = level.getRecipeManager()
                .getRecipeFor(CCRecipes.CULTIVATING.getType(), inventoryWrapper, level);
        if (stageRecipe.isPresent()) {
            activateStageRecipe(stageRecipe.get());
            return true;
        }

        Optional<RecipeHolder<StackingCultivatingRecipe>> stackRecipe = level.getRecipeManager()
                .getRecipeFor(CCRecipes.STACKING_CULTIVATING.getType(), inventoryWrapper, level);
        if (stackRecipe.isPresent()) {
            activateStackRecipe(stackRecipe.get());
            return true;
        }

        return false;
    }

    private void activateStageRecipe(RecipeHolder<CultivatingRecipe> recipe) {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null) return;
        controllerBE.currentRecipe = Optional.of(recipe);
        controllerBE.recipeMode = RecipeMode.STAGE_BASED;
        controllerBE.processingDuration = recipe.value().getProcessingDuration() > 0 ? recipe.value().getProcessingDuration() / 10 : 10;
        controllerBE.progress = 0;
        level.setBlock(worldPosition, getBlockState().setValue(CultivationTankBlock.PLANTED, true).setValue(CultivationTankBlock.GROWTH_STAGE, 0), 3);
        controllerBE.setChanged();
        controllerBE.notifyUpdate();
    }

    private void activateStackRecipe(RecipeHolder<StackingCultivatingRecipe> recipe) {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null) return;
        controllerBE.currentRecipe = Optional.of(recipe);
        controllerBE.recipeMode = RecipeMode.STACK_BASED;
        controllerBE.processingDuration = recipe.value().getProcessingDuration() > 0 ? recipe.value().getProcessingDuration() / 10 : 20;
        controllerBE.maxHeight = recipe.value().getMaxHeight();
        controllerBE.progress = 0;
        controllerBE.currentHeight = 1;
        level.setBlock(worldPosition, getBlockState().setValue(CultivationTankBlock.PLANTED, true), 3);
        controllerBE.setChanged();
        controllerBE.notifyUpdate();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.recipeToLoad != null && this.level != null && this.isController()) {
            this.level.getRecipeManager().byKey(this.recipeToLoad).ifPresent(recipe -> {
                this.currentRecipe = Optional.of(recipe);
                if (recipe.value() instanceof CultivatingRecipe cr) {
                    this.processingDuration = cr.getProcessingDuration() > 0 ? cr.getProcessingDuration() / 10 : 10;
                } else if (recipe.value() instanceof StackingCultivatingRecipe sr) {
                    this.processingDuration = sr.getProcessingDuration() > 0 ? sr.getProcessingDuration() / 10 : 20;
                }
            });
            this.recipeToLoad = null;
        }

        if (this.lastKnownPos == null) {
            this.lastKnownPos = this.getBlockPos();
        } else if (!this.lastKnownPos.equals(this.worldPosition)) {
            this.onPositionChanged();
            return;
        }
        if (this.updateConnectivity) {
            this.updateConnectivity();
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (level == null || level.isClientSide || !isController()) return;

        if (isWatered) {
            if (wateredTickCounter-- <= 0) {
                isWatered = false;
                setChanged();
                notifyUpdate();
            }
        }

        updateWorkingState();

        if (getBlockState().getValue(CultivationTankBlock.WORKING) && getBlockState().getValue(CultivationTankBlock.PLANTED)) {
            if (recipeMode == RecipeMode.STAGE_BASED) {
                boolean canGrow = currentRecipe.map(holder -> {
                    if (holder.value() instanceof CultivatingRecipe recipe) {
                        return recipe.getHeight() <= 1 || getHeight() >= recipe.getHeight();
                    }
                    return true;
                }).orElse(false);
                if (!canGrow) return;
            }

            if (!isMature()) {
                float speedMultiplier = getSpeedMultiplier();
                if (speedMultiplier > 0) {
                    growthAccumulator += speedMultiplier;
                    int pointsToApply = (int) growthAccumulator;
                    if (pointsToApply > 0) {
                        switch (recipeMode) {
                            case STAGE_BASED: handleStageGrowth(pointsToApply); break;
                            case STACK_BASED: handleStackingGrowth(pointsToApply); break;
                        }
                        growthAccumulator -= pointsToApply;
                    }
                }

                if (isMature()) {
                    harvestCooldown = MATURE_DISPLAY_TICKS;
                }
            } else {
                if (harvestCooldown > 0) {
                    harvestCooldown--;
                }
            }
            setChanged();
        }
    }

    public float getSpeedMultiplier() {
        BlockEntity beBelow = level.getBlockEntity(worldPosition.below());
        if (beBelow instanceof KineticBlockEntity kineticBE) {
            float speed = Math.abs(kineticBE.getSpeed());
            if (speed < 32) {
                return speed / 32.0f;
            }
            return Mth.lerp(Mth.clamp((speed - 32) / (256 - 32), 0, 1), 1.0f, 2.0f);
        }
        return 0;
    }


    private void handleStageGrowth(int points) {
        progress += points;
        setChanged();
        updateVisualGrowthStage();
    }

    private void handleStackingGrowth(int points) {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null) return;

        controllerBE.progress += points;
        int limit = Math.min(controllerBE.getHeight(), controllerBE.maxHeight);

        while (controllerBE.progress >= controllerBE.processingDuration) {
            if (controllerBE.currentHeight >= limit) {
                controllerBE.progress = controllerBE.processingDuration;
                break;
            }
            controllerBE.progress -= controllerBE.processingDuration;
            controllerBE.currentHeight++;
            controllerBE.setChanged();
            controllerBE.notifyUpdate();
        }
    }

    public void onHarvest() {
        this.onHarvest(false);
    }


    public void onHarvest(boolean replant) {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null) return;

        controllerBE.progress = 0;
        controllerBE.harvestCooldown = 0;

        if (controllerBE.recipeMode == RecipeMode.STACK_BASED) {

            controllerBE.currentHeight = 1;
        } else if (controllerBE.recipeMode == RecipeMode.STAGE_BASED) {
            if (replant) {

            } else {

                controllerBE.currentRecipe = Optional.empty();
                controllerBE.recipeMode = RecipeMode.NONE;
                controllerBE.currentHeight = 0;
                for (int i = 0; i < controllerBE.getHeight(); i++) {
                    BlockPos pos = controllerBE.getBlockPos().above(i);
                    BlockState blockState = level.getBlockState(pos);
                    if (blockState.getBlock() instanceof CultivationTankBlock) {
                        level.setBlock(pos, blockState.setValue(CultivationTankBlock.PLANTED, false).setValue(CultivationTankBlock.GROWTH_STAGE, 0), 3);
                    }
                }
            }
        }
        controllerBE.setChanged();
        controllerBE.notifyUpdate();
    }

    public RecipeMode getRecipeMode() {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        return controllerBE != null ? controllerBE.recipeMode : RecipeMode.NONE;
    }

    public Optional<RecipeHolder<?>> getCurrentRecipe() {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        return controllerBE != null ? controllerBE.currentRecipe : Optional.empty();
    }

    public int getCurrentHeight() {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        return controllerBE != null ? controllerBE.currentHeight : 0;
    }

    public float getStageGrowthRatio() {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null || controllerBE.processingDuration == 0) return 0;
        return (float) controllerBE.progress / controllerBE.processingDuration;
    }

    public boolean isMature() {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null) return false;

        if (controllerBE.recipeMode == RecipeMode.STAGE_BASED) {
            return getCalculatedGrowthStage() >= (TOTAL_GROWTH_STAGES - 1);
        }
        if (controllerBE.recipeMode == RecipeMode.STACK_BASED) {
            int limit = Math.min(controllerBE.getHeight(), controllerBE.maxHeight);
            return controllerBE.currentHeight >= limit;
        }
        return false;
    }

    public boolean isReadyForHarvest() {
        return isMature() && harvestCooldown <= 0;
    }

    private int getCalculatedGrowthStage() {
        return Mth.floor(getStageGrowthRatio() * (TOTAL_GROWTH_STAGES - 1));
    }

    private void updateVisualGrowthStage() {
        int currentStage = getBlockState().getValue(CultivationTankBlock.GROWTH_STAGE);
        int calculatedStage = getCalculatedGrowthStage();

        if (currentStage != calculatedStage) {
            level.setBlock(worldPosition, getBlockState().setValue(CultivationTankBlock.GROWTH_STAGE, calculatedStage), 3);
        }
    }

    public void updateWorkingState() {
        if (level == null) return;
        boolean shouldBeWorking = false;
        BlockState belowState = level.getBlockState(worldPosition.below());

        if (belowState.getBlock() instanceof CultivationBaseBlock && belowState.getValue(CultivationBaseBlock.WORKING)) {
            shouldBeWorking = true;
        }

        if (getBlockState().getValue(CultivationTankBlock.WORKING) != shouldBeWorking) {
            level.setBlock(worldPosition, getBlockState().setValue(CultivationTankBlock.WORKING, shouldBeWorking), 3);
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (isController()) {
            compound.putInt("RecipeMode", this.recipeMode.ordinal());
            currentRecipe.ifPresent(recipeHolder -> compound.putString("RecipeId", recipeHolder.id().toString()));
            compound.putInt("Progress", progress);
            compound.putInt("CurrentHeight", currentHeight);
            compound.putInt("MaxHeight", maxHeight);
            compound.putInt("HarvestCooldown", harvestCooldown);
            compound.putFloat("GrowthAccumulator", growthAccumulator);
            if (isWatered) {
                compound.putBoolean("Watered", true);
            }
        }

        if (this.updateConnectivity)
            compound.putBoolean("Uninitialized", true);
        if (this.lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(this.lastKnownPos));
        if (!isController())
            compound.put("Controller", NbtUtils.writeBlockPos(this.controller));
        if (isController()) {
            compound.putInt("Height", this.height);
        }
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (isController()) {
            recipeMode = RecipeMode.values()[compound.getInt("RecipeMode")];

            recipeToLoad = null;
            currentRecipe = Optional.empty();
            if (compound.contains("RecipeId")) {
                recipeToLoad = ResourceLocation.tryParse(compound.getString("RecipeId"));
            }

            progress = compound.getInt("Progress");
            currentHeight = compound.getInt("CurrentHeight");
            maxHeight = compound.getInt("MaxHeight");
            harvestCooldown = compound.getInt("HarvestCooldown");
            growthAccumulator = compound.getFloat("GrowthAccumulator");
            isWatered = compound.getBoolean("Watered");
        }

        this.updateConnectivity = compound.contains("Uninitialized");
        this.lastKnownPos = NbtUtils.readBlockPos(compound, "LastKnownPos").orElse(null);
        this.controller = NbtUtils.readBlockPos(compound, "Controller").orElse(null);

        if (isController()) {
            this.height = compound.getInt("Height");
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    private void onPositionChanged() {
        this.removeController(true);
        this.lastKnownPos = this.worldPosition;
    }

    protected void updateConnectivity() {
        this.updateConnectivity = false;
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        if (!isController()) {
            CultivationTankBlockEntity controllerBE = getControllerBE();
            if (controllerBE != null) {
                controllerBE.updateConnectivity();
            }
            return;
        }


        BlockPos bottomMostPos = getBlockPos();
        while (level.getBlockState(bottomMostPos.below()).is(getBlockState().getBlock())) {
            bottomMostPos = bottomMostPos.below();
        }

        BlockEntity be = level.getBlockEntity(bottomMostPos);
        if (be instanceof CultivationTankBlockEntity bottomBE) {

            ConnectivityHandler.formMulti(bottomBE);
        } else {
            ConnectivityHandler.formMulti(this);
        }
    }

    public void removeController(boolean keepContents) {
        if (this.level.isClientSide) return;
        this.updateConnectivity = true;
        this.controller = null;
        this.width = 1;
        this.height = 1;

        BlockState state = getBlockState();
        if (state.getBlock() instanceof CultivationTankBlock) {
            state = state.setValue(CultivationTankBlock.TOP, true).setValue(CultivationTankBlock.BOTTOM, true);
            getLevel().setBlock(worldPosition, state, 3);
        }
        setChanged();
        sendData();
    }

    @Override
    public void setController(BlockPos controller) {
        if (this.level.isClientSide && !isVirtual()) return;
        if (controller.equals(this.controller)) return;
        this.controller = controller;
        setChanged();
        sendData();
    }

    @Override
    public BlockPos getController() {
        return isController() ? this.worldPosition : this.controller;
    }

    @Override
    public CultivationTankBlockEntity getControllerBE() {
        if (isController() || !hasLevel()) return this;
        BlockEntity be = this.level.getBlockEntity(this.controller);
        if (be instanceof CultivationTankBlockEntity) return (CultivationTankBlockEntity) be;
        return null;
    }

    @Override
    public boolean isController() {
        return this.controller == null || this.worldPosition.equals(this.controller);
    }

    @Override
    public BlockPos getLastKnownPos() { return this.lastKnownPos; }

    @Override
    public Direction.Axis getMainConnectionAxis() { return Direction.Axis.Y; }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if (longAxis == Direction.Axis.Y) return 32;
        return 1;
    }

    @Override
    public int getMaxWidth() { return 1; }

    @Override
    public int getHeight() {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE != null) return controllerBE.height;
        return this.height;
    }

    @Override
    public void setHeight(int height) { this.height = height; }

    @Override
    public int getWidth() { return this.width; }

    @Override
    public void setWidth(int width) { this.width = width; }

    @Override
    public void preventConnectivityUpdate() { this.updateConnectivity = false; }

    public void scheduleConnectivityUpdate() {
        this.updateConnectivity = true;
        setChanged();
    }

    @Override
    public void notifyMultiUpdated() {
        if (level == null || level.isClientSide) {
            return;
        }

        if (!isController()) {
            return;
        }
        int height = getHeight();
        BlockPos controllerPos = getBlockPos();


        for (int i = 0; i < height; i++) {
            BlockPos currentPos = controllerPos.above(i);
            BlockState currentState = level.getBlockState(currentPos);

            if (currentState.getBlock() instanceof CultivationTankBlock) {
                boolean isBottom = (i == 0);
                boolean isTop = (i == height - 1);

                BlockState newState = currentState
                        .setValue(CultivationTankBlock.BOTTOM, isBottom)
                        .setValue(CultivationTankBlock.TOP, isTop);

                if (newState != currentState) {
                    level.setBlock(currentPos, newState, 3);
                }
            }
        }
    }

    public void setPonderProgress(int progress) {
        this.progress = progress;
    }
    public int getProgress() {
        return this.progress;
    }
    public int getLazyTickRate() {
        return this.lazyTickRate;
    }
    public int getInternalProcessingDuration() {
        return this.processingDuration;
    }

    public boolean isWatered() {
        CultivationTankBlockEntity controller = getControllerBE();
        if (controller == null) return false;
        return controller.isWatered;
    }

    public void setWatered(boolean watered) {
        CultivationTankBlockEntity controller = getControllerBE();
        if (controller == null) return;

        controller.wateredTickCounter = 2;
        if (controller.isWatered != watered) {
            controller.isWatered = watered;
            controller.setChanged();
            controller.notifyUpdate();
        }
    }
}
