package marketMaster.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import marketMaster.handler.ChatWebSocketHandler;

@Configuration
@EnableWebSocket // 啟用WebSocket支持
@Profile("!test") // 指定這個配置在非測試環境下生效
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// 將其映射到 "/chat" 路徑
		registry.addHandler(new ChatWebSocketHandler(), "/chat").setAllowedOrigins("*");
	}

	@Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxSessionIdleTimeout(15 * 60 * 1000L); // 設置最大會話的閒置時間
        return container;
    }
}
