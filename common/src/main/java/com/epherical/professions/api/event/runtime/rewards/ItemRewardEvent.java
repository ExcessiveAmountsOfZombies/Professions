package com.epherical.professions.api.event.runtime.rewards;


import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.api.actions.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ItemRewardEvent extends RewardEvent {

    public static final EventKey<OccupationExperienceEvent> KEY =
            new EventKey<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "reward_item"), OccupationExperienceEvent.class);


    private final ItemStack rewardCopy;

    private ItemStack newReward;

    public ItemRewardEvent(Occupation occupation, Action<?> action, ProfessionContext context, ItemStack rewardCopy) {
        super(KEY, occupation, action, context);
        this.rewardCopy = rewardCopy;
        this.newReward = rewardCopy;
    }


    public ItemStack getRewardCopy() {
        return rewardCopy;
    }

    public void setRewardCopy(ItemStack rewardCopy) {
        this.newReward = rewardCopy;
    }

    public ItemStack getNewReward() {
        return newReward;
    }
}
