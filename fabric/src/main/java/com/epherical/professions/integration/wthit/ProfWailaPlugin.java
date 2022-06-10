package com.epherical.professions.integration.wthit;

import com.epherical.professions.Constants;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

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
            tooltip.addLine(Component.nullToEmpty("bozoville"));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, IServerAccessor accessor, IPluginConfig config) {

    }
}
