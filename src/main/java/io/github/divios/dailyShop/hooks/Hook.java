package io.github.divios.dailyShop.hooks;

import org.jetbrains.annotations.Nullable;

interface Hook<T> {

    boolean isOn();
    @Nullable T getApi();

}
