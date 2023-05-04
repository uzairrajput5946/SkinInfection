# SkinInfection

The Project Folder contains 3 applications
1. Android Client
Android Application with a basic UI to get images from user for detection of skin infections. 
Build Steps:
a. Build the application using android command line tool or any IDE such as Android studio. The .apk file is also included.
b. Install the application from apk on android phone
2. Node js Server
Backend processing server that receives requests from android application and forwards request to python server. Moreover, store in MYSQL database
Build Steps:
a. Run npm i. This will install all modules
b. Run node index.js. This will start the server and listen for connections on server port 3000
3. Python ML server
Python ML server that uses the generated model from classifier and uses that to generate prediction
a. Install conda
b. Run the file using conda. It will run the ML server
