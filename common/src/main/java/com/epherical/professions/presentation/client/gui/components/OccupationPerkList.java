package com.epherical.professions.presentation.client.gui.components;

import com.epherical.professions.PerkManager;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.data.config.ProfessionConfig;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.perks.Perk;
import com.epherical.professions.presentation.client.RenderHelperUtil;
import com.epherical.professions.presentation.client.gui.screen.OccupationPerkMenuScreen;
import com.epherical.professions.presentation.client.gui.widget.OccupationMenuButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OccupationPerkList extends AbstractOccupationSelector<OccupationPerkList.Entry> {

    private static final int ITEMS_PER_ROW = 3;
    private static final int PERK_BUTTON_WIDTH = 94;
    private static final int PERK_BUTTON_HEIGHT = 46;
    private static final int PERK_BUTTON_SPACING = 1;
    private static final int PERK_ICON_OFFSET_X = 3;
    private static final int PERK_ICON_OFFSET_Y = 12;
    private static final Component CLAIMED_TEXT = Component.translatable("professions.screen.occupation_perk_list.claimed")
            .setStyle(Style.EMPTY.withColor(ProfessionConfig.success));
    private static final Component UNCLAIMED_TEXT = Component.translatable("professions.screen.occupation_perk_list.unclaimed");
    private static final Component LOCKED_TEXT = Component.translatable("professions.screen.occupation_perk_list.locked")
            .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors));
    private static final Component DISABLED_TEXT = Component.translatable("professions.screen.occupation_perk_list.disabled")
            .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors));

    private final Occupation occupation;

    private final Set<Button> activatedButtons = new HashSet<>();
    private final Set<ResourceLocation> ids = new HashSet<>();
    private final Set<Button> allButtons = new HashSet<>();

    public OccupationPerkList(Minecraft mc, int width, int top, int bottom, int height, Occupation occupation) {
        super(mc, width, top, bottom, height);
        this.occupation = occupation;

        addEntries();
    }

    public void addEntries() {
        clearEntries();
        PerkManager perkManager = ProfessionsCommon.INSTANCE.getPerkManager();

        List<Perk> perksByProfession = perkManager.getPerksByProfession(occupation.getProfession());
        IProfessionalPlayer player = ProfessionsCommon.INSTANCE.getPlayerManager().getPlayer(Minecraft.getInstance().getUser().getProfileId());
        boolean arePerksEnabled = player == null || perkManager.arePerksEnabled(player);
        List<EntryItem> items = new ArrayList<>(perksByProfession.size());
        for (Perk perk : perksByProfession) {
            items.add(EntryItem.create(perk, occupation, arePerksEnabled));
        }

        for (int i = 0; i < items.size(); i += ITEMS_PER_ROW) {
            addEntry(new Entry(items.subList(i, Math.min(i + ITEMS_PER_ROW, items.size()))));
        }
    }

    public Set<Button> getActivatedButtons() {
        return activatedButtons;
    }

    public Set<Button> getAllButtons() {
        return allButtons;
    }

    @Override
    protected void renderListSeparators(GuiGraphics pGuiGraphics) {
    }

    @Override
    protected int getRowTop(int pIndex) {
        return super.getRowTop(pIndex);
    }

    @Override
    public int getBottom() {
        return super.getBottom();
    }

    protected int getRowBottom(int pIndex) {
        return super.getRowBottom(pIndex);
    }

    public Occupation getProfession() {
        return occupation;
    }

    public Set<ResourceLocation> getIds() {
        return ids;
    }

    public record EntryItem(Perk perk, ItemStack icon, Component claimStatus, boolean claimable, Component title, Component description) {

        private static EntryItem create(Perk perk, Occupation occupation, boolean perksEnabled) {
            ResourceLocation perkId = perk.getId();
            boolean claimable = occupation.hasClaimedPerk(perkId);
            Component status;
            if (!claimable) { // It has not been claimed
                if (!occupation.hasUnclaimedPerk(perkId)) {
                    // It's not in the unclaimed perk section either, so it must be locked.
                    status = LOCKED_TEXT;
                    claimable = false;
                } else {
                    status = UNCLAIMED_TEXT;
                    claimable = true;
                }
            } else {
                status = CLAIMED_TEXT;
                claimable = false;
            }

            if (!perksEnabled) {
                status = DISABLED_TEXT;
                claimable = false;
            }

            return new EntryItem(perk, new ItemStack(perk.getItemIcon()), status, claimable,
                    Component.translatable(perk.getTitle()),
                    Component.translatable(perk.getDescription()));
        }
    }

    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        private final List<EntryItem> items;
        private final List<OccupationMenuButton> perkButtons;

        public Entry(List<EntryItem> items) {
            this.items = items;
            this.perkButtons = new ArrayList<>(items.size());

            for (EntryItem item : items) {
                OccupationMenuButton button = OccupationMenuButton.omButton(Component.empty(), pressed -> {
                    if (activatedButtons.contains(pressed)) {
                        ids.remove(item.perk().getId());
                        activatedButtons.remove(pressed);
                    } else {
                        ids.add(item.perk().getId());
                        activatedButtons.add(pressed);
                    }
                        })
                        .pos(0, 0)
                        .size(PERK_BUTTON_WIDTH, PERK_BUTTON_HEIGHT)
                        .background(OccupationPerkMenuScreen.SPRITES)
                        .tooltip(Tooltip.create(buildTooltip(item)))
                        .build();
                if (!item.claimable) {
                    button.active = false;
                }
                allButtons.add(button);
                this.perkButtons.add(button);
            }
        }

        @Override
        public void render(GuiGraphics gfx,
                           int index, int y, int x, int rowWidth, int rowHeight,
                           int mouseX, int mouseY, boolean hovering, float partialTick) {


           // gfx.fill(x, y, rowWidth, rowHeight, 0xFFFFFFFF);

            for (int i = 0; i < items.size(); i++) {
                EntryItem item = items.get(i);
                OccupationMenuButton button = perkButtons.get(i);
                int buttonX = x + i * (PERK_BUTTON_WIDTH + PERK_BUTTON_SPACING);
                int buttonY = y + (rowHeight - PERK_BUTTON_HEIGHT) / 2;
                button.setPosition(buttonX, buttonY);
                button.render(gfx, mouseX, mouseY, partialTick);


                RenderHelperUtil.drawScaled(gfx, buttonX + (button.getWidth() / 2), buttonY + 4, 0.66f, () -> {
                    gfx.drawCenteredString(minecraft.font, item.title(), 0, 0, 0xFFFFFF);
                });

                RenderHelperUtil.drawWrappedScaledString(gfx, minecraft.font, item.description(), buttonX + PERK_ICON_OFFSET_X + 20, buttonY +  12, 0.66f, 110, 0xFFFFFF);

                RenderHelperUtil.drawScaled(gfx, buttonX + (button.getWidth() / 2), buttonY + rowHeight - 9, 0.75f, () -> {
                    gfx.drawCenteredString(minecraft.font, item.claimStatus(), 0, 0, 0xFFFFFF);
                });



                if (item.perk().getTextureIcon().isPresent()) {
                    gfx.blitSprite(item.perk().getTextureIcon().get(),  buttonX + PERK_ICON_OFFSET_X, buttonY + PERK_ICON_OFFSET_Y, 16, 16);
                } else {
                    gfx.renderFakeItem(item.icon(), buttonX + PERK_ICON_OFFSET_X, buttonY + PERK_ICON_OFFSET_Y);
                }
            }
        }

        private Component buildTooltip(EntryItem item) {
            Perk perk = item.perk();
            return Component.translatable(
                    "professions.screen.occupation_perk_list.tooltip",
                    perk.getLevelRequirement(),
                    Component.translatable(perk.getDescription()),
                    item.claimStatus());
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return perkButtons;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return perkButtons;
        }
    }
}
