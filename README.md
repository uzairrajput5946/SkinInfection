# SkinInfection

The Project Folder contains 3 applications<br />
1. Android Client
Android Application with a basic UI to get images from user for detection of skin infections.<br />
Build Steps:<br />
a. Build the application using android command line tool or any IDE such as Android studio. The .apk file is also included.<br />
b. Install the application from apk on android phone<br />
2. Node js Server
Backend processing server that receives requests from android application and forwards request to python server. Moreover, store in MYSQL database<br />
Build Steps:<br />
a. Run npm i. This will install all modules<br />
b. Run node index.js. This will start the server and listen for connections on server port 3000<br />
3. Python ML server
Python ML server that uses the generated model from classifier and uses that to generate prediction<br />
a. Install conda<br />
b. Run the file using conda. It will run the ML server
