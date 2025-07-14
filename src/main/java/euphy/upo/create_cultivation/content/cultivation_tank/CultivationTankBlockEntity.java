package euphy.upo.create_cultivation.content.cultivation_tank;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import euphy.upo.create_cultivation.content.cultivation_base.CultivationBaseBlock;
import euphy.upo.create_cultivation.content.recipes.CultivatingRecipe;
import euphy.upo.create_cultivation.content.recipes.StackingCultivatingRecipe;
import euphy.upo.create_cultivation.registry.CCRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class CultivationTankBlockEntity extends SmartBlockEntity {


    private Optional<RecipeHolder<?>> currentRecipe = Optional.empty();
    private RecipeMode recipeMode = RecipeMode.NONE;

    private int progress = 0;
    private int processingDuration = 10;

    private static final int TOTAL_GROWTH_STAGES = 4;

    private int currentHeight = 0;
    private int maxHeight = 3;

    public enum RecipeMode { NONE, STAGE_BASED, STACK_BASED }

    public CultivationTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (level != null && !level.isClientSide) {
            if (currentRecipe.isEmpty() && getBlockState().getValue(CultivationTankBlock.PLANTED)) {
                level.setBlock(worldPosition, getBlockState().setValue(CultivationTankBlock.PLANTED, false), 3);
            }
        }
    }


    public boolean plant(ItemStack seedStack) {
        if (level == null) return false;

        RecipeInput inventoryWrapper = new RecipeInput() {
            @Override
            public ItemStack getItem(int slot) {
                return slot == 0 ? seedStack : ItemStack.EMPTY;
            }
            @Override
            public int size() {
                return 1;
            }
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
        this.currentRecipe = Optional.of(recipe);
        this.recipeMode = RecipeMode.STAGE_BASED;
        this.processingDuration = recipe.value().getProcessingDuration() > 0 ? recipe.value().getProcessingDuration() / 10 : 10;
        this.progress = 0;
        level.setBlock(worldPosition, getBlockState().setValue(CultivationTankBlock.PLANTED, true).setValue(CultivationTankBlock.GROWTH_STAGE, 0), 3);
        setChanged();
        notifyUpdate();
    }

    private void activateStackRecipe(RecipeHolder<StackingCultivatingRecipe> recipe) {
        this.currentRecipe = Optional.of(recipe);
        this.recipeMode = RecipeMode.STACK_BASED;
        this.processingDuration = recipe.value().getProcessingDuration() > 0 ? recipe.value().getProcessingDuration() / 10 : 20;
        this.maxHeight = recipe.value().getMaxHeight();
        this.progress = 0;
        this.currentHeight = 1;
        level.setBlock(worldPosition, getBlockState().setValue(CultivationTankBlock.PLANTED, true), 3);
        setChanged();
        notifyUpdate();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (level == null || level.isClientSide) return;

        updateWorkingState();

        if (getBlockState().getValue(CultivationTankBlock.WORKING) && getBlockState().getValue(CultivationTankBlock.PLANTED)) {
            switch (recipeMode) {
                case STAGE_BASED -> handleStageGrowth();
                case STACK_BASED -> handleStackingGrowth();
            }
        }
    }

    private void handleStageGrowth() {
        if (isMature()) return;
        progress++;
        setChanged();
        updateVisualGrowthStage();
    }

    private void handleStackingGrowth() {
        if (isMature()) return;
        progress++;
        if (progress >= processingDuration) {
            progress = 0;
            currentHeight++;
            setChanged();
            notifyUpdate();
        }
    }

    public void onHarvest() {
        this.currentRecipe = Optional.empty();
        this.recipeMode = RecipeMode.NONE;
        this.progress = 0;
        this.currentHeight = 0;
        BlockState newState = getBlockState().setValue(CultivationTankBlock.PLANTED, false)
                .setValue(CultivationTankBlock.GROWTH_STAGE, 0);
        level.setBlock(worldPosition, newState, 3);
        setChanged();
        notifyUpdate();
    }



    public RecipeMode getRecipeMode() {
        return recipeMode;
    }

    public Optional<RecipeHolder<?>> getCurrentRecipe() {
        return currentRecipe;
    }

    public int getCurrentHeight() {
        return currentHeight;
    }

    public float getStageGrowthRatio() {
        if (processingDuration == 0) return 0;
        return (float) progress / processingDuration;
    }

    public boolean isMature() {
        if (recipeMode == RecipeMode.STAGE_BASED) {
            return getCalculatedGrowthStage() >= (TOTAL_GROWTH_STAGES - 1);
        }
        if (recipeMode == RecipeMode.STACK_BASED) {
            return currentHeight >= maxHeight;
        }
        return false;
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
        super.write(compound, registries, clientPacket);
        compound.putInt("RecipeMode", this.recipeMode.ordinal());
        currentRecipe.ifPresent(recipeHolder -> compound.putString("RecipeId", recipeHolder.id().toString()));
        compound.putInt("Progress", progress);
        compound.putInt("CurrentHeight", currentHeight);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        recipeMode = RecipeMode.values()[compound.getInt("RecipeMode")];
        currentRecipe = Optional.empty();
        if (compound.contains("RecipeId")) {
            ResourceLocation recipeId = ResourceLocation.parse(compound.getString("RecipeId"));
            if (level != null) {
                level.getRecipeManager().byKey(recipeId).ifPresent(recipe -> {
                    this.currentRecipe = Optional.of(recipe);
                    if (recipe.value() instanceof CultivatingRecipe cr) {
                        this.processingDuration = cr.getProcessingDuration() > 0 ? cr.getProcessingDuration() / 10 : 10;
                    } else if (recipe.value() instanceof StackingCultivatingRecipe sr) {
                        this.processingDuration = sr.getProcessingDuration() > 0 ? sr.getProcessingDuration() / 10 : 20;
                        this.maxHeight = sr.getMaxHeight();
                    }
                });
            }
        }
        progress = compound.getInt("Progress");
        currentHeight = compound.getInt("CurrentHeight");
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
}
