package database

import PlatformConfiguration
//import app.cash.sqldelight.db.QueryResult
//import app.cash.sqldelight.db.SqlDriver
//import app.cash.sqldelight.db.SqlSchema
//import app.cash.sqldelight.driver.worker.WebWorkerDriver
//import kotlinx.coroutines.Dispatchers
//import org.w3c.dom.Worker
//
//actual class DbDriverFactory actual constructor(platformConfiguration: PlatformConfiguration) {
//    actual fun createDriver(schema: SqlSchema<QueryResult.Value<Unit>>, name: String): SqlDriver
//            = WebWorkerDriver(
//        Worker(
//            js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)""")
//        )
//    )
//}