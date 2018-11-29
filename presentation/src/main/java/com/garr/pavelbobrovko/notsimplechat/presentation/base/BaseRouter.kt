package com.garr.pavelbobrovko.notsimplechat.presentation.base;

import android.support.v4.app.FragmentManager
import android.util.Log
import android.widget.Toast
import com.pavelbobrovko.garr.domain.utils.ConstantInterface

abstract class BaseRouter<A : BaseActivity>(val activity: A) {

    fun goBack() {
        activity.onBackPressed()
    }

    fun showError(e: Throwable) {
        Log.d(ConstantInterface.LOG_TAG, "Router.showError ${e.message.toString()}")
        Toast.makeText(activity, "Error " + e.toString(),
                Toast.LENGTH_SHORT)
    }

    fun replaceFragment(fragmentManager: FragmentManager,
                        fragment: BaseFragment,
                        containerResId: Int, addToBackStack: Boolean = false) {

        var fragmentTransition = fragmentManager.beginTransaction()

        fragmentTransition.replace(containerResId, fragment,
                fragment::class.java.canonicalName)

        if(addToBackStack) {
            fragmentTransition.addToBackStack(null)
        }

        fragmentTransition.commit()
    }
}