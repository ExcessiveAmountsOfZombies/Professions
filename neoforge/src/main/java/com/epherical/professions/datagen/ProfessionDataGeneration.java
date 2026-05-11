package com.epherical.professions.datagen;

import com.epherical.professions.NeoForgeProfessionsMod;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.datagen.action.BuildingActionProvider;
import com.epherical.professions.datagen.action.CraftingActionProvider;
import com.epherical.professions.datagen.action.EnchantingActionProvider;
import com.epherical.professions.datagen.action.FarmingActionProvider;
import com.epherical.professions.datagen.action.FishingActionProvider;
import com.epherical.professions.datagen.action.HuntingActionProvider;
import com.epherical.professions.datagen.action.LoggingActionProvider;
import com.epherical.professions.datagen.action.MinerActionProvider;
import com.epherical.professions.datagen.action.SmithingActionProvider;
import com.epherical.professions.datagen.perks.BuildingPerkProvider;
import com.epherical.professions.datagen.perks.CraftingPerkProvider;
import com.epherical.professions.datagen.perks.EnchantingPerkProvider;
import com.epherical.professions.datagen.perks.FarmingPerkProvider;
import com.epherical.professions.datagen.perks.FishingPerkProvider;
import com.epherical.professions.datagen.perks.HuntingPerkProvider;
import com.epherical.professions.datagen.perks.LoggingPerkProvider;
import com.epherical.professions.datagen.perks.MiningPerkProvider;
import com.epherical.professions.datagen.perks.SmithingPerkProvider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.mbertoli.jfep.Parser;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

@EventBusSubscriber(modid = ProfessionsCommon.MOD_ID)
public final class ProfessionDataGeneration {


    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {

        RegistrySetBuilder builder = new RegistrySetBuilder()
                .add(NeoForgeProfessionsMod.PROFESSION_REGISTRY_KEY, ctx -> {
                    /*ctx.register(id("alchemy"), new Builder(rl("alchemy"))
                            .nameColor(TextColor.parseColor("#a100e0").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by brewing potions."
                            })
                            .display(Component.literal("Alchemy"))
                            .build());*/

                    ctx.register(id("building"), new Builder(rl("building"))
                            .nameColor(TextColor.parseColor("#f2de00").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Progress through building projects",
                                    "by placing structural blocks."
                            })
                            .icon(Items.OAK_PLANKS)
                            .display(Component.literal("Building"))
                            .build());


                    ctx.register(id("crafting"), new Builder(rl("crafting"))
                            .nameColor(TextColor.parseColor("#f2a100").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by crafting."
                            })
                            .icon(Items.CRAFTING_TABLE)
                            .display(Component.literal("Crafting"))
                            .build());

                    ctx.register(id("enchanting"), new Builder(rl("enchanting"))
                            .nameColor(TextColor.parseColor("#c9008d").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by enchanting."
                            })
                            .icon(Items.ENCHANTING_TABLE)
                            .display(Component.literal("Enchanting"))
                            .build());

                    ctx.register(id("farming"), new Builder(rl("farming"))
                            .nameColor(TextColor.parseColor("#107d0e").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by farming."
                            })
                            .icon(Items.WHEAT)
                            .display(Component.literal("Farming"))
                            .build());

                    ctx.register(id("fishing"), new Builder(rl("fishing"))
                            .nameColor(TextColor.parseColor("#0a91c7").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by fishing."
                            })
                            .icon(Items.FISHING_ROD)
                            .display(Component.literal("Fishing"))
                            .build());

                    ctx.register(id("hunting"), new Builder(rl("hunting"))
                            .nameColor(TextColor.parseColor("#a6542e").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by hunting animals, killing monsters, and exploring"
                            })
                            .icon(Items.BOW)
                            .display(Component.literal("Hunting"))
                            .build());

                    ctx.register(id("logging"), new Builder(rl("logging"))
                            .nameColor(TextColor.parseColor("#9e3011").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by farming trees."
                            })
                            .icon(Items.OAK_SAPLING)
                            .display(Component.literal("Logging"))
                            .build());

                    ctx.register(id("mining"), new Builder(rl("mining"))
                            .nameColor(TextColor.parseColor("#666E63").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by mining ores and minerals."
                            })
                            .icon(Items.DIAMOND_PICKAXE)
                            .display(Component.literal("Mining"))
                            .build());

                    ctx.register(id("smithing"), new Builder(rl("smithing"))
                            .nameColor(TextColor.parseColor("#84abad").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by smithing items."
                            })
                            .icon(Items.ANVIL)
                            .display(Component.literal("Smithing"))
                            .build());

                    /*ctx.register(id("trading"), new Builder(rl("trading"))
                            .nameColor(TextColor.parseColor("#2dcf08").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by trading items to villagers."
                            })
                            .display(Component.literal("Trading"))
                            .build());*/
                });



        PackOutput out = event.getGenerator().getPackOutput();
        DatapackBuiltinEntriesProvider professionsProvider = new DatapackBuiltinEntriesProvider(
                out,
                event.getLookupProvider(),
                builder,
                java.util.Set.of(ProfessionsCommon.MOD_ID)
        );
        event.getGenerator().addProvider(true, professionsProvider);
        event.getGenerator().addProvider(event.includeServer(), new CategoryDataProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new MinerActionProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new MiningPerkProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new LoggingPerkProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new FarmingPerkProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new FishingPerkProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new HuntingPerkProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new EnchantingPerkProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new CraftingPerkProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new SmithingPerkProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new BuildingPerkProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new LoggingActionProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new FarmingActionProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new FishingActionProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new HuntingActionProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new EnchantingActionProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new CraftingActionProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new SmithingActionProvider(out, professionsProvider.getRegistryProvider()));
        event.getGenerator().addProvider(event.includeServer(), new BuildingActionProvider(out, professionsProvider.getRegistryProvider()));

    }


    public static ResourceKey<Profession> id(String path) {
        return ResourceKey.create(NeoForgeProfessionsMod.PROFESSION_REGISTRY_KEY, rl(path));
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, path);
    }


    public static final class Builder {
        private final ResourceLocation id;
        private Component display = Component.empty();
        private String[] description = new String[0];
        private TextColor nameColor = TextColor.parseColor("#FFFFFF").getOrThrow();
        private TextColor descColor = TextColor.parseColor("#AAAAAA").getOrThrow();
        private Item icon = Items.STONE_PICKAXE;
        private int maxLevel = 0;
        private ResourceLocation levelUpSound = ResourceLocation.parse("minecraft:entity.player.levelup");
        private String expEquation = "1000+(lvl*50)";
        private final NavigableMap<Integer, Parser> expScalers = new TreeMap<>();

        public Builder(ResourceLocation id) {
            this.id = id;
        }

        public Builder display(Component txt) {
            this.display = txt;
            return this;
        }

        public Builder description(String[] txt) {
            this.description = txt;
            return this;
        }

        public Builder nameColor(TextColor color) {
            this.nameColor = color;
            return this;
        }

        public Builder descColor(TextColor color) {
            this.descColor = color;
            return this;
        }

        public Builder icon(Item icon) {
            this.icon = icon;
            return this;
        }

        public Builder maxLevel(int lvl) {
            this.maxLevel = lvl;
            return this;
        }

        public Builder levelUpSound(ResourceLocation sound) {
            this.levelUpSound = sound;
            return this;
        }

        public Builder expEquation(String eq) {
            this.expEquation = eq;
            return this;
        }

        public Builder expScaler(int level, String equation) {
            this.expScalers.put(level, new Parser(equation));
            return this;
        }

        public Profession build() {
            return new Profession(
                    new Profession.Formatting(display, List.of(description), nameColor, descColor, icon),
                    new Profession.Settings(maxLevel, levelUpSound),
                    new Profession.ExpScaling(new Parser(expEquation), new TreeMap<>(expScalers))
            );
        }
    }
}
