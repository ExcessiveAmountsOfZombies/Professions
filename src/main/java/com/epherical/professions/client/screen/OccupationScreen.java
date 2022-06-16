package com.epherical.professions.client.screen;

import com.epherical.professions.CommonPlatform;
import com.epherical.professions.client.widgets.CommandButton;
import com.epherical.professions.client.widgets.ProfessionsListingWidget;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.networking.CommandButtons;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.util.ActionDisplay;
import com.epherical.professions.util.LevelDisplay;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OccupationScreen<T> extends Screen {

    public static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("professions", "textures/gui/occupation_menu.png");
    protected int imageWidth = 256;
    protected int imageHeight = 170;

    @Nullable
    private Screen prevScreen;
    private ProfessionsListingWidget list;
    private CommandButtons button;
    private List<ProfessionsListingWidget.AbstractEntry> entries;

    private final MutableComponent NO_ENTRIES = Component.translatable("professions.gui.no_entries")
            .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables));
    private final MutableComponent NO_ENTRIES_LEAVE = Component.translatable("professions.gui.no_entries.leave")
            .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables));

    public OccupationScreen(List<T> list, Minecraft minecraft, OccupationListCreator<T> creator, CommandButtons buttons) {
        super(Component.nullToEmpty(""));
        this.button = buttons;
        this.entries = creator.createEntries(this, minecraft, list);
    }

    public OccupationScreen<T> addPrevious(Screen prevScreen) {
        this.prevScreen = prevScreen;
        return this;
    }

    @Override
    protected void init() {
        super.init();
        this.list = new ProfessionsListingWidget(this, minecraft,
                this.width,
                (this.height), // height
                (this.height / 2 - 85), // top
                (this.height / 2 + 76), // bottom
                24);
        // row 1
        addRenderableWidget(new CommandButton(new ItemStack(Items.EMERALD), this.width / 2 + 43, this.height / 2 - 80,
                Component.translatable("professions.gui.join"),
                button1 -> CommonPlatform.platform.sendButtonPacket(CommandButtons.JOIN)));
        addRenderableWidget(new CommandButton(new ItemStack(Items.REDSTONE), this.width / 2 + 43 + 38 + 3, this.height / 2 - 80,
                Component.translatable("professions.gui.leave"),
                button1 -> CommonPlatform.platform.sendButtonPacket(CommandButtons.LEAVE)));
        //row 2
        addRenderableWidget(new CommandButton(new ItemStack(Items.BOOK), this.width / 2 + 43, this.height / 2 - 80 + 48 + 3,
                Component.translatable("professions.gui.info"),
                button1 -> CommonPlatform.platform.sendButtonPacket(CommandButtons.INFO)));
        addRenderableWidget(new CommandButton(new ItemStack(Items.AMETHYST_SHARD), this.width / 2 + 43 + 38 + 3, this.height / 2 - 80 + 48 + 3,
                Component.translatable("professions.gui.top"),
                button1 -> CommonPlatform.platform.sendButtonPacket(CommandButtons.TOP)));
        // row 3
        addRenderableWidget(new CommandButton(new ItemStack(Items.BARRIER), this.width / 2 + 43 + (38 + 3) / 2, this.height / 2 - 80 + (48 + 3) * 2,
                Component.translatable("professions.gui.close"),
                button1 -> this.minecraft.setScreen(prevScreen)));
        this.addWidget(list);
        list.reset(entries);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        int ofx = (this.width - imageWidth) / 2;
        int ofy = (this.height - imageHeight) / 2;
        renderOccupationWindow(poseStack, ofx, ofy);


        Optional<GuiEventListener> element = list.getChildAt(mouseX, mouseY);

        if (entries.size() == 0) {
            drawCenteredString(poseStack, font, NO_ENTRIES, ((this.width - imageWidth) / 2) + 83, ofy + 50, -1);
            if (button == CommandButtons.LEAVE) {
                drawCenteredString(poseStack, font, NO_ENTRIES_LEAVE, ((this.width - imageWidth) / 2) + 83, ofy + 70, -1);
            }
        }

        list.render(poseStack, mouseX, mouseY, partialTick);
        super.render(poseStack, mouseX, mouseY, partialTick);

        if (element.isPresent()) {
            ProfessionsListingWidget.AbstractEntry entry = (ProfessionsListingWidget.AbstractEntry) element.get();
            entry.getButton().renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    public void renderOccupationWindow(PoseStack stack, int offsetX, int offsetY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WINDOW_LOCATION);
        this.blit(stack, offsetX, offsetY, 0, 0, imageWidth, imageHeight);
        RenderSystem.setShaderColor(1.0F, 0.0F, 1.0F, 1.0F);
    }

    /*private int adjustedHeight() {
        return (Math.max(52, this.height - 128 - 15) / 2);
    }

    private int scrollHeight() {
        return 80 + adjustedHeight() * 16 - 8;
    }

    private int centeredWidth() {
        return (this.width - 256) / 2;
    }*/

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public CommandButtons getButton() {
        return button;
    }

    public static <T> List<ProfessionsListingWidget.AbstractEntry> createOccupationEntries(OccupationScreen<T> screen, Minecraft minecraft, List<Occupation> occupations) {
        List<ProfessionsListingWidget.AbstractEntry> entries = new ArrayList<>();
        for (Occupation occupation : occupations) {
            entries.add(new ProfessionsListingWidget.OccupationEntry(screen, screen.list, minecraft, occupation));
        }
        return entries;
    }

    public static <T> List<ProfessionsListingWidget.AbstractEntry> createProfessionEntries(OccupationScreen<T> screen, Minecraft minecraft, List<Profession> professions, CommandButtons command) {
        List<ProfessionsListingWidget.AbstractEntry> entries = new ArrayList<>();
        for (Profession profession : professions) {
            switch (command) {
                case JOIN, LEAVE, INFO -> entries.add(new ProfessionsListingWidget.ProfessionEntry(screen, screen.list, minecraft, profession));
            }

        }
        return entries;
    }

    public static <T> List<ProfessionsListingWidget.AbstractEntry> createInfoEntries(OccupationScreen<T> screen, Minecraft minecraft, List<ActionDisplay> displays) {
        List<ProfessionsListingWidget.AbstractEntry> entries = new ArrayList<>();
        for (ActionDisplay display : displays) {
            entries.add(new ProfessionsListingWidget.InfoEntry(screen, screen.list, minecraft, display.getHeader()));
            for (Component component : display.getActionInformation()) {
                entries.add(new ProfessionsListingWidget.InfoEntry(screen, screen.list, minecraft, component));
            }
        }
        return entries;
    }

    public static <T> List<ProfessionsListingWidget.AbstractEntry> createTopEntries(OccupationScreen<T> screen, Minecraft minecraft, List<LevelDisplay> displays) {
        List<ProfessionsListingWidget.AbstractEntry> entries = new ArrayList<>();
        for (LevelDisplay display : displays) {
            entries.add(new ProfessionsListingWidget.LevelEntry(screen, screen.list, minecraft, display.uuid(), display.level()));
        }
        return entries;
    }

    public interface OccupationListCreator<T> {
        List<ProfessionsListingWidget.AbstractEntry> createEntries(OccupationScreen<T> screen, Minecraft minecraft, List<T> display);
    }
}
