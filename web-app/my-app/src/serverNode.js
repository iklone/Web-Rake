const express = require('express');
const cors = require('cors');
const app = express();
const mysql = require('mysql');
const SELECT_ALL_TASKS_QUERY = "SELECT * FROM Task";

app.use(cors());
app.get('/tasks', (req, res) => {
    console.log("GO TO /tasks TO SEE ALL TASKS");

    const conn = mysql.createConnection({
        host: 'mysql.cs.nott.ac.uk',
        user: 'psyjct',
        password: '1234Fred',
        database: 'psyjct'
    });

    conn.query(SELECT_ALL_TASKS_QUERY, (err,rows, fields) => {
        if (err) {
            return err;
        }
    })
});

app.listen(4000, () => {
    console.log("Server is up and listening on 4000...")
});