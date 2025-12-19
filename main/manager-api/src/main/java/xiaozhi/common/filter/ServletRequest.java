package xiaozhi.common.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;

/**
 * BaseServletRequest：BaseServletRequest
 *
 * @author zhangjie
 * @date 2025/12/19 下午4:10
 */
public class ServletRequest extends HttpServletRequestWrapper {

    private StringBuilder content = new StringBuilder();
    private boolean isUpload = false;

    public ServletRequest(HttpServletRequest request, boolean isUpload) throws IOException {
        super(request);
        if (isUpload) {
            return;
        }
        BufferedReader bufferread = request.getReader();
        CharBuffer bos = CharBuffer.allocate(20480);
        while(bufferread.read(bos) != -1) {
            bos.flip();
            content.append(bos);
        }
        bos.clear();
        bufferread.close();
    }

    public String getContent() {
        if (isUpload) {
            return "[Stream]";
        }
        return this.content.toString();
    }
    public String getFileName(Part part) {
        if (isUpload) {
            String head = part.getHeader("Content-Disposition");
            return head.substring(head.indexOf("filename=\"") + 10, head.lastIndexOf("\""));
        }
        return "";
    }


    @SneakyThrows
    @Override
    public BufferedReader getReader() {
        if (isUpload) {
            super.getReader();
        }
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
    @SneakyThrows
    @Override
    public ServletInputStream getInputStream() {
        if (isUpload) {
            super.getInputStream();
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(getContent().getBytes());
        return new ServletInputStream() {
            public int read() {
                return inputStream.read();
            }
            public boolean isFinished() {
                return false;
            }
            public boolean isReady() {
                return false;
            }
            public void setReadListener(ReadListener arg0) {
            }
        };
    }
}

