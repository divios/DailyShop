package io.github.divios.dailyShop.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Exceptions {

    public static void tryTo(RunnableException runnable) {
        tryTo(() -> {
            runnable.run();
            return null;
        });
    }

    public static void tryTo(RunnableException runnable, String msg) {
        tryTo(() -> {
            runnable.run();
            return null;
        }, msg);
    }

    public static void tryTo(RunnableException runnable, Consumer<Exception> errHandler) {
        try {
            runnable.run();
        } catch (Exception err) {
            errHandler.accept(err);
        }
    }

    public static <T> T tryTo(SupplierException<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    public static <T> T tryTo(SupplierException<T> supplier, String msg) {
        try {
            return supplier.get();
        } catch (Exception err) {
            throw new RuntimeException(msg);
        }
    }


    @FunctionalInterface
    public interface RunnableException {
        void run() throws Exception;
    }

    @FunctionalInterface
    public interface SupplierException<K> {
        K get() throws Exception;
    }

}
