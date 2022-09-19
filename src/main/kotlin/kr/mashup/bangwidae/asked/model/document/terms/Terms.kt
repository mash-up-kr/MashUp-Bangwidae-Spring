package kr.mashup.bangwidae.asked.model.document.terms

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("terms")
data class Terms(
    @Id
    val id: ObjectId? = null,
    val title: String,
    val content: String,
    val deleted: Boolean = false,
    val priority: Int = 0,
    val isNecessary: Boolean = false,

    @Version
    var version: Int? = null,
    @CreatedDate var createdAt: LocalDateTime? = null,
    @LastModifiedDate var updatedAt: LocalDateTime? = null,
)
