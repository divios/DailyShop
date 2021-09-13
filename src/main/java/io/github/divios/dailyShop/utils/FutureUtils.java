package io.github.divios.dailyShop.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FutureUtils {

    public static <T> T waitFor(CompletableFuture<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
