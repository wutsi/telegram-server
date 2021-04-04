package com.wutsi.telegram.dao

import com.wutsi.telegram.entity.ShareEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ShareRepository : CrudRepository<ShareEntity, Long>
