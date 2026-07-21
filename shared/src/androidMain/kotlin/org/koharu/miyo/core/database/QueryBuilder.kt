package org.koharu.miyo.core.database

/**
 * Cross-platform SQL query builder.
 */
class QueryBuilder {
	private val selectClause = StringBuilder()
	private val fromClause = StringBuilder()
	private val whereConditions = mutableListOf<String>()
	private val orderByClause = StringBuilder()
	private val limitClause = StringBuilder()
	private val offsetClause = StringBuilder()
	private val groupByClause = StringBuilder()
	private val havingClause = StringBuilder()

	fun select(vararg columns: String): QueryBuilder {
		selectClause.append("SELECT ")
		selectClause.append(columns.joinToString(", "))
		return this
	}

	fun selectAll(): QueryBuilder {
		selectClause.append("SELECT *")
		return this
	}

	fun from(table: String): QueryBuilder {
		fromClause.append(" FROM ")
		fromClause.append(table)
		return this
	}

	fun where(condition: String): QueryBuilder {
		whereConditions.add(condition)
		return this
	}

	fun and(condition: String): QueryBuilder {
		if (whereConditions.isNotEmpty()) {
			whereConditions.add("AND $condition")
		} else {
			whereConditions.add(condition)
		}
		return this
	}

	fun or(condition: String): QueryBuilder {
		if (whereConditions.isNotEmpty()) {
			whereConditions.add("OR $condition")
		} else {
			whereConditions.add(condition)
		}
		return this
	}

	fun orderBy(column: String, ascending: Boolean = true): QueryBuilder {
		orderByClause.append(" ORDER BY ")
		orderByClause.append(column)
		orderByClause.append(if (ascending) " ASC" else " DESC")
		return this
	}

	fun limit(count: Int): QueryBuilder {
		limitClause.append(" LIMIT ")
		limitClause.append(count)
		return this
	}

	fun offset(count: Int): QueryBuilder {
		offsetClause.append(" OFFSET ")
		offsetClause.append(count)
		return this
	}

	fun groupBy(column: String): QueryBuilder {
		groupByClause.append(" GROUP BY ")
		groupByClause.append(column)
		return this
	}

	fun having(condition: String): QueryBuilder {
		havingClause.append(" HAVING ")
		havingClause.append(condition)
		return this
	}

	fun build(): String {
		val query = StringBuilder()

		query.append(selectClause)
		query.append(fromClause)

		if (whereConditions.isNotEmpty()) {
			query.append(" WHERE ")
			query.append(whereConditions.joinToString(" "))
		}

		query.append(groupByClause)
		query.append(havingClause)
		query.append(orderByClause)
		query.append(limitClause)
		query.append(offsetClause)

		return query.toString()
	}

	fun buildWithArgs(): Pair<String, List<String>> {
		// For prepared statements with arguments
		// This is a simplified version - in production, use parameterized queries
		return Pair(build(), emptyList())
	}
}
