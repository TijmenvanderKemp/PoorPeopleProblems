package poorpeopleproblems.patches

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.saveAndContinue.SaveFile
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch
import com.megacrit.cardcrawl.shop.StoreRelic
import basemod.ReflectionHacks
import com.megacrit.cardcrawl.shop.ShopScreen
import com.megacrit.cardcrawl.relics.AbstractRelic
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier
import poorpeopleproblems.PoorPeopleProblemsMod
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator
import kotlin.Throws
import javassist.CtBehavior
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher
import com.megacrit.cardcrawl.ui.buttons.ProceedButton
import com.evacipated.cardcrawl.modthespire.lib.LineFinder
import com.evacipated.cardcrawl.modthespire.lib.Matcher
import org.apache.logging.log4j.LogManager
import java.lang.Exception
import java.util.ArrayList

@Suppress("unused")
@SpirePatch(clz = AbstractDungeon::class, method = "nextRoomTransition", paramtypez = [SaveFile::class])
object ReinsertUnboughtRelicsPatch {
    private val logger = LogManager.getLogger(
        ReinsertUnboughtRelicsPatch::class.java.name
    )

    @Suppress("UNUSED_PARAMETER")
    @SpireInsertPatch(locator = Locator::class)
    fun thisIsOurActualPatchMethod(instance: AbstractDungeon, saveFile: SaveFile?) {
        ReflectionHacks.getPrivate<ArrayList<StoreRelic>>(AbstractDungeon.shopScreen, ShopScreen::class.java, "relics")
            .filter { !it.isPurchased }
            .map { it.relic }
            .forEach {
                tryToAddRelic(getTierList(it), it)
            }
    }

    private fun getTierList(it: AbstractRelic) = when (it.tier) {
        RelicTier.COMMON -> AbstractDungeon.commonRelicPool
        RelicTier.UNCOMMON -> AbstractDungeon.uncommonRelicPool
        RelicTier.RARE -> AbstractDungeon.rareRelicPool
        RelicTier.SHOP -> AbstractDungeon.shopRelicPool
        else -> arrayListOf<String>()
            .also { logger.error("Don't know where this relic belongs: $it") }
    }

    private fun tryToAddRelic(relicList: ArrayList<String>, relic: AbstractRelic) {
        if (AbstractDungeon.player.relics.any { relic.relicId == it.relicId }) {
            logger.info("Player already has relic " + relic.relicId)
            return
        }
        if (relic.relicId in relicList) {
            logger.info("Relic pool already has relic " + relic.relicId)
            return
        }
        logger.info("Pool: $relicList")
        val lastElement = relicList.size - 1
        val minimumPosition = PoorPeopleProblemsMod.howFarBackShuffle - 1
        if (lastElement <= minimumPosition) {
            logger.info("Adding relic ${relic.relicId} at the end of the list.")
            relicList.add(relic.relicId)
        }
        val insertPosition = AbstractDungeon.merchantRng.random(minimumPosition, lastElement)
        logger.info("Adding relic ${relic.relicId} at position (0-index) $insertPosition")
        relicList.add(insertPosition, relic.relicId)
    }

    private class Locator : SpireInsertLocator() {
        @Throws(Exception::class)
        override fun Locate(ctMethodToPatch: CtBehavior): IntArray {
            val finalMatcher: Matcher = MethodCallMatcher(ProceedButton::class.java, "setLabel")
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher)
        }
    }
}