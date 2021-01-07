package poorpeopleproblems.patches

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch
import com.megacrit.cardcrawl.shop.StoreRelic
import com.megacrit.cardcrawl.shop.ShopScreen
import com.megacrit.cardcrawl.relics.AbstractRelic
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier
import poorpeopleproblems.PoorPeopleProblemsMod
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator
import kotlin.Throws
import javassist.CtBehavior
import com.evacipated.cardcrawl.modthespire.lib.LineFinder
import com.evacipated.cardcrawl.modthespire.lib.Matcher
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch
import org.apache.logging.log4j.LogManager
import java.lang.Exception
import java.util.ArrayList

@Suppress("unused")
@SpirePatch(clz = StoreRelic::class, method = "purchaseRelic")
object RemoveRelicWhenBoughtPatch {
    private val logger = LogManager.getLogger(RemoveRelicWhenBoughtPatch::class.java.name)

    @SpirePrefixPatch
    @JvmStatic
    fun thisIsOurActualPatchMethod(instance: StoreRelic) {
        removeRelicFromPool(instance.relic)
    }

    private fun removeRelicFromPool(relic: AbstractRelic) {
        getTierList(relic).remove(relic.relicId)
        logger.info("Removed relic ${relic.relicId}")
    }

    private fun getTierList(it: AbstractRelic) = when (it.tier) {
        RelicTier.COMMON -> AbstractDungeon.commonRelicPool
        RelicTier.UNCOMMON -> AbstractDungeon.uncommonRelicPool
        RelicTier.RARE -> AbstractDungeon.rareRelicPool
        RelicTier.SHOP -> AbstractDungeon.shopRelicPool
        else -> arrayListOf<String>()
            .also { logger.error("Don't know where this relic belongs: $it") }
    }
}