package com.ivanovsky.passnotes.presentation.core.viewmodel

import com.ivanovsky.passnotes.presentation.core.BaseCellViewModel
import com.ivanovsky.passnotes.presentation.core.event.Event.Companion.toEvent
import com.ivanovsky.passnotes.presentation.core.event.EventProvider
import com.ivanovsky.passnotes.presentation.core.model.NotePropertyCellModel

class NotePropertyCellViewModel(
    override val model: NotePropertyCellModel,
    private val eventProvider: EventProvider
) : BaseCellViewModel(model) {

    fun onCopyButtonClicked() {
        eventProvider.send((COPY_BUTTON_CLICK_EVENT to model.value).toEvent())
    }

    companion object {
        val COPY_BUTTON_CLICK_EVENT = NotePropertyCellViewModel::class.qualifiedName + "_copyTextClickEvent"
    }
}