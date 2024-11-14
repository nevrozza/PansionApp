package resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.imageResource
import pansion.common.utils_compose.generated.resources.*
import pansion.common.utils_compose.generated.resources.Geologica_Black
import pansion.common.utils_compose.generated.resources.Geologica_BlackItalic
import pansion.common.utils_compose.generated.resources.Geologica_Bold
import pansion.common.utils_compose.generated.resources.Geologica_BoldItalic
import pansion.common.utils_compose.generated.resources.Geologica_ExtraBold
import pansion.common.utils_compose.generated.resources.Geologica_ExtraBoldItalic
import pansion.common.utils_compose.generated.resources.Geologica_ExtraLight
import pansion.common.utils_compose.generated.resources.Geologica_ExtraLightItalic
import pansion.common.utils_compose.generated.resources.Geologica_Light
import pansion.common.utils_compose.generated.resources.Geologica_LightItalic
import pansion.common.utils_compose.generated.resources.Geologica_Medium
import pansion.common.utils_compose.generated.resources.Geologica_MediumItalic
import pansion.common.utils_compose.generated.resources.Geologica_Regular
import pansion.common.utils_compose.generated.resources.Geologica_RegularItalic
import pansion.common.utils_compose.generated.resources.Geologica_SemiBold
import pansion.common.utils_compose.generated.resources.Geologica_SemiBoldItalic
import pansion.common.utils_compose.generated.resources.Geologica_Thin
import pansion.common.utils_compose.generated.resources.Geologica_ThinItalic
import pansion.common.utils_compose.generated.resources.MGU
import pansion.common.utils_compose.generated.resources.Res
import pansion.common.utils_compose.generated.resources.emoji0
import pansion.common.utils_compose.generated.resources.*
import pansion.common.utils_compose.generated.resources.emoji1
import pansion.common.utils_compose.generated.resources.emoji2
import pansion.common.utils_compose.generated.resources.emoji3
import pansion.common.utils_compose.generated.resources.emoji4
import pansion.common.utils_compose.generated.resources.emoji5
import pansion.common.utils_compose.generated.resources.emoji6

//Прости меня, Господи

@Composable
fun getAvatarImageVector(avatarId: Int): ImageBitmap? {
    // ok, it works better...

    val image = when (avatarId / 1000) {
        1 -> {
            with(Images.Avatars.Anime) {
                when (avatarId) {
                    this.anime1Id -> this.anime1.second.image
                    this.anime2Id -> this.anime2.second.image
                    this.anime3Id -> this.anime3.second.image
                    this.anime4Id -> this.anime4.second.image
                    this.anime5Id -> this.anime5.second.image
                    this.anime6Id -> this.anime6.second.image
                    this.anime7Id -> this.anime7.second.image
                    this.anime8Id -> this.anime8.second.image
                    this.anime9Id -> this.anime9.second.image
                    this.anime10Id -> this.anime10.second.image
                    this.anime11Id -> this.anime11.second.image
                    this.anime12Id -> this.anime12.second.image
                    else -> null
                }
            }
        }

        2 -> {
            with(Images.Avatars.Cats) {
                when(avatarId) {
                    this.cat1Id -> this.cat1.second.image
                    this.cat2Id -> this.cat2.second.image
                    this.cat3Id -> this.cat3.second.image
                    this.cat4Id -> this.cat4.second.image
                    this.cat5Id -> this.cat5.second.image
                    this.cat6Id -> this.cat6.second.image
                    this.cat7Id -> this.cat7.second.image
                    this.catGunId -> this.catGun.second.image
                    else -> null
                }
            }
        }//catsCostedAvatars
        3 -> {
            with (Images.Avatars.MemCats) {
                when(avatarId) {
                    this.catM1Id -> this.catM1.second.image
                    this.catM2Id -> this.catM2.second.image
                    this.catM3Id -> this.catM3.second.image
                    this.catM4Id -> this.catM4.second.image
                    this.catM5Id -> this.catM5.second.image
                    else -> null
                }
            }
        }//catsMCostedAvatars
        4 -> {
            with (Images.Avatars.Symbols) {
                when(avatarId) {
                    this.crimeaId -> this.crimea.second.image
                    this.russiaId -> this.russia.second.image
                    this.pansionId -> this.pansion.second.image
                    this.pansionPrintId -> this.pansionPrint.second.image
                    else -> null
                }
            }
        }//symbolsCostedAvatars
        5 -> {
            with (Images.Avatars.Other) {
                when (avatarId) {
                    this.flowers2Id -> this.flowers2.second.image
                    this.headId -> this.head.second.image
                    this.hedgehogId -> this.hedgehog.second.image
                    this.mimiId -> this.mimi.second.image
                    this.november1Id -> this.november1.second.image
                    this.november2Id -> this.november2.second.image
                    this.skyId -> this.sky.second.image
                    this.starsId -> this.stars.second.image
                    this.starId -> this.star.second.image
                    this.handsId -> this.hands.second.image
                    else -> null
                }
            }
        } //othersCostedAvatars
        6 -> {
            with (Images.Avatars.Pictures) {
                when (avatarId) {
                    this.alyonaId -> this.alyona.second.image
                    this.bearId -> this.bear.second.image
                    this.blackSquareId -> this.blackSquare.second.image
                    this.bogatir1Id -> this.bogatir1.second.image
                    this.bogatir2Id -> this.bogatir2.second.image
                    this.bogatir3Id -> this.bogatir3.second.image
                    this.forestId -> this.forest.second.image
                    this.persikId -> this.persik.second.image
                    this.rainbowId -> this.rainbow.second.image
                    this.unknownId -> this.unknown.second.image
                    this.vsadnicaId -> this.vsadnica.second.image
                    this.vsadnicaSisterId -> this.vsadnicaSister.second.image
                    this.clownId -> this.clown.second.image
                    this.deathId -> this.death.second.image
                    this.fallAngelId -> this.fallAngel.second.image
                    this.flowersId -> this.flowers.second.image
                    this.wtfId -> this.wtf.second.image
                    else -> null
                }
            }
        }//picturesCostedAvatars
        7 -> {
            with (Images.Avatars.Smeshariki) {
                when (avatarId) {
                    this.smesh1Id -> this.smesh1.second.image
                    this.smesh2Id -> this.smesh2.second.image
                    this.smesh3Id -> this.smesh3.second.image
                    this.smesh4Id -> this.smesh4.second.image
                    this.smesh5Id -> this.smesh5.second.image
                    this.smesh6Id -> this.smesh6.second.image
                    this.smesh7Id -> this.smesh7.second.image
                    this.smesh8Id -> this.smesh8.second.image
                    this.smesh9Id -> this.smesh9.second.image
                    this.smesh10Id -> this.smesh10.second.image
                    this.smesh11Id -> this.smesh11.second.image
                    this.smesh12Id -> this.smesh12.second.image
                    this.smesh13Id -> this.smesh13.second.image
                    else -> null
                }
            }
        }//smesharikiCostedAvatars
        else -> {
            with (Images.Avatars.Nevrozq) {
                when (avatarId) {
                    this.me1Id -> this.me1.second.image
                    this.me2Id -> this.me2.second.image
                    this.me3Id -> this.me3.second.image
                    else -> null
                }
            }
        }//nevrozqCostedAvatars
    }
    return image//list[avatarId]?.image
    //imageResource(Res.drawable.anime1)//Images.avatarsMap[avatarId]
}

data class PricedAvatar(
    val image: ImageBitmap,
    val price: Int
)


data object Images {
    val MGU: ImageBitmap
        @Composable get() = imageResource(Res.drawable.MGU)


    val avatarsMap: Map<Int, ImageBitmap>
        @Composable get() {
            return (
                    animeCostedAvatars
                            + catsCostedAvatars
                            + catsMCostedAvatars
                            + symbolsCostedAvatars
                            + picturesCostedAvatars
                            + othersCostedAvatars
                            + nevrozqCostedAvatars
                            + smesharikiCostedAvatars
                    ).toList().associate { it.first to it.second.image }
        }

    val animeCostedAvatars: Map<Int, PricedAvatar>
        @Composable get() {
            with(Images.Avatars.Anime) {
                return listOf(
                    anime1, anime2, anime3,
                    anime4, anime5, anime6,
                    anime7, anime8, anime10, anime9,
                    anime11, anime12
                ).associate { it.first to it.second }
            }
        }
    val nevrozqCostedAvatars: Map<Int, PricedAvatar>
        @Composable get() {
            with(Images.Avatars.Nevrozq) {
                return listOf(
                    me1, me2, me3
                ).associate { it.first to it.second }
            }
        }
    val catsCostedAvatars: Map<Int, PricedAvatar>
        @Composable get() {
            with(Images.Avatars.Cats) {
                return listOf(
                    cat1, cat2, cat3,
                    cat4, cat5, cat6,
                    cat7,
                    catGun
                ).associate { it.first to it.second }
            }
        }

    val catsMCostedAvatars: Map<Int, PricedAvatar>
        @Composable get() {
            with(Images.Avatars.MemCats) {
                return listOf(
                    catM1, catM2, catM3,
                    catM4, catM5
                ).associate { it.first to it.second }
            }
        }

    val symbolsCostedAvatars: Map<Int, PricedAvatar>
        @Composable get() {
            with(Images.Avatars.Symbols) {
                return listOf(
                    crimea, russia, pansion,
                    pansionPrint
                ).associate { it.first to it.second }
            }
        }
    val picturesCostedAvatars: Map<Int, PricedAvatar>
        @Composable get() {
            with(Images.Avatars.Pictures) {
                return listOf(
                    alyona, bear, blackSquare,
                    bogatir1, bogatir2, bogatir3,
                    forest, persik, rainbow,
                    unknown, vsadnica, vsadnicaSister,
                    clown, death, fallAngel, flowers,
                    wtf
                ).associate { it.first to it.second }
            }
        }
    val othersCostedAvatars: Map<Int, PricedAvatar>
        @Composable get() {
            with(Images.Avatars.Other) {
                return listOf(
                    flowers2, head, hedgehog,
                    mimi, november1, november2,
                    sky, star, stars, hands
                ).associate { it.first to it.second }
            }
        }

    val smesharikiCostedAvatars: Map<Int, PricedAvatar>
        @Composable get() {
            with(Images.Avatars.Smeshariki) {
                return listOf(
                    smesh1, smesh2, smesh3,
                    smesh4, smesh5, smesh6,
                    smesh7, smesh8, smesh9,
                    smesh10, smesh11, smesh12,
                    smesh13
                ).associate { it.first to it.second }
            }
        }

    data object Avatars {

        //1
        data object Anime {
            const val anime1Id = 1000 + 2
            const val anime2Id = 1000 + 3
            const val anime3Id = 1000 + 4
            const val anime4Id = 1000 + 5
            const val anime5Id = 1000 + 6
            const val anime6Id = 1000 + 7
            const val anime7Id = 1000 + 8
            const val anime8Id = 1000 + 9
            const val anime9Id = 1000 + 10
            const val anime10Id = 1000 + 11
            const val anime11Id = 1000 + 12
            const val anime12Id = 1000 + 13
            val anime1: Pair<Int, PricedAvatar>
                @Composable get() = anime1Id  to PricedAvatar(imageResource(Res.drawable.anime1), 30)
            val anime2: Pair<Int, PricedAvatar>
                @Composable get() = anime2Id to PricedAvatar(imageResource(Res.drawable.anime2), 10)
            val anime3: Pair<Int, PricedAvatar>
                @Composable get() = anime3Id to PricedAvatar(imageResource(Res.drawable.anime3), 10)
            val anime4: Pair<Int, PricedAvatar>
                @Composable get() = 1000 + 5 to PricedAvatar(imageResource(Res.drawable.anime4), 20)
            val anime5: Pair<Int, PricedAvatar>
                @Composable get() = 1000 + 6 to PricedAvatar(imageResource(Res.drawable.anime5), 30)
            val anime6: Pair<Int, PricedAvatar>
                @Composable get() = 1000 + 7 to PricedAvatar(imageResource(Res.drawable.anime6), 20)
            val anime7: Pair<Int, PricedAvatar>
                @Composable get() = 1000 + 8 to PricedAvatar(imageResource(Res.drawable.anime7), 30)
            val anime8: Pair<Int, PricedAvatar>
                @Composable get() = 1000 + 9 to PricedAvatar(imageResource(Res.drawable.anime8), 30)
            val anime9: Pair<Int, PricedAvatar>
                @Composable get() = 1000 + 10 to PricedAvatar(imageResource(Res.drawable.anime9), 15)
            val anime10: Pair<Int, PricedAvatar>
                @Composable get() = 1000 + 11 to PricedAvatar(imageResource(Res.drawable.anime10), 15)
            val anime11: Pair<Int, PricedAvatar>
                @Composable get() = 1000 + 12 to PricedAvatar(imageResource(Res.drawable.anime11), 30)
            val anime12: Pair<Int, PricedAvatar>
                @Composable get() = 1000 + 13 to PricedAvatar(imageResource(Res.drawable.anime12), 20)
        }

        //2
        data object Cats {
            const val cat1Id = 2000 + 12
            const val cat2Id = 2000 + 13
            const val cat3Id = 2000 + 14
            const val cat4Id = 2000 + 15
            const val cat5Id = 2000 + 16
            const val cat6Id = 2000 + 17
            const val cat7Id = 2000 + 19
            const val catGunId = 2000 + 18
            val cat1: Pair<Int, PricedAvatar>
                @Composable get() = 2000 + 12 to PricedAvatar(imageResource(Res.drawable.cat1), 30)
            val cat2: Pair<Int, PricedAvatar>
                @Composable get() = 2000 + 13 to PricedAvatar(imageResource(Res.drawable.cat2), 30)
            val cat3: Pair<Int, PricedAvatar>
                @Composable get() = 2000 + 14 to PricedAvatar(imageResource(Res.drawable.cat3), 20)
            val cat4: Pair<Int, PricedAvatar>
                @Composable get() = 2000 + 15 to PricedAvatar(imageResource(Res.drawable.cat4), 20)
            val cat5: Pair<Int, PricedAvatar>
                @Composable get() = 2000 + 16 to PricedAvatar(imageResource(Res.drawable.cat5), 20)
            val cat6: Pair<Int, PricedAvatar>
                @Composable get() = 2000 + 17 to PricedAvatar(imageResource(Res.drawable.cat6), 10)
            val cat7: Pair<Int, PricedAvatar>
                @Composable get() = 2000 + 19 to PricedAvatar(imageResource(Res.drawable.cat7), 20)
            val catGun: Pair<Int, PricedAvatar>
                @Composable get() = 2000 + 18 to PricedAvatar(imageResource(Res.drawable.catGun), 10)
        }

        //3
        data object MemCats {
            const val catM1Id = 3000 + 19
            const val catM2Id = 3000 + 20
            const val catM3Id = 3000 + 21
            const val catM4Id = 3000 + 22
            const val catM5Id = 3000 + 23
            val catM1: Pair<Int, PricedAvatar>
                @Composable get() = 3000 + 19 to PricedAvatar(imageResource(Res.drawable.catM1), 10)
            val catM2: Pair<Int, PricedAvatar>
                @Composable get() = 3000 + 20 to PricedAvatar(imageResource(Res.drawable.catM2), 10)
            val catM3: Pair<Int, PricedAvatar>
                @Composable get() = 3000 + 21 to PricedAvatar(imageResource(Res.drawable.catM3), 20)
            val catM4: Pair<Int, PricedAvatar>
                @Composable get() = 3000 + 22 to PricedAvatar(imageResource(Res.drawable.catM4), 30)
            val catM5: Pair<Int, PricedAvatar>
                @Composable get() = 3000 + 23 to PricedAvatar(imageResource(Res.drawable.catM5), 30)
        }

        //4
        data object Symbols {
            const val crimeaId = 4000 + 24
            const val russiaId = 4000 + 25
            const val pansionId = 4000 + 26
            const val pansionPrintId = 4000 + 27
            val crimea: Pair<Int, PricedAvatar>
                @Composable get() = 4000 + 24 to PricedAvatar(imageResource(Res.drawable.flCrimea), 0)
            val russia: Pair<Int, PricedAvatar>
                @Composable get() = 4000 + 25 to PricedAvatar(imageResource(Res.drawable.flRussia), 0)
            val pansion: Pair<Int, PricedAvatar>
                @Composable get() = 4000 + 26 to PricedAvatar(imageResource(Res.drawable.pansion), 0)
            val pansionPrint: Pair<Int, PricedAvatar>
                @Composable get() = 4000 + 27 to PricedAvatar(imageResource(Res.drawable.pansionPrint), 0)
        }

        //0_0
        data object Nevrozq {
            const val me1Id = -1
            const val me2Id = -2
            const val me3Id = -3
            val me1: Pair<Int, PricedAvatar>
                @Composable get() = -1 to PricedAvatar(imageResource(Res.drawable.me1), 0)
            val me2: Pair<Int, PricedAvatar>
                @Composable get() = -2 to PricedAvatar(imageResource(Res.drawable.me2), 0)
            val me3: Pair<Int, PricedAvatar>
                @Composable get() = -3 to PricedAvatar(imageResource(Res.drawable.me3), 0)
        }

        //5
        data object Other {
            const val flowers2Id = 5000 + 28
            const val headId = 5000 + 29
            const val hedgehogId = 5000 + 30
            const val mimiId = 5000 + 31
            const val november1Id = 5000 + 32
            const val november2Id = 5000 + 33
            const val skyId = 5000 + 34
            const val starsId = 5000 + 35
            const val starId = 5000 + 36
            const val handsId = 5000 + 37
            val flowers2: Pair<Int, PricedAvatar>
                @Composable get() = 5000 + 28 to PricedAvatar(imageResource(Res.drawable.otFlowers2), 5)
            val head: Pair<Int, PricedAvatar>
                @Composable get() = 5000 + 29 to PricedAvatar(imageResource(Res.drawable.otHead), 5)
            val hedgehog: Pair<Int, PricedAvatar>
                @Composable get() = 5000 + 30 to PricedAvatar(imageResource(Res.drawable.otHedgehog), 25)
            val mimi: Pair<Int, PricedAvatar>
                @Composable get() = 5000 + 31 to PricedAvatar(imageResource(Res.drawable.otMimi), 15)
            val november1: Pair<Int, PricedAvatar>
                @Composable get() = 5000 + 32 to PricedAvatar(imageResource(Res.drawable.otNovember1), 10)
            val november2: Pair<Int, PricedAvatar>
                @Composable get() = 5000 + 33 to PricedAvatar(imageResource(Res.drawable.otNovember2), 10)
            val sky: Pair<Int, PricedAvatar>
                @Composable get() = 5000 + 34 to PricedAvatar(imageResource(Res.drawable.otSky), 20)
            val stars: Pair<Int, PricedAvatar>
                @Composable get() = 5000 + 35 to PricedAvatar(imageResource(Res.drawable.otStars), 15)
            val star: Pair<Int, PricedAvatar>
                @Composable get() = 5000 + 36 to PricedAvatar(imageResource(Res.drawable.otStar), 15)
            val hands: Pair<Int, PricedAvatar>
                @Composable get() = 5000 + 37 to PricedAvatar(imageResource(Res.drawable.otHands), 10)
        }

        //6
        data object Pictures {
            const val alyonaId = 6000 + 37
            const val bearId = 6000 + 38
            const val blackSquareId = 6000 + 39
            const val bogatir1Id = 6000 + 40
            const val bogatir2Id = 6000 + 41
            const val bogatir3Id = 6000 + 42
            const val forestId = 6000 + 43
            const val persikId = 6000 + 44
            const val rainbowId = 6000 + 45
            const val unknownId = 6000 + 46
            const val vsadnicaId = 6000 + 47
            const val vsadnicaSisterId = 6000 + 48
            const val clownId = 6000 + 49
            const val deathId = 6000 + 50
            const val fallAngelId = 6000 + 51
            const val flowersId = 6000 + 52
            const val wtfId = 6000 + 53
            val alyona: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 37 to PricedAvatar(imageResource(Res.drawable.rkAlyona), 10)
            val bear: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 38 to PricedAvatar(imageResource(Res.drawable.rkBear), 10)
            val blackSquare: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 39 to PricedAvatar(imageResource(Res.drawable.rkBlackSquare), 15)
            val bogatir1: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 40 to PricedAvatar(imageResource(Res.drawable.rkBogatir1), 30)
            val bogatir2: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 41 to PricedAvatar(imageResource(Res.drawable.rkBogatir2), 20)
            val bogatir3: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 42 to PricedAvatar(imageResource(Res.drawable.rkBogatir3), 10)
            val forest: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 43 to PricedAvatar(imageResource(Res.drawable.rkForest), 5)
            val persik: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 44 to PricedAvatar(imageResource(Res.drawable.rkPersik), 15)
            val rainbow: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 45 to PricedAvatar(imageResource(Res.drawable.rkRainbow), 10)
            val unknown: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 46 to PricedAvatar(imageResource(Res.drawable.rkUnknown), 15)
            val vsadnica: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 47 to PricedAvatar(imageResource(Res.drawable.rkVsadnica), 15)
            val vsadnicaSister: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 48 to PricedAvatar(imageResource(Res.drawable.rkVsadnica_sister), 15)
            val clown: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 49 to PricedAvatar(imageResource(Res.drawable.rpClown), 40)
            val death: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 50 to PricedAvatar(imageResource(Res.drawable.rpDeath), 40)
            val fallAngel: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 51 to PricedAvatar(imageResource(Res.drawable.rpFallAngel), 40)
            val flowers: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 52 to PricedAvatar(imageResource(Res.drawable.rpFlowers), 10)
            val wtf: Pair<Int, PricedAvatar>
                @Composable get() = 6000 + 53 to PricedAvatar(imageResource(Res.drawable.rpWtf), 30)
        }

        //7
        data object Smeshariki {
            val smesh1Id = 7000 + 54
            val smesh2Id = 7000 + 55
            val smesh3Id = 7000 + 56
            val smesh4Id = 7000 + 57
            val smesh5Id = 7000 + 58
            val smesh6Id = 7000 + 59
            val smesh7Id = 7000 + 60
            val smesh8Id = 7000 + 61
            val smesh9Id = 7000 + 62
            val smesh10Id = 7000 + 63
            val smesh11Id = 7000 + 64
            val smesh12Id = 7000 + 65
            val smesh13Id = 7000 + 66
            val smesh1: Pair<Int, PricedAvatar>
                @Composable get() = 7000 + 54 to PricedAvatar(imageResource(Res.drawable.smesh1), 15)
            val smesh2: Pair<Int, PricedAvatar>
                @Composable get() = 7000 + 55 to PricedAvatar(imageResource(Res.drawable.smesh2), 15)
            val smesh3: Pair<Int, PricedAvatar>
                @Composable get() = 7000 + 56 to PricedAvatar(imageResource(Res.drawable.smesh3), 15)
            val smesh4: Pair<Int, PricedAvatar>
                @Composable get() = 7000 + 57 to PricedAvatar(imageResource(Res.drawable.smesh4), 15)
            val smesh5: Pair<Int, PricedAvatar>
                @Composable get() = 7000 + 58 to PricedAvatar(imageResource(Res.drawable.smesh5), 15)
            val smesh6: Pair<Int, PricedAvatar>
                @Composable get() = 7000 + 59 to PricedAvatar(imageResource(Res.drawable.smesh6), 15)
            val smesh7: Pair<Int, PricedAvatar>
                @Composable get() = 7000 + 60 to PricedAvatar(imageResource(Res.drawable.smesh7), 15)
            val smesh8: Pair<Int, PricedAvatar>
                @Composable get() = 7000 + 61 to PricedAvatar(imageResource(Res.drawable.smesh8), 15)
            val smesh9: Pair<Int, PricedAvatar>
                @Composable get() = 7000 + 62 to PricedAvatar(imageResource(Res.drawable.smesh9), 15)
            val smesh10: Pair<Int, PricedAvatar>
                @Composable get() = 7000 + 63 to PricedAvatar(imageResource(Res.drawable.smesh10), 30)
            val smesh11: Pair<Int, PricedAvatar>
                @Composable get() = 7000 + 64 to PricedAvatar(imageResource(Res.drawable.smesh11), 20)
            val smesh12: Pair<Int, PricedAvatar>
                @Composable get() = 7000 + 65 to PricedAvatar(imageResource(Res.drawable.smesh12), 20)
            val smesh13: Pair<Int, PricedAvatar>
                @Composable get() = 7000 + 66 to PricedAvatar(imageResource(Res.drawable.smesh13), 30)
        }
    }

    data object Emoji {
        val emoji0: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji0)
        val emoji1: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji1)
        val emoji2: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji2)
        val emoji3: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji3)
        val emoji4: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji4)
        val emoji5: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji5)
        val emoji6: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji6)
    }
}

val GeologicaFont: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.Geologica_Black, FontWeight.Black, FontStyle.Normal),
        Font(Res.font.Geologica_BlackItalic, FontWeight.Black, FontStyle.Italic),
        Font(Res.font.Geologica_Bold, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.Geologica_BoldItalic, FontWeight.Bold, FontStyle.Italic),
        Font(Res.font.Geologica_SemiBold, FontWeight.SemiBold, FontStyle.Normal),
        Font(Res.font.Geologica_SemiBoldItalic, FontWeight.SemiBold, FontStyle.Italic),
        Font(Res.font.Geologica_ExtraBold, FontWeight.ExtraBold, FontStyle.Normal),
        Font(Res.font.Geologica_ExtraBoldItalic, FontWeight.ExtraBold, FontStyle.Italic),
        Font(Res.font.Geologica_Medium, FontWeight.Medium, FontStyle.Normal),
        Font(Res.font.Geologica_MediumItalic, FontWeight.Medium, FontStyle.Italic),
        Font(Res.font.Geologica_Regular, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.Geologica_RegularItalic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.Geologica_Light, FontWeight.Light, FontStyle.Normal),
        Font(Res.font.Geologica_LightItalic, FontWeight.SemiBold, FontStyle.Italic),
        Font(Res.font.Geologica_ExtraLight, FontWeight.ExtraLight, FontStyle.Normal),
        Font(Res.font.Geologica_ExtraLightItalic, FontWeight.ExtraLight, FontStyle.Italic),
        Font(Res.font.Geologica_Thin, FontWeight.Thin, FontStyle.Normal),
        Font(Res.font.Geologica_ThinItalic, FontWeight.Thin, FontStyle.Italic),
    )