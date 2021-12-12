package me.cookie.rejoinrewards.data.sql.database

import dev.emortal.munchcrunch.database.DBCache
import dev.emortal.munchcrunch.database.Values
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.cookie.rejoinrewards.MethodHolder
import me.cookie.rejoinrewards.RejoinRewards
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class SQLStorage(
    private val host: String,
    private val port: String,
    private val username: String,
    private val password: String,
    private val database: String
) {
    private val logger = JavaPlugin.getPlugin(RejoinRewards::class.java).logger

    private var connection: Connection? = null


    fun insertIntoTable(
        table: String,
        columns: List<String>,
        whatToRun: MethodHolder = MethodHolder(),
        vararg data: Values
    ) = runBlocking {
        launch {
            var values = ""
            var sqlColumns = "("
            for(i in 1..columns.size){
                if(i == columns.size){
                    sqlColumns += columns[i-1]
                    continue
                }
                sqlColumns += "${columns[i-1]},"
            }
            sqlColumns += ")"
            for(i in 1..data.size){
                values += "("
                for(y in 1..data[i-1].values.size){
                    if(y == data[i-1].values.size){
                        values += if(data[i-1].values[y-1] is String){
                            "'${data[i-1].values[y-1]}'"
                        }else{
                            "${data[i-1].values[y-1]}"
                        }
                        continue
                    }
                    values += if(data[i-1].values[y-1] is String){
                        "'${data[i-1].values[y-1]}',"
                    }else{
                        "${data[i-1].values[y-1]},"
                    }
                }
                if(i == data.size){
                    values += ")"
                    continue
                }
                values += "),"
            }

            val preparedStatement = connection!!.prepareStatement("INSERT INTO $table $sqlColumns VALUES $values;")

            try{
                preparedStatement.executeUpdate()
                logger.info("Successfully inserted $values into $table")
            }catch (ex: SQLException){
                ex.printStackTrace()
            }
            whatToRun.codeToRun()
        }
    }

    fun updateColumnsWhere(
        table: String,
        columns: List<String>,
        where: String,
        values: Values,
        whatToRun: MethodHolder = MethodHolder()
    ) = runBlocking {
        launch {
            var sqlString = "UPDATE $table SET "
            var valuesString = ""

            for(i in 1..columns.size){
                if(i == columns.size){
                    valuesString += if(values.values[i-1] is String){
                        "${columns[i-1]} = '${values.values[i-1]}' "
                    }else{
                        "${columns[i-1]} = ${values.values[i-1]} "
                    }
                    continue
                }
                valuesString += if(values.values[i-1] is String){
                    "${columns[i-1]} = '${values.values[i-1]}', "
                }else{
                    "${columns[i-1]} = ${values.values[i-1]}, "
                }
            }
            sqlString += valuesString
            sqlString += "WHERE $where;"
            val preparedStatement = connection!!.prepareStatement(sqlString)
            try{
                preparedStatement.executeUpdate()
                logger.info("Successfully updated $columns to ${valuesString}where $where")
            }catch (ex: SQLException){
                ex.printStackTrace()
            }
            whatToRun.codeToRun()
        }
    }

    fun getRowsWhere(
        table: String,
        column: String,
        where: String,
        limit: Int = 1,
        dbCache: DBCache,
        whatToRun: MethodHolder = MethodHolder()
    ) = runBlocking {
        launch {
            val preparedStatement = connection!!.prepareStatement("SELECT $column FROM $table WHERE $where")
            val resultList = mutableListOf<Values>()
            val results = preparedStatement.executeQuery()
            var i = 0
            while (results.next() && i < limit){
                i++
                resultList.add(Values(results.getString(1)))
            }
            dbCache.rows = resultList
            whatToRun.codeToRun()
        }
    }

    fun getTable(table: String,
                 orderColumn: String = "",
                 orderStyle: String = "",
                 limit: Int = Int.MAX_VALUE,
                 dbCache: DBCache,
                 whatToRun: MethodHolder = MethodHolder()
    ) = runBlocking {
        launch {
            val columnsStatement = connection!!
                .prepareStatement(
                    "SELECT COLUMN_NAME " +
                            "FROM INFORMATION_SCHEMA.COLUMNS " +
                            "WHERE TABLE_NAME  = '$table' " +
                            "ORDER BY ORDINAL_POSITION"
                )
            val columnsList = mutableListOf<String>()
            val columnsResults = columnsStatement.executeQuery()

            while (columnsResults.next()){
                columnsList.add(columnsResults.getString(1))
            }

            val rows = mutableListOf(Values(*columnsList.toTypedArray()))

            var sqlQuery = "SELECT * FROM $table"

            if(orderColumn.isNotBlank()){
                sqlQuery += " ORDER BY $orderColumn $orderStyle"
            }

            val tablePreparedStatement = connection!!.prepareStatement(sqlQuery)
            val tableResults = tablePreparedStatement.executeQuery()

            val tempList = mutableListOf<Any>()

            var i = 0
            while (tableResults.next() && i < limit){
                i++
                for (y in 1..columnsList.size){
                    tempList.add(tableResults.getString(y))
                }
                rows.add(Values(*tempList.toTypedArray()))
                tempList.clear()
            }
            dbCache.tableRows = rows
            whatToRun.codeToRun()
        }
    }

    fun connect(){
        if(connection != null){
            if(connection!!.isClosed) {
                try{
                    connection = createConnection()
                    logger.info("Connected to ${database}!")
                }catch (ex: SQLException){
                    ex.printStackTrace()
                }
                return
            }
            logger.info("Is already connected to ${database}!")
        }else{
            try{
                connection = createConnection()
                logger.info("Connected to ${database}!")
            }catch (ex: SQLException){
                ex.printStackTrace()
            }
        }
    }

    private fun createConnection(): Connection {
        Class.forName("org.mariadb.jdbc.Driver")
        return DriverManager.getConnection(
            "jdbc:mariadb://${host}:${port}/${database}",
            username, password
        )
    }
    fun disconnect() {
        if (connection == null){
            logger.info("No connection to disconnect!")
            return
        }
        if(connection!!.isClosed) {
            logger.info("No connection to disconnect!")
            return
        }
        logger.info("Closed connection")
        connection?.close()
    }
}