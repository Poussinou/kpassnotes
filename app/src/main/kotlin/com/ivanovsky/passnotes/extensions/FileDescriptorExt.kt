package com.ivanovsky.passnotes.extensions

import com.ivanovsky.passnotes.data.entity.FSType
import com.ivanovsky.passnotes.data.entity.FileDescriptor
import com.ivanovsky.passnotes.data.entity.KeyType
import com.ivanovsky.passnotes.data.entity.UsedFile
import com.ivanovsky.passnotes.domain.ResourceProvider
import com.ivanovsky.passnotes.util.StringUtils.EMPTY

fun FileDescriptor.toUsedFile(
    addedTime: Long,
    lastAccessTime: Long? = null,
    keyType: KeyType = KeyType.PASSWORD
): UsedFile =
    UsedFile(
        fsAuthority = fsAuthority,
        filePath = path,
        fileUid = uid,
        fileName = name,
        addedTime = addedTime,
        lastAccessTime = lastAccessTime,
        keyType = keyType
    )

fun FileDescriptor.formatReadablePath(resourceProvider: ResourceProvider): String {
    return when (fsAuthority.type) {
        FSType.UNDEFINED -> {
            path
        }
        FSType.INTERNAL_STORAGE, FSType.EXTERNAL_STORAGE -> {
            path
        }
        FSType.WEBDAV -> {
            val url = fsAuthority.credentials?.formatReadableUrl() ?: EMPTY
            url + path
        }
        FSType.SAF -> {
            path
        }
        FSType.GIT -> {
            val url = fsAuthority.credentials?.formatReadableUrl() ?: EMPTY
            url + path
        }
    }
}