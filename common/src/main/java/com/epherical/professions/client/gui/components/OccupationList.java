package com.epherical.professions.client.gui.components;

import com.epherical.professions.CommonClass;
import com.epherical.professions.Constants;
import com.epherical.professions.core.Profession;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class OccupationList extends AbstractOccupationSelector<OccupationList.Entry> {


    public OccupationList(Minecraft mc, int width, int top, int bottom, int height) {
        super(mc, width, top, bottom, height);

        mc.getSingleplayerServer().registryAccess().lookupOrThrow(CommonClass.PROFESSION_REGISTRY_KEY)
                .listElements().forEach(professionReference -> addEntry(new Entry(professionReference.value())));
    }



    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        private static final ResourceLocation TEXTURE = ResourceLocation
                .fromNamespaceAndPath(Constants.MOD_ID, "occupation/occupation_menu_profession_button_enabled");


        Profession profession;

        public Entry(Profession profession) {
            this.profession = profession;
        }



        @Override
        public void render(GuiGraphics gfx,
                           int index, int y, int x, int rowWidth, int rowHeight,
                           int mouseX, int mouseY, boolean hovering, float partialTick) {



            gfx.drawString(minecraft.font, Component.literal(profession.displayNameRaw()).withStyle(Style.EMPTY.withColor(profession.professionColor())), x + 4, y + 3, 0xFFFFFF);

            float s = 0.5f;

            int tx = x + 4;
            int ty = y + 22;

            gfx.pose().pushPose();
            gfx.pose().translate(tx, ty, 0);
            gfx.pose().scale(s, s, 1);
            gfx.pose().translate(-tx, -ty, 0);
            gfx.drawString(minecraft.font, "LvL 25", tx, ty, 0xFFFFFF);
            gfx.pose().popPose();



            gfx.blitSprite(TEXTURE, 94, 32, 0, 0, x, y, rowWidth, 32);

            //System.out.println(hovering);

            //gfx.blitSprite(TEXTURE, x,y ,rowWidth , rowHeight);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }
    }
}
