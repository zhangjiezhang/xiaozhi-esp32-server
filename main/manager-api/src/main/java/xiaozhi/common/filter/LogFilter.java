package xiaozhi.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * LogFilter：LogFilter
 *
 * @author zhangjie
 * @date 2025/12/19 下午4:07
 */
@Slf4j
public class LogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        printRequestLog(requestWrapper);
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            printResponseLog(responseWrapper, startTime);
        }
    }

    protected final void printRequestLog(ContentCachingRequestWrapper request) {
        String requestContent = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        StringBuilder responseStr = new StringBuilder();
        StringBuilder header = new StringBuilder();
        boolean one = true;
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            if (one) {
                one = false;
            } else {
                header.append(System.lineSeparator()).append("\t\t");
            }
            header.append(key).append("=").append(value).append("; ");
        }
        responseStr.append(System.lineSeparator());
        responseStr.append("-----------------------请求参数---------------------------------").append(System.lineSeparator());
        responseStr.append("request IP   :").append(getClientIpAddr(request)).append(System.lineSeparator());
        responseStr.append("URL          :").append(request.getRequestURL().toString()).append(System.lineSeparator());
        responseStr.append("URI          :").append(request.getRequestURI()).append(System.lineSeparator());
        responseStr.append("Method       :").append(request.getMethod()).append(System.lineSeparator());
        responseStr.append("Headers      :").append(header).append(System.lineSeparator());
        responseStr.append("Params       :").append(System.lineSeparator()).append(requestContent).append(System.lineSeparator());
        responseStr.append("--------------------------end---------------------------------").append(System.lineSeparator());
        log.info(responseStr.toString());
    }

    /**
     * 打印Response Log
     */
    protected final void printResponseLog(ContentCachingResponseWrapper response, Long startTime) {
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;
        String responseContent;
        if (isBinaryContent(response.getContentType())) {
            responseContent = "[ Binary Data / Image - Skipped Log ]";
        } else {
            responseContent = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        }
        StringBuilder header = new StringBuilder();
        boolean one = true;
        for (String key : response.getHeaderNames()) {
            String value = response.getHeader(key);
            if (one) {
                one = false;
            } else {
                header.append(System.lineSeparator()).append("\t\t");
            }
            header.append(key).append("=").append(value).append("; ");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        sb.append("-----------------------响应信息---------------------------------").append(System.lineSeparator());
        sb.append("Status       :").append(response.getStatus()).append(System.lineSeparator());
        sb.append("CostTime     :").append(executeTime).append("ms").append(System.lineSeparator());
        sb.append("Headers      :").append(header).append(System.lineSeparator());
        sb.append("Response     :").append(System.lineSeparator()).append(responseContent).append(System.lineSeparator());
        sb.append("---------------------------end----------------------------------").append(System.lineSeparator());
        log.info(sb.toString());
    }

    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private boolean isBinaryContent(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.contains("image") ||        // 图片 (image/png, image/jpeg)
                contentType.contains("video") ||       // 视频
                contentType.contains("audio") ||       // 音频
                contentType.contains("pdf") ||         // PDF
                contentType.contains("octet-stream");  // 二进制流 (文件下载)
    }
}
