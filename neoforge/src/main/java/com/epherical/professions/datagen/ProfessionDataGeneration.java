package com.epherical.professions.datagen;

import com.epherical.professions.Constants;
import com.epherical.professions.NeoForgeRegistrarBackend;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.register.PlatformBootstrap;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.mbertoli.jfep.Parser;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = Constants.MOD_ID)
public final class ProfessionDataGeneration {


    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        System.out.println("[TRACE] GatherDataEvent fired");   // ①

        RegistrySetBuilder builder = new RegistrySetBuilder()
                .add(ProfessionsMod.PROFESSION_REGISTRY_KEY, ctx -> {
                    System.out.println("[TRACE] inside bootstrap");    // ③
                    ctx.register(id("alchemy"), new Builder(rl("alchemy"))
                            .nameColor(TextColor.parseColor("#a100e0").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by brewing potions."
                            })
                            .display(Component.literal("Alchemy"))
                            .maxLevel(100)
                            .build());

                    ctx.register(id("building"), new Builder(rl("building"))
                            .nameColor(TextColor.parseColor("#f2de00").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by placing blocks."
                            })
                            .display(Component.literal("Building"))
                            .maxLevel(100)
                            .build());

                    ctx.register(id("crafting"), new Builder(rl("crafting"))
                            .nameColor(TextColor.parseColor("#f2a100").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by crafting."
                            })
                            .display(Component.literal("Crafting"))
                            .maxLevel(100)
                            .build());

                    ctx.register(id("enchanting"), new Builder(rl("enchanting"))
                            .nameColor(TextColor.parseColor("#c9008d").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by enchanting."
                            })
                            .display(Component.literal("Enchanting"))
                            .maxLevel(100)
                            .build());

                    ctx.register(id("farming"), new Builder(rl("farming"))
                            .nameColor(TextColor.parseColor("#107d0e").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by farming."
                            })
                            .display(Component.literal("Farming"))
                            .maxLevel(100)
                            .build());

                    ctx.register(id("fishing"), new Builder(rl("fishing"))
                            .nameColor(TextColor.parseColor("#0a91c7").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by fishing."
                            })
                            .display(Component.literal("Fishing"))
                            .maxLevel(100)
                            .build());

                    ctx.register(id("hunting"), new Builder(rl("hunting"))
                            .nameColor(TextColor.parseColor("#a6542e").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by hunting animals, killing monsters, and exploring"
                            })
                            .display(Component.literal("Hunting"))
                            .maxLevel(100)
                            .build());

                    ctx.register(id("logging"), new Builder(rl("logging"))
                            .nameColor(TextColor.parseColor("#9e3011").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by farming trees."
                            })
                            .display(Component.literal("Logging"))
                            .maxLevel(100)
                            .build());

                    ctx.register(id("mining"), new Builder(rl("mining"))
                            .nameColor(TextColor.parseColor("#666E63").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by mining ores and minerals."
                            })
                            .display(Component.literal("Mining"))
                            .maxLevel(100)
                            .build());

                    ctx.register(id("smithing"), new Builder(rl("smithing"))
                            .nameColor(TextColor.parseColor("#84abad").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by smithing items."
                            })
                            .display(Component.literal("Smithing"))
                            .maxLevel(100)
                            .build());

                    ctx.register(id("trading"), new Builder(rl("trading"))
                            .nameColor(TextColor.parseColor("#2dcf08").getOrThrow())
                            .descColor(TextColor.parseColor("#FFFFFF").getOrThrow())
                            .description(new String[]{
                                    "Earn money and experience",
                                    "by trading items to villagers."
                            })
                            .display(Component.literal("Trading"))
                            .maxLevel(100)
                            .build());
                });


        PackOutput out = event.getGenerator().getPackOutput();

        System.out.println("[TRACE] includeServer="+event.includeServer());
        event.getGenerator().addProvider(
                true,
                new DatapackBuiltinEntriesProvider(
                        out,
                        event.getLookupProvider(),
                        builder,
                        Set.of(Constants.MOD_ID)));
        System.out.println("[TRACE] provider added");                  // ②

    }



    public static ResourceKey<Profession> id(String path) {
        return ResourceKey.create(ProfessionsMod.PROFESSION_REGISTRY_KEY, rl(path));
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }



    public static final class Builder {
        private final ResourceLocation id;
        private Component display     = Component.empty();
        private String[] description = new String[0];
        private TextColor nameColor   = TextColor.parseColor("#FFFFFF").getOrThrow();
        private TextColor descColor   = TextColor.parseColor("#AAAAAA").getOrThrow();
        private int maxLevel          = 30;
        private String expEquation    = "1000*1.05^(lvl-1)";

        public Builder(ResourceLocation id) { this.id = id; }

        public Builder display(Component txt)          { this.display = txt; return this; }
        public Builder description(String[] txt)      { this.description = txt; return this; }
        public Builder nameColor(TextColor color)      { this.nameColor = color; return this; }
        public Builder descColor(TextColor color)      { this.descColor = color; return this; }
        public Builder maxLevel(int lvl)               { this.maxLevel = lvl; return this; }
        public Builder expEquation(String eq)          { this.expEquation = eq; return this; }

        public Profession build() {
            return new Profession(
                    id, display, List.of(description),
                    nameColor, descColor,
                    maxLevel, new Parser(expEquation)
            );
        }
    }


}
