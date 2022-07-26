package kr.mashup.bangwidae.asked.service.post

import kr.mashup.bangwidae.asked.controller.dto.PostDto
import kr.mashup.bangwidae.asked.controller.dto.PostEditRequest
import kr.mashup.bangwidae.asked.exception.DoriDoriException
import kr.mashup.bangwidae.asked.exception.DoriDoriExceptionType
import kr.mashup.bangwidae.asked.model.User
import kr.mashup.bangwidae.asked.model.post.Post
import kr.mashup.bangwidae.asked.model.post.PostLike
import kr.mashup.bangwidae.asked.repository.PostLikeRepository
import kr.mashup.bangwidae.asked.repository.PostRepository
import kr.mashup.bangwidae.asked.repository.UserRepository
import kr.mashup.bangwidae.asked.service.place.PlaceService
import kr.mashup.bangwidae.asked.utils.GeoUtils
import kr.mashup.bangwidae.asked.utils.getLatitude
import kr.mashup.bangwidae.asked.utils.getLongitude
import org.bson.types.ObjectId
import org.springframework.data.domain.PageRequest
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
    private val placeService: PlaceService,
    private val userRepository: UserRepository,
    private val postLikeRepository: PostLikeRepository
) : WithPostAuthorityValidator {
    fun findById(id: ObjectId): Post {
        return postRepository.findByIdAndDeletedFalse(id)
            ?: throw DoriDoriException.of(DoriDoriExceptionType.NOT_EXIST)
    }

    fun write(post: Post): Post {
        return postRepository.save(updatePlaceInfo(post))
    }

    fun edit(user: User, postId: ObjectId, request: PostEditRequest): Post {
        val post = findById(postId)
            .also { it.validateToUpdate(user) }
        return postRepository.save(updatePlaceInfo(post.update(request)))
    }

    fun delete(user: User, postId: ObjectId) {
        val post = findById(postId).also { it.validateToDelete(user) }
        postRepository.save(post.delete())
    }

    fun getNearPost(
        longitude: Double, latitude: Double, meterDistance: Double, lastId: ObjectId?, size: Int
    ): List<PostDto> {
        val location = GeoUtils.geoJsonPoint(longitude, latitude)
        val distance = Distance(meterDistance / 1000, Metrics.KILOMETERS)
        val postList = postRepository.findByLocationNearAndIdBeforeAndDeletedFalseOrderByIdDesc(
            location,
            lastId ?: ObjectId(),
            distance,
            PageRequest.of(0, size)
        )
        val userMap = userRepository.findAllByIdIn(postList.map { it.userId }).associateBy { it.id }
        return postList.map { PostDto.from(userMap[it.userId]!!, it) }
    }

    fun getPostById(id: ObjectId): PostDto {
        val post = findById(id)
        val user = userRepository.findByIdOrNull(post.userId)
            ?: throw DoriDoriException.of(DoriDoriExceptionType.POST_WRITER_USER_NOT_EXIST)
        return PostDto.from(user, post)
    }

    fun postLike(postId: ObjectId, userId: ObjectId) {
        require(postRepository.existsByIdAndDeletedFalse(postId)) {
            throw DoriDoriException.of(DoriDoriExceptionType.NOT_EXIST)
        }
        if (!postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            postLikeRepository.save(PostLike(userId = userId, postId = postId))
        }
    }

    fun postUnlike(postId: ObjectId, userId: ObjectId) {
        postLikeRepository.findByPostIdAndUserId(postId, userId)?.let {
            postLikeRepository.delete(it)
        }
    }

    private fun updatePlaceInfo(post: Post): Post {
        val longitude = post.location.getLongitude()
        val latitude = post.location.getLatitude()
        val region = placeService.reverseGeocode(longitude, latitude)
        val representativeAddress = placeService.getRepresentativeAddress(longitude, latitude)
        return post.copy(representativeAddress = representativeAddress, region = region)
    }
}