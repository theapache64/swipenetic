package com.theapache64.swipenetic.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "swipes"
)
data class Swipe(
    @ColumnInfo(name = "timestamp") val timestamp: Date,
    @ColumnInfo(name = "type") val type: Type
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    enum class Type {
        IN, OUT
    }
}
