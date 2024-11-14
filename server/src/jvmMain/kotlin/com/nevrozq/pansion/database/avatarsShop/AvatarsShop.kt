import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update


object AvatarsShop : Table() {
    private val login = this.varchar("login", 30)
    private val avatarId = this.integer("avatarId")

    fun add(login: String, avatarId: Int) {
        transaction {
            AvatarsShop.insert {
                it[AvatarsShop.login] = login
                it[AvatarsShop.avatarId] = avatarId
            }
        }
    }

    fun fetchAvatars(login: String): List<Int> {
        return transaction {
            AvatarsShop.select { (AvatarsShop.login eq login) }.map { it[avatarId] }
        }
    }
}

