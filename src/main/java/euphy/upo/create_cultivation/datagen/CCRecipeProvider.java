package euphy.upo.create_cultivation.datagen;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import euphy.upo.create_cultivation.CreateCultivationCraft;
import euphy.upo.create_cultivation.content.recipes.CultivatingRecipe;
import euphy.upo.create_cultivation.content.recipes.CultivatingRecipeBuilder;
import euphy.upo.create_cultivation.content.recipes.CultivatingRecipeParams;
import euphy.upo.create_cultivation.content.recipes.StackingCultivatingRecipeBuilder;
import euphy.upo.create_cultivation.registry.CCBlocks;
import euphy.upo.create_cultivation.registry.CCRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;


public class CCRecipeProvider extends ProcessingRecipeGen<CultivatingRecipeParams, CultivatingRecipe, CultivatingRecipeBuilder> {

    public CCRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateCultivationCraft.MODID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CCRecipes.CULTIVATING;
    }

    @Override
    protected CultivatingRecipeBuilder getBuilder(ResourceLocation id) {
        return new CultivatingRecipeBuilder(id);
    }


    @Override
    public void buildRecipes(RecipeOutput consumer) {

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, CCBlocks.CULTIVATION_BASE.get())
                .pattern("GDG")
                .pattern("ACA")
                .pattern("ABA")
                .define('G', Items.GLOWSTONE_DUST)
                .define('D', ItemTags.DIRT)
                .define('A', AllBlocks.ANDESITE_CASING)
                .define('C', AllBlocks.COGWHEEL.get())
                .define('B', Items.BARREL)
                .unlockedBy("has_andesite_casing", has(AllBlocks.ANDESITE_CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, CCBlocks.CULTIVATION_TANK.get())
                .pattern("GGG")
                .pattern("G G")
                .pattern("A A")
                .define('G', Tags.Items.GLASS_BLOCKS)
                .define('A', AllItems.ANDESITE_ALLOY.get())
                .unlockedBy("has_andesite_alloy", has(AllItems.ANDESITE_ALLOY.get()))
                .save(consumer);


        //小麦
        getBuilder(asResource("wheat_from_seeds"))
                .require(Items.WHEAT_SEEDS)
                .output(Items.WHEAT, 1)
                .output(0.25f, Items.WHEAT, 1)
                .output(Items.WHEAT_SEEDS, 1)
                .output(0.5f, Items.WHEAT_SEEDS, 1)
                .output(0.5f, Items.WHEAT_SEEDS, 1)
                .duration(18000)
                .cropBlock(Blocks.WHEAT)
                .build(consumer);
        //萝卜
        getBuilder(asResource("carrot"))
                .require(Items.CARROT)
                .output(Items.CARROT, 2)
                .output(0.5f, Items.CARROT, 1)
                .duration(18000)
                .cropBlock(Blocks.CARROTS)
                .build(consumer);
        //土豆
        getBuilder(asResource("potato"))
                .require(Items.POTATO)
                .output(Items.POTATO, 2)
                .output(0.5f, Items.POTATO, 1)
                .output(0.05f, Items.POISONOUS_POTATO, 1)
                .duration(18000)
                .cropBlock(Blocks.POTATOES)
                .build(consumer);
        //甜菜
        getBuilder(asResource("beetroot_from_seeds"))
                .require(Items.BEETROOT_SEEDS)
                .output(Items.BEETROOT, 1)
                .output(0.5f, Items.BEETROOT, 1)
                .output(Items.BEETROOT_SEEDS, 2)
                .duration(15000)
                .cropBlock(Blocks.BEETROOTS)
                .build(consumer);
        //西瓜
        getBuilder(asResource("melon_from_seeds"))
                .require(Items.MELON_SEEDS)
                .output(Items.MELON_SLICE, 4)
                .output(0.5f, Items.MELON_SLICE, 4)
                .output(Items.MELON_SEEDS, 1)
                .duration(25000)
                .cropBlock(Blocks.MELON_STEM)
                .build(consumer);
        //南瓜
        getBuilder(asResource("pumpkin_from_seeds"))
                .require(Items.PUMPKIN_SEEDS)
                .output(Items.PUMPKIN, 1)
                .output(Items.PUMPKIN_SEEDS, 1)
                .duration(25000)
                .cropBlock(Blocks.PUMPKIN_STEM)
                .build(consumer);
        //下界疣
        getBuilder(asResource("nether_wart"))
                .require(Items.NETHER_WART)
                .output(Items.NETHER_WART, 2)
                .output(0.25f, Items.NETHER_WART, 2)
                .duration(36000)
                .cropBlock(Blocks.NETHER_WART)
                .build(consumer);
        //甜浆果
        getBuilder(asResource("sweet_berries"))
                .require(Items.SWEET_BERRIES)
                .output(Items.SWEET_BERRIES, 2)
                .output(0.5f, Items.SWEET_BERRIES, 1)
                .duration(18000)
                .cropBlock(Blocks.SWEET_BERRY_BUSH)
                .build(consumer);
        //火把花
        getBuilder(asResource("torchflower_from_seeds"))
                .require(Items.TORCHFLOWER_SEEDS)
                .output(Items.TORCHFLOWER, 1)
                .output(0.25f, Items.TORCHFLOWER_SEEDS, 1)
                .duration(18000)
                .cropBlock(Blocks.TORCHFLOWER_CROP)
                .build(consumer);
        //瓶子草
        getBuilder(asResource("pitcher_plant_from_pod"))
                .require(Items.PITCHER_POD)
                .output(Items.PITCHER_PLANT, 1)
                .duration(18000)
                .cropBlock(Blocks.PITCHER_CROP)
                .height(2)
                .build(consumer);
        //发光浆果
        getBuilder(asResource("glow_berries"))
                .require(Items.GLOW_BERRIES)
                .output(Items.GLOW_BERRIES, 2)
                .output(0.5f, Items.GLOW_BERRIES, 1)
                .duration(18000)
                .cropBlock(Blocks.CAVE_VINES)
                .build(consumer);
        //可可豆
        getBuilder(asResource("cocoa_beans"))
                .require(Items.COCOA_BEANS)
                .output(Items.COCOA_BEANS, 3)
                .output(0.25f, Items.COCOA_BEANS, 1)
                .duration(15000)
                .cropBlock(Blocks.COCOA)
                .build(consumer);
        //花卉
        //蒲公英
        getBuilder(asResource("dandelion"))
                .require(Items.DANDELION)
                .output(Items.DANDELION, 2)
                .output(0.1f, Items.DANDELION, 1)
                .duration(15000)
                .cropBlock(Blocks.DANDELION)
                .build(consumer);
        //虞美人
        getBuilder(asResource("poppy"))
                .require(Items.POPPY)
                .output(Items.POPPY, 2)
                .output(0.1f, Items.POPPY, 1)
                .duration(15000)
                .cropBlock(Blocks.POPPY)
                .build(consumer);
        //红色郁金香
        getBuilder(asResource("red_tulip"))
                .require(Items.RED_TULIP)
                .output(Items.RED_TULIP, 2)
                .output(0.1f, Items.RED_TULIP, 1)
                .duration(15000)
                .cropBlock(Blocks.RED_TULIP)
                .build(consumer);
        //橙色郁金香
        getBuilder(asResource("orange_tulip"))
                .require(Items.ORANGE_TULIP)
                .output(Items.ORANGE_TULIP, 2)
                .output(0.1f, Items.ORANGE_TULIP, 1)
                .duration(15000)
                .cropBlock(Blocks.ORANGE_TULIP)
                .build(consumer);
        //白色郁金香
        getBuilder(asResource("white_tulip"))
                .require(Items.WHITE_TULIP)
                .output(Items.WHITE_TULIP, 2)
                .output(0.1f, Items.WHITE_TULIP, 1)
                .duration(15000)
                .cropBlock(Blocks.WHITE_TULIP)
                .build(consumer);
        //粉色郁金香
        getBuilder(asResource("pink_tulip"))
                .require(Items.PINK_TULIP)
                .output(Items.PINK_TULIP, 2)
                .output(0.1f, Items.PINK_TULIP, 1)
                .duration(15000)
                .cropBlock(Blocks.PINK_TULIP)
                .build(consumer);
        //铃兰
        getBuilder(asResource("lily_of_the_valley"))
                .require(Items.LILY_OF_THE_VALLEY)
                .output(Items.LILY_OF_THE_VALLEY, 2)
                .output(0.1f, Items.LILY_OF_THE_VALLEY, 1)
                .duration(15000)
                .cropBlock(Blocks.LILY_OF_THE_VALLEY)
                .build(consumer);
        //蓝花美耳草
        getBuilder(asResource("azure_bluet"))
                .require(Items.AZURE_BLUET)
                .output(Items.AZURE_BLUET, 2)
                .output(0.1f, Items.AZURE_BLUET, 1)
                .duration(15000)
                .cropBlock(Blocks.AZURE_BLUET)
                .build(consumer);
        //凋零玫瑰
        getBuilder(asResource("wither_rose"))
                .require(Items.WITHER_ROSE)
                .output(Items.WITHER_ROSE, 2)
                .output(0.1f, Items.WITHER_ROSE, 1)
                .duration(15000)
                .cropBlock(Blocks.WITHER_ROSE)
                .build(consumer);
        //滨菊
        getBuilder(asResource("oxeye_daisy"))
                .require(Items.OXEYE_DAISY)
                .output(Items.OXEYE_DAISY, 2)
                .output(0.1f, Items.OXEYE_DAISY, 1)
                .duration(15000)
                .cropBlock(Blocks.OXEYE_DAISY)
                .build(consumer);
        //矢车菊
        getBuilder(asResource("cornflower"))
                .require(Items.CORNFLOWER)
                .output(Items.CORNFLOWER, 2)
                .output(0.1f, Items.CORNFLOWER, 1)
                .duration(15000)
                .cropBlock(Blocks.CORNFLOWER)
                .build(consumer);
        //兰花
        getBuilder(asResource("blue_orchid"))
                .require(Items.BLUE_ORCHID)
                .output(Items.BLUE_ORCHID, 2)
                .output(0.1f, Items.BLUE_ORCHID, 1)
                .duration(15000)
                .cropBlock(Blocks.BLUE_ORCHID)
                .build(consumer);
        //绒球葱
        getBuilder(asResource("allium"))
                .require(Items.ALLIUM)
                .output(Items.ALLIUM, 2)
                .output(0.1f, Items.ALLIUM, 1)
                .duration(15000)
                .cropBlock(Blocks.ALLIUM)
                .build(consumer);
        //向日葵
        getBuilder(asResource("sunflower"))
                .require(Items.SUNFLOWER)
                .output(Items.SUNFLOWER, 2)
                .output(0.1f, Items.SUNFLOWER, 1)
                .duration(15000)
                .cropBlock(Blocks.SUNFLOWER)
                .height(2)
                .build(consumer);
        //丁香
        getBuilder(asResource("lilac"))
                .require(Items.LILAC)
                .output(Items.LILAC, 2)
                .output(0.1f, Items.LILAC, 1)
                .duration(15000)
                .cropBlock(Blocks.LILAC)
                .height(2)
                .build(consumer);
        //玫瑰丛
        getBuilder(asResource("rose_bush"))
                .require(Items.ROSE_BUSH)
                .output(Items.ROSE_BUSH, 2)
                .output(0.1f, Items.ROSE_BUSH, 1)
                .duration(15000)
                .cropBlock(Blocks.ROSE_BUSH)
                .height(2)
                .build(consumer);
        //牡丹
        getBuilder(asResource("peony"))
                .require(Items.PEONY)
                .output(Items.PEONY, 2)
                .output(0.1f, Items.PEONY, 1)
                .duration(15000)
                .cropBlock(Blocks.PEONY)
                .height(2)
                .build(consumer);
        //孢子花
        getBuilder(asResource("spore_blossom"))
                .require(Items.SPORE_BLOSSOM)
                .output(Items.SPORE_BLOSSOM, 2)
                .output(0.1f, Items.SPORE_BLOSSOM, 1)
                .duration(15000)
                .cropBlock(Blocks.SPORE_BLOSSOM)
                .build(consumer);
        //红色蘑菇
        getBuilder(asResource("red_mushroom"))
                .require(Items.RED_MUSHROOM)
                .output(Items.RED_MUSHROOM, 2)
                .output(0.25f, Items.RED_MUSHROOM, 1)
                .duration(20000)
                .cropBlock(Blocks.RED_MUSHROOM)
                .build(consumer);
        //棕色蘑菇
        getBuilder(asResource("brown_mushroom"))
                .require(Items.BROWN_MUSHROOM)
                .output(Items.BROWN_MUSHROOM, 2)
                .output(0.25f, Items.BROWN_MUSHROOM, 1)
                .duration(20000)
                .cropBlock(Blocks.BROWN_MUSHROOM)
                .build(consumer);
        //绯红蘑菇
        getBuilder(asResource("crimson_fungus"))
                .require(Items.CRIMSON_FUNGUS)
                .output(Items.CRIMSON_FUNGUS, 2)
                .output(0.25f, Items.CRIMSON_FUNGUS, 1)
                .duration(25000)
                .cropBlock(Blocks.CRIMSON_FUNGUS)
                .build(consumer);
        //诡异蘑菇
        getBuilder(asResource("warped_mushroom"))
                .require(Items.WARPED_FUNGUS)
                .output(Items.WARPED_FUNGUS, 2)
                .output(0.25f, Items.WARPED_FUNGUS, 1)
                .duration(25000)
                .cropBlock(Blocks.WARPED_FUNGUS)
                .build(consumer);
        //大型垂滴叶
        getBuilder(asResource("big_dripleaf"))
                .require(Items.BIG_DRIPLEAF)
                .output(Items.BIG_DRIPLEAF, 2)
                .output(0.25f, Items.BIG_DRIPLEAF, 1)
                .duration(22000)
                .cropBlock(Blocks.BIG_DRIPLEAF)
                .build(consumer);
        //小型垂滴叶
        getBuilder(asResource("small_dripleaf"))
                .require(Items.SMALL_DRIPLEAF)
                .output(Items.SMALL_DRIPLEAF, 2)
                .output(0.25f, Items.SMALL_DRIPLEAF, 1)
                .duration(22000)
                .cropBlock(Blocks.SMALL_DRIPLEAF)
                .height(2)
                .build(consumer);
        //粉红花簇
        getBuilder(asResource("pink_petals"))
                .require(Items.PINK_PETALS)
                .output(Items.PINK_PETALS, 2)
                .output(0.25f, Items.PINK_PETALS, 1)
                .duration(10000)
                .cropBlock(Blocks.PINK_PETALS)
                .build(consumer);
        //睡莲
        getBuilder(asResource("lily_pad"))
                .require(Items.LILY_PAD)
                .output(Items.LILY_PAD, 2)
                .output(0.25f, Items.LILY_PAD, 1)
                .duration(15000)
                .cropBlock(Blocks.LILY_PAD)
                .build(consumer);
        //藤蔓
        getBuilder(asResource("vine"))
                .require(Items.VINE)
                .output(Items.VINE, 2)
                .output(0.25f, Items.VINE, 1)
                .duration(15000)
                .cropBlock(Blocks.VINE)
                .build(consumer);
        //矮草丛
        getBuilder(asResource("short_grass"))
                .require(Items.SHORT_GRASS)
                .output(Items.SHORT_GRASS, 2)
                .output(0.25f, Items.SHORT_GRASS, 1)
                .duration(10000)
                .cropBlock(Blocks.SHORT_GRASS)
                .build(consumer);
        //蕨
        getBuilder(asResource("fern"))
                .require(Items.FERN)
                .output(Items.FERN, 2)
                .output(0.25f, Items.FERN, 1)
                .duration(10000)
                .cropBlock(Blocks.FERN)
                .build(consumer);
        //海泡菜
        getBuilder(asResource("sea_pickle"))
                .require(Items.SEA_PICKLE)
                .output(Items.SEA_PICKLE, 2)
                .output(0.25f, Items.SEA_PICKLE, 1)
                .duration(12000)
                .cropBlock(Blocks.SEA_PICKLE)
                .build(consumer);
        //管珊瑚扇
        getBuilder(asResource("tube_coral_fan"))
                .require(Items.TUBE_CORAL_FAN)
                .output(Items.TUBE_CORAL_FAN, 2)
                .output(0.25f, Items.TUBE_CORAL_FAN, 1)
                .duration(30000)
                .cropBlock(Blocks.TUBE_CORAL_FAN)
                .build(consumer);
        //脑纹珊瑚扇
        getBuilder(asResource("brain_coral_fan"))
                .require(Items.BRAIN_CORAL_FAN)
                .output(Items.BRAIN_CORAL_FAN, 2)
                .output(0.25f, Items.BRAIN_CORAL_FAN, 1)
                .duration(30000)
                .cropBlock(Blocks.BRAIN_CORAL_FAN)
                .build(consumer);
        //气泡珊瑚扇
        getBuilder(asResource("bubble_coral_fan"))
                .require(Items.BUBBLE_CORAL_FAN)
                .output(Items.BUBBLE_CORAL_FAN, 2)
                .output(0.25f, Items.BUBBLE_CORAL_FAN, 1)
                .duration(30000)
                .cropBlock(Blocks.BUBBLE_CORAL_FAN)
                .build(consumer);
        //火珊瑚扇
        getBuilder(asResource("fire_coral_fan"))
                .require(Items.FIRE_CORAL_FAN)
                .output(Items.FIRE_CORAL_FAN, 2)
                .output(0.25f, Items.FIRE_CORAL_FAN, 1)
                .duration(30000)
                .cropBlock(Blocks.FIRE_CORAL_FAN)
                .build(consumer);
        //鹿角珊瑚扇
        getBuilder(asResource("horn_coral_fan"))
                .require(Items.HORN_CORAL_FAN)
                .output(Items.HORN_CORAL_FAN, 2)
                .output(0.25f, Items.HORN_CORAL_FAN, 1)
                .duration(30000)
                .cropBlock(Blocks.HORN_CORAL_FAN)
                .build(consumer);
        //发光地衣
        getBuilder(asResource("glow_lichen"))
                .require(Items.GLOW_LICHEN)
                .output(Items.GLOW_LICHEN, 2)
                .output(0.25f, Items.GLOW_LICHEN, 1)
                .duration(30000)
                .cropBlock(Blocks.GLOW_LICHEN)
                .build(consumer);
        //海草
        getBuilder(asResource("seagrass"))
                .require(Items.SEAGRASS)
                .output(Items.SEAGRASS, 2)
                .output(0.25f, Items.SEAGRASS, 1)
                .duration(10000)
                .cropBlock(Blocks.SEAGRASS)
                .build(consumer);
        //覆地苔藓
        getBuilder(asResource("moss_carpet"))
                .require(Items.MOSS_CARPET)
                .output(Items.MOSS_CARPET, 2)
                .output(0.25f, Items.MOSS_CARPET, 1)
                .duration(10000)
                .cropBlock(Blocks.MOSS_CARPET)
                .build(consumer);



        //甘蔗
        createStacking("sugar_cane", b -> b
                .require(Items.SUGAR_CANE)
                .result(new ProcessingOutput(new ItemStack(Items.SUGAR_CANE, 1), 1.0f))
                .duration(18000)
                .maxHeight(4)
                .blockToRender(Blocks.SUGAR_CANE)
        ).build(consumer);
        //仙人掌
        createStacking("cactus", b -> b
                .require(Items.CACTUS)
                .result(new ProcessingOutput(new ItemStack(Items.CACTUS, 1), 1.0f))
                .duration(18000)
                .maxHeight(4)
                .blockToRender(Blocks.CACTUS)
        ).build(consumer);
        //海带
        createStacking("kelp", b -> b
                .require(Items.KELP)
                .result(new ProcessingOutput(new ItemStack(Items.KELP, 1), 1.0f))
                .duration(10000)
                .maxHeight(8)
                .blockToRender(Blocks.KELP_PLANT)
        ).build(consumer);
        //竹子
        createStacking("bamboo", b -> b
                .require(Items.BAMBOO)
                .result(new ProcessingOutput(new ItemStack(Items.BAMBOO, 1), 1.0f))
                .duration(3600)
                .maxHeight(12)
                .blockToRender(Blocks.BAMBOO)
        ).build(consumer);
        //紫颂植株
        createStacking("chorus_plant", b -> b
                .require(Items.CHORUS_FRUIT)
                .result(new ProcessingOutput(new ItemStack(Items.CHORUS_FRUIT, 1), 1.0f))
                .duration(36000)
                .maxHeight(12)
                .blockToRender(Blocks.CHORUS_PLANT)
        ).build(consumer);
    }





    private CultivatingRecipeBuilder createCultivating(String name, UnaryOperator<CultivatingRecipeBuilder> builder) {
        return builder.apply(new CultivatingRecipeBuilder(asResource(name)));
    }

    private StackingCultivatingRecipeBuilder createStacking(String name, UnaryOperator<StackingCultivatingRecipeBuilder> builder) {
        return builder.apply(new StackingCultivatingRecipeBuilder(asResource(name)));
    }


}
