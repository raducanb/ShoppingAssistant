var oracledb = require('oracledb');
var dbConfig = require('./dbconfig.js');

oracledb.outFormat = oracledb.OBJECT
var connectionPool;
function createPool(callback) {
    oracledb.createPool(
        {
            user: dbConfig.user,
            password: dbConfig.password,
            connectString: dbConfig.connectString
        },
        function (err, pool) {
            if (err) throw err
            connectionPool = pool
            // callback()
        }
    );
}
createPool();

module.exports.getShoppingLists = getShoppingLists

function getShoppingLists(userId, callback) {
    if (connectionPool === undefined) {
        setTimeout(function() {
            getShoppingLists(shoppingLists)
        }, 3000);
        return;
    }
    connectionPool.getConnection(
        function (err, connection) {
            if (err) {
                console.error(err.message);
                return;
            }
            connection.execute(
                "SELECT * " +
                "FROM lists " +
                "WHERE user_id = :id",
                [userId],
                function (err, result) {
                    if (err) {
                        console.error(err.message)
                        connection.close()
                        return
                    }
                    callback(result.rows)
                    console.log(result.rows)
                    connection.close()
                });
        }
    )
}