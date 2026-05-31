package com.epherical.professions.api.client;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Requirements;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.gating.requirements.AdvancementRequirement;
import com.epherical.professions.api.actions.GateRequirement;
import com.epherical.professions.model.gating.requirements.GateRequirementType;
import com.epherical.professions.model.gating.requirements.LevelRequirement;
import com.epherical.professions.presentation.client.RenderHelperUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


public interface GateRenderer<T extends GateRequirement> {

    Map<GateRequirementType, GateRenderer<?>> GATE_RENDERER = new HashMap<>();

    GateRenderer<AdvancementRequirement> ADVANCEMENT_RENDERER =
            GateRenderer.register(Requirements.ADVANCEMENT_REQUIREMENT, (gfx, mc, meetsRequirement, x, y, width, height,
                                                                         player, currentOccupation,
                                                                         context, type) -> {
                int color = meetsRequirement ? 0x316e15 : 0xb52222;

                gfx.vLine(x + 20, y + 2, y + height - 4, 0xFF6f4d15);
                gfx.renderFakeItem(new ItemStack(Items.BOOK), x + 2, y + 1);

                // todo; if we can put the name of the advancement in here that would be good
                RenderHelperUtil.drawWrappedScaledString(gfx, mc.font, Component.translatable("Advancement: %s", type.advancement().toString()), x + 24, y + 2, 0.8f, width - 24, color);


            });


    GateRenderer<LevelRequirement> LEVEL_RENDERER =
            GateRenderer.register(Requirements.LEVEL_REQUIREMENT, (gfx, mc, meetsRequirement,  x, y, width, height,
                                                                   player, currentOccupation,
                                                                   context, type) -> {
                int color = meetsRequirement ? 0x316e15 : 0x6e1515;

                gfx.vLine(x + 20, y + 2, y + height - 4, 0xFF6f4d15);
                gfx.renderFakeItem(new ItemStack(currentOccupation.getProfession().value().formatting().icon()), x + 2, y + 1);
                RenderHelperUtil.drawScaledString(gfx, mc.font, Component.translatable("%s Level %s", currentOccupation.getProfession().value().displayNameRaw(), type.level()), x + 24, y + 2, 0.8f, color, false);


                Component msg = Component.translatable("%s/%s", currentOccupation.getLevel(), type.level());
                int length = mc.font.width(msg);




                RenderHelperUtil.drawScaledString(gfx, mc.font, msg,  x + width - length - 2, y + 10, 1f, color, false);
            });



    void render(GuiGraphics gfx, Minecraft mc, boolean meetsRequirement, int x, int y, int width, int height,
                IProfessionalPlayer player, Occupation currentOccupation,
                ProfessionContext context, T type);



    static <T extends GateRequirement> GateRenderer<T> register(GateRequirementType gateRequirementType, GateRenderer<T> rendererRegistry) {
        GATE_RENDERER.put(gateRequirementType, rendererRegistry);
        return  rendererRegistry;
    }

    @Nullable
    static GateRenderer<? extends GateRequirement> getRenderer(GateRequirementType gateRequirementType) {
        return GATE_RENDERER.get(gateRequirementType);
    }






}
