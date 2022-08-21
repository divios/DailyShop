package io.github.divios.dailyShop.hooks;

import com.solodevelopment.tokens.Tokens;
import io.github.divios.core_lib.utils.Log;
import org.jetbrains.annotations.Nullable;

public class TokenGCHook implements Hook<Tokens> {

    private Tokens api = null;
    private boolean isHooked = false;

    TokenGCHook() {
        tryToHook();
    }

    private void tryToHook() {
        try {
            api = Tokens.getInstance();
            Log.info("Hooked to TokensApi by GC");
            isHooked = true;
        } catch (Exception ignored) {}
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Nullable
    @Override
    public Tokens getApi() {
        return api;
    }
}
