package com.epherical.professions.presentation.model;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record GateDisplay<T>(@Nullable Holder<T> holder, ItemStack icon, Component name) {}
