package com.epherical.professions.client.screen;

import com.epherical.professions.client.widgets.OccupationsList;
import com.epherical.professions.client.widgets.CommandButton;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.networking.ClientHandler;
import com.epherical.professions.networking.CommandButtons;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OccupationScreen extends Screen {

    public static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("professions", "textures/gui/occupation_menu.png");
    protected int imageWidth = 256;
    protected int imageHeight = 170;

    @Nullable
    private Screen prevScreen;
    private OccupationsList list;
    private CommandButtons button;
    private List<OccupationsList.AbstractEntry> entries;

    private final MutableComponent NO_ENTRIES = new TranslatableComponent("professions.gui.no_entries")
            .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables));
    private final MutableComponent NO_ENTRIES_LEAVE = new TranslatableComponent("professions.gui.no_entries.leave")
            .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables));

    public OccupationScreen(List<Profession> list, CommandButtons buttons) {
        super(Component.nullToEmpty(""));
        // todo: create better constructor at some point.
        this.button = buttons;
        this.entries = createProfessionEntries(this, list);
    }

    public OccupationScreen(List<Occupation> occupations) {
        super(Component.nullToEmpty(""));
        this.button = null;
        this.entries = createOccupationEntries(this, occupations);
    }

    public OccupationScreen addPrevious(Screen prevScreen) {
        this.prevScreen = prevScreen;
        return this;
    }

    @Override
    protected void init() {
        super.init();
        this.list = new OccupationsList(this, minecraft,
                this.width,
                (this.height), // height
                (this.height / 2 - 85), // top
                (this.height / 2 + 76), // bottom
                24);
        // row 1
        addRenderableWidget(new CommandButton(new ItemStack(Items.EMERALD), this.width / 2 + 43, this.height / 2 - 80, new TranslatableComponent("professions.gui.join"), button1 -> {
            ClientHandler.sendButtonPacket(CommandButtons.JOIN);
        }));
        addRenderableWidget(new CommandButton(new ItemStack(Items.REDSTONE), this.width / 2 + 43 + 38 + 3, this.height / 2 - 80,new TranslatableComponent("professions.gui.leave"), button1 -> {
            ClientHandler.sendButtonPacket(CommandButtons.LEAVE);
        }));
        //row 2
        addRenderableWidget(new CommandButton(new ItemStack(Items.BOOK), this.width / 2 + 43, this.height / 2 - 80 + 48 + 3, new TranslatableComponent("professions.gui.info"), button1 -> {
            ClientHandler.sendButtonPacket(CommandButtons.INFO);
        }));
        addRenderableWidget(new CommandButton(new ItemStack(Items.COMPARATOR), this.width / 2 + 43 + 38 + 3, this.height / 2 - 80 + 48 + 3,  new TranslatableComponent("professions.gui.top"), button1 -> {
            ClientHandler.sendButtonPacket(CommandButtons.TOP);
        }));
        // row 3
        addRenderableWidget(new CommandButton(new ItemStack(Items.BARRIER), this.width / 2 + 43 + (38 + 3) / 2, this.height / 2 - 80 + (48 + 3) * 2,  new TranslatableComponent("professions.gui.close"), button1 -> {
            this.minecraft.setScreen(prevScreen);
        }));
        this.addWidget(list);
        list.reset(entries);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        //System.out.println(this.width);
        int ofx = (this.width - imageWidth) / 2;
        int ofy = (this.height - imageHeight) / 2;
        renderOccupationWindow(poseStack, ofx, ofy, mouseX, mouseY);


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
            OccupationsList.AbstractEntry entry = (OccupationsList.AbstractEntry) element.get();
            entry.getButton().renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    public void renderOccupationWindow(PoseStack stack, int offsetX, int offsetY, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WINDOW_LOCATION);
        this.blit(stack, offsetX, offsetY, 0, 0, imageWidth, imageHeight);
        RenderSystem.setShaderColor(1.0F, 0.0F, 1.0F, 1.0F);
    }

    private int adjustedHeight() {
        return (Math.max(52, this.height - 128 - 15) / 2);
    }

    private int scrollHeight() {
        return 80 + adjustedHeight() * 16 - 8;
    }

    private int centeredWidth() {
        return (this.width - 256) / 2;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public CommandButtons getButton() {
        return button;
    }

    public static List<OccupationsList.AbstractEntry> createOccupationEntries(OccupationScreen screen, List<Occupation> occupations) {
        List<OccupationsList.AbstractEntry> entries = new ArrayList<>();
        for (Occupation occupation : occupations) {
            entries.add(new OccupationsList.OccupationEntry(screen, screen.list, screen.minecraft, occupation));
        }
        return entries;
    }

    public static List<OccupationsList.AbstractEntry> createProfessionEntries(OccupationScreen screen, List<Profession> professions) {
        List<OccupationsList.AbstractEntry> entries = new ArrayList<>();
        for (Profession profession : professions) {
            entries.add(new OccupationsList.ProfessionEntry(screen, screen.list, screen.minecraft, profession));
        }
        return entries;
    }
}
