package euphy.upo.create_cultivation.content.cultivation_tank;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.block.IBE;
import euphy.upo.create_cultivation.registry.CCBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class CultivationTankBlock extends Block implements IBE<CultivationTankBlockEntity> {

    public static final BooleanProperty PLANTED = BooleanProperty.create("planted");
    //public static final BooleanProperty WORKING = BooleanProperty.create("working");
    public static final IntegerProperty GROWTH_STAGE = IntegerProperty.create("growth_stage", 0, 3);

    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");

    private static final VoxelShape TANK_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public CultivationTankBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(PLANTED, false)
                //.setValue(WORKING, false)
                .setValue(GROWTH_STAGE, 0)
                .setValue(TOP, true)
                .setValue(BOTTOM, true));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        ItemStack heldItem = player.getItemInHand(hand);

        return onBlockEntityUse(level, pos, be -> {

            if (heldItem.isEmpty()) {
                if (be.isPlanted()) {
                    if (!level.isClientSide) {
                        be.getCurrentRecipe().ifPresent(recipe -> {
                            if (recipe instanceof ProcessingRecipe<?> pr && !pr.getIngredients().isEmpty()) {
                                ItemStack seedStack = pr.getIngredients().get(0).getItems()[0].copy();
                                if (!player.getInventory().add(seedStack)) {
                                    player.drop(seedStack, false);
                                }
                            }
                        });
                        be.clearTank();
                    }
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.PASS;
                }
            }



            if (be.isPlanted() || level.isClientSide) {
                return InteractionResult.PASS;
            }


            if (be.plant(heldItem)) {
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getBlock() == oldState.getBlock() || isMoving)
            return;

        world.neighborChanged(pos.below(), this, pos);
        world.neighborChanged(pos.above(), this, pos);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof CultivationTankBlockEntity tankBE)) {
                return;
            }
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(tankBE);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (level.isClientSide) return;

        BlockPos relativeFrom = fromPos.subtract(pos);
        if (relativeFrom.getX() == 0 && relativeFrom.getZ() == 0 && Math.abs(relativeFrom.getY()) == 1) {
            if (level.getBlockState(fromPos).getBlock() == this || block == this) {
                withBlockEntityDo(level, pos, CultivationTankBlockEntity::scheduleConnectivityUpdate);
            }
        }
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return TANK_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PLANTED, GROWTH_STAGE, TOP, BOTTOM);
    }

    @Override
    public Class<CultivationTankBlockEntity> getBlockEntityClass() {
        return CultivationTankBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CultivationTankBlockEntity> getBlockEntityType() {
        return CCBlockEntities.CULTIVATION_TANK.get();
    }
}