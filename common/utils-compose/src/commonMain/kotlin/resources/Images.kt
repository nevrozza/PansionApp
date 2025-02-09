package resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import pansion.Geologica_Black
import pansion.Geologica_BlackItalic
import pansion.Geologica_Bold
import pansion.Geologica_BoldItalic
import pansion.Geologica_ExtraBold
import pansion.Geologica_ExtraBoldItalic
import pansion.Geologica_ExtraLight
import pansion.Geologica_ExtraLightItalic
import pansion.Geologica_Light
import pansion.Geologica_LightItalic
import pansion.Geologica_Medium
import pansion.Geologica_MediumItalic
import pansion.Geologica_Regular
import pansion.Geologica_RegularItalic
import pansion.Geologica_SemiBold
import pansion.Geologica_SemiBoldItalic
import pansion.Geologica_Thin
import pansion.Geologica_ThinItalic
import pansion.Res

//Прости меня, Господи
@Composable
fun getAvatarPath(avatarId: Int): String? {
    // ok, it works better...

    val path = when (avatarId / 1000) {
        1 -> {
            with(Images.Avatars.Anime) {
                when (avatarId) {
                    this.ANIME_1_ID -> this.anime1.second.path
                    this.ANIME_2_ID -> this.anime2.second.path
                    this.ANIME_3_ID -> this.anime3.second.path
                    this.ANIME_4_ID -> this.anime4.second.path
                    this.ANIME_5_ID -> this.anime5.second.path
                    this.ANIME_6_ID -> this.anime6.second.path
                    this.ANIME_7_ID -> this.anime7.second.path
                    this.ANIME_8_ID -> this.anime8.second.path
                    this.ANIME_9_ID -> this.anime9.second.path
                    this.ANIME_10_ID -> this.anime10.second.path
                    this.ANIME_11_ID -> this.anime11.second.path
                    this.ANIME_12_ID -> this.anime12.second.path
                    else -> null
                }
            }
        }

        2 -> {
            with(Images.Avatars.Cats) {
                when (avatarId) {
                    this.CAT_1_ID -> this.cat1.second.path
                    this.CAT_2_ID -> this.cat2.second.path
                    this.CAT_3_ID -> this.cat3.second.path
                    this.CAT_4_ID -> this.cat4.second.path
                    this.CAT_5_ID -> this.cat5.second.path
                    this.CAT_6_ID -> this.cat6.second.path
                    this.CAT_7_ID -> this.cat7.second.path
                    this.CAT_GUN_ID -> this.catGun.second.path
                    else -> null
                }
            }
        }//catsCostedAvatars
        3 -> {
            with(Images.Avatars.MemCats) {
                when (avatarId) {
                    this.MEM_CAT_1_ID -> this.catM1.second.path
                    this.MEM_CAT_2_ID -> this.catM2.second.path
                    this.MEM_CAT_3_ID -> this.catM3.second.path
                    this.MEM_CAT_4_ID -> this.catM4.second.path
                    this.MEM_CAT_5_ID -> this.catM5.second.path
                    else -> null
                }
            }
        }//catsMCostedAvatars
        4 -> {
            with(Images.Avatars.Symbols) {
                when (avatarId) {
                    this.CRIMEA_ID -> this.crimea.second.path
                    this.RUSSIA_ID -> this.russia.second.path
                    this.PANSION_ID -> this.pansion.second.path
                    this.PANSION_PRINT_ID -> this.pansionPrint.second.path
                    else -> null
                }
            }
        }//symbolsCostedAvatars
        5 -> {
            with(Images.Avatars.Other) {
                when (avatarId) {
                    this.FLOWERS_2_ID -> this.flowers2.second.path
                    this.HEAD_ID -> this.head.second.path
                    this.HEDGEHOG_ID -> this.hedgehog.second.path
                    this.MIMI_ID -> this.mimi.second.path
                    this.NOVEMBER_1_ID -> this.november1.second.path
                    this.NOVEMBER_2_ID -> this.november2.second.path
                    this.SKY_ID -> this.sky.second.path
                    this.STARS_ID -> this.stars.second.path
                    this.STAR_ID -> this.star.second.path
                    this.HANDS_ID -> this.hands.second.path
                    else -> null
                }
            }
        } //othersCostedAvatars
        6 -> {
            with(Images.Avatars.Pictures) {
                when (avatarId) {
                    this.ALYONA_ID -> this.alyona.second.path
                    this.BEAR_ID -> this.bear.second.path
                    this.BLACK_SQUARE_ID -> this.blackSquare.second.path
                    this.BOGATIR_1_ID -> this.bogatir1.second.path
                    this.BOGATIR_2_ID -> this.bogatir2.second.path
                    this.BOGATIR_3_ID -> this.bogatir3.second.path
                    this.FOREST_ID -> this.forest.second.path
                    this.PERSIK_ID -> this.persik.second.path
                    this.RAINBOW_ID -> this.rainbow.second.path
                    this.UNKNOWN_ID -> this.unknown.second.path
                    this.VSADNICA_ID -> this.vsadnica.second.path
                    this.VSADNICA_SISTER_ID -> this.vsadnicaSister.second.path
                    this.CLOWN_ID -> this.clown.second.path
                    this.DEATH_ID -> this.death.second.path
                    this.FALL_ANGEL_ID -> this.fallAngel.second.path
                    this.FLOWERS_ID -> this.flowers.second.path
                    this.WTF_ID -> this.wtf.second.path
                    else -> null
                }
            }
        }//picturesCostedAvatars
        7 -> {
            with(Images.Avatars.Smeshariki) {
                when (avatarId) {
                    this.SMESH_1_ID -> this.smesh1.second.path
                    this.SMESH_2_ID -> this.smesh2.second.path
                    this.SMESH_3_ID -> this.smesh3.second.path
                    this.SMESH_4_ID -> this.smesh4.second.path
                    this.SMESH_5_ID -> this.smesh5.second.path
                    this.SMESH_6_ID -> this.smesh6.second.path
                    this.SMESH_7_ID -> this.smesh7.second.path
                    this.SMESH_8_ID -> this.smesh8.second.path
                    this.SMESH_9_ID -> this.smesh9.second.path
                    this.SMESH_10_ID -> this.smesh10.second.path
                    this.SMESH_11_ID -> this.smesh11.second.path
                    this.SMESH_12_ID -> this.smesh12.second.path
                    this.SMESH_13_ID -> this.smesh13.second.path
                    else -> null
                }
            }
        }//smesharikiCostedAvatars
        else -> {
            with(Images.Avatars.Nevrozq) {
                when (avatarId) {
                    this.NEVROZQ_1_ID -> this.me1.second.path
                    this.NEVROZQ_2_ID -> this.me2.second.path
                    this.NEVROZQ_3_ID -> this.me3.second.path
                    else -> null
                }
            }
        }//nevrozqCostedAvatars
    }
    return path//list[avatarId]?.image
}

data class PricedAvatar(
    val path: String?,
    val price: Int
)


data object Images {
    @OptIn(ExperimentalResourceApi::class)
    suspend fun confetti() =
        Res.readBytes("files/confetti.json").decodeToString()

//    val avatarsMap: Map<Int, ImageBitmap>
//        @Composable get() {
//            return (
//                    animeCostedAvatars
//                            + catsCostedAvatars
//                            + catsMCostedAvatars
//                            + symbolsCostedAvatars
//                            + picturesCostedAvatars
//                            + othersCostedAvatars
//                            + nevrozqCostedAvatars
//                            + smesharikiCostedAvatars
//                    ).toList().associate { it.first to it.second.image }
//        }

    val animeCostedAvatars: Map<Int, PricedAvatar>
        get() {
            with(Avatars.Anime) {
                return listOf(
                    anime1, anime2, anime3,
                    anime4, anime5, anime6,
                    anime7, anime8, anime10, anime9,
                    anime11, anime12
                ).associate { it.first to it.second }
            }
        }
    val nevrozqCostedAvatars: Map<Int, PricedAvatar>
        get() {
            with(Avatars.Nevrozq) {
                return listOf(
                    me1, me2, me3
                ).associate { it.first to it.second }
            }
        }
    val catsCostedAvatars: Map<Int, PricedAvatar>
        get() {
            with(Avatars.Cats) {
                return listOf(
                    cat1, cat2, cat3,
                    cat4, cat5, cat6,
                    cat7,
                    catGun
                ).associate { it.first to it.second }
            }
        }

    val catsMCostedAvatars: Map<Int, PricedAvatar>
        get() {
            with(Avatars.MemCats) {
                return listOf(
                    catM1, catM2, catM3,
                    catM4, catM5
                ).associate { it.first to it.second }
            }
        }

    val symbolsCostedAvatars: Map<Int, PricedAvatar>
        get() {
            with(Avatars.Symbols) {
                return listOf(
                    crimea, russia, pansion,
                    pansionPrint
                ).associate { it.first to it.second }
            }
        }
    val picturesCostedAvatars: Map<Int, PricedAvatar>
        get() {
            with(Avatars.Pictures) {
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
        get() {
            with(Avatars.Other) {
                return listOf(
                    flowers2, head, hedgehog,
                    mimi, november1, november2,
                    sky, star, stars, hands
                ).associate { it.first to it.second }
            }
        }

    val smesharikiCostedAvatars: Map<Int, PricedAvatar>
        get() {
            with(Avatars.Smeshariki) {
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
            const val ANIME_1_ID = 1000 + 2
            const val ANIME_2_ID = 1000 + 3
            const val ANIME_3_ID = 1000 + 4
            const val ANIME_4_ID = 1000 + 5
            const val ANIME_5_ID = 1000 + 6
            const val ANIME_6_ID = 1000 + 7
            const val ANIME_7_ID = 1000 + 8
            const val ANIME_8_ID = 1000 + 9
            const val ANIME_9_ID = 1000 + 10
            const val ANIME_10_ID = 1000 + 11
            const val ANIME_11_ID = 1000 + 12
            const val ANIME_12_ID = 1000 + 13
            val anime1: Pair<Int, PricedAvatar>
                get() = ANIME_1_ID to PricedAvatar("anime1", 30) //imageResource(
            val anime2: Pair<Int, PricedAvatar>
                get() = ANIME_2_ID to PricedAvatar("anime2", 10)
            val anime3: Pair<Int, PricedAvatar>
                get() = ANIME_3_ID to PricedAvatar("anime3", 10)
            val anime4: Pair<Int, PricedAvatar>
                get() = ANIME_4_ID to PricedAvatar("anime4", 20)
            val anime5: Pair<Int, PricedAvatar>
                get() = ANIME_5_ID to PricedAvatar("anime5", 30)
            val anime6: Pair<Int, PricedAvatar>
                get() = ANIME_6_ID to PricedAvatar("anime6", 20)
            val anime7: Pair<Int, PricedAvatar>
                get() = ANIME_7_ID to PricedAvatar("anime7", 30)
            val anime8: Pair<Int, PricedAvatar>
                get() = ANIME_8_ID to PricedAvatar("anime8", 30)
            val anime9: Pair<Int, PricedAvatar>
                get() = ANIME_9_ID to PricedAvatar("anime9", 15)
            val anime10: Pair<Int, PricedAvatar>
                get() = ANIME_10_ID to PricedAvatar("anime10", 15)
            val anime11: Pair<Int, PricedAvatar>
                get() = ANIME_11_ID to PricedAvatar("anime11", 30)
            val anime12: Pair<Int, PricedAvatar>
                get() = ANIME_12_ID to PricedAvatar("anime12", 20)
        }

        //2
        data object Cats {
            const val CAT_1_ID = 2000 + 12
            const val CAT_2_ID = 2000 + 13
            const val CAT_3_ID = 2000 + 14
            const val CAT_4_ID = 2000 + 15
            const val CAT_5_ID = 2000 + 16
            const val CAT_6_ID = 2000 + 17
            const val CAT_7_ID = 2000 + 19
            const val CAT_GUN_ID = 2000 + 18
            val cat1: Pair<Int, PricedAvatar>
                get() = CAT_1_ID to PricedAvatar("cat1", 30)
            val cat2: Pair<Int, PricedAvatar>
                get() = CAT_2_ID to PricedAvatar("cat2", 30)
            val cat3: Pair<Int, PricedAvatar>
                get() = CAT_3_ID to PricedAvatar("cat3", 20)
            val cat4: Pair<Int, PricedAvatar>
                get() = CAT_4_ID to PricedAvatar("cat4", 20)
            val cat5: Pair<Int, PricedAvatar>
                get() = CAT_5_ID to PricedAvatar("cat5", 20)
            val cat6: Pair<Int, PricedAvatar>
                get() = CAT_6_ID to PricedAvatar("cat6", 10)
            val cat7: Pair<Int, PricedAvatar>
                get() = CAT_7_ID to PricedAvatar("cat7", 20)
            val catGun: Pair<Int, PricedAvatar>
                get() = CAT_GUN_ID to PricedAvatar("catGun", 10)
        }

        //3
        data object MemCats {
            const val MEM_CAT_1_ID = 3000 + 19
            const val MEM_CAT_2_ID = 3000 + 20
            const val MEM_CAT_3_ID = 3000 + 21
            const val MEM_CAT_4_ID = 3000 + 22
            const val MEM_CAT_5_ID = 3000 + 23
            val catM1: Pair<Int, PricedAvatar>
                get() = MEM_CAT_1_ID to PricedAvatar("catM1", 10)
            val catM2: Pair<Int, PricedAvatar>
                get() = MEM_CAT_2_ID to PricedAvatar("catM2", 10)
            val catM3: Pair<Int, PricedAvatar>
                get() = MEM_CAT_3_ID to PricedAvatar("catM3", 20)
            val catM4: Pair<Int, PricedAvatar>
                get() = MEM_CAT_4_ID to PricedAvatar("catM4", 30)
            val catM5: Pair<Int, PricedAvatar>
                get() = MEM_CAT_5_ID to PricedAvatar("catM5", 30)
        }

        //4
        data object Symbols {
            const val CRIMEA_ID = 4000 + 24
            const val RUSSIA_ID = 4000 + 25
            const val PANSION_ID = 4000 + 26
            const val PANSION_PRINT_ID = 4000 + 27
            val crimea: Pair<Int, PricedAvatar>
                get() = CRIMEA_ID to PricedAvatar("flCrimea", 0)
            val russia: Pair<Int, PricedAvatar>
                get() = RUSSIA_ID to PricedAvatar("flRussia", 0)
            val pansion: Pair<Int, PricedAvatar>
                get() = PANSION_ID to PricedAvatar("pansion", 0)
            val pansionPrint: Pair<Int, PricedAvatar>
                get() = PANSION_PRINT_ID to PricedAvatar("pansionPrint", 0)
        }

        //0_0
        data object Nevrozq {
            const val NEVROZQ_1_ID = -1
            const val NEVROZQ_2_ID = -2
            const val NEVROZQ_3_ID = -3
            val me1: Pair<Int, PricedAvatar>
                get() = NEVROZQ_1_ID to PricedAvatar("me1", 0)
            val me2: Pair<Int, PricedAvatar>
                get() = NEVROZQ_2_ID to PricedAvatar("me2", 0)
            val me3: Pair<Int, PricedAvatar>
                get() = NEVROZQ_3_ID to PricedAvatar("me3", 0)
        }

        //5
        data object Other {
            const val FLOWERS_2_ID = 5000 + 28
            const val HEAD_ID = 5000 + 29
            const val HEDGEHOG_ID = 5000 + 30
            const val MIMI_ID = 5000 + 31
            const val NOVEMBER_1_ID = 5000 + 32
            const val NOVEMBER_2_ID = 5000 + 33
            const val SKY_ID = 5000 + 34
            const val STARS_ID = 5000 + 35
            const val STAR_ID = 5000 + 36
            const val HANDS_ID = 5000 + 37
            val flowers2: Pair<Int, PricedAvatar>
                get() = FLOWERS_2_ID to PricedAvatar("otFlowers2", 5)
            val head: Pair<Int, PricedAvatar>
                get() = HEAD_ID to PricedAvatar("otHead", 5)
            val hedgehog: Pair<Int, PricedAvatar>
                get() = HEDGEHOG_ID to PricedAvatar("otHedgehog", 25)
            val mimi: Pair<Int, PricedAvatar>
                get() = MIMI_ID to PricedAvatar("otMimi", 15)
            val november1: Pair<Int, PricedAvatar>
                get() = NOVEMBER_1_ID to PricedAvatar("otNovember1", 10)
            val november2: Pair<Int, PricedAvatar>
                get() = NOVEMBER_2_ID to PricedAvatar("otNovember2", 10)
            val sky: Pair<Int, PricedAvatar>
                get() = SKY_ID to PricedAvatar("otSky", 20)
            val stars: Pair<Int, PricedAvatar>
                get() = STARS_ID to PricedAvatar("otStars", 15)
            val star: Pair<Int, PricedAvatar>
                get() = STAR_ID to PricedAvatar("otStar", 15)
            val hands: Pair<Int, PricedAvatar>
                get() = HANDS_ID to PricedAvatar("otHands", 10)
        }

        //6
        data object Pictures {
            const val ALYONA_ID = 6000 + 37
            const val BEAR_ID = 6000 + 38
            const val BLACK_SQUARE_ID = 6000 + 39
            const val BOGATIR_1_ID = 6000 + 40
            const val BOGATIR_2_ID = 6000 + 41
            const val BOGATIR_3_ID = 6000 + 42
            const val FOREST_ID = 6000 + 43
            const val PERSIK_ID = 6000 + 44
            const val RAINBOW_ID = 6000 + 45
            const val UNKNOWN_ID = 6000 + 46
            const val VSADNICA_ID = 6000 + 47
            const val VSADNICA_SISTER_ID = 6000 + 48
            const val CLOWN_ID = 6000 + 49
            const val DEATH_ID = 6000 + 50
            const val FALL_ANGEL_ID = 6000 + 51
            const val FLOWERS_ID = 6000 + 52
            const val WTF_ID = 6000 + 53
            val alyona: Pair<Int, PricedAvatar>
                get() = ALYONA_ID to PricedAvatar("rkAlyona", 10)
            val bear: Pair<Int, PricedAvatar>
                get() = BEAR_ID to PricedAvatar("rkBear", 10)
            val blackSquare: Pair<Int, PricedAvatar>
                get() = BLACK_SQUARE_ID to PricedAvatar("rkBlackSquare", 15)
            val bogatir1: Pair<Int, PricedAvatar>
                get() = BOGATIR_1_ID to PricedAvatar("rkBogatir1", 30)
            val bogatir2: Pair<Int, PricedAvatar>
                get() = BOGATIR_2_ID to PricedAvatar("rkBogatir2", 20)
            val bogatir3: Pair<Int, PricedAvatar>
                get() = BOGATIR_3_ID to PricedAvatar("rkBogatir3", 10)
            val forest: Pair<Int, PricedAvatar>
                get() = FOREST_ID to PricedAvatar("rkForest", 5)
            val persik: Pair<Int, PricedAvatar>
                get() = PERSIK_ID to PricedAvatar("rkPersik", 15)
            val rainbow: Pair<Int, PricedAvatar>
                get() = RAINBOW_ID to PricedAvatar("rkRainbow", 10)
            val unknown: Pair<Int, PricedAvatar>
                get() = UNKNOWN_ID to PricedAvatar("rkUnknown", 15)
            val vsadnica: Pair<Int, PricedAvatar>
                get() = VSADNICA_ID to PricedAvatar("rkVsadnica", 15)
            val vsadnicaSister: Pair<Int, PricedAvatar>
                get() = VSADNICA_SISTER_ID to PricedAvatar("rkVsadnica_sister", 15)
            val clown: Pair<Int, PricedAvatar>
                get() = CLOWN_ID to PricedAvatar("rpClown", 40)
            val death: Pair<Int, PricedAvatar>
                get() = DEATH_ID to PricedAvatar("rpDeath", 40)
            val fallAngel: Pair<Int, PricedAvatar>
                get() = FALL_ANGEL_ID to PricedAvatar("rpFallAngel", 40)
            val flowers: Pair<Int, PricedAvatar>
                get() = FLOWERS_ID to PricedAvatar("rpFlowers", 10)
            val wtf: Pair<Int, PricedAvatar>
                get() = WTF_ID to PricedAvatar("rpWtf", 30)
        }

        //7
        data object Smeshariki {
            const val SMESH_1_ID = 7000 + 54
            const val SMESH_2_ID = 7000 + 55
            const val SMESH_3_ID = 7000 + 56
            const val SMESH_4_ID = 7000 + 57
            const val SMESH_5_ID = 7000 + 58
            const val SMESH_6_ID = 7000 + 59
            const val SMESH_7_ID = 7000 + 60
            const val SMESH_8_ID = 7000 + 61
            const val SMESH_9_ID = 7000 + 62
            const val SMESH_10_ID = 7000 + 63
            const val SMESH_11_ID = 7000 + 64
            const val SMESH_12_ID = 7000 + 65
            const val SMESH_13_ID = 7000 + 66
            val smesh1: Pair<Int, PricedAvatar>
                get() = SMESH_1_ID to PricedAvatar("smesh1", 15)
            val smesh2: Pair<Int, PricedAvatar>
                get() = SMESH_2_ID to PricedAvatar("smesh2", 15)
            val smesh3: Pair<Int, PricedAvatar>
                get() = SMESH_3_ID to PricedAvatar("smesh3", 15)
            val smesh4: Pair<Int, PricedAvatar>
                get() = SMESH_4_ID to PricedAvatar("smesh4", 15)
            val smesh5: Pair<Int, PricedAvatar>
                get() = SMESH_5_ID to PricedAvatar("smesh5", 15)
            val smesh6: Pair<Int, PricedAvatar>
                get() = SMESH_6_ID to PricedAvatar("smesh6", 15)
            val smesh7: Pair<Int, PricedAvatar>
                get() = SMESH_7_ID to PricedAvatar("smesh7", 15)
            val smesh8: Pair<Int, PricedAvatar>
                get() = SMESH_8_ID to PricedAvatar("smesh8", 15)
            val smesh9: Pair<Int, PricedAvatar>
                get() = SMESH_9_ID to PricedAvatar("smesh9", 15)
            val smesh10: Pair<Int, PricedAvatar>
                get() = SMESH_10_ID to PricedAvatar("smesh10", 30)
            val smesh11: Pair<Int, PricedAvatar>
                get() = SMESH_11_ID to PricedAvatar("smesh11", 20)
            val smesh12: Pair<Int, PricedAvatar>
                get() = SMESH_12_ID to PricedAvatar("smesh12", 20)
            val smesh13: Pair<Int, PricedAvatar>
                get() = SMESH_13_ID to PricedAvatar("smesh13", 30)
        }
    }

    data object Emoji {
        private const val PATH_EMOJI = "emojis/"
        const val EMOJI_0 = "${PATH_EMOJI}emoji0.webp"
        const val EMOJI_1 = "${PATH_EMOJI}emoji1.webp"
        const val EMOJI_2 = "${PATH_EMOJI}emoji2.webp"
        const val EMOJI_3 = "${PATH_EMOJI}emoji3.webp"
        const val EMOJI_4 = "${PATH_EMOJI}emoji4.webp"
        const val EMOJI_5 = "${PATH_EMOJI}emoji5.webp"
        const val EMOJI_6 = "${PATH_EMOJI}emoji6.webp"
        const val EMOJI_7 = "${PATH_EMOJI}emoji7.webp"
        const val EMOJI_COOK = "${PATH_EMOJI}emoji_cook.webp"
        const val EMOJI_WOAH = "${PATH_EMOJI}emoji_woah.webp"
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