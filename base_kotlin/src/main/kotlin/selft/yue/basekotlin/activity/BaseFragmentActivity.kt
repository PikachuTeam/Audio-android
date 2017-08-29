package selft.yue.basekotlin.activity

import android.os.Bundle
import selft.yue.basekotlin.R
import selft.yue.basekotlin.fragment.BaseFragment

/**
 * Created by dongc on 8/26/2017.
 */
abstract class BaseFragmentActivity : BaseActivity() {

    override fun getLayoutResId(): Int = R.layout.base_fragment_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().add(R.id.fragment_container, getFirstFragment()).commit()
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0)
            getCurrentFragment().onBackPressed()
        else
            super.onBackPressed()
    }

    abstract fun getFirstFragment(): BaseFragment

    fun getCurrentFragment(): BaseFragment =
            fragmentManager.findFragmentById(R.id.fragment_container) as BaseFragment
}