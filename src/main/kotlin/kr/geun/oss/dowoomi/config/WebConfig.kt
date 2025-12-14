package kr.geun.oss.dowoomi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver
import java.nio.charset.StandardCharsets
import kotlin.text.startsWith

/**
 * WebConfig 클래스 - Spring Boot + React 통합 웹 설정
 *
 * 중요: CORS 설정 금지!
 * ==================
 * 이 애플리케이션은 Spring Boot와 React가 동일한 서버(포트 10220)에서
 * 통합되어 실행되므로 CORS(Cross-Origin Resource Sharing) 설정이 불필요하며,
 * 오히려 문제를 일으킬 수 있습니다.
 *
 * CORS를 추가하지 않는 이유:
 * 1. Same-Origin: 프론트엔드와 백엔드가 같은 origin(http://localhost:10220)에서 실행
 * 2. 보안: 불필요한 CORS 설정은 보안 취약점이 될 수 있음
 * 3. 성능: CORS preflight 요청 등의 오버헤드 제거
 * 4. 단순성: 설정 복잡도 감소
 *
 * CORS 관련 메서드들을 추가하지 마세요:
 * - addCorsMappings()
 * - corsConfigurationSource()
 * - CorsConfiguration 관련 Bean
 */

@Configuration
class WebConfig() : WebMvcConfigurer {

  /**
   * NOTE: CORS 설정은 제거됨
   * 이유: Spring Boot와 React가 동일한 서버(포트 10220)에서 제공되므로 CORS가 불필요함
   * 프론트엔드와 백엔드가 같은 origin에서 실행되어 Same-Origin Policy 적용 안됨
   */
  override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
    val stringConverter = StringHttpMessageConverter(StandardCharsets.UTF_8)
    stringConverter.setWriteAcceptCharset(false)
    converters.add(0, stringConverter)
    super.configureMessageConverters(converters)
  }

  // React Router 브라우저 히스토리 지원을 위한 설정
  override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
    registry.addResourceHandler("/**")
      .addResourceLocations("classpath:/static/")
      .resourceChain(true)
      .addResolver(object : PathResourceResolver() {
        override fun getResource(resourcePath: String, location: Resource): Resource? {
          val requestedResource = location.createRelative(resourcePath)

          // 실제 파일이 존재하면 그대로 반환
          if (requestedResource.exists() && requestedResource.isReadable) {
            return requestedResource
          }

          // API 경로가 아니고 실제 파일이 없으면 index.html로 포워딩 (SPA 라우팅)
          return if (!resourcePath.startsWith("/api/")) {
            ClassPathResource("/static/index.html")
          } else {
            null
          }
        }
      })

    // PWA 매니페스트 및 서비스 워커 파일에 대한 설정 추가
//    registry.addResourceHandler("/manifest.json",)
//      .addResourceLocations("classpath:/static/")

    // PWA 아이콘을 위한 리소스 핸들러 추가
    registry.addResourceHandler("/icons/**")
      .addResourceLocations("classpath:/static/icons/")

//    registry.addResourceHandler("/uploads/**")
//      .addResourceLocations("file:$uploadDirectory/")
//      .setCachePeriod(3600) // 1시간 캐시
  }
}
