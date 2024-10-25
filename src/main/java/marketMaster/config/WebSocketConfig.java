package marketMaster.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import marketMaster.handler.ChatWebSocketHandler;

@Configuration
@EnableWebSocket // 啟用WebSocket支持
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// 註冊後台聊天的 WebSocket 處理器
		registry.addHandler(chatWebSocketHandler, "/chat").setAllowedOrigins("*").addInterceptors(new WebSocketHandshakeInterceptor());
		// 註冊前台聊天的 WebSocket 處理器
		registry.addHandler(chatWebSocketHandler, "/front/chat").setAllowedOrigins("*").addInterceptors(new WebSocketHandshakeInterceptor());
	}

	@Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        container.setMaxSessionIdleTimeout(15 * 60 * 1000L); // 設置最大會話的閒置時間
        return container;
    }
}
