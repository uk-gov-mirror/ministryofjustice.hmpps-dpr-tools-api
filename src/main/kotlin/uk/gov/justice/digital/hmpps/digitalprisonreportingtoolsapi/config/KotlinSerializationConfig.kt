package uk.gov.justice.digital.hmpps.digitalprisonreportingtoolsapi.config

import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KotlinSerializationConfig {

  @Bean
  fun kotlinxJson() = Json {
    prettyPrint = false
    allowComments = false
    allowTrailingComma = false
    allowStructuredMapKeys = false
    allowSpecialFloatingPointValues = false
    coerceInputValues = false
    decodeEnumsCaseInsensitive = true
    encodeDefaults = true
    explicitNulls = true
    ignoreUnknownKeys = false
    isLenient = false
  }
}
