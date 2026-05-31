package com.epherical.professions.model.gating;

import com.epherical.professions.api.actions.Gate;
import com.epherical.professions.bootstrap.Gates;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.presentation.model.GateDisplay;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlaceGate extends Gate<Block> {

    public static final MapCodec<PlaceGate> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(PlaceGate::buildCommon),
                    Gate.tagOrElementListCodec(Registries.BLOCK).fieldOf("blocks").forGetter(PlaceGate::getValues)
            ).apply(i, PlaceGate::new)
    );

    public PlaceGate(Common common, List<Either<TagKey<Block>, ResourceKey<Block>>> values) {
        super(common, values);
    }

    @Override
    public Common buildCommon() {
        return Common.build(this);
    }

    @Override
    public GateType getGateType() {
        return Gates.PLACE;
    }

    @Override
    public ResourceKey<? extends Registry<Block>> getRegistryKey() {
        return Registries.BLOCK;
    }

    @Override
    public List<GateDisplay<Block>> getDisplays(@Nullable RegistryAccess registryAccess) {
        if (registryAccess == null) {
            return List.of();
        }
        HolderLookup.RegistryLookup<Block> blockRegistryLookup = registryAccess.lookupOrThrow(getRegistryKey());

        List<GateDisplay<Block>> displays = new ArrayList<>();
        for (Either<TagKey<Block>, ResourceKey<Block>> value : getValues()) {
            value.ifLeft(blockTagKey -> blockRegistryLookup.get(blockTagKey).ifPresent(block -> {
                        for (Holder<Block> blockHolder : block) {
                            Block block1 = blockHolder.value();
                            displays.add(new GateDisplay<>(blockHolder, new ItemStack(block1), Component.translatable(block1.getDescriptionId())));
                        }
                    }))
                    .ifRight(blockKey -> {
                        blockRegistryLookup.get(blockKey).ifPresent(holder -> {
                            Block block = holder.value();
                            displays.add(new GateDisplay<>(holder, new ItemStack(block), Component.translatable(block.getDescriptionId())));
                        });
                    });
        }

        return displays;
    }

    @Override
    public boolean test(ProfessionContext context) {
        BlockState blockState = context.getPossibleParameter(ProfessionParameter.THIS_BLOCK_STATE);
        Block block = null;
        if (blockState == null) {
            ItemStack stack = context.getPossibleParameter(ProfessionParameter.ITEM_INVOLVED);
            if (stack != null && stack.getItem() instanceof BlockItem blockItem) {
                block = blockItem.getBlock();
            } else {
                return false;
            }
        } else {
            block = blockState.getBlock();
        }

        for (Either<TagKey<Block>, ResourceKey<Block>> value : getValues()) {
            if (value.left().isPresent() && block.builtInRegistryHolder().is(value.left().get())) {
                return true;
            }
            if (value.right().isPresent() && block.builtInRegistryHolder().is(value.right().get())) {
                return true;
            }
        }

        return false;
    }

    public static class Builder extends Gate.Builder<Builder, Block> {
        public Builder(Holder<Profession> profession) {
            super(profession);
        }

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Gate<Block> build() {
            return new PlaceGate(new Common(getProfession(), getRequirements()), getTargets());
        }
    }
}
