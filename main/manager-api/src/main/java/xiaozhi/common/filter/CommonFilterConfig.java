package xiaozhi.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FilterConfig：FilterConfig
 *
 * @author zhangjie
 * @date 2025/12/19 下午4:45
 */
@Slf4j
@Configuration
public class CommonFilterConfig {

    @Bean
    LogFilter logFilter() {
        return new LogFilter();
    }

    @Bean
    FilterRegistrationBean<LogFilter> logFilterBean(LogFilter logFilter) {
        FilterRegistrationBean<LogFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(logFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setName("logFilter");
        filterRegistrationBean.setOrder(0);
        log.info("filter[{}] {} pathPatterns:[{}]", 0, LogFilter.class.getName(), "/*");
        return filterRegistrationBean;
    }
}
