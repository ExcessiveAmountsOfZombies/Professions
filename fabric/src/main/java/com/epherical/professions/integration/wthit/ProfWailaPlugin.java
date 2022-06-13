package com.epherical.professions.integration.wthit;

import com.epherical.professions.Constants;
import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.Unlocks;
import com.epherical.professions.util.ProfessionUtil;
import com.mojang.datafixers.util.Pair;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.UUID;

public class ProfWailaPlugin implements IWailaPlugin, IBlockComponentProvider, IServerDataProvider<Block> {

    public static final ResourceLocation LOCKED = Constants.modID("locked");

    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(LOCKED, true);
        registrar.addComponent(this, TooltipPosition.BODY, Block.class);
    }


    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(LOCKED)) {
            UUID uuid = accessor.getPlayer().getUUID();
            ProfessionalPlayer pPlayer = ProfessionsFabric.getInstance().getPlayerManager().getPlayer(uuid);
            if (pPlayer == null) {
                return;
            }
            Block block = accessor.getBlock();
            Pair<Unlock.Singular<Block>, Boolean> pair = ProfessionUtil.canUse(pPlayer, Unlocks.BLOCK_UNLOCK, block);
            if (!pair.getSecond()) {
                Unlock.Singular<Block> singular = pair.getFirst();
                tooltip.addLine(new TextComponent("❌ ").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors))
                        .append(new TranslatableComponent("professions.tooltip.drop_req",
                                singular.getProfessionDisplay(),
                                new TextComponent(String.valueOf(singular.getUnlockLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                                .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors))));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag data, IServerAccessor accessor, IPluginConfig config) {

    }
}
