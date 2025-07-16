package euphy.upo.create_cultivation.content.cultivation_tank;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.IBE;
import euphy.upo.create_cultivation.registry.CCBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
    public static final BooleanProperty WORKING = BooleanProperty.create("working");
    public static final IntegerProperty GROWTH_STAGE = IntegerProperty.create("growth_stage", 0, 3);

    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");

    private static final VoxelShape TANK_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public CultivationTankBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(PLANTED, false)
                .setValue(WORKING, false)
                .setValue(GROWTH_STAGE, 0)
                .setValue(TOP, true)
                .setValue(BOTTOM, true));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return TANK_SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide && state.getValue(PLANTED)) {
            withBlockEntityDo(level, pos, tankBE -> {
                tankBE.getCurrentRecipe().ifPresent(recipeHolder -> {
                    ItemStack seedStack = recipeHolder.value().getIngredients().get(0).getItems()[0].copy();
                    seedStack.setCount(1);


                    BlockPos dropPos = pos;
                    Direction clickedFace = hitResult.getDirection();

                    if (clickedFace.getAxis() != Direction.Axis.Y) {
                        dropPos = pos.relative(clickedFace);
                    }
                    else if (clickedFace == Direction.UP) {
                        dropPos = pos.relative(player.getDirection().getOpposite());
                    }

                    popResource(level, dropPos, seedStack);
                });

                tankBE.clearTank();
            });
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(PLANTED) || !state.getValue(WORKING) || level.isClientSide || stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CultivationTankBlockEntity tankBE) {
            if (tankBE.plant(stack)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        if (level.isClientSide) {
            return;
        }


        BlockPos relativeFrom = fromPos.subtract(pos);
        if (Math.abs(relativeFrom.getX()) + Math.abs(relativeFrom.getZ()) == 0 && Math.abs(relativeFrom.getY()) == 1) {
            BlockState fromState = level.getBlockState(fromPos);
            if (fromState.getBlock() == this || block == this) {
                withBlockEntityDo(level, pos, CultivationTankBlockEntity::scheduleConnectivityUpdate);
            }
        }


        if (pos.equals(fromPos.above())) {
            return;
        }
        if (fromPos.equals(pos.below())) {
            withBlockEntityDo(level, pos, CultivationTankBlockEntity::updateWorkingState);
        }
    }


    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() == this || isMoving) {
            return;
        }
        withBlockEntityDo(level, pos, CultivationTankBlockEntity::updateWorkingState);
        level.neighborChanged(pos.below(), this, pos);
        level.neighborChanged(pos.above(), this, pos);
    }


    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof CultivationTankBlockEntity tankBE))
                return;
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(tankBE);
        }
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PLANTED, WORKING, GROWTH_STAGE, TOP, BOTTOM);
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