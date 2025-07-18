package euphy.upo.create_cultivation.content.cultivation_tank;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import euphy.upo.create_cultivation.registry.CCBlocks;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CultivationTankCTBehaviour extends ConnectedTextureBehaviour {

    public CultivationTankCTBehaviour() {}

    @Override
    @Nullable
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @NotNull TextureAtlasSprite sprite) {
        return CCBlocks.CULTIVATION_TANK_SHIFT;
    }

    @Override
    @Nullable
    public CTType getDataType(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction face) {
        return AllCTTypes.VERTICAL;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        if (face.getAxis().isVertical()) {
            return false;
        }
        if (!(other.getBlock() instanceof CultivationTankBlock)) {
            return false;
        }
        if (pos.getX() != otherPos.getX() || pos.getZ() != otherPos.getZ()) {
            return false;
        }
        return true;
    }
}
