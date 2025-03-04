package com.ivanovsky.passnotes.presentation.core.model

data class NotePropertyCellModel(
    override val id: String,
    val name: String,
    val value: String,
    val isCopyButtonVisible: Boolean
) : BaseCellModel()