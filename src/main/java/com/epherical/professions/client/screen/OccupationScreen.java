package com.epherical.professions.client.screen;

import com.epherical.professions.CommonPlatform;
import com.epherical.professions.client.widgets.CommandButton;
import com.epherical.professions.client.widgets.Hidden;
import com.epherical.professions.client.widgets.HoldsProfession;
import com.epherical.professions.client.widgets.ProfessionsListingWidget;
import com.epherical.professions.client.widgets.Selectable;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.networking.CommandButtons;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.util.ActionDisplay;
import com.epherical.professions.util.LevelDisplay;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;


public class OccupationScreen<T> extends Screen {

    public static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("professions", "textures/gui/new_occupation_menu.png");
    protected int imageWidth = 256;
    protected int imageHeight = 170;

    @Nullable
    private static Screen prevScreen;
    private ProfessionsListingWidget list;
    private CommandButtons button;
    private List<ProfessionsListingWidget.AbstractEntry> entries;

    private boolean anyOccupationSelected = false;
    private HoldsProfession professionHolder;

    private final List<Widget> renderables = Lists.newArrayList();
    private final List<Hidden> buttonsThatHide = Lists.newArrayList();

    private final MutableComponent NO_ENTRIES = new TranslatableComponent("professions.gui.no_entries")
            .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables));
    private final MutableComponent NO_ENTRIES_LEAVE = new TranslatableComponent("professions.gui.no_entries.leave")
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
                this.width - 4,
                (this.height), // height
                (this.height / 2 - 81), // top
                (this.height / 2 + 76), // bottom
                button == CommandButtons.INFO ? 16 : 24);
        // column 1
        initWidget(new CommandButton(false, this.width / 2 - 24, this.height / 2 - 76,
                new TranslatableComponent("professions.gui.join"),
                button1 -> {
                    if (button != CommandButtons.JOIN) {
                        addPrevious(this);
                    }
                    CommonPlatform.platform.sendButtonPacket(CommandButtons.JOIN);
                }));
        initWidget(new CommandButton(false, this.width / 2 - 24, this.height / 2 - 76 + 20 + 3,
                new TranslatableComponent("professions.gui.leave"),
                button1 -> {
                    if (button != CommandButtons.LEAVE) {
                        addPrevious(this);
                    }
                    CommonPlatform.platform.sendButtonPacket(CommandButtons.LEAVE);
                }));
        // column 2
        initWidget(new CommandButton(false, this.width / 2 - 24 + 40, this.height / 2 - 76,
                new TranslatableComponent("professions.gui.top"),
                button1 -> {
                    if (button != CommandButtons.TOP) {
                        addPrevious(this);
                    }
                    CommonPlatform.platform.sendButtonPacket(CommandButtons.TOP);
                }));

        if (button != CommandButtons.INFO) {
            CommandButton infoButton = new CommandButton(true, this.width / 2 - 24 + 40, this.height / 2 - 76 + 20 + 3,
                    new TranslatableComponent("professions.gui.info"),
                    button1 -> {
                        addPrevious(this);
                        CommonPlatform.platform.getClientNetworking().attemptInfoPacket(professionHolder.getProfession().getKey());
                    });

            initWidget(infoButton);
            buttonsThatHide.add(infoButton);
        }

        // row 3
        initWidget(new CommandButton(false, this.width / 2 + 100 + (38 + 3) / 2, this.height / 2 - 86,
                new TranslatableComponent("professions.gui.close"),
                button1 -> this.minecraft.setScreen(null), true, 16, 16, CommandButton.SmallIcon.BAD));


        if (prevScreen != null) {
            initWidget(new CommandButton(false, this.width / 2 + 100 - 18 + (38 + 3) / 2, this.height / 2 - 86,
                    new TranslatableComponent("professions.gui.close"),
                    button1 -> {
                        Screen screen = prevScreen;
                        prevScreen = null;
                        this.minecraft.setScreen(screen);
                    }, true, 16, 16, CommandButton.SmallIcon.BACK));
        }

        this.addWidget(list);
        list.resetAndAddEntries(entries);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        int ofx = (this.width - imageWidth) / 2;
        int ofy = (this.height - imageHeight) / 2;
        renderOccupationWindow(poseStack, ofx, ofy);

        if (entries.size() == 0) {
            drawCenteredString(poseStack, font, NO_ENTRIES, ((this.width - imageWidth) / 2) + 83, ofy + 50, -1);
            if (button == CommandButtons.LEAVE) {
                drawCenteredString(poseStack, font, NO_ENTRIES_LEAVE, ((this.width - imageWidth) / 2) + 83, ofy + 70, -1);
            }
        }

        list.render(poseStack, mouseX, mouseY, partialTick);
        //super.render(poseStack, mouseX, mouseY, partialTick);
        for (Widget renderable : renderables) {
            if (renderable instanceof Hidden hidden && hidden.isHidden()) {
                continue;
            }
            renderable.render(poseStack, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int $$2) {
        Optional<GuiEventListener> element = list.getChildAt(mouseX, mouseY);
        if (element.isPresent()) {
            GuiEventListener listener = element.get();
            if (listener instanceof Selectable selectable) {
                selectable.setSelected(!selectable.isSelected());
                for (ProfessionsListingWidget.AbstractEntry child : list.children()) {
                    if (child.equals(selectable)) {
                        continue;
                    }
                    ((ProfessionsListingWidget.OccupationEntry) child).setSelected(false);
                }
                anyOccupationSelected = selectable.isSelected();
                if (selectable instanceof HoldsProfession profession) {
                    professionHolder = profession;
                }
                if (anyOccupationSelected) {
                    for (Hidden hidden : buttonsThatHide) {
                        hidden.setHidden(false);
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, $$2);
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


    public <T extends GuiEventListener & Widget & NarratableEntry> void initWidget(T widget) {
        addWidget(widget);
        this.renderables.add(widget);
    }

    @Override
    protected void clearWidgets() {
        super.clearWidgets();
        this.renderables.clear();
    }

    @Override
    public void onClose() {
        super.onClose();
        prevScreen = null;
    }

    @Override
    public void removed() {
        super.removed();
    }

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
            List<ActionDisplay.Icon> acts = new ArrayList<>(display.getActionInformation());
            for (List<ActionDisplay.Icon> icons : Lists.partition(acts, 5)) {
                entries.add(new ProfessionsListingWidget.InfoEntry(screen, screen.list, minecraft, icons));
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
