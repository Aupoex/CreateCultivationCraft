package euphy.upo.create_cultivation.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import euphy.upo.create_cultivation.registry.CCRecipes;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CCCompatRecipeGenerator implements DataProvider {
    private final PackOutput packOutput;

    public CCCompatRecipeGenerator(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Path outputFolder = this.packOutput.getOutputFolder();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        generateFarmersDelightRecipes((id, json) -> {
            Path recipePath = outputFolder.resolve("data/" + id.getNamespace() + "/recipe/compat/" + id.getPath() + ".json");
            futures.add(DataProvider.saveStable(cache, json, recipePath));
        });

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void generateFarmersDelightRecipes(RecipeAcceptor acceptor) {
        //农夫乐事：卷心菜
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "farmersdelight/cabbage"),
                createCultivatingRecipe(
                        "farmersdelight",
                        "farmersdelight:cabbage_seeds",
                        new String[]{"farmersdelight:cabbage,1", "farmersdelight:cabbage_seeds,1", "farmersdelight:cabbage_seeds,1,0.5"},
                        18000,
                        "farmersdelight:cabbages"
                )
        );
        //农夫乐事：番茄
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "farmersdelight/tomato"),
                createCultivatingRecipe(
                        "farmersdelight",
                        "farmersdelight:tomato_seeds",
                        new String[]{"farmersdelight:tomato,2", "farmersdelight:tomato,1,0.5", "farmersdelight:tomato_seeds,1"},
                        18000,
                        "farmersdelight:tomatoes"
                )
        );
        //农夫乐事：洋葱
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "farmersdelight/onion"),
                createCultivatingRecipe(
                        "farmersdelight",
                        "farmersdelight:onion",
                        new String[]{"farmersdelight:onion,2", "farmersdelight:onion,1,0.5"},
                        18000,
                        "farmersdelight:onions"
                )
        );
        //农夫乐事：稻米
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "farmersdelight/rice"),
                createCultivatingRecipe(
                        "farmersdelight",
                        "farmersdelight:rice",
                        new String[]{"farmersdelight:rice_panicle,2", "farmersdelight:rice_panicle,1,0.5","farmersdelight:rice,1"},
                        18000,
                        "farmersdelight:rice"
                )
        );
        /*
        //自然环境：车轮花
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/cartwheel"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:cartwheel",
                        new String[]{"environmental:cartwheel,2", "environmental:cartwheel,1,0.5"},
                        140,
                        "environmental:cartwheel"
                )
        );
        //自然环境：蓝铃花
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/bluebell"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:bluebell",
                        new String[]{"environmental:bluebell,2", "environmental:bluebell,1,0.5"},
                        140,
                        "environmental:bluebell"
                )
        );
        //自然环境：紫罗兰
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/violet"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:violet",
                        new String[]{"environmental:violet,2", "environmental:violet,1,0.5"},
                        140,
                        "environmental:violet"
                )
        );
        //自然环境：石竹
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/dianthus"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:dianthus",
                        new String[]{"environmental:dianthus,2", "environmental:dianthus,1,0.5"},
                        140,
                        "environmental:dianthus"
                )
        );
        //自然环境：莲花
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/red_lotus_flower"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:red_lotus_flower",
                        new String[]{"environmental:red_lotus_flower,2", "environmental:red_lotus_flower,1,0.5"},
                        140,
                        "environmental:red_lotus_flower"
                )
        );
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/white_lotus_flower"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:white_lotus_flower",
                        new String[]{"environmental:white_lotus_flower,2", "environmental:white_lotus_flower,1,0.5"},
                        140,
                        "environmental:white_lotus_flower"
                )
        );
        //自然环境：木槿花
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/yellow_hibiscus"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:yellow_hibiscus",
                        new String[]{"environmental:yellow_hibiscus,2", "environmental:yellow_hibiscus,1,0.5"},
                        140,
                        "environmental:yellow_hibiscus"
                )
        );
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/orange_hibiscus"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:orange_hibiscus",
                        new String[]{"environmental:orange_hibiscus,2", "environmental:orange_hibiscus,1,0.5"},
                        140,
                        "environmental:orange_hibiscus"
                )
        );
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/red_hibiscus"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:red_hibiscus",
                        new String[]{"environmental:red_hibiscus,2", "environmental:red_hibiscus,1,0.5"},
                        140,
                        "environmental:red_hibiscus"
                )
        );
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/pink_hibiscus"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:pink_hibiscus",
                        new String[]{"environmental:pink_hibiscus,2", "environmental:pink_hibiscus,1,0.5"},
                        140,
                        "environmental:pink_hibiscus"
                )
        );
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/purple_hibiscus"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:purple_hibiscus",
                        new String[]{"environmental:purple_hibiscus,2", "environmental:purple_hibiscus,1,0.5"},
                        140,
                        "environmental:purple_hibiscus"
                )
        );
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/purple_hibiscus"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:purple_hibiscus",
                        new String[]{"environmental:purple_hibiscus,2", "environmental:purple_hibiscus,1,0.5"},
                        140,
                        "environmental:purple_hibiscus"
                )
        );
        //自然环境：鹤望兰
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/bird_of_paradise"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:bird_of_paradise",
                        new String[]{"environmental:bird_of_paradise,2", "environmental:bird_of_paradise,1,0.5"},
                        140,
                        "environmental:bird_of_paradise"
                )
        );
        //自然环境：翠雀花
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/pink_delphinium"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:pink_delphinium",
                        new String[]{"environmental:pink_delphinium,2", "environmental:pink_delphinium,1,0.5"},
                        140,
                        "environmental:pink_delphinium"
                )
        );
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/blue_delphinium"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:blue_delphinium",
                        new String[]{"environmental:blue_delphinium,2", "environmental:blue_delphinium,1,0.5"},
                        140,
                        "environmental:blue_delphinium"
                )
        );
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/purple_delphinium"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:purple_delphinium",
                        new String[]{"environmental:purple_delphinium,2", "environmental:purple_delphinium,1,0.5"},
                        140,
                        "environmental:purple_delphinium"
                )
        );
        acceptor.accept(
                ResourceLocation.fromNamespaceAndPath("create_cultivation", "environmental/white_delphinium"),
                createCultivatingRecipe(
                        "environmental",
                        "environmental:white_delphinium",
                        new String[]{"environmental:white_delphinium,2", "environmental:white_delphinium,1,0.5"},
                        140,
                        "environmental:white_delphinium"
                )
        );
         */





    }

    private JsonObject createCultivatingRecipe(String modIdCondition, String ingredientId, String[] results, int duration, String cropBlockId) {
        JsonObject json = new JsonObject();
        JsonArray conditions = new JsonArray();
        JsonObject modLoaded = new JsonObject();
        modLoaded.addProperty("type", "neoforge:mod_loaded");
        modLoaded.addProperty("modid", modIdCondition);
        conditions.add(modLoaded);
        json.add("neoforge:conditions", conditions);

        json.addProperty("type", CCRecipes.CULTIVATING.getId().toString());

        JsonArray ingredientsArray = new JsonArray();
        JsonObject ingredientJson = new JsonObject();
        ingredientJson.addProperty("item", ingredientId);
        ingredientsArray.add(ingredientJson);
        json.add("ingredients", ingredientsArray);

        JsonArray resultsArray = new JsonArray();
        for (String resultStr : results) {
            String[] parts = resultStr.split(",");
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("id", parts[0]);
            resultJson.addProperty("count", Integer.parseInt(parts[1]));
            if (parts.length > 2) {
                resultJson.addProperty("chance", Float.parseFloat(parts[2]));
            }
            resultsArray.add(resultJson);
        }
        json.add("results", resultsArray);

        json.addProperty("processingDuration", duration);
        json.addProperty("crop_block", cropBlockId);

        return json;
    }

    @Override
    public String getName() {
        return "Create Cultivation Compat Recipes";
    }

    @FunctionalInterface
    protected interface RecipeAcceptor {
        void accept(ResourceLocation id, JsonObject recipe);
    }
}