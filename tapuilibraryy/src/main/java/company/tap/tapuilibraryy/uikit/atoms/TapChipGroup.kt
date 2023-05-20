package company.tap.tapuilibraryy.uikit.atoms

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.R
import company.tap.tapuilibraryy.fontskit.enums.TapFont
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.themekit.theme.TextViewTheme
import kotlinx.android.synthetic.main.tap_chip_group.view.*


/**
 * A ChipGroup is used to hold multiple Chips. By default, the chips are reflowed across
 * multiple lines. Set the attribute to constrain the chips to a single horizontal line.
 *  @param context The Context the view is running in, through which it can
 *  access the current theme, resources, etc.
 *  @param attrs The attributes of the XML Button tag being used to inflate the view.
 **/
class TapChipGroup(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    val groupName by lazy { findViewById<TapTextViewNew>(R.id.group_name) }
    val groupAction by lazy { findViewById<TapTextViewNew>(R.id.group_action) }
    val chipsRecycler by lazy { findViewById<RecyclerView>(R.id.chip_recycler) }
    val headerView by lazy { findViewById<RecyclerView>(R.id.header_view) }


    //Initialize views
    init {
        inflate(context, R.layout.tap_chip_group, this)
        setTheme(groupName,groupAction,chipsRecycler)
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") setFontsEnglish() else setFontsArabic()
    }

    fun setTheme(groupName: TapTextViewNew?, groupAction: TapTextViewNew?, chipsRecycler: RecyclerView?) {
        val groupNameTextViewTheme = TextViewTheme()
        groupNameTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("horizontalList.headers.gatewayHeader.leftButton.labelTextColor"))
        groupNameTextViewTheme.textSize =  ThemeManager.getFontSize("horizontalList.headers.gatewayHeader.leftButton.labelTextFont")
        groupName?.setTheme(groupNameTextViewTheme)

        val groupActionTextViewTheme = TextViewTheme()
        groupActionTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("horizontalList.headers.gatewayHeader.rightButton.labelTextColor"))
        groupActionTextViewTheme.textSize =  ThemeManager.getFontSize("horizontalList.headers.gatewayHeader.rightButton.labelTextFont")
        groupAction?.setTheme(groupActionTextViewTheme)

        linearMainView.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        chipsRecycler?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
    }

    private fun setFontsEnglish() {

        groupName?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
        groupAction?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
    }

    private fun setFontsArabic() {
        groupName?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )
        groupAction?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
    }

}