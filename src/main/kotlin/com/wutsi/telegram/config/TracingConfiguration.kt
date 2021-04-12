package com.wutsi.telegram.config

import com.wutsi.tracing.DynamicTracingContext
import com.wutsi.tracing.TracingContext
import feign.RequestInterceptor
import org.springframework.beans.factory.`annotation`.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.`annotation`.Bean
import org.springframework.context.`annotation`.Configuration
import javax.servlet.Filter

@Configuration
public class TracingConfiguration(
    @Autowired
    private val context: ApplicationContext
) {
    @Bean
    public fun tracingFilter(): Filter = com.wutsi.tracing.TracingFilter(tracingContext())

    @Bean
    public fun tracingContext(): TracingContext = DynamicTracingContext(context)

    @Bean
    public fun tracingRequestInterceptor(): RequestInterceptor =
        com.wutsi.tracing.TracingRequestInterceptor("telegram-server", tracingContext())
}
