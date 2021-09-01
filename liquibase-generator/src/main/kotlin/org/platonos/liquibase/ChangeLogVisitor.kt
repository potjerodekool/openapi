package org.platonos.liquibase

import liquibase.change.Change
import liquibase.change.core.CreateTableChange
import liquibase.changelog.ChangeSet
import liquibase.changelog.DatabaseChangeLog

class ChangeLogVisitor {

    fun visit(databaseChangeLog: DatabaseChangeLog) {
        databaseChangeLog.changeSets
            .filterNotNull()
            .forEach { changeSet ->
            visit(changeSet)
        }
    }

    private fun visit(changeSet: ChangeSet) {
        changeSet.changes
            .filterNotNull()
            .forEach { change ->
                visit(change)
            }
    }

    private fun visit(change: Change) {
        if (change is CreateTableChange) {

        } else {
            TODO("Not yet implemented")
        }
    }
}