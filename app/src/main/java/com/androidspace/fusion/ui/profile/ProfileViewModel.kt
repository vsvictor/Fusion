package com.androidspace.fusion.ui.profile

import android.app.Application
import android.view.View
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.data.model.ProfileData
import com.androidspace.fusion.data.model.RegionData
import com.androidspace.fusion.domain.usecase.UseCaseTokens
import com.androidspace.fusion.ui.profile.data.OnProfile
import com.androidspace.fusion.ui.profile.data.ProfileObservable

class ProfileViewModel(override val app: Application, private val useCaseTokens: UseCaseTokens) : BaseViewModel<Object>(app) {

    var onProfile: OnProfile? = null
    val profile = ProfileObservable(ProfileData())
    init {
        useCaseTokens.access = ""
    }
    fun checkLogin(){
        if(useCaseTokens.isLoggined) onProfile?.onProfileLoad() else onProfile?.onProfileEmpty()
    }
    fun loadProfile(){
        val prof =ProfileData(1, "Віктор", "Джурляк", "Дмитрович", "2723510755", "0989420913", "dvictor74@gmail.com")
        profile.profile = prof
    }
    fun onNewEmail(view: View){}
    fun onLogin(view:View){
        useCaseTokens.access = "asdvnsmamsdcdvmo123123ycwewef4gf1vqerv"
        checkLogin()
    }
    fun onLanguage(view: View){}
    fun onFriends(view: View){}
    fun onRate(view: View){}
}