package com.ivanovsky.passnotes.domain.usecases

import android.content.Context
import com.ivanovsky.passnotes.data.entity.OperationResult
import com.ivanovsky.passnotes.data.repository.EncryptedDatabaseRepository
import com.ivanovsky.passnotes.domain.DatabaseLockInteractor
import com.ivanovsky.passnotes.domain.entity.DatabaseStatus
import com.ivanovsky.passnotes.injection.GlobalInjector.inject
import com.ivanovsky.passnotes.presentation.service.LockService
import com.ivanovsky.passnotes.presentation.service.model.LockServiceCommand

class LockDatabaseUseCase {

    private val dbRepository: EncryptedDatabaseRepository by inject()
    private val lockInteractor: DatabaseLockInteractor by inject()
    private val context: Context by inject()

    fun lockIfNeed(): OperationResult<Unit> {
        val db = dbRepository.database ?: return OperationResult.success(Unit)

        val status = db.status
        if (status == DatabaseStatus.POSTPONED_CHANGES) {
            LockService.runCommand(context, LockServiceCommand.SyncAndLock(db.file))
            return OperationResult.success(Unit)
        }

        val close = dbRepository.close()
        if (close.isFailed) {
            return close.takeError()
        }

        lockInteractor.stopServiceIfNeed()

        return close.takeStatusWith(Unit)
    }
}