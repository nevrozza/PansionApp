package resources

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.*
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
import pansion.common.utils_compose.generated.resources.Res

//Прости меня, Господи


//
//@Composable
//fun x(resource: DrawableResource): ImageBitmap {
//    val resourceReader = LocalResourceReader.currentOrPreview
//    val resourceEnvironment = rememberResourceEnvironment()
//    val imageBitmap by rememberResourceState(
//        resource, resourceReader, resourceEnvironment, { emptyImageBitmap }
//    ) { env ->
//        val item = resource.getResourceItemByEnvironment(env)
//        val resourceDensityQualifier = item.qualifiers.firstOrNull { it is DensityQualifier } as? DensityQualifier
//        val resourceDensity = resourceDensityQualifier?.dpi ?: DensityQualifier.MDPI.dpi
//        val screenDensity = resourceEnvironment.density.dpi
//        val path = item.path
//        val cached = loadImage(path, "$path-${screenDensity}dpi", resourceReader) {
//            ImageCache.Bitmap(it.toImageBitmap(resourceDensity, screenDensity))
//        } as ImageCache.Bitmap
//        cached.bitmap
//    }
//    return imageBitmap
//}


@Composable
fun imageResource(r: DrawableResource): ImageBitmap = org.jetbrains.compose.resources.imageResource(r)




@Composable
fun getAvatarPath(avatarId: Int): String? {
    // ok, it works better...

    val path = when (avatarId / 1000) {
        1 -> {
            with(Images.Avatars.Anime) {
                when (avatarId) {
                    this.anime1Id -> this.anime1.second.path
                    this.anime2Id -> this.anime2.second.path
                    this.anime3Id -> this.anime3.second.path
                    this.anime4Id -> this.anime4.second.path
                    this.anime5Id -> this.anime5.second.path
                    this.anime6Id -> this.anime6.second.path
                    this.anime7Id -> this.anime7.second.path
                    this.anime8Id -> this.anime8.second.path
                    this.anime9Id -> this.anime9.second.path
                    this.anime10Id -> this.anime10.second.path
                    this.anime11Id -> this.anime11.second.path
                    this.anime12Id -> this.anime12.second.path
                    else -> null
                }
            }
        }

        2 -> {
            with(Images.Avatars.Cats) {
                when (avatarId) {
                    this.cat1Id -> this.cat1.second.path
                    this.cat2Id -> this.cat2.second.path
                    this.cat3Id -> this.cat3.second.path
                    this.cat4Id -> this.cat4.second.path
                    this.cat5Id -> this.cat5.second.path
                    this.cat6Id -> this.cat6.second.path
                    this.cat7Id -> this.cat7.second.path
                    this.catGunId -> this.catGun.second.path
                    else -> null
                }
            }
        }//catsCostedAvatars
        3 -> {
            with(Images.Avatars.MemCats) {
                when (avatarId) {
                    this.catM1Id -> this.catM1.second.path
                    this.catM2Id -> this.catM2.second.path
                    this.catM3Id -> this.catM3.second.path
                    this.catM4Id -> this.catM4.second.path
                    this.catM5Id -> this.catM5.second.path
                    else -> null
                }
            }
        }//catsMCostedAvatars
        4 -> {
            with(Images.Avatars.Symbols) {
                when (avatarId) {
                    this.crimeaId -> this.crimea.second.path
                    this.russiaId -> this.russia.second.path
                    this.pansionId -> this.pansion.second.path
                    this.pansionPrintId -> this.pansionPrint.second.path
                    else -> null
                }
            }
        }//symbolsCostedAvatars
        5 -> {
            with(Images.Avatars.Other) {
                when (avatarId) {
                    this.flowers2Id -> this.flowers2.second.path
                    this.headId -> this.head.second.path
                    this.hedgehogId -> this.hedgehog.second.path
                    this.mimiId -> this.mimi.second.path
                    this.november1Id -> this.november1.second.path
                    this.november2Id -> this.november2.second.path
                    this.skyId -> this.sky.second.path
                    this.starsId -> this.stars.second.path
                    this.starId -> this.star.second.path
                    this.handsId -> this.hands.second.path
                    else -> null
                }
            }
        } //othersCostedAvatars
        6 -> {
            with(Images.Avatars.Pictures) {
                when (avatarId) {
                    this.alyonaId -> this.alyona.second.path
                    this.bearId -> this.bear.second.path
                    this.blackSquareId -> this.blackSquare.second.path
                    this.bogatir1Id -> this.bogatir1.second.path
                    this.bogatir2Id -> this.bogatir2.second.path
                    this.bogatir3Id -> this.bogatir3.second.path
                    this.forestId -> this.forest.second.path
                    this.persikId -> this.persik.second.path
                    this.rainbowId -> this.rainbow.second.path
                    this.unknownId -> this.unknown.second.path
                    this.vsadnicaId -> this.vsadnica.second.path
                    this.vsadnicaSisterId -> this.vsadnicaSister.second.path
                    this.clownId -> this.clown.second.path
                    this.deathId -> this.death.second.path
                    this.fallAngelId -> this.fallAngel.second.path
                    this.flowersId -> this.flowers.second.path
                    this.wtfId -> this.wtf.second.path
                    else -> null
                }
            }
        }//picturesCostedAvatars
        7 -> {
            with(Images.Avatars.Smeshariki) {
                when (avatarId) {
                    this.smesh1Id -> this.smesh1.second.path
                    this.smesh2Id -> this.smesh2.second.path
                    this.smesh3Id -> this.smesh3.second.path
                    this.smesh4Id -> this.smesh4.second.path
                    this.smesh5Id -> this.smesh5.second.path
                    this.smesh6Id -> this.smesh6.second.path
                    this.smesh7Id -> this.smesh7.second.path
                    this.smesh8Id -> this.smesh8.second.path
                    this.smesh9Id -> this.smesh9.second.path
                    this.smesh10Id -> this.smesh10.second.path
                    this.smesh11Id -> this.smesh11.second.path
                    this.smesh12Id -> this.smesh12.second.path
                    this.smesh13Id -> this.smesh13.second.path
                    else -> null
                }
            }
        }//smesharikiCostedAvatars
        else -> {
            with(Images.Avatars.Nevrozq) {
                when (avatarId) {
                    this.me1Id -> this.me1.second.path
                    this.me2Id -> this.me2.second.path
                    this.me3Id -> this.me3.second.path
                    else -> null
                }
            }
        }//nevrozqCostedAvatars
    }
    return path//list[avatarId]?.image
    //imageResource(Res.drawable.anime1)//Images.avatarsMap[avatarId]
}


data object RIcons {
    const val HomeWork = "list-check-solid.svg"
    const val Check = "check-solid.svg"
    const val Close = "xmark-solid.svg"
    const val ChevronLeft = "chevron-left-solid.svg"
    const val MagicWand = "wand-magic-sparkles-solid.svg"
    const val Gift = "gift-solid.svg"
    const val Save = "floppy-disk-solid.svg"
    const val Add = "plus-solid.svg"
    const val Link = "link-solid.svg"

    const val Qr = "qrcode-solid.svg"

    const val TrashCanRegular = "trash-can-regular.svg"
    const val Refresh = "rotate-right-solid.svg"
    const val SovietSettings = "screwdriver-wrench-solid.svg"
    const val Group = "users-solid.svg"
    const val SmallGroup = "user-group-solid.svg"
    const val Home = "house-solid.svg"
    const val School = "school-solid.svg"
    const val Book = "book-solid.svg"
    const val ContactBook = "address-book-regular.svg"
    const val Table = "table-list-solid.svg"

    const val Settings = "gear-solid.svg"

    const val Trophy = "trophy-solid.svg"

    const val Calendar = "calendar-regular.svg"

    const val SchoolCap = "graduation-cap-solid.svg"
    const val SchoolCapOutlined = "graduation-cap-outlined.svg"

    const val MGU = "MGU.png"

    const val User = "user-solid.svg"

    const val VisibilityOff = "eye-slash-solid.svg"
    const val Visibility = "eye-solid.svg"


    const val QuestionCircle = "circle-question-regular.svg"

    const val BigBrush = "brush-solid.svg"

    const val Repeat = "repeat-solid.svg"

    const val Logout = "arrow-right-from-bracket-solid.svg"

    const val Puzzle = "puzzle-piece-solid.svg"

    const val PersonAdd = "user-plus-solid.svg"

    const val SwapHoriz = "arrow-right-arrow-left-solid.svg"

    const val ErrorOutline = "circle-exclamation-solid.svg"

    const val Edit = "pen-solid.svg"

    const val Menu = "bars-solid.svg"

    const val Thumbtack = "thumbtack-solid.svg"

    const val History = "clock-rotate-left-solid.svg"

    const val Tune = "sliders-solid.svg"

    const val Like = "thumbs-up-solid.svg"

    const val Fire = "fire-solid.svg"

    const val Search = "magnifying-glass-solid.svg"

    const val Upload = "cloud-arrow-up-solid.svg"

    const val Star = "star-solid.svg"
    const val StarOutlined = "star-regular.svg"

    const val Shield = "shield-cat-solid.svg"

    const val MoreVert = "ellipsis-vertical-solid.svg"

    const val Coins = "coins-solid.svg"

    const val RocketLaunch = "rocket-launch-solid.svg"
    const val Rocket = "rocket-solid.svg"


    const val Newspaper = "newspaper-solid.svg"

    const val Comment = "comment-solid.svg"

    const val Ball = "basketball-solid.svg"

    const val Styler = "styler-solid.svg"

    const val Celebration = "celebration-solid.svg"

    const val CuteCheck = "cute-check.svg"

    const val CheckCircleOutline = "circle-check-regular.svg"

    const val Restaraunt = "utensils-solid.svg"

    const val Schedule = "clock-regular.svg"

    const val HourglassBottom = "hourglass-end-solid.svg"

    const val Dining = "dining-solid.svg"

    const val Receipt = "receipt-solid.svg"

    const val ManageSearch = "manage-search-solid.svg"

    const val PlaylistAddCheckCircle = "playlist-add-check-circle.svg"

    const val Minus = "minus-solid.svg"

    const val AutoMode = "auto-mode-solid.svg"
    const val DarkMode = "dark-mode-solid.svg"
    const val LightMode = "light-mode-solid.svg"

    const val Translate = "translate.svg"

    const val Telegram = "telegram-brands-solid.svg"

    const val Key = "key-solid.svg"

    const val FlashlightOn = "flashlight-on-solid.svg"
    const val FlashlightOff = "flashlight-off-solid.svg"

    const val Maximize = "maximize-solid.svg"
    const val Minimize = "minimize-solid.svg"


    data object Devices {
        data object Web {
            const val Yandex = "yandex-brands-solid.svg"
            const val Safari = "safari-brands-solid.svg"
            const val Edge = "edge-brands-solid.svg"
            const val Opera = "opera-brands-solid.svg"
            const val Chrome = "chrome-brands-solid.svg"
            const val Firefox = "firefox-browser-brands-solid.svg"
            const val Web = "globe-solid.svg"
        }

        data object Desktop {
            const val computer = "computer-solid.svg"
            const val laptop = "laptop-solid.svg"
            const val windows = "windows-brands-solid.svg"
        }

        data object Mobile {
            const val Android = "android-brands-solid.svg"
            const val Apple = "apple-brands-solid.svg"
        }
    }

}

//@Composable
//fun getAvatarImageVector(avatarId: Int): ImageBitmap? {
//    // ok, it works better...
//
//    val image = when (avatarId / 1000) {
//        1 -> {
//            with(Images.Avatars.Anime) {
//                when (avatarId) {
//                    this.anime1Id -> this.anime1.second.image
//                    this.anime2Id -> this.anime2.second.image
//                    this.anime3Id -> this.anime3.second.image
//                    this.anime4Id -> this.anime4.second.image
//                    this.anime5Id -> this.anime5.second.image
//                    this.anime6Id -> this.anime6.second.image
//                    this.anime7Id -> this.anime7.second.image
//                    this.anime8Id -> this.anime8.second.image
//                    this.anime9Id -> this.anime9.second.image
//                    this.anime10Id -> this.anime10.second.image
//                    this.anime11Id -> this.anime11.second.image
//                    this.anime12Id -> this.anime12.second.image
//                    else -> null
//                }
//            }
//        }
//
//        2 -> {
//            with(Images.Avatars.Cats) {
//                when (avatarId) {
//                    this.cat1Id -> this.cat1.second.image
//                    this.cat2Id -> this.cat2.second.image
//                    this.cat3Id -> this.cat3.second.image
//                    this.cat4Id -> this.cat4.second.image
//                    this.cat5Id -> this.cat5.second.image
//                    this.cat6Id -> this.cat6.second.image
//                    this.cat7Id -> this.cat7.second.image
//                    this.catGunId -> this.catGun.second.image
//                    else -> null
//                }
//            }
//        }//catsCostedAvatars
//        3 -> {
//            with(Images.Avatars.MemCats) {
//                when (avatarId) {
//                    this.catM1Id -> this.catM1.second.image
//                    this.catM2Id -> this.catM2.second.image
//                    this.catM3Id -> this.catM3.second.image
//                    this.catM4Id -> this.catM4.second.image
//                    this.catM5Id -> this.catM5.second.image
//                    else -> null
//                }
//            }
//        }//catsMCostedAvatars
//        4 -> {
//            with(Images.Avatars.Symbols) {
//                when (avatarId) {
//                    this.crimeaId -> this.crimea.second.image
//                    this.russiaId -> this.russia.second.image
//                    this.pansionId -> this.pansion.second.image
//                    this.pansionPrintId -> this.pansionPrint.second.image
//                    else -> null
//                }
//            }
//        }//symbolsCostedAvatars
//        5 -> {
//            with(Images.Avatars.Other) {
//                when (avatarId) {
//                    this.flowers2Id -> this.flowers2.second.image
//                    this.headId -> this.head.second.image
//                    this.hedgehogId -> this.hedgehog.second.image
//                    this.mimiId -> this.mimi.second.image
//                    this.november1Id -> this.november1.second.image
//                    this.november2Id -> this.november2.second.image
//                    this.skyId -> this.sky.second.image
//                    this.starsId -> this.stars.second.image
//                    this.starId -> this.star.second.image
//                    this.handsId -> this.hands.second.image
//                    else -> null
//                }
//            }
//        } //othersCostedAvatars
//        6 -> {
//            with(Images.Avatars.Pictures) {
//                when (avatarId) {
//                    this.alyonaId -> this.alyona.second.image
//                    this.bearId -> this.bear.second.image
//                    this.blackSquareId -> this.blackSquare.second.image
//                    this.bogatir1Id -> this.bogatir1.second.image
//                    this.bogatir2Id -> this.bogatir2.second.image
//                    this.bogatir3Id -> this.bogatir3.second.image
//                    this.forestId -> this.forest.second.image
//                    this.persikId -> this.persik.second.image
//                    this.rainbowId -> this.rainbow.second.image
//                    this.unknownId -> this.unknown.second.image
//                    this.vsadnicaId -> this.vsadnica.second.image
//                    this.vsadnicaSisterId -> this.vsadnicaSister.second.image
//                    this.clownId -> this.clown.second.image
//                    this.deathId -> this.death.second.image
//                    this.fallAngelId -> this.fallAngel.second.image
//                    this.flowersId -> this.flowers.second.image
//                    this.wtfId -> this.wtf.second.image
//                    else -> null
//                }
//            }
//        }//picturesCostedAvatars
//        7 -> {
//            with(Images.Avatars.Smeshariki) {
//                when (avatarId) {
//                    this.smesh1Id -> this.smesh1.second.image
//                    this.smesh2Id -> this.smesh2.second.image
//                    this.smesh3Id -> this.smesh3.second.image
//                    this.smesh4Id -> this.smesh4.second.image
//                    this.smesh5Id -> this.smesh5.second.image
//                    this.smesh6Id -> this.smesh6.second.image
//                    this.smesh7Id -> this.smesh7.second.image
//                    this.smesh8Id -> this.smesh8.second.image
//                    this.smesh9Id -> this.smesh9.second.image
//                    this.smesh10Id -> this.smesh10.second.image
//                    this.smesh11Id -> this.smesh11.second.image
//                    this.smesh12Id -> this.smesh12.second.image
//                    this.smesh13Id -> this.smesh13.second.image
//                    else -> null
//                }
//            }
//        }//smesharikiCostedAvatars
//        else -> {
//            with(Images.Avatars.Nevrozq) {
//                when (avatarId) {
//                    this.me1Id -> this.me1.second.image
//                    this.me2Id -> this.me2.second.image
//                    this.me3Id -> this.me3.second.image
//                    else -> null
//                }
//            }
//        }//nevrozqCostedAvatars
//    }
//    return imageResource(image ?: Images.MGUResource)//list[avatarId]?.image
//    //imageResource(Res.drawable.anime1)//Images.avatarsMap[avatarId]
//}

data class PricedAvatar(
    val path: String?,
    val price: Int
)


data object Images {
    @OptIn(ExperimentalResourceApi::class)
    suspend fun Confetti() = Res.readBytes("files/confetti.json").decodeToString()

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
        get() {
            with(Images.Avatars.Nevrozq) {
                return listOf(
                    me1, me2, me3
                ).associate { it.first to it.second }
            }
        }
    val catsCostedAvatars: Map<Int, PricedAvatar>
        get() {
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
        get() {
            with(Images.Avatars.MemCats) {
                return listOf(
                    catM1, catM2, catM3,
                    catM4, catM5
                ).associate { it.first to it.second }
            }
        }

    val symbolsCostedAvatars: Map<Int, PricedAvatar>
        get() {
            with(Images.Avatars.Symbols) {
                return listOf(
                    crimea, russia, pansion,
                    pansionPrint
                ).associate { it.first to it.second }
            }
        }
    val picturesCostedAvatars: Map<Int, PricedAvatar>
        get() {
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
        get() {
            with(Images.Avatars.Other) {
                return listOf(
                    flowers2, head, hedgehog,
                    mimi, november1, november2,
                    sky, star, stars, hands
                ).associate { it.first to it.second }
            }
        }

    val smesharikiCostedAvatars: Map<Int, PricedAvatar>
        get() {
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
                get() = anime1Id to PricedAvatar("anime1", 30) //imageResource(
            val anime2: Pair<Int, PricedAvatar>
                get() = anime2Id to PricedAvatar("anime2", 10)
            val anime3: Pair<Int, PricedAvatar>
                get() = anime3Id to PricedAvatar("anime3", 10)
            val anime4: Pair<Int, PricedAvatar>
                get() = 1000 + 5 to PricedAvatar("anime4", 20)
            val anime5: Pair<Int, PricedAvatar>
                get() = 1000 + 6 to PricedAvatar("anime5", 30)
            val anime6: Pair<Int, PricedAvatar>
                get() = 1000 + 7 to PricedAvatar("anime6", 20)
            val anime7: Pair<Int, PricedAvatar>
                get() = 1000 + 8 to PricedAvatar("anime7", 30)
            val anime8: Pair<Int, PricedAvatar>
                get() = 1000 + 9 to PricedAvatar("anime8", 30)
            val anime9: Pair<Int, PricedAvatar>
                get() = 1000 + 10 to PricedAvatar("anime9", 15)
            val anime10: Pair<Int, PricedAvatar>
                get() = 1000 + 11 to PricedAvatar("anime10", 15)
            val anime11: Pair<Int, PricedAvatar>
                get() = 1000 + 12 to PricedAvatar("anime11", 30)
            val anime12: Pair<Int, PricedAvatar>
                get() = 1000 + 13 to PricedAvatar("anime12", 20)
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
                get() = 2000 + 12 to PricedAvatar("cat1", 30)
            val cat2: Pair<Int, PricedAvatar>
                get() = 2000 + 13 to PricedAvatar("cat2", 30)
            val cat3: Pair<Int, PricedAvatar>
                get() = 2000 + 14 to PricedAvatar("cat3", 20)
            val cat4: Pair<Int, PricedAvatar>
                get() = 2000 + 15 to PricedAvatar("cat4", 20)
            val cat5: Pair<Int, PricedAvatar>
                get() = 2000 + 16 to PricedAvatar("cat5", 20)
            val cat6: Pair<Int, PricedAvatar>
                get() = 2000 + 17 to PricedAvatar("cat6", 10)
            val cat7: Pair<Int, PricedAvatar>
                get() = 2000 + 19 to PricedAvatar("cat7", 20)
            val catGun: Pair<Int, PricedAvatar>
                get() = 2000 + 18 to PricedAvatar("catGun", 10)
        }

        //3
        data object MemCats {
            const val catM1Id = 3000 + 19
            const val catM2Id = 3000 + 20
            const val catM3Id = 3000 + 21
            const val catM4Id = 3000 + 22
            const val catM5Id = 3000 + 23
            val catM1: Pair<Int, PricedAvatar>
                get() = 3000 + 19 to PricedAvatar("catM1", 10)
            val catM2: Pair<Int, PricedAvatar>
                get() = 3000 + 20 to PricedAvatar("catM2", 10)
            val catM3: Pair<Int, PricedAvatar>
                get() = 3000 + 21 to PricedAvatar("catM3", 20)
            val catM4: Pair<Int, PricedAvatar>
                get() = 3000 + 22 to PricedAvatar("catM4", 30)
            val catM5: Pair<Int, PricedAvatar>
                get() = 3000 + 23 to PricedAvatar("catM5", 30)
        }

        //4
        data object Symbols {
            const val crimeaId = 4000 + 24
            const val russiaId = 4000 + 25
            const val pansionId = 4000 + 26
            const val pansionPrintId = 4000 + 27
            val crimea: Pair<Int, PricedAvatar>
                get() = 4000 + 24 to PricedAvatar("flCrimea", 0)
            val russia: Pair<Int, PricedAvatar>
                get() = 4000 + 25 to PricedAvatar("flRussia", 0)
            val pansion: Pair<Int, PricedAvatar>
                get() = 4000 + 26 to PricedAvatar("pansion", 0)
            val pansionPrint: Pair<Int, PricedAvatar>
                get() = 4000 + 27 to PricedAvatar("pansionPrint", 0)
        }

        //0_0
        data object Nevrozq {
            const val me1Id = -1
            const val me2Id = -2
            const val me3Id = -3
            val me1: Pair<Int, PricedAvatar>
                get() = -1 to PricedAvatar("me1", 0)
            val me2: Pair<Int, PricedAvatar>
                get() = -2 to PricedAvatar("me2", 0)
            val me3: Pair<Int, PricedAvatar>
                get() = -3 to PricedAvatar("me3", 0)
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
                get() = 5000 + 28 to PricedAvatar("otFlowers2", 5)
            val head: Pair<Int, PricedAvatar>
                get() = 5000 + 29 to PricedAvatar("otHead", 5)
            val hedgehog: Pair<Int, PricedAvatar>
                get() = 5000 + 30 to PricedAvatar("otHedgehog", 25)
            val mimi: Pair<Int, PricedAvatar>
                get() = 5000 + 31 to PricedAvatar("otMimi", 15)
            val november1: Pair<Int, PricedAvatar>
                get() = 5000 + 32 to PricedAvatar("otNovember1", 10)
            val november2: Pair<Int, PricedAvatar>
                get() = 5000 + 33 to PricedAvatar("otNovember2", 10)
            val sky: Pair<Int, PricedAvatar>
                get() = 5000 + 34 to PricedAvatar("otSky", 20)
            val stars: Pair<Int, PricedAvatar>
                get() = 5000 + 35 to PricedAvatar("otStars", 15)
            val star: Pair<Int, PricedAvatar>
                get() = 5000 + 36 to PricedAvatar("otStar", 15)
            val hands: Pair<Int, PricedAvatar>
                get() = 5000 + 37 to PricedAvatar("otHands", 10)
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
                get() = 6000 + 37 to PricedAvatar("rkAlyona", 10)
            val bear: Pair<Int, PricedAvatar>
                get() = 6000 + 38 to PricedAvatar("rkBear", 10)
            val blackSquare: Pair<Int, PricedAvatar>
                get() = 6000 + 39 to PricedAvatar("rkBlackSquare", 15)
            val bogatir1: Pair<Int, PricedAvatar>
                get() = 6000 + 40 to PricedAvatar("rkBogatir1", 30)
            val bogatir2: Pair<Int, PricedAvatar>
                get() = 6000 + 41 to PricedAvatar("rkBogatir2", 20)
            val bogatir3: Pair<Int, PricedAvatar>
                get() = 6000 + 42 to PricedAvatar("rkBogatir3", 10)
            val forest: Pair<Int, PricedAvatar>
                get() = 6000 + 43 to PricedAvatar("rkForest", 5)
            val persik: Pair<Int, PricedAvatar>
                get() = 6000 + 44 to PricedAvatar("rkPersik", 15)
            val rainbow: Pair<Int, PricedAvatar>
                get() = 6000 + 45 to PricedAvatar("rkRainbow", 10)
            val unknown: Pair<Int, PricedAvatar>
                get() = 6000 + 46 to PricedAvatar("rkUnknown", 15)
            val vsadnica: Pair<Int, PricedAvatar>
                get() = 6000 + 47 to PricedAvatar("rkVsadnica", 15)
            val vsadnicaSister: Pair<Int, PricedAvatar>
                get() = 6000 + 48 to PricedAvatar("rkVsadnica_sister", 15)
            val clown: Pair<Int, PricedAvatar>
                get() = 6000 + 49 to PricedAvatar("rpClown", 40)
            val death: Pair<Int, PricedAvatar>
                get() = 6000 + 50 to PricedAvatar("rpDeath", 40)
            val fallAngel: Pair<Int, PricedAvatar>
                get() = 6000 + 51 to PricedAvatar("rpFallAngel", 40)
            val flowers: Pair<Int, PricedAvatar>
                get() = 6000 + 52 to PricedAvatar("rpFlowers", 10)
            val wtf: Pair<Int, PricedAvatar>
                get() = 6000 + 53 to PricedAvatar("rpWtf", 30)
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
                get() = 7000 + 54 to PricedAvatar("smesh1", 15)
            val smesh2: Pair<Int, PricedAvatar>
                get() = 7000 + 55 to PricedAvatar("smesh2", 15)
            val smesh3: Pair<Int, PricedAvatar>
                get() = 7000 + 56 to PricedAvatar("smesh3", 15)
            val smesh4: Pair<Int, PricedAvatar>
                get() = 7000 + 57 to PricedAvatar("smesh4", 15)
            val smesh5: Pair<Int, PricedAvatar>
                get() = 7000 + 58 to PricedAvatar("smesh5", 15)
            val smesh6: Pair<Int, PricedAvatar>
                get() = 7000 + 59 to PricedAvatar("smesh6", 15)
            val smesh7: Pair<Int, PricedAvatar>
                get() = 7000 + 60 to PricedAvatar("smesh7", 15)
            val smesh8: Pair<Int, PricedAvatar>
                get() = 7000 + 61 to PricedAvatar("smesh8", 15)
            val smesh9: Pair<Int, PricedAvatar>
                get() = 7000 + 62 to PricedAvatar("smesh9", 15)
            val smesh10: Pair<Int, PricedAvatar>
                get() = 7000 + 63 to PricedAvatar("smesh10", 30)
            val smesh11: Pair<Int, PricedAvatar>
                get() = 7000 + 64 to PricedAvatar("smesh11", 20)
            val smesh12: Pair<Int, PricedAvatar>
                get() = 7000 + 65 to PricedAvatar("smesh12", 20)
            val smesh13: Pair<Int, PricedAvatar>
                get() = 7000 + 66 to PricedAvatar("smesh13", 30)
        }
    }

    data object Emoji {
        val pathEmoji = "emojis/"
        val emoji0 = "${pathEmoji}emoji0.webp"
        val emoji1 = "${pathEmoji}emoji1.webp"
        val emoji2 = "${pathEmoji}emoji2.webp"
        val emoji3 = "${pathEmoji}emoji3.webp"
        val emoji4 = "${pathEmoji}emoji4.webp"
        val emoji5 = "${pathEmoji}emoji5.webp"
        val emoji6 = "${pathEmoji}emoji6.webp"
        val emoji7 = "${pathEmoji}emoji7.webp"
        val emojiCook = "${pathEmoji}emoji_cook.webp"
        val emojiWoah = "${pathEmoji}emoji_woah.webp"
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