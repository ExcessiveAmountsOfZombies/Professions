package com.epherical.professions.model.perks;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.platform.Services;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.Occupation;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.DoubleBinaryOperator;

public abstract class Perk {

    private static final Codec<Holder<Profession>> PROFESSION_CODEC = RegistryFixedCodec.create(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
            .mapResult(new Codec.ResultFunction<>() {
                @Override
                public <E> DataResult<Pair<Holder<Profession>, E>> apply(DynamicOps<E> ops, E input, DataResult<Pair<Holder<Profession>, E>> result) {
                    return result.mapError(Perk::explainProfessionDecodeError);
                }

                @Override
                public <E> DataResult<E> coApply(DynamicOps<E> ops, Holder<Profession> input, DataResult<E> result) {
                    return result.mapError(Perk::explainProfessionDecodeError);
                }
            });

    public static final Codec<Perk> TYPED_CODEC = Services.PLATFORM.getPerkTypeRegistry().byNameCodec().dispatch(
            "perk", Perk::getType, PerkType::codec);

    public static final MapCodec<Common> COMMON = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    PROFESSION_CODEC.fieldOf("profession").forGetter(Common::profession),
                    Codec.DOUBLE.fieldOf("amount").forGetter(Common::amount),
                    ModificationStage.CODEC.fieldOf("modificationStage").forGetter(Common::modificationStage),
                    Codec.STRING.fieldOf("description").forGetter(Common::description),
                    Codec.STRING.fieldOf("title").forGetter(Common::title),
                    ResourceLocation.CODEC.optionalFieldOf("textureIcon").forGetter(Common::textureIcon),
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("itemIcon").forGetter(Common::itemIcon),
                    Codec.INT.fieldOf("levelRequirement").forGetter(Common::levelRequirement)
            ).apply(i, Common::new)
    );

    private final Holder<Profession> profession;
    private final double amount;
    private final ModificationStage modificationStage;
    private final String description;
    private final String title;
    private final Optional<ResourceLocation> textureIcon;
    private final Item itemIcon;
    private final int levelRequirement;
    private @Nullable ResourceLocation fileId;

    protected Perk(Common common) {
        this.profession = common.profession;
        this.amount = common.amount;
        this.modificationStage = common.modificationStage;
        this.description = common.description;
        this.textureIcon = common.textureIcon;
        this.itemIcon = common.itemIcon;
        this.levelRequirement = common.levelRequirement;
        this.title = common.title;
    }


    public abstract PerkType getType();

    public abstract Common buildCommon();

    public ModificationStage getModificationStage() {
        return modificationStage;
    }

    public double getValue() {
        return amount;
    }

    public Applicator applicator(ServerPlayer serverPlayer) {
        return (stage, startingValue, accumulatedBonuses) -> startingValue;
    }

    public void onActivate(Occupation occupation, IProfessionalPlayer player, ServerPlayer serverPlayer) {

    }

    public void onDeactivate(Occupation occupation, IProfessionalPlayer player, ServerPlayer serverPlayer) {

    }

    /**
     * Recalculates the status of a perk based on the provided occupation, player,
     * and server player context. This method evaluates necessary conditions and
     * updates the perk's validity status.
     *
     * @param occupation    The occupation associated with the player's current role or profession.
     * @param player        The professional player instance relevant to the recalculation.
     * @param serverPlayer  The context of the server-side player relevant to the operation.
     * @return The result of the recalculation, which is a {@link PerkStatus} indicating whether the perk is VALID or INVALID.
     */
    public PerkStatus onRecalculate(Occupation occupation, IProfessionalPlayer player, ServerPlayer serverPlayer) {
        return PerkStatus.VALID;
    }

    public Holder<Profession> getProfession() {
        return profession;
    }

    public String getDescription() {
        return description;
    }

    public  Optional<ResourceLocation> getTextureIcon() {
        return textureIcon;
    }

    public Item getItemIcon() {
        return itemIcon;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public @Nullable ResourceLocation getId() {
        return fileId;
    }

    public String getTitle() {
        return title;
    }

    public void setId(ResourceLocation fileId) {
        if (this.fileId != null && !this.fileId.equals(fileId)) {
            throw new IllegalStateException("Perk file id already set to " + this.fileId + ", cannot reset to " + fileId);
        }
        this.fileId = fileId;
    }

    public record Common(Holder<Profession> profession, double amount, ModificationStage modificationStage, String description, String title,
                         Optional<ResourceLocation> textureIcon, Item itemIcon, int levelRequirement) {
        public static Common build(Perk perk) {
            return new Common(perk.getProfession(), perk.getValue(), perk.getModificationStage(), perk.getDescription(), perk.getTitle(),
                    perk.getTextureIcon(), perk.getItemIcon(), perk.getLevelRequirement());
        }
    }

    private static String explainProfessionDecodeError(String error) {
        return error + ". Failed to decode perk field 'profession'; the referenced profession is probably missing, disabled, or not loaded yet.";
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Perk perk = (Perk) o;
        return Objects.equals(fileId, perk.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fileId);
    }

    public enum PerkStatus {
        VALID,
        INVALID;
    }

    public interface Applicator {
        double apply(ModificationStage stage, double startingValue, double accumulatedBonuses);
    }

    public enum ModificationStage implements StringRepresentable {
        FLAT("flat", 1000, Double::sum),
        ADDITIVE("additive", 2000, (given, operand) -> given * (1 + operand)),
        MULTIPLICATIVE("multiplicative", 3000, (given, operand) -> given * operand);

        public static final Codec<ModificationStage> CODEC = StringRepresentable.fromEnum(ModificationStage::values);



        private final String serializedName;
        private final int priority;
        private final DoubleBinaryOperator operation;

        ModificationStage(String serializedName, int priority, DoubleBinaryOperator operation) {
            this.serializedName = serializedName;
            this.priority = priority;
            this.operation = operation;
        }

        public int priority() {
            return priority;
        }

        public double apply(double given, double operand) {
            return operation.applyAsDouble(given, operand);
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }
}
