const express = require('express');
const cors = require('cors');
const app = express();

app.use(cors());
app.listen(4000, () => {
    console.log("Server is up and listening on 4000...")
});




