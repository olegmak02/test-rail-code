package systems.ajax.codetests

import org.springframework.boot.runApplication
import systems.ajax.codetests.application.port.output.TestrailManagerOutPort

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val context = runApplication<CodeTestsApplication>(*args)
        val bean = context.getBean(TestrailManagerOutPort::class.java)
        bean.publish()
    }
}
