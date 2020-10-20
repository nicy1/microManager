# MicroManager

The project assumes the systems has Micromanager already installed.
If not, please follow the instructions:
 * [Ubuntu](https://micro-manager.org/wiki/Linux_installation_from_source_(Ubuntu))
 * [Windows](https://micro-manager.org/wiki/Download_Micro-Manager_Latest_Release)

In the main package there is the main  method, it will start the server, and the main functionalities are in the Connection class.
This package contains also a Client class, that is a client for testing the methods of the server.

In the features package there some useful classes that are intended for the most important methods, according to design.md file.

The program leverages [ffmpeg](https://www.ffmpeg.org/), a is a collection of libraries and tools to record, convert and stream audio and video. 
This software is supposed to be already deployed on your machine, you can download it [here](https://www.ffmpeg.org/download.html).
On Ubuntu, it is possible to run the following commands:

		sudo add-apt-repository ppa:jonathonf/ffmpeg-3
		sudo apt-get update
		sudo apt-get install ffmpeg libav-tools x264 x265
		
Maven command:

     mvn install:install-file -Dfile=lib/ij.jar -DgroupId=com.sample -DartifactId=ij -Dversion=1.0 -Dpackaging=jar
     
     mvn install:install-file -Dfile=lib/MMJ_.jar -DgroupId=com.sample -DartifactId=mmj -Dversion=1.0 -Dpackaging=jar
     
     mvn install:install-file -Dfile=lib/MMCoreJ.jar -DgroupId=com.sample -DartifactId=mmcorej -Dversion=1.0 -Dpackaging=jar
     
Modify the `secret.txt` file in order to communicate with the client.
The client must have the same secret.

To communicate with `LiveMicro Server` you need to trust its CA. Since in the testbed it has a self-signed certificate, it is necessary to add the server certificate itself in the Trustore.

		keytool -import -alias ca -file server.cert -keystore cacerts -storepass changeIt

In order to run the project, you can run the following commands in the project folder:
		
		mvn verify
		
		mvn exec:java -Dexec.mainClass=main.Main
		
		
For testing purpose, the LiveMicro Server should have a certificate and this should be included in trustore `trustStore`.

If you would like to add multiple microscope in the project, remember to change the value of `MICRO_ID` 
in `src/main/java/utils/Constants` file, according to the `LiveMicro Server` version.

## Real microscope

We tested the program with an Olympus IX81. In particular we use an Olympus IX81 microscope equipped with a prior and Qimaging camera.

In this context, moving the microscope of 2600 either x or y, corresponds to move the microscope of 1 inch.
