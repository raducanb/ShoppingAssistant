require('dotenv').config()
const oracleDb = require('./oracleDbRouter.js')
const express = require('express')
const app = express()

var bodyParser = require('body-parser')
app.use(bodyParser.json() );
app.use(bodyParser.urlencoded({
  extended: true
})); 

app.get('/searchShoppingLists', function(req, res) {
    oracleDb.searchShoppingLists(req.query.user_id, req.query.search_text, function(shoppingLists) {
        respObj = {}
        respObj.lists = shoppingLists;
        res.send(respObj);
    })
})

app.get('/deleteItem', function(req, res) {
    oracleDb.deleteItem(req.query.item_id);
})

app.get('/addItem', function(req, res) {
    oracleDb.addItem(req.query.last_item_id, req.query.name, req.query.list_id, req.query.category_id);
})

app.get('/shoppingLists', function(req, res) {
    oracleDb.getShoppingLists(req.query.user_id, req.query.page, function(shoppingLists) {
        respObj = {}
        respObj.lists = shoppingLists;
        res.send(respObj);
    })
})

app.get('/shoppingListProducts', function(req, res) {
    oracleDb.getShoppingListProducts(req.query.shopping_list_id, function(products) {
        res.send(products);
    })
})

app.post('/login', function(req, res) {
    oracleDb.loginUser(req.body.username, req.body.password, function(success, resp, errorMessage) {
        var respObj = {}
        respObj.success = success;
        if (resp != null) {
            respObj.user_id = resp.user_id;
        }
        respObj.error_message = errorMessage;
        res.send(JSON.stringify(respObj));
    })
})

app.listen(3000, function () {
  console.log('Example app listening on port 3000!')
})