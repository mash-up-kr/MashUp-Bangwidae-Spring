package kr.mashup.bangwidae.asked.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import kr.mashup.bangwidae.asked.controller.dto.*
import kr.mashup.bangwidae.asked.model.User
import kr.mashup.bangwidae.asked.service.UserService
import kr.mashup.bangwidae.asked.service.question.QuestionService
import org.bson.types.ObjectId
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import springfox.documentation.annotations.ApiIgnore

@Api(tags = ["유저 컨트롤러"])
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
    private val questionService: QuestionService,
) {

    @ApiOperation("ID/PW 회원가입")
    @PostMapping("/join")
    fun joinUser(
        @RequestBody joinUserRequest: JoinUserRequest
    ): ApiResponse<JoinUserResponse> {
        return ApiResponse.success(userService.joinUser(joinUserRequest))
    }

    @ApiOperation("닉네임 설정")
    @PostMapping("/nickname")
    fun createNickname(
        @ApiIgnore @AuthenticationPrincipal user: User,
        @RequestBody updateNicknameRequest: UpdateNicknameRequest
    ): ApiResponse<Boolean> {
        return ApiResponse.success(userService.updateNickname(user, updateNicknameRequest.nickname))
    }

    @ApiOperation("프로필 설정")
    @PostMapping("/profile")
    fun createProfile(
        @ApiIgnore @AuthenticationPrincipal user: User,
        @RequestBody updateProfileRequest: UpdateProfileRequest
    ): ApiResponse<Boolean> {
        return ApiResponse.success(
            userService.updateProfile(
                user,
                updateProfileRequest.description,
                updateProfileRequest.tags
            )
        )
    }

    @ApiOperation("프로필 이미지 업로드")
    @PostMapping("/profile/image")
    fun profileImageUpload(
        @ApiIgnore @AuthenticationPrincipal user: User,
        @RequestParam image: MultipartFile
    ): ApiResponse<String> {
        return ApiResponse.success(userService.updateProfileImage(user, image))
    }

    // 우선 principal 동작 테스트 용도
    @ApiOperation("내 정보")
    @GetMapping("/me")
    fun getMyInfo(
        @ApiIgnore @AuthenticationPrincipal user: User
    ): ApiResponse<String> {
        return ApiResponse.success(user.nickname!!)
    }

    @ApiOperation("답변완료(본인)")
    @GetMapping("/answered-questions")
    fun getMyAnsweredQuestions(
        @ApiIgnore @AuthenticationPrincipal user: User,
        @RequestParam size: Int,
        @RequestParam(required = false) lastId: ObjectId?,
    ): ApiResponse<AnsweredQuestionsDto> {
        return questionService.findAnswerCompleteByToUser(
            user = user,
            lastId = lastId,
            size = size + 1,
        ).let {
            ApiResponse.success(
                AnsweredQuestionsDto.from(
                    questions = it.questions,
                    userMapByUserId = it.userMapByUserId,
                    answerMapByQuestionId = it.answerMapByQuestionId,
                    requestedSize = size,
                )
            )
        }
    }

    @ApiOperation("받은 질문(본인)")
    @GetMapping("/received-questions")
    fun getMyReceivedQuestions(
        @ApiIgnore @AuthenticationPrincipal user: User,
        @RequestParam size: Int,
        @RequestParam(required = false) lastId: ObjectId?,
    ): ApiResponse<ReceivedQuestionsDto> {
        return questionService.findAnswerWaitingByToUser(
            user = user,
            lastId = lastId,
            size = size + 1,
        ).let {
            ApiResponse.success(
                ReceivedQuestionsDto.from(
                    questions = it.questions,
                    userMapByUserId = it.userMapByUserId,
                    requestedSize = size,
                )
            )
        }
    }
}