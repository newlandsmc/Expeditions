package me.cookie.rejoinrewards.data.sql.database

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.cookie.rejoinrewards.RejoinRewards
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class H2Storage {
    private val plugin = JavaPlugin.getPlugin(RejoinRewards::class.java)
    private val logger = plugin.logger

    private var connection: Connection? = null

    fun initTable(
        name: String,
        columns: List<String>,
    ) = runBlocking {
        launch {
            var sqlString = "CREATE TABLE IF NOT EXISTS $name "
            var valuesString = "("
            for((i, column) in columns.withIndex()){
                if(i+1 == columns.size){
                    valuesString += "$column)"
                    break
                }
                valuesString += "$column, "
            }
            sqlString += "$valuesString;"


            val preparedStatement = connection!!.prepareStatement(sqlString)

            try{
                preparedStatement.execute()
                logger.info("Successfully initialized table $name with values $valuesString")
            }catch (ex: SQLException){
                ex.printStackTrace()
            }
        }
    }

    fun insertIntoTable(
        table: String,
        columns: List<String>,
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
        }
    }

    fun updateColumnsWhere(
        table: String,
        columns: List<String>,
        where: String,
        values: Values,
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
                logger.info("Successfully updated $columns to $valuesString where $where")
            }catch (ex: SQLException){
                ex.printStackTrace()
            }
        }
    }

    fun getRowsWhere(
        table: String,
        column: String,
        where: String,
        limit: Int = 1,
    ): List<Values> {
        val preparedStatement = connection!!.prepareStatement("SELECT $column FROM $table WHERE $where;")
        val resultList = mutableListOf<Values>()
        val results = preparedStatement.executeQuery()
        var i = 0
        while (results.next() && i < limit){
            i++
            resultList.add(Values(results.getString(1)))
        }
        return resultList
    }

    fun getTable(table: String,
                 orderColumn: String = "",
                 orderStyle: String = "",
                 limit: Int = Int.MAX_VALUE,
    ): List<Values> {
        val columnsStatement = connection!!
            .prepareStatement(
                "SELECT COLUMN_NAME " +
                        "FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_NAME  = '$table' " +
                        "ORDER BY ORDINAL_POSITION;"
            )
        val columnsList = mutableListOf<String>()
        val columnsResults = columnsStatement.executeQuery()

        while (columnsResults.next()){
            columnsList.add(columnsResults.getString(1))
        }

        val rows = mutableListOf(Values(*columnsList.toTypedArray()))

        var sqlQuery = "SELECT * FROM $table"

        if(orderColumn.isNotBlank()){
            sqlQuery += " ORDER BY $orderColumn $orderStyle;"
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

        return rows
    }

    fun connect(){
        if(connection != null){
            if(connection!!.isClosed) {
                try{
                    connection = createConnection()
                    logger.info("Connected to playerData!")
                }catch (ex: SQLException){
                    ex.printStackTrace()
                }
                return
            }
            logger.info("Is already connected to playerData!")
        }else{
            try{
                connection = createConnection()
                logger.info("Connected to playerData!")
            }catch (ex: SQLException){
                ex.printStackTrace()
            }
        }
    }

    private fun createConnection(): Connection {
        Class.forName("org.h2.Driver")
        return DriverManager.getConnection(
            "jdbc:h2:${plugin.dataFolder.absolutePath}\\data\\playerData"
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