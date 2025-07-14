package euphy.upo.create_cultivation.content.cultivation_tank;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import euphy.upo.create_cultivation.registry.CCBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;



public class CultivationTankBlock extends Block implements IBE<CultivationTankBlockEntity> {

    public static final BooleanProperty PLANTED = BooleanProperty.create("planted");
    public static final BooleanProperty WORKING = BooleanProperty.create("working");
    public static final IntegerProperty GROWTH_STAGE = IntegerProperty.create("growth_stage", 0, 3);

    public CultivationTankBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(PLANTED, false)
                .setValue(WORKING, false)
                .setValue(GROWTH_STAGE, 0));
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
        if (fromPos.equals(pos.below())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CultivationTankBlockEntity tankBE) {
                tankBE.updateWorkingState();
            }
        }
    }


    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CultivationTankBlockEntity tankBE) {
            tankBE.updateWorkingState();
        }
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PLANTED, WORKING, GROWTH_STAGE);
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
