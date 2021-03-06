package com.sirionrazzer.diary.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class TrackItem(
    @PrimaryKey
    @Required
    var id: String,
    var templateId: String, // id of template
    var deleted: Boolean,
    var name: String,
    var image: Int,
    var hasTextField: Boolean, // item has optional text field
    var hasNumberField: Boolean, // item has optional number field
    open var status: Boolean, // on/off status of item
    open var textField: String?, // optional text field
    open var numberField: Float?, // optional number field
    open var date: Long, // creation time like Calendar.getInstance().timeInMillis
    open var position: Int
) : RealmObject() {
    // constructor used by Realm
    constructor() : this(
        "",
        "",
        false,
        "",
        0,
        false,
        false,
        false,
        "",
        0f,
        0,
        0
    )
}
