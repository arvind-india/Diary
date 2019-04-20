package com.sirionrazzer.diary.trackitem

import androidx.lifecycle.ViewModel
import com.sirionrazzer.diary.Diary
import com.sirionrazzer.diary.R
import com.sirionrazzer.diary.models.TrackItemTemplate
import com.sirionrazzer.diary.models.TrackItemTemplateDao
import io.realm.Realm
import java.util.*

class TemplateItemCreatorViewModel: ViewModel() {

    val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }


    var templateDao = TrackItemTemplateDao(realm)
    var hasChanged: Boolean = false

    lateinit var template: TrackItemTemplate

    init {
        Diary.app.appComponent.inject(this)

        createNewTemplate()
    }


    override fun onCleared() {
        realm.close()
        super.onCleared()
    }


    private fun createNewTemplate() {
        var position = templateDao.getAllTemplates().size

        template = TrackItemTemplate(
            UUID.randomUUID().toString(),
            false,
            "",
            R.drawable.diary_logo,
            R.drawable.diary_logo,
            hasTextField = false,
            hasNumberField = false,
            position = position.inc()
        )
    }


    fun saveNewTemplate() {
        templateDao.addTemplate(template)
    }
}