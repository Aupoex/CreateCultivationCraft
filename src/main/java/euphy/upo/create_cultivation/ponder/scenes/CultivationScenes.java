package euphy.upo.create_cultivation.ponder.scenes;

import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlockEntity;
import euphy.upo.create_cultivation.registry.CCBlocks;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.item.Items.WHEAT;
import static net.minecraft.world.item.Items.WHEAT_SEEDS;

public class CultivationScenes {
    public static void cultivating(SceneBuilder builder, SceneBuildingUtil util){
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("cultivation", "栽培");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();
        scene.idle(15);

        ElementLink<WorldSectionElement> init_tank = scene.world().showIndependentSection(util.select().position(1, 2, 1), Direction.DOWN);

        scene.world().showSection(util.select().fromTo(1, 1, 1, 2, 1, 1), Direction.DOWN);
        scene.world().moveSection(init_tank, util.vector().of(0, 0, 0), 4);
        scene.idle(15);
        scene.world().setKineticSpeed(util.select().fromTo(2, 1, 1, 2, 1, 1), 16);
        scene.world().setKineticSpeed(util.select().fromTo(1, 1, 1, 1, 1, 1), -16);
        scene.overlay().showText(100)
                .text("栽培基座需要配合栽培罐使用。把栽培基座放到栽培罐下方，并通入应力，它们就会开始工作")
                .pointAt(Vec3.atCenterOf(util.grid().at(1, 1, 1)))
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(110);

        scene.overlay().showControls(util.vector().centerOf(1, 2, 1), Pointing.DOWN, 25)
                .rightClick()
                .withItem(WHEAT_SEEDS.getDefaultInstance());

        BlockPos tankPos = util.grid().at(1, 2, 1);
        scene.world().modifyBlockEntity(tankPos, CultivationTankBlockEntity.class, be -> {
            be.plant(new ItemStack(Items.WHEAT_SEEDS));
        });

        scene.idle(45);

        scene.overlay().showText(55)
                .text("手持可种植物品右键栽培罐，将其种植在内,空手右键可以再取出种子。。")
                .pointAt(Vec3.atCenterOf(util.grid().at(1, 2, 1)))
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(65);

        scene.overlay().showText(90)
                .text("栽培机器会自动照顾并收获作物，如果收获物有可种植物品，栽培机器会自动补种。")
                .pointAt(Vec3.atCenterOf(util.grid().at(1, 2, 1)))
                .placeNearTarget();

        scene.world().modifyBlockEntity(tankPos, CultivationTankBlockEntity.class, be -> be.setPonderProgress(1));scene.idle(10);
        scene.world().modifyBlockEntity(tankPos, CultivationTankBlockEntity.class, be -> be.setPonderProgress(2));scene.idle(10);
        scene.world().modifyBlockEntity(tankPos, CultivationTankBlockEntity.class, be -> be.setPonderProgress(3));scene.idle(10);
        scene.world().modifyBlockEntity(tankPos, CultivationTankBlockEntity.class, be -> be.setPonderProgress(4));scene.idle(10);
        scene.world().modifyBlockEntity(tankPos, CultivationTankBlockEntity.class, be -> be.setPonderProgress(5));scene.idle(10);
        scene.world().modifyBlockEntity(tankPos, CultivationTankBlockEntity.class, be -> be.setPonderProgress(6));scene.idle(10);
        scene.world().modifyBlockEntity(tankPos, CultivationTankBlockEntity.class, be -> be.setPonderProgress(7));scene.idle(10);
        scene.world().modifyBlockEntity(tankPos, CultivationTankBlockEntity.class, be -> be.setPonderProgress(8));scene.idle(10);
        scene.world().modifyBlockEntity(tankPos, CultivationTankBlockEntity.class, be -> be.setPonderProgress(9));scene.idle(40);
        scene.world().modifyBlockEntity(tankPos, CultivationTankBlockEntity.class, be -> be.setPonderProgress(0));scene.idle(1);

        Selection spoutStack = util.select().fromTo(2, 3, 1, 2, 3, 2);
        Selection spoutIt = util.select().fromTo(2, 3, 1, 2, 3, 1);
        ElementLink<WorldSectionElement> spoutIn = scene.world().showIndependentSection(spoutStack, Direction.DOWN);
        scene.world().moveSection(spoutIn, util.vector().of(-1, 0, 0), 20);scene.idle(25);
        scene.world().modifyBlockEntityNBT(spoutIt, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));scene.idle(25);
        scene.world().modifyBlockEntityNBT(spoutIt, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));scene.idle(25);
        scene.overlay().showText(40)
                .text("注液器可以浇灌它，收成会更多。")
                .pointAt(Vec3.atCenterOf(util.grid().at(2, 3, 1)))
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(45);
        scene.world().hideIndependentSection(spoutIn, Direction.UP);

        scene.world().showSection(util.select().fromTo(0, 1, 1, 0, 1, 1), Direction.DOWN);
        scene.idle(10);
        BlockPos funnel = util.grid().at(0, 1, 1);
        Vec3 spawnPoint = util.vector().blockSurface(funnel, Direction.DOWN)
                .add(0, -0.2, 0);
        scene.overlay().showText(40)
                .text("收获物会存储在基座中，可以用漏斗或者溜槽提取。")
                .pointAt(Vec3.atCenterOf(util.grid().at(0, 1, 1)))
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(25);
        scene.world().flapFunnel(funnel, true);
        scene.world().createEntity((Level world) -> {
            ItemEntity entity = new ItemEntity(world, spawnPoint.x, spawnPoint.y, spawnPoint.z, new ItemStack(WHEAT));
            entity.isNoGravity();
            return entity;
        });
        scene.idle(20);
        scene.world().flapFunnel(funnel, true);
        scene.world().createEntity((Level world) -> {
            ItemEntity entity = new ItemEntity(world, spawnPoint.x, spawnPoint.y, spawnPoint.z, new ItemStack(WHEAT_SEEDS));
            entity.isNoGravity();
            return entity;
        });
        scene.idle(25);

        scene.idle(50);

        Selection actorPrototype = util.select().position(1, 2, 0);
        ElementLink<WorldSectionElement> actor1 = scene.world().showIndependentSection(actorPrototype, Direction.DOWN);
        scene.world().moveSection(actor1, util.vector().of(0, 1, 1), 0);
        scene.idle(6);
        ElementLink<WorldSectionElement> actor2 = scene.world().showIndependentSection(actorPrototype, Direction.DOWN);
        scene.world().moveSection(actor2, util.vector().of(0, 2, 1), 0);
        scene.idle(6);
        ElementLink<WorldSectionElement> actor3 = scene.world().showIndependentSection(actorPrototype, Direction.DOWN);
        scene.world().moveSection(actor3, util.vector().of(0, 3, 1), 0);
        scene.idle(6);

        BlockPos transformationCenter = new BlockPos(1, 3, 1);
        scene.effects().indicateSuccess(transformationCenter.below());
        scene.effects().indicateSuccess(transformationCenter);
        scene.effects().indicateSuccess(transformationCenter.above());
        scene.idle(3);
        scene.world().moveSection(actor1, util.vector().of(0, -100, 0), 0);
        scene.world().moveSection(actor2, util.vector().of(0, -100, 0), 0);
        scene.world().moveSection(actor3, util.vector().of(0, -100, 0), 0);
        scene.world().moveSection(init_tank, util.vector().of(0, -100, 0), 0);

        Selection finalStateSelection = util.select().fromTo(2, 1, 0, 2, 4, 0);
        ElementLink<WorldSectionElement> finalStateActor = scene.world().showIndependentSectionImmediately(finalStateSelection);

        scene.world().moveSection(finalStateActor, util.vector().of(-1, 1, 1), 0);
        scene.idle(20);

        scene.overlay().showText(90)
                .text("你可以继续往上堆叠栽培罐来增加高度，有些植物需要足够的高度才能生长收获。")
                .pointAt(Vec3.atCenterOf(util.grid().at(1, 2, 1)))
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(100);


        scene.overlay().showText(800)
                .text("你也可以通过数据包自定义")
                .independent();

        scene.markAsFinished();
    }
}
