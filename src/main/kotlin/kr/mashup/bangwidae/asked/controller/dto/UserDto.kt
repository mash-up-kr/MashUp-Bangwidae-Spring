package kr.mashup.bangwidae.asked.controller.dto

import kr.mashup.bangwidae.asked.model.User
import kr.mashup.bangwidae.asked.service.question.QuestionDomain

data class UserInfoDto(
    val userId: String,
    val nickname: String?,
    val profileDescription: String?,
    val tags: List<String>,
    val level: Int,
    val profileImageUrl: String?
) {
    companion object {
        fun from(user: User): UserInfoDto {
            return UserInfoDto(
                userId = user.id!!.toHexString(),
                nickname = user.nickname,
                profileDescription = user.description,
                tags = user.tags,
                profileImageUrl = user.getUserProfileImageUrl(),
                level = user.level
            )
        }
    }
}

data class UserLinkShareInfoDto(
    val user: UserInfoDto,
    val representativeWardName: String,
    val questions: List<QuestionAndAnswerDto>,
) {
    data class UserInfoDto(
        val id: String,
        val nickname: String,
        val profileDescription: String,
        val tags: List<String>,
        val profileImageUrl: String,
        val level: Int,
    ) {
        companion object {
            fun from(user: User) = UserInfoDto(
                id = user.id!!.toHexString(),
                nickname = user.nickname!!,
                profileDescription = user.description ?: "",
                tags = user.tags,
                profileImageUrl = user.getUserProfileImageUrl(),
                level = user.level,
            )
        }
    }

    data class QuestionAndAnswerDto(
        val questionId: String,
        val questionContent: String,
        val answerContent: String,
    ) {
        companion object {
            fun from(question: QuestionDomain) = QuestionAndAnswerDto(
                questionId = question.id,
                questionContent = question.content,
                answerContent = question.answer!!.content,
            )
        }
    }

    companion object {
        fun from(user: User, questions: List<QuestionDomain>) = UserLinkShareInfoDto(
            user = UserInfoDto.from(user),
            representativeWardName = "todo - 우리집",
            questions = questions.map { QuestionAndAnswerDto.from(it) },
        )
    }
}

data class JoinUserRequest(
    val email: String,
    val password: String
)

data class JoinUserResponse(
    val accessToken: String,
    val refreshToken: String
)

data class UpdateNicknameRequest(
    val nickname: String
)

data class UpdateProfileRequest(
    val description: String,
    val tags: List<String>,
)

data class UserSettingsDto(
    val userId: String,
    val notification: Boolean,
    val nightNotification: Boolean,
    val locationInfo: Boolean,
) {
    companion object {
        fun from(user: User): UserSettingsDto {
            return UserSettingsDto(
                userId = user.id!!.toHexString(),
                notification = user.settings.notification,
                nightNotification = user.settings.nightNotification,
                locationInfo = user.settings.locationInfo,
            )
        }
    }
}

data class EditUserSettingsRequest(
    val notification: Boolean,
    val nightNotification: Boolean,
    val locationInfo: Boolean,
)