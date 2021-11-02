package shaprj.openapi.citibankapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class CitibankapiApplication

fun main(args: Array<String>) {
    runApplication<CitibankapiApplication>(*args)
}
