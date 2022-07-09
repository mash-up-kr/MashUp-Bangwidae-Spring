package kr.mashup.bangwidae.asked.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import kr.mashup.bangwidae.asked.controller.dto.ApiResponse
import kr.mashup.bangwidae.asked.controller.dto.CreateWardRequest
import kr.mashup.bangwidae.asked.controller.dto.WardDto
import kr.mashup.bangwidae.asked.model.User
import kr.mashup.bangwidae.asked.service.WardService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

@Api(tags = ["와드 컨트롤러"])
@RestController
@RequestMapping("/api/v1/ward")
class WardController(
    private val wardService: WardService
) {
    @ApiOperation("와드 심기")
    @PostMapping
    fun createWard(
        @ApiIgnore @AuthenticationPrincipal user: User,
        @RequestBody createWardRequest: CreateWardRequest
    ): ApiResponse<Boolean> {
        return ApiResponse.success(
            wardService.createWard(
                user = user,
                name = createWardRequest.name,
                longitude = createWardRequest.longitude,
                latitude = createWardRequest.latitude
            )
        )
    }

    @ApiOperation("내 와드")
    @GetMapping
    fun getMyWards(
        @ApiIgnore @AuthenticationPrincipal user: User
    ): ApiResponse<List<WardDto>> {
        return ApiResponse.success(
            wardService.getMyWards(user)
                .map { WardDto.from(it) }
        )
    }
}