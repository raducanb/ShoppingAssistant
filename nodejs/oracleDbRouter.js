var oracledb = require('oracledb');
var dbConfig = require('./dbconfig.js');

oracledb.autoCommit = true
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
module.exports.loginUser = loginUser
module.exports.getShoppingListProducts = getShoppingListProducts
module.exports.searchShoppingLists = searchShoppingLists
module.exports.deleteItem = deleteItem
module.exports.addItem = addItem;

function addItem(last_item_id, name, list_id, category_id) {
    getConnection(function (connection) {
        connection.execute(
            "INSERT INTO PRODUCTS VALUES(:v_id, :v_name, :v_list_id, :v_category_id)",
            {
                v_id: last_item_id + 1,
                v_name: name,
                v_list_id: list_id,
                v_category_id: category_id
            },
            function (err, result) {
                console.log(result);
                connection.close();
            });
    });
}

function deleteItem(itemId) {
    getConnection(function (connection) {
        connection.execute(
            "DELETE FROM PRODUCTS WHERE ID = :v_item_id",
            {
                v_item_id: itemId
            },
            function (err, result) {
                console.log(result);
                connection.close();
            });
    });
}

function searchShoppingLists(userId, searchText, callback) {
    getConnection(function (connection) {
        connection.execute(
            "SELECT * " +
            "FROM LISTS " +
            "WHERE name LIKE '%' ||" + searchText + "|| '%' AND user_id = :v_user_id",
            {
                v_user_id: userId
            },
            function (err, result) {
                if (err) {
                    callback([]);
                } else {
                    callback(result.rows);
                }
                connection.close();
            });
    });
}

function getShoppingListProducts(shoppingListId, callback) {
    getConnection(function (connection) {
        connection.execute(
            "SELECT p.ID, p.NAME, p.CATEGORY_ID, c.NAME AS CATEGORY_NAME " +
            "FROM PRODUCTS p JOIN CATEGORIES c on p.CATEGORY_ID = c.ID " +
            "WHERE p.LIST_ID = :v_list_id",
            {
                v_list_id: shoppingListId
            },
            function (err, result) {
                if (err) {
                    callback([]);
                } else {
                    callback(result.rows);
                }
                connection.close();
            });
    });
}

function loginUser(username, password, callback) {
    getConnection(function (connection) {
        connection.execute(
            "BEGIN :user_id := auth.login_with_credentials(:v_username, :v_password); END;",
            {
                user_id: { dir: oracledb.BIND_OUT, type: oracledb.NUMBER, maxSize: 40 },
                v_username: username,
                v_password: password
            },
            function (err, result) {
                if (err) {
                    console.error(err.message);
                    callback(false, null, err.message.split("\n")[0]);
                    connection.close()
                    return;
                }
                callback(true, result.outBinds, null);
                console.log(result.outBinds);
                connection.close()
            });
    })
}

var shoppingListsCallback;
function getShoppingLists(userId, page, callback) {
    shoppingListsCallback = callback
    getConnection(function (connection) {
        connection.execute(
            "BEGIN :ret := shopping_lists.get_for_user_id(:v_user_id, :v_page); END;",
            {
                ret: { type: oracledb.CURSOR, dir: oracledb.BIND_OUT },
                v_user_id: userId,
                v_page: page
            },
            function (err, result) {
                if (err) {
                    console.error(err.message);
                    connection.close();
                    return;
                }
                fetchRowsFromRS(connection, result.outBinds.ret, 20, callback);
            });
    })
}

var rowsGot = [];
function fetchRowsFromRS(connection, resultSet, numRows, callback) {
    rowsGot = []
    resultSet.getRows(
        numRows,
        function (err, rows) {
            if (err) {
                console.log(err);
                doClose(connection, resultSet);
            } else if (rows.length === 0) {
                doClose(connection, resultSet);
            } else if (rows.length > 0) {
                shoppingListsCallback(rows);
                fetchRowsFromRS(connection, resultSet, numRows);
            }
        });
}

function doRelease(connection) {
    connection.close(
        function (err) {
            if (err) { console.error(err.message); }
        });
}

function doClose(connection, resultSet) {
    resultSet.close(
        function (err) {
            if (err) { console.error(err.message); }
            doRelease(connection);
        });
}

function getConnection(completion) {
    if (connectionPool === undefined) {
        setTimeout(function () {
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
            completion(connection);
        }
    )
}