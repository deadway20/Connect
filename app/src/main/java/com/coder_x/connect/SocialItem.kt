package com.coder_x.connect

import com.coder_x.connect.database.PostEntity

sealed class SocialItem {
    object EmployeeStatus : SocialItem() // نوع العنصر الأول: الشريط الأفقي
    object CreatePost : SocialItem()     // النوع الثاني: what's on your mind
    data class Post(val postEntity: PostEntity) : SocialItem() // النوع الثالث: بوست فعلي
}
