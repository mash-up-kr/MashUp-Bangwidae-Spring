package kr.mashup.bangwidae.asked.model.post

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("post")
data class Post(
	val id: ObjectId? = null,
	val content: String = "",
	@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
	val location: GeoJsonPoint,
	val representativeAddress: String?,

	@CreatedDate
	val createdAt: LocalDateTime = LocalDateTime.now(),
	@LastModifiedDate
	val updatedAt: LocalDateTime = LocalDateTime.now()
)