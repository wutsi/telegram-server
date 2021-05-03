package com.wutsi.telegram.endpoint

import com.wutsi.telegram.`delegate`.ShareDelegate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class ShareController(
    private val `delegate`: ShareDelegate
) {
    @GetMapping("/v1/telegram/share")
    @PreAuthorize(value = "hasAuthority('telegram')")
    public fun invoke(@RequestParam(name = "story-id", required = false) storyId: Long) {
        delegate.invoke(storyId)
    }
}
