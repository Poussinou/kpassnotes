package com.ivanovsky.passnotes.domain.usecases

import com.ivanovsky.passnotes.data.entity.OperationResult
import com.ivanovsky.passnotes.data.repository.EncryptedDatabaseRepository
import com.ivanovsky.passnotes.injection.GlobalInjector.inject

class DatabaseLockUseCase {

    private val dbRepository: EncryptedDatabaseRepository by inject()

    fun lockIfNeed(): OperationResult<Unit> {
        if (!dbRepository.isOpened) {
            return OperationResult.success(Unit)
        }

        val close = dbRepository.close()
        if (close.isFailed) {
            return close.takeError()
        }

        return close.takeStatusWith(Unit)
    }
}