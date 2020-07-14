package com.wwdz;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

/**
 * @Author: cmj
 * @Description: CompletableFeature使用
 * https://javadoop.com/post/completable-future
 * @Date: 2020/7/14
 */
public class CompletableFeature {
    /**
     * 任务的顺序执行
     *
     * runAsync 方法接收的是 Runnable 的实例，意味着它没有返回值
     * supplyAsync 方法对应的是有返回值的情况
     * 这两个方法的带 executor 的变种，表示让任务在指定的线程池中
     */
    @Test
    public void piplineExcute() {

        //        任务 A 无返回值 第 2 行和第 3 行代码中，resultA 其实是 null

        CompletableFuture.runAsync(() -> {}).thenRun(() -> {});
        CompletableFuture.runAsync(() -> {}).thenAccept(resultA -> {});
        CompletableFuture.runAsync(() -> {}).thenApply(resultA -> "resultB");
        //        thenRun(Runnable runnable)，任务 A 执行完执行 B，并且 B 不需要 A 的结果。
        CompletableFuture.supplyAsync(() -> "resultA").thenRun(() -> {});
        //        thenAccept(Consumer action)，任务 A 执行完执行 B，B 需要 A 的结果，但是任务 B 不返回值。
        CompletableFuture.supplyAsync(() -> "resultA").thenAccept(resultA -> {});
        //        thenApply(Function fn)，任务 A 执行完执行 B，B 需要 org.checkerframework.checker.units.qual.A 的结果，同时任务 B 有返回值。
        CompletableFuture.supplyAsync(() -> "resultA").thenApply(resultA -> resultA + " resultB");

    }

    /**
     * 异常处理方法
     *
     * exceptionally  相当tryfinall
     * handle   根据异常分别处理
     */
    @Test
    public void handleException(){
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException();
        })
                .exceptionally(ex -> "errorResultA")
                .thenApply(resultA -> resultA + " resultB")
                .thenApply(resultB -> resultB + " resultC")
                .thenApply(resultC -> resultC + " resultD");

        System.out.println(future.join());


        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "resultA")
                .thenApply(resultA -> resultA + " resultB")
                // 任务 C 抛出异常
                .thenApply(resultB -> {throw new RuntimeException();})
                // 处理任务 C 的返回值或异常
                .handle((result, throwable) -> {
                    if (throwable != null) {
                        return "errorResultC";
                    }
                    return  result;
                })
                .thenApply(resultC -> resultC + " resultD");

        System.out.println(future2.join());
    }

    /**
     * 并行执行
     * runAfterBoth  两个任务的结果
     * thenAcceptBoth 表示后续的处理不需要返回值
     * henCombine 表示需要返回值。
     */
    @Test
    public  void parallel(){
        CompletableFuture<String> cfA = CompletableFuture.supplyAsync(() -> "resultA");
        CompletableFuture<String> cfB = CompletableFuture.supplyAsync(() -> "resultB");

        cfA.thenAcceptBoth(cfB, (resultA, resultB) -> {});
        cfA.thenCombine(cfB, (resultA, resultB) -> "result A + B");
        cfA.runAfterBoth(cfB, () -> {});
    }

    /**
     * 取多个任务的结果
     * allOf    多个feature都执行结束
     * anyOf    任意一个执行结束
     */
    @Test
    public void multipleResult(){

        CompletableFuture cfA = CompletableFuture.supplyAsync(() -> "resultA");
        CompletableFuture cfB = CompletableFuture.supplyAsync(() -> 123);
        CompletableFuture cfC = CompletableFuture.supplyAsync(() -> "resultC");

        CompletableFuture<Void> future = CompletableFuture.allOf(cfA, cfB, cfC);
        // 所以这里的 join() 将阻塞，直到所有的任务执行结束
        future.join();


        CompletableFuture cfA2 = CompletableFuture.supplyAsync(() -> "resultA");
        CompletableFuture cfB2 = CompletableFuture.supplyAsync(() -> 123);
        CompletableFuture cfC2 = CompletableFuture.supplyAsync(() -> "resultC");

        CompletableFuture<Object> future2 = CompletableFuture.anyOf(cfA2, cfB2, cfC2);
        future2.join();
    }

//    如果你的 anyOf(...) 只需要处理两个 CompletableFuture 实例，那么也可以使用 xxxEither() 来处理，


}
