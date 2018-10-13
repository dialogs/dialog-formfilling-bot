package im.dlg.botsdk;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class DialogExecutor {
    private Executor executor;

    public DialogExecutor(Integer parallelism) {
        this.executor = Executors.newFixedThreadPool(parallelism);
    }

    <T> CompletableFuture<T> convert(ListenableFuture<T> fut) {
        CompletableFuture<T> res = new CompletableFuture<>();
        Futures.addCallback(fut, new FutureCallback<T>() {
            @Override
            public void onSuccess(@Nullable T result) {
                res.complete(result);
            }

            @Override
            public void onFailure(Throwable t) {
                res.completeExceptionally(t);
            }
        }, executor);
        return res;
    }

    public Executor getExecutor() {
        return executor;
    }
}
