require('dotenv').config()
const oracleDb = require('./oracleDbRouter.js')
const express = require('express')
const app = express()

app.get('/', function(req, res) {
    oracleDb.getShoppingLists(455, function(shoppingLists) {
        res.send(shoppingLists)
    })
})

app.listen(3000, function () {
  console.log('Example app listening on port 3000!')
})