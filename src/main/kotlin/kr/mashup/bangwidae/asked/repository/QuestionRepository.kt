package kr.mashup.bangwidae.asked.repository

import kr.mashup.bangwidae.asked.model.question.Question
import kr.mashup.bangwidae.asked.model.question.QuestionStatus
import org.bson.types.ObjectId
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuestionRepository : MongoRepository<Question, ObjectId> {
    fun findByIdAndDeletedFalse(id: ObjectId): Question?

    fun findByToUserIdAndStatusAndIdBeforeAndDeletedFalseOrderByCreatedAtDesc(
        toUserId: ObjectId,
        status: QuestionStatus,
        lastId: ObjectId,
        pageRequest: PageRequest,
    ): List<Question>
}
