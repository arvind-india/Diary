package com.sirionrazzer.diary.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class TrackItemTemplate(
    @PrimaryKey
    @Required
    open var id: String,
    open var deleted: Boolean,
    open var name: String,
    open var image: Int,
    open var hasTextField: Boolean,
    open var hasNumberField: Boolean,
    open var position: Int // relative position in layout, for displaying items in some order
) : RealmObject() {
    // constructor used by Realm
    constructor() : this("", false, "", 0, false, false, 0)
}
