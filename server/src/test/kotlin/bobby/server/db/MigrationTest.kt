package bobby.server.db

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager
import kotlin.test.assertTrue

class MigrationTest {
    @Test
    fun testMigrationsCreateAllTables() {
        PostgreSQLContainer("postgres:16-alpine").use { db ->
            db.start()
            Flyway.configure().dataSource(db.jdbcUrl, db.username, db.password).load().migrate()
            val expected = setOf("users", "contributor_skills", "questions", "entries", "confirmations", "flags")
            DriverManager.getConnection(db.jdbcUrl, db.username, db.password).use { conn ->
                val rs = conn.metaData.getTables(null, "public", "%", arrayOf("TABLE"))
                val actual = generateSequence { if (rs.next()) rs.getString("TABLE_NAME") else null }.toSet()
                assertTrue(actual.containsAll(expected))
            }
        }
    }
}
