package com.wutsi.telegram.entity

import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_SHARE")
data class ShareEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "site_id")
    val siteId: Long = -1,

    @Column(name = "story_id")
    val storyId: Long = -1,

    @Column(name = "telegram_message_id")
    val telegramMessageId: Long? = null,

    @Column(name = "telegram_chat_id")
    val telegramChatId: String = "",

    @Column(name = "share_date_time")
    val shareDateTime: OffsetDateTime = OffsetDateTime.now(),

    val success: Boolean = true,

    @Column(name = "error_code")
    val errorCode: Int? = null,

    @Column(name = "error_description")
    val errorMessage: String? = null
)
