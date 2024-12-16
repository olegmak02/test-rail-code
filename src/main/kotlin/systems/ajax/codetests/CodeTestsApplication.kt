package systems.ajax.codetests

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CodeTestsApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<CodeTestsApplication>(*args)
}
