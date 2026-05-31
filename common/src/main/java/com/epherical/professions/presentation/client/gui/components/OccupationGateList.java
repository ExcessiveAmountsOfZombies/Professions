package com.epherical.professions.presentation.client.gui.components;

import com.epherical.professions.GateManager;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.api.actions.Gate;
import com.epherical.professions.model.gating.GateReport;
import com.epherical.professions.presentation.client.RenderHelperUtil;
import com.epherical.professions.presentation.model.GateDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class OccupationGateList extends AbstractOccupationSelector<OccupationGateList.Entry> {

    private static final ResourceLocation GATE_BUTTON_ENABLED = ResourceLocation.fromNamespaceAndPath(
            ProfessionsCommon.MOD_ID, "occupation/gates/gate_button_enabled");
    private static final ResourceLocation GATE_BUTTON_HOVERED = ResourceLocation.fromNamespaceAndPath(
            ProfessionsCommon.MOD_ID, "occupation/gates/gate_button_hovered");
    private static final ResourceLocation GATE_ROW_ICON_HIGHLIGHT = ResourceLocation.fromNamespaceAndPath(
            ProfessionsCommon.MOD_ID, "occupation/gates/gate_row_icon_highlight");
    private static final ResourceLocation GREY_LOCK_ICON = ResourceLocation.fromNamespaceAndPath(
            ProfessionsCommon.MOD_ID, "occupation/icons/grey_lock");

    private final Occupation occupation;

    public OccupationGateList(Minecraft mc, int width, int top, int bottom, int height, Occupation occupation) {
        super(mc, width, top, bottom, height);
        this.occupation = occupation;

        addEntries();
    }

    public void addEntries() {
        clearEntries();
        GateManager gateManager = ProfessionsCommon.INSTANCE.getGateManager();
        Collection<Gate<?>> gatesByProfession = gateManager.getGatesByProfession(occupation.getProfession());
        for (Gate<?> gate : gatesByProfession) {
            addEntry(new Entry(gate, occupation));
        }
    }

    @Override
    public void setSelected(@Nullable OccupationGateList.Entry pSelected) {
        super.setSelected(pSelected);
    }

    @Override
    protected void renderListSeparators(@NotNull GuiGraphics pGuiGraphics) {
    }

    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        private static final int ROW_ICON_SIZE = 16;
        private static final int ICON_TEXT_SPACING = 5;
        private static final long TAG_ROTATION_INTERVAL_MS = 1000L;

        private final String gateTypeLine;
        private final List<? extends GateDisplay<?>> targetDisplays;
        private final ItemStack defaultGateIcon = ItemStack.EMPTY;
        private final GateReport gateReport = new GateReport();
        private final ProfessionContext context; // todo; fix later probably
        private final IProfessionalPlayer player; // todo; fix later probably

        public Entry(Gate<?> gate, Occupation occupation) {
            RegistryAccess registryAccess = minecraft.level != null ? minecraft.level.registryAccess() : null;
            this.gateTypeLine = Component.translatable(gate.getGateType().translationKey()).getString();


            player = ProfessionsCommon.INSTANCE.getPlayerManager().getPlayer(Minecraft.getInstance().getUser().getProfileId());
            context = ProfessionContext.gateBuilder(minecraft.level, gate.getGateType(), player).build();
            gate.getGatePredicate().test(context, occupation, gateReport, gate);


            this.targetDisplays = gate.getDisplays(registryAccess);
        }


        public String getGateTypeLine() {
            return gateTypeLine;
        }

        public GateReport getGateReport() {
            return gateReport;
        }

        public List<? extends GateDisplay<?>> getTargetDisplays() {
            return targetDisplays;
        }

        public IProfessionalPlayer getPlayer() {
            return player;
        }

        public ProfessionContext getContext() {
            return context;
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        @Override
        public void render(GuiGraphics gfx,
                           int index, int y, int x, int rowWidth, int rowHeight,
                           int mouseX, int mouseY, boolean hovering, float partialTick) {

            ResourceLocation backgroundSprite = hovering || this.equals(getSelected()) ? GATE_BUTTON_HOVERED : GATE_BUTTON_ENABLED;
            gfx.blitSprite(backgroundSprite, x, y, rowWidth - 2, rowHeight + 3);

            GateDisplay<?> display = getCurrentTargetDisplay();
            ItemStack icon = display.icon();
            String name = display.name().getString();



            int iconX = x + 2;
            int iconY = y + 2;
            gfx.blitSprite(GATE_ROW_ICON_HIGHLIGHT, iconX, iconY, 26, 26);
            if (!icon.isEmpty()) {
                RenderHelperUtil.drawScaled(gfx, iconX, iconY + 1, 1.5f, () -> gfx.renderFakeItem(icon, 0, 0));
            }

            int lockX = x + rowWidth - 18 - 5;
            int lockY = y + (rowHeight - 15) / 2;


            if (!gateReport.isAllowed()) {
                gfx.setColor(1.5f, 0f, 0f, 1f);
                gfx.blitSprite(GREY_LOCK_ICON, lockX, lockY, 18, 18);
                gfx.setColor(1f, 1f, 1f, 1f);
            } else {
                gfx.setColor(0f, 1.5f, 0f, 1f);
                gfx.blitSprite(GREY_LOCK_ICON, lockX, lockY, 18, 18);
                gfx.setColor(1f, 1f, 1f, 1f);
            }


            int textX = iconX + ROW_ICON_SIZE + ICON_TEXT_SPACING + 7;
            int maxTextWidth = Math.max(10, lockX - textX - 2);
            RenderHelperUtil.drawScaledString(gfx, minecraft.font, minecraft.font.plainSubstrByWidth(name,
                    (int) (maxTextWidth / 0.65f)), textX, y + 5, 0.65f, 0xFFFFFF, false);
            RenderHelperUtil.drawScaledString(gfx, minecraft.font, minecraft.font.plainSubstrByWidth(gateTypeLine,
                    (int) (maxTextWidth / 0.5f)), textX, y + 20, 0.5f, 0xDDB56A, false);
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            boolean clicked = super.mouseClicked(pMouseX, pMouseY, pButton);
            setSelected(this);
            playDownSound(Minecraft.getInstance().getSoundManager());
            return clicked;
        }

        public GateDisplay<?> getCurrentTargetDisplay() {
            if (targetDisplays.isEmpty()) {
                return new GateDisplay<>(null, defaultGateIcon, Component.literal("Empty"));
            }

            long tick = System.currentTimeMillis() / TAG_ROTATION_INTERVAL_MS;
            int index = (int) (tick % targetDisplays.size());
            return targetDisplays.get(index);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of();
        }
    }

}
