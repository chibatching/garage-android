package com.sys1yagi.android.garage.core.executor

import com.sys1yagi.android.garage.core.request.GarageRequest
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

// キューの並べ替え
// キューからのRequestの除去条件を知っている
class Executor() {

    //TODO ordered queue
    val queue = LinkedBlockingDeque<Runnable>()

    val threadExecutor = ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            queue);

    fun enqueue(request: GarageRequest) {
        threadExecutor.execute(object : Runnable {
            override fun run() {
                request.execute(
                        { response ->
                            request.invoker?.callbackSuccess?.invoke(response)
                        },
                        { error ->
                            request.invoker?.callbackFailed?.invoke(error)
                        }
                )
            }
        })
    }

    fun stop() {
        threadExecutor.shutdown()
    }
}
