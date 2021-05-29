package com.ivanovsky.passnotes.domain.interactor.unlock

import com.ivanovsky.passnotes.data.entity.FileDescriptor
import com.ivanovsky.passnotes.data.entity.OperationError
import com.ivanovsky.passnotes.data.entity.OperationError.MESSAGE_RECORD_IS_ALREADY_EXISTS
import com.ivanovsky.passnotes.data.entity.OperationError.Type.NETWORK_IO_ERROR
import com.ivanovsky.passnotes.data.entity.OperationError.newDbError
import com.ivanovsky.passnotes.data.entity.OperationResult
import com.ivanovsky.passnotes.data.entity.UsedFile
import com.ivanovsky.passnotes.data.repository.EncryptedDatabaseRepository
import com.ivanovsky.passnotes.data.repository.UsedFileRepository
import com.ivanovsky.passnotes.data.repository.keepass.KeepassDatabaseKey
import com.ivanovsky.passnotes.domain.FileSyncHelper
import com.ivanovsky.passnotes.extensions.toFileDescriptor

class UnlockInteractor(
    private val fileRepository: UsedFileRepository,
    private val dbRepo: EncryptedDatabaseRepository,
    private val fileSyncHelper: FileSyncHelper
) {

    fun hasActiveDatabase(): Boolean {
        return dbRepo.isOpened
    }

    fun closeActiveDatabase(): OperationResult<Unit> {
        if (!dbRepo.isOpened) {
            return OperationResult.success(Unit)
        }

        val closeResult = dbRepo.close()
        return closeResult.takeStatusWith(Unit)
    }

    fun getRecentlyOpenedFiles(): OperationResult<List<FileDescriptor>> {
        return OperationResult.success(loadAndSortUsedFiles())
    }

    private fun loadAndSortUsedFiles(): List<FileDescriptor> {
        return fileRepository.getAll()
            .sortedByDescending { file -> file.lastAccessTime ?: file.addedTime }
            .map { file -> file.toFileDescriptor() }
    }

    fun openDatabase(key: KeepassDatabaseKey, file: FileDescriptor): OperationResult<Boolean> {
        val result = OperationResult<Boolean>()

        var syncError: OperationError? = null
        val locallyModifiedFile = fileSyncHelper.getModifiedFileByUid(file.uid, file.fsAuthority)
        if (locallyModifiedFile != null) {
            val syncResult = fileSyncHelper.resolve(locallyModifiedFile)
            if (syncResult.isFailed && syncResult.error.type != NETWORK_IO_ERROR) {
                syncError = syncResult.error
            }
        }

        if (syncError == null) {
            val openResult = dbRepo.open(key, file)
            if (openResult.isSucceededOrDeferred) {
                updateFileAccessTime(file)
                result.obj = true
            } else {
                result.error = openResult.error
            }
        } else {
            result.error = syncError
        }

        return result
    }

    private fun updateFileAccessTime(file: FileDescriptor) {
        val usedFile = fileRepository.findByUid(file.uid, file.fsAuthority)
        if (usedFile != null) {
            val updatedFile = usedFile.copy(
                lastAccessTime = System.currentTimeMillis()
            )

            fileRepository.update(updatedFile)
        }
    }

    fun saveUsedFileWithoutAccessTime(file: UsedFile): OperationResult<Boolean> {
        val result = OperationResult<Boolean>()

        val existing = fileRepository.findByUid(file.fileUid, file.fsAuthority)
        if (existing == null) {
            val newFile = file.copy(
                lastAccessTime = null
            )

            fileRepository.insert(newFile)

            result.obj = true
        } else {
            result.obj = false
            result.error = newDbError(MESSAGE_RECORD_IS_ALREADY_EXISTS)
        }

        return result
    }
}
