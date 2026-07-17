package org.koharu.miyo.core.database

/**
 * Cross-platform database migration interface.
 */
interface Migration {
	val fromVersion: Int
	val toVersion: Int

	suspend fun migrate(database: DatabaseManager)
}

/**
 * Migration manager for handling database version upgrades.
 */
class MigrationManager(
	private val migrations: List<Migration> = emptyList()
) {
	suspend fun migrateIfNeeded(
	database: DatabaseManager,
	currentVersion: Int,
	targetVersion: Int
) {
		val applicableMigrations = migrations
			.filter { it.fromVersion >= currentVersion && it.toVersion <= targetVersion }
			.sortedBy { it.fromVersion }

		for (migration in applicableMigrations) {
			migration.migrate(database)
		}
	}

	fun addMigration(migration: Migration) {
		(migrations as MutableList).add(migration)
	}
}
