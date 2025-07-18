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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class CultivationTankBlockEntity extends SmartBlockEntity implements IMultiBlockEntityContainer {

    private Optional<Recipe<?>> currentRecipe = Optional.empty();
    private ResourceLocation recipeToLoad;
    private RecipeMode recipeMode = RecipeMode.NONE;
    private int processingDuration = 10;
    public boolean isWatered;
    private int wateredTickCounter;
    private int progress = 0;
    private int harvestCooldown = 0;
    private int currentHeight = 0;
    private int maxHeight = 3;
    private float growthAccumulator = 0.0f;
    private BlockPos controller;
    private BlockPos lastKnownPos;
    private boolean updateConnectivity;
    private int height = 1;
    private int width = 1;
    private boolean initialized = false;
    private int lazyTickCounter;
    private static final int MATURE_DISPLAY_TICKS = 6;
    private static final int TOTAL_GROWTH_STAGES = 4;

    public enum RecipeMode { NONE, STAGE_BASED, STACK_BASED }

    public CultivationTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        updateConnectivity = true;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    private void activateRecipe(Recipe<?> recipe, RecipeMode mode) {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null || level == null) return;

        controllerBE.currentRecipe = Optional.of(recipe);
        controllerBE.recipeMode = mode;
        controllerBE.progress = 0;
        controllerBE.growthAccumulator = 0f;
        controllerBE.harvestCooldown = 0;
        controllerBE.isWatered = false;

        if (recipe instanceof CultivatingRecipe cr) {
            int recipeDuration = cr.getProcessingDuration();
            controllerBE.processingDuration = recipeDuration > 0 ? recipeDuration / 10 : 10;
        } else if (recipe instanceof StackingCultivatingRecipe sr) {
            int recipeDuration = sr.getProcessingDuration();
            controllerBE.processingDuration = recipeDuration > 0 ? recipeDuration / 10 : 20;
            controllerBE.maxHeight = sr.getMaxHeight();
            controllerBE.currentHeight = 1;
        }

        for (int i = 0; i < controllerBE.getHeight(); i++) {
            BlockPos posInStack = controllerBE.getBlockPos().above(i);
            BlockState stateInStack = level.getBlockState(posInStack);
            if (stateInStack.is(controllerBE.getBlockState().getBlock())) {
                level.setBlock(posInStack, stateInStack.setValue(CultivationTankBlock.PLANTED, true).setValue(CultivationTankBlock.GROWTH_STAGE, 0), 3);
            }
        }

        controllerBE.setChanged();
        controllerBE.sendData();
    }

    @Override
    public void tick() {
        super.tick();
        if (lazyTickCounter-- <= 0) {
            lazyTickCounter = 10;
            lazyTick();
        }
        if (!initialized && hasLevel()) {
            initialize();
            initialized = true;
        }
        if (recipeToLoad != null && level != null && isController()) {
            level.getRecipeManager().byKey(recipeToLoad).ifPresent(recipe -> {
                this.currentRecipe = Optional.of(recipe);
                if (recipe instanceof CultivatingRecipe cr) {

                    this.processingDuration = cr.getProcessingDuration() > 0 ? cr.getProcessingDuration() / 10 : 10;
                } else if (recipe instanceof StackingCultivatingRecipe sr) {
                    this.processingDuration = sr.getProcessingDuration() > 0 ? sr.getProcessingDuration() / 10 : 20;
                    this.maxHeight = sr.getMaxHeight();
                }
            });
            recipeToLoad = null;
        }
        if (lastKnownPos == null) lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
            onPositionChanged();
            return;
        }
        if (this.updateConnectivity) {
            this.updateConnectivity();
        }
    }


    public boolean plant(ItemStack seedStack) {
        if (level == null || seedStack.isEmpty()) return false;
        if (!isWorking()) return false;
        Container inventoryWrapper = new SimpleContainer(seedStack);
        Optional<CultivatingRecipe> stageRecipe = level.getRecipeManager()
                .getRecipeFor(CCRecipes.CULTIVATING.getType(), inventoryWrapper, level);
        if (stageRecipe.isPresent()) {
            activateRecipe(stageRecipe.get(), RecipeMode.STAGE_BASED);
            return true;
        }
        Optional<StackingCultivatingRecipe> stackRecipe = level.getRecipeManager()
                .getRecipeFor(CCRecipes.STACKING_CULTIVATING.getType(), inventoryWrapper, level);
        if (stackRecipe.isPresent()) {
            activateRecipe(stackRecipe.get(), RecipeMode.STACK_BASED);
            return true;
        }
        return false;
    }

    @Override
    public void lazyTick() {
        if (level == null || level.isClientSide || !isController()) return;
        if (isWatered) {
            if (wateredTickCounter-- <= 0) {
                isWatered = false;
                setChanged();
            }
        }
        if (isPlanted() && isWorking()) {
            if (!isMature()) {
                float speedMultiplier = getSpeedMultiplier();
                if (speedMultiplier > 0) {
                    growthAccumulator += speedMultiplier;
                    int pointsToApply = (int) growthAccumulator;
                    if (pointsToApply > 0) {
                        switch (recipeMode) {
                            case STAGE_BASED -> handleStageGrowth(pointsToApply);
                            case STACK_BASED -> handleStackingGrowth(pointsToApply);
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

    private boolean isWorking() {
        if (level == null) return false;
        BlockState belowState = level.getBlockState(worldPosition.below());
        if (belowState.getBlock() instanceof CultivationBaseBlock) {
            return belowState.getValue(CultivationBaseBlock.WORKING);
        }
        BlockEntity beBelow = level.getBlockEntity(worldPosition.below());
        if (beBelow instanceof CultivationTankBlockEntity tank) {
            return tank.isWorking();
        }
        return false;
    }

    private void handleStageGrowth(int points) {
        progress += points;
        updateVisualGrowthStage();
    }

    private void handleStackingGrowth(int points) {
        progress += points;
        int limit = Math.min(getHeight(), maxHeight);
        while (progress >= processingDuration && currentHeight < limit) {
            progress -= processingDuration;
            currentHeight++;
        }
        setChanged();
        sendData();
    }

    public float getSpeedMultiplier() {
        if (level == null) return 0;
        BlockEntity beBelow = level.getBlockEntity(worldPosition.below());
        if (beBelow instanceof CultivationTankBlockEntity tankBE) {
            return tankBE.getSpeedMultiplier();
        }
        if (beBelow instanceof KineticBlockEntity kineticBE) {
            float speed = Math.abs(kineticBE.getSpeed());
            float multiplier = 0;
            if (speed < 32) {
                multiplier = speed / 32.0f;
            } else {
                multiplier = Mth.lerp(Mth.clamp((speed - 32) / (256.0f - 32.0f), 0.0f, 1.0f), 1.0f, 2.0f);
            }
            return multiplier / 2.0f;
        }
        return 0;
    }

    public float getStageGrowthRatio() {
        if (processingDuration == 0) return 0f;
        return Mth.clamp((float) progress / processingDuration, 0, 1);
    }

    public boolean isMature() {
        if (currentRecipe.isEmpty()) return false;
        if (recipeMode == RecipeMode.STAGE_BASED) {
            return getCalculatedGrowthStage() >= (TOTAL_GROWTH_STAGES - 1);
        }
        if (recipeMode == RecipeMode.STACK_BASED) {
            int limit = Math.min(getHeight(), maxHeight);
            return currentHeight >= limit && progress >= processingDuration;
        }
        return false;
    }

    private int getCalculatedGrowthStage() {
        return Mth.floor(getStageGrowthRatio() * (TOTAL_GROWTH_STAGES - 1));
    }

    private void updateVisualGrowthStage() {
        if (level == null || !isController()) return;
        for(int i=0; i < getHeight(); i++) {
            BlockPos pos = getBlockPos().above(i);
            BlockState state = level.getBlockState(pos);
            if(state.getBlock() instanceof CultivationTankBlock) {
                int currentStage = state.getValue(CultivationTankBlock.GROWTH_STAGE);
                int calculatedStage = getCalculatedGrowthStage();
                if (currentStage != calculatedStage) {
                    level.setBlock(pos, state.setValue(CultivationTankBlock.GROWTH_STAGE, calculatedStage), 3);
                }
            }
        }
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        initialized = compound.getBoolean("Initialized");
        updateConnectivity = compound.contains("Uninitialized");
        if (compound.contains("LastKnownPos")) lastKnownPos = NbtUtils.readBlockPos(compound.getCompound("LastKnownPos"));
        if (compound.contains("Controller")) controller = NbtUtils.readBlockPos(compound.getCompound("Controller"));
        else controller = null;
        if (isController()) {
            height = compound.getInt("Height");
            width = compound.getInt("Width");
            recipeMode = RecipeMode.values()[compound.getInt("RecipeMode")];
            if (compound.contains("RecipeId")) {
                recipeToLoad = ResourceLocation.parse(compound.getString("RecipeId"));
            } else {
                recipeToLoad = null;
                currentRecipe = Optional.empty();
            }
            progress = compound.getInt("Progress");
            currentHeight = compound.getInt("CurrentHeight");
            maxHeight = compound.getInt("MaxHeight");
            isWatered = compound.getBoolean("isWatered");
            growthAccumulator = compound.getFloat("GrowthAccumulator");
            harvestCooldown = compound.getInt("HarvestCooldown");
        }
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putBoolean("Initialized", initialized);
        if (updateConnectivity) compound.putBoolean("Uninitialized", true);
        if (lastKnownPos != null) compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController() && controller != null) compound.put("Controller", NbtUtils.writeBlockPos(controller));
        if (isController()) {
            compound.putInt("Height", height);
            compound.putInt("Width", width);
            compound.putInt("RecipeMode", recipeMode.ordinal());
            currentRecipe.ifPresent(r -> compound.putString("RecipeId", r.getId().toString()));
            compound.putInt("Progress", progress);
            compound.putInt("CurrentHeight", currentHeight);
            compound.putInt("MaxHeight", maxHeight);
            compound.putBoolean("isWatered", isWatered);
            compound.putFloat("GrowthAccumulator", growthAccumulator);
            compound.putInt("HarvestCooldown", harvestCooldown);
        }
    }

    public void onHarvest(boolean replant) {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null) return;
        controllerBE.progress = 0;
        controllerBE.growthAccumulator = 0f;
        controllerBE.harvestCooldown = 0;
        if (controllerBE.recipeMode == RecipeMode.STACK_BASED) {
            controllerBE.currentHeight = 1;
        }
        if (!replant) {
            controllerBE.clearTank();
        } else {
            updateVisualGrowthStage();
        }
        controllerBE.setChanged();
        controllerBE.sendData();
    }

    public void clearTank() {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null || level == null) return;
        controllerBE.currentRecipe = Optional.empty();
        controllerBE.recipeMode = RecipeMode.NONE;
        controllerBE.progress = 0;
        controllerBE.growthAccumulator = 0f;
        controllerBE.currentHeight = 0;
        controllerBE.harvestCooldown = 0;
        controllerBE.isWatered = false;
        for (int i = 0; i < controllerBE.height; i++) {
            BlockPos posInStack = controllerBE.getBlockPos().above(i);
            BlockState stateInStack = level.getBlockState(posInStack);
            if (stateInStack.getBlock() instanceof CultivationTankBlock) {
                level.setBlock(posInStack, stateInStack.setValue(CultivationTankBlock.PLANTED, false).setValue(CultivationTankBlock.GROWTH_STAGE, 0), 3);
            }
        }
        controllerBE.setChanged();
        controllerBE.sendData();
    }

    @Override
    public void initialize() {
        super.initialize();
        sendData();
        if (level != null && level.isClientSide)
            invalidateRenderBoundingBox();
    }

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    protected void updateConnectivity() {
        updateConnectivity = false;
        if (level == null || level.isClientSide) return;
        if (!isController()) {
            CultivationTankBlockEntity controllerBE = getControllerBE();
            if (controllerBE != null)
                controllerBE.updateConnectivity();
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

    public boolean isReadyForHarvest() { return isMature() && harvestCooldown <= 0; }

    public RecipeMode getRecipeMode() {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        return controllerBE != null ? controllerBE.recipeMode : RecipeMode.NONE;
    }

    public Optional<?> getCurrentRecipe() {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        return controllerBE != null ? controllerBE.currentRecipe : Optional.empty();
    }

    public boolean isPlanted() { return getBlockState().getValue(CultivationTankBlock.PLANTED); }

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

    @Override
    public void setController(BlockPos controller) {
        if (level != null && level.isClientSide && !isVirtual()) return;
        if (controller.equals(this.controller)) return;
        this.controller = controller;
        setChanged();
        sendData();
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @Override
    public CultivationTankBlockEntity getControllerBE() {
        if (isController()) return this;
        if (level == null || controller == null) return null;
        BlockEntity be = level.getBlockEntity(controller);
        if (be instanceof CultivationTankBlockEntity)
            return (CultivationTankBlockEntity) be;
        return null;
    }

    @Override
    public boolean isController() {
        return controller == null || worldPosition.equals(controller);
    }

    @Override
    public BlockPos getLastKnownPos() { return lastKnownPos; }

    @Override
    public void removeController(boolean keepContents) {
        if (level == null || level.isClientSide) return;
        updateConnectivity = true;
        initialized = false;
        controller = null;
        width = 1;
        height = 1;
        if (keepContents) {
            if (currentRecipe.isPresent())
                recipeToLoad = currentRecipe.get().getId();
        } else {
            clearTank();
        }
        BlockState state = getBlockState();
        if (state.getBlock() instanceof CultivationTankBlock) {
            level.setBlock(worldPosition, state.setValue(CultivationTankBlock.TOP, true).setValue(CultivationTankBlock.BOTTOM, true), 3);
        }
        setChanged();
        sendData();
    }

    @Override
    public void preventConnectivityUpdate() { updateConnectivity = false; }

    public void scheduleConnectivityUpdate() { updateConnectivity = true; }

    @Override
    public void notifyMultiUpdated() {
        if (level == null || level.isClientSide) return;
        if (!isController()) return;
        for (int i = 0; i < height; i++) {
            BlockPos currentPos = worldPosition.above(i);
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
    public int getHeight() { return isController() ? height : getControllerBE() != null ? getControllerBE().getHeight() : 1; }
    @Override
    public void setHeight(int height) { this.height = height; }
    @Override
    public int getWidth() { return isController() ? width : getControllerBE() != null ? getControllerBE().getWidth() : 1; }
    @Override
    public void setWidth(int width) { this.width = width; }
    public int getCurrentHeight() {
        CultivationTankBlockEntity controllerBE = getControllerBE();
        return controllerBE != null ? controllerBE.currentHeight : 0;
    }
    public boolean isWatered() {
        CultivationTankBlockEntity controller = getControllerBE();
        if (controller == null) return false;
        return controller.isWatered;
    }

    public int getInternalProcessingDuration() { return this.processingDuration; }
    public int getProgress() {
        return this.progress;
    }
    public int getLazyTickRate() {
        return 10;
    }
    public void setPonderProgress(int progress) {
        this.progress = progress;
    }
}