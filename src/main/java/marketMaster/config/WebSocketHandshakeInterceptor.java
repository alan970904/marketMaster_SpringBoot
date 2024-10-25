package marketMaster.config;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
        // 從URL中獲取employeeId參數
        String uri = request.getURI().toString();
        String employeeId = UriComponentsBuilder.fromUriString(uri)
                .build()
                .getQueryParams()
                .getFirst("employeeId");
        
        if (employeeId != null) {
            attributes.put("employeeId", employeeId);
            return true;
        }
		return false;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {

	}

}
