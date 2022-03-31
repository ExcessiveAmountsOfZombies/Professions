package com.epherical.professions.commands;

import com.epherical.professions.config.ProfessionConfig;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class CommandUsage<S> {
    public static final String ARGUMENT_SEPARATOR = " ";
    private static final String USAGE_OPTIONAL_OPEN = "[";
    private static final String USAGE_OPTIONAL_CLOSE = "]";
    private static final String USAGE_REQUIRED_OPEN = "(";
    private static final String USAGE_REQUIRED_CLOSE = ")";
    private static final String USAGE_OR = "|";


    public static <S> Map<CommandNode<S>, MutableComponent> getSmartUsage(final CommandNode<S> node, final S source) {
        final Map<CommandNode<S>, MutableComponent> result = new LinkedHashMap<>();

        final boolean optional = node.getCommand() != null;
        for (final CommandNode<S> child : node.getChildren()) {
            final MutableComponent usage = getSmartUsage(child, source, optional, false);
            if (usage != null) {
                result.put(child, usage);
            }
        }
        return result;
    }

    private static <S> MutableComponent getSmartUsage(final CommandNode<S> node, final S source, final boolean optional, final boolean deep) {
        if (!node.canUse(source)) {
            return null;
        }

        final MutableComponent self = optional ? new TextComponent(USAGE_OPTIONAL_OPEN + node.getUsageText() + USAGE_OPTIONAL_CLOSE) : new TextComponent(node.getUsageText());
        final boolean childOptional = node.getCommand() != null;
        final String open = childOptional ? USAGE_OPTIONAL_OPEN : USAGE_REQUIRED_OPEN;
        final String close = childOptional ? USAGE_OPTIONAL_CLOSE : USAGE_REQUIRED_CLOSE;

        if (!deep) {
            if (node.getRedirect() != null) {
                final String redirect = node.getRedirect().getUsageText();
                return self.append(ARGUMENT_SEPARATOR + redirect).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors));
            } else {
                final Collection<CommandNode<S>> children = node.getChildren().stream().filter(c -> c.canUse(source)).toList();
                if (children.size() == 1) {
                    final MutableComponent usage = getSmartUsage(children.iterator().next(), source, childOptional, childOptional);
                    if (usage != null) {
                        return self.setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)).append(ARGUMENT_SEPARATOR).append(usage.setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)));
                    }
                } else if (children.size() > 1) {
                    final Set<MutableComponent> childUsage = new LinkedHashSet<>();
                    for (final CommandNode<S> child : children) {
                        final MutableComponent usage = getSmartUsage(child, source, childOptional, true);
                        if (usage != null) {
                            childUsage.add(usage);
                        }
                    }
                    if (childUsage.size() == 1) {
                        final MutableComponent usage = childUsage.iterator().next();
                        return self.append(ARGUMENT_SEPARATOR).append((childOptional
                                ? new TranslatableComponent(USAGE_OPTIONAL_OPEN + "%s" + USAGE_OPTIONAL_CLOSE, usage.setStyle(Style.EMPTY.withColor(ProfessionConfig.variables))): usage));
                    } else if (childUsage.size() > 1) {
                        final StringBuilder builder = new StringBuilder(open);
                        int count = 0;
                        for (final CommandNode<S> child : children) {
                            if (count > 0) {
                                builder.append(USAGE_OR);
                            }
                            builder.append(child.getUsageText());
                            count++;
                        }
                        if (count > 0) {
                            builder.append(close);
                            return self.append(ARGUMENT_SEPARATOR + builder);
                        }
                    }
                }
            }
        }
        return self.setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors));
    }
}
