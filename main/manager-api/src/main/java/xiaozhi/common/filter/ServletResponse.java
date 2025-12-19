package xiaozhi.common.filter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * ServletResponse：ServletResponse
 *
 * @author zhangjie
 * @date 2025/12/19 下午4:24
 */
@Slf4j
public class ServletResponse extends HttpServletResponseWrapper {

    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    public ServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(output, false, StandardCharsets.UTF_8);
    }
    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }
            @Override
            public void setWriteListener(WriteListener listener) {
            }
            @Override
            public void write(int b) {
                output.write(b);
            }
        };
    }

    @SneakyThrows
    public String getContent() {
        return output.toString(StandardCharsets.UTF_8);
    }
    public byte[] getStreamContent() {
        return output.toByteArray();
    }

}
