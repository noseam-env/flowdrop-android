package me.nelonn.flowdrop.app;

import androidx.annotation.NonNull;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanCallback {
    private final CountDownLatch countDownLatch;
    private final AtomicBoolean atomicBoolean;

    public AtomicBooleanCallback() {
        countDownLatch = new CountDownLatch(1);
        atomicBoolean = new AtomicBoolean(false);
    }

    public void set(boolean value) {
        atomicBoolean.set(value);
        countDownLatch.countDown();
    }

    public @NonNull Optional<Boolean> await(long timeout, TimeUnit unit) {
        try {
            if (!countDownLatch.await(timeout, unit)) {
                return Optional.empty();
            }
        } catch (InterruptedException e) {
            return Optional.empty();
        }
        return Optional.of(atomicBoolean.get());
    }
}
