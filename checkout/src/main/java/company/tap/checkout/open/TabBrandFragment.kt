package company.tap.checkout.open

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import company.tap.checkout.R
import company.tap.checkout.internal.utils.AppColorTheme
import company.tap.checkout.internal.utils.createDrawableGradientForBlurry
import company.tap.checkout.internal.utils.getDimensionsInDp
import company.tap.tapuilibrary.uikit.ktx.loadAppThemManagerFromPath
import company.tap.tapuilibrary.uikit.views.TapBrandView

class TabBrandFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_brand, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabBrand = view.findViewById<TapBrandView>(R.id.tab_brand)
        val topHeaderView = view.findViewById<ImageView>(R.id.poweredByImage)
        topHeaderView?.setImageResource(R.drawable.powered_by_tap)
        topHeaderView?.scaleType = ImageView.ScaleType.CENTER_CROP
        topHeaderView?.layoutParams?.width = context.getDimensionsInDp(120)
        topHeaderView?.layoutParams?.height = context.getDimensionsInDp(22)

        topHeaderView?.setBackgroundColor(resources.getColor(R.color.black_color))

       tabBrand?.setBackgroundDrawable(
            createDrawableGradientForBlurry(
                intArrayOf(
                    loadAppThemManagerFromPath(AppColorTheme.PoweredByTapBackgroundColor),
                    loadAppThemManagerFromPath(AppColorTheme.PoweredByTapBackgroundColor),
                    loadAppThemManagerFromPath(AppColorTheme.PoweredByTapBackgroundColor),
                )
            )
        )

    }

}