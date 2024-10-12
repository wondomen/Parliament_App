package com.example.finnishmp_app.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ParliamentMember(
    @PrimaryKey val hetekaId: Int,
    @ColumnInfo(name = "first_name") val firstname: String,
    @ColumnInfo(name = "last_name") val lastname: String,
    @ColumnInfo(name = "party") val party: String,
    @ColumnInfo(name = "minister") val minister: Boolean,
    @ColumnInfo(name = "picture_url") val pictureUrl: String,
    @ColumnInfo(name = "twitter") var twitter: String? = null,
    @ColumnInfo(name = "born_year") var bornYear: String? = null,
    @ColumnInfo(name = "constituency") var constituency: String? = null,
    @ColumnInfo(name = "rating") var rating: String? = null,
    @ColumnInfo(name = "notes") var notes: String? = null
) {
}