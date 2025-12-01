package kr.geun.oss.dowoomi.config

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * SQLite TEXT <-> LocalDate 변환기
 */
@Converter(autoApply = true)
class LocalDateConverter : AttributeConverter<LocalDate?, String?> {

    override fun convertToDatabaseColumn(attribute: LocalDate?): String? {
        return attribute?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    override fun convertToEntityAttribute(dbData: String?): LocalDate? {
        return dbData?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
    }
}

/**
 * SQLite TEXT <-> LocalDateTime 변환기
 */
@Converter(autoApply = true)
class LocalDateTimeConverter : AttributeConverter<LocalDateTime?, String?> {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun convertToDatabaseColumn(attribute: LocalDateTime?): String? {
        return attribute?.format(formatter)
    }

    override fun convertToEntityAttribute(dbData: String?): LocalDateTime? {
        return dbData?.let { LocalDateTime.parse(it, formatter) }
    }
}
