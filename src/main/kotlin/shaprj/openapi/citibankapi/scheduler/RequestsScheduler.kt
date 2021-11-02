package shaprj.openapi.citibankapi.scheduler

import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import shaprj.openapi.citibankapi.util.processRequests


@EnableAsync
@EnableScheduling
@Component
open class RequestsScheduler {
    @Async
    @Scheduled(fixedRate = 30000)
    @Throws(InterruptedException::class)
    open fun scheduleRequestsSimple() {
        processRequests()
    }
}