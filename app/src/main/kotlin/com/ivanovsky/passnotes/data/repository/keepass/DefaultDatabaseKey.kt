package com.ivanovsky.passnotes.data.repository.keepass

import com.ivanovsky.passnotes.data.entity.KeyType
import com.ivanovsky.passnotes.data.entity.OperationResult
import com.ivanovsky.passnotes.data.repository.encdb.EncryptedDatabaseKey

class DefaultDatabaseKey : EncryptedDatabaseKey {

    override val type: KeyType
        get() = throw IllegalStateException()

    override fun getKey(): OperationResult<ByteArray> {
        throw IllegalStateException()
    }
}