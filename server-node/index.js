var express = require('express');
var app = express();
var cors = require('cors');
const multer = require('multer');
const mysql = require('mysql');

var con = mysql.createConnection({
    host: "127.0.0.1",
    user: "root",
    password: "111",
    port: 4406,
    database: "mydb"
});

con.connect(function(err) {
    if (err) throw err;
    console.log("Connected!");
});

function runQuery(query) {
  con.query(query, function (err, result) {
    if (err) throw err;
    console.log("Result: " + result);
    return result;
  });
}

// use it before all route definitions
app.use(cors({origin: '*'}));
app.use(express.urlencoded( {extended: true} ));
app.use(express.json());

app.use(function (req, res, next) {
  res.header("Access-Control-Allow-Origin", "*"); // update to match the domain you will make the request from
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

// var storage = multer.diskStorage({
//     destination: function (req, file, cb) {
//       cb(null, 'uploadedFiles/')
//     },
//     filename: function (req, file, cb) {
//       cb(null, file.originalname)
//     }
// })
//create multer instance
// var upload = multer({ storage: storage })

app.get('/', function (req, res) {
  res.send('Skin Infection')
});

// app.post('/uploadImage', upload.single('filetoupload'), function (req, res, next) {  
//     console.log(req.body.description);  
//     runQuery("select * from users");
//     res.status(200).send({'message' : "file uploaded"});
//   });

app.post('/upload_image', async (req, res) => {
  const response = await fetch('http://66.71.52.154:3001/classify_image', {
    method: 'POST',
    headers: {
      'Accept': 'application/json',
      'Content-type': 'application/json'
    },
    body: JSON.stringify(req.body)
  })

  const json = await response.json()

  console.log(json)

  var response1 = await runQuery("insert into users values ('"+req.body.image+"','"+json.class+"');")
  res.status(200).send({
    'message' : 'Image uploaded',
    'result': json.class
  });
})

var server = app.listen(3000, '66.71.52.154', () => {
  var host = server.address().address
  var port = server.address().port
  console.log("Example app listening at http://%s:%s", host, port)
})
