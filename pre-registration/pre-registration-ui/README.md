# Angular Build &amp; Deployment Guide

To start off we will be needing a Virtual Machine (VM) of at least 1 core processor and 4 GB memory with Red Hat OS installed. Please follow the following steps after the pre-requisites are met.

- Install GIT – GIT is a version control software, we will be using it to download the source code on our system. Follow the following steps to install GIT on your system.
  - yum update (to update all the RHEL packages)
  - yum install git (to install git)
  - git --version (to verify if git is installed or not)
- Install Docker – Dockeris thesoftware used to create a runnable image from the existing source code. Follow the following steps to install Docker on your system.
  - yum install docker (to install docker)
  - docker --version (to verify if docker is installed on your system)
- Install node.js – To build the angular code using angular cli that runs on node. Follow the following steps to install node.js on your system.
  - yum install rh-nodejs8 (to install node )
  - scl enable rh-nodejs8 bash (to add node.js to environment)
  - node --version (to verify node.js is working)
- Install angular cli – To install angular cli for building the code into deployable artifacts. Follow the following steps to install angular cli on your system.
  - npm install -g @angular/cli (to install angular cli)
  - ng --version (to verify angular is installed in system)
- Check out the source code from GIT – To download the source code from git. Follow the following steps to download source code on your system.
  - git clone https://github.com/mosip/mosip.git (to clone the source code repository from git)
- Build the code – Follow the following steps to build the source code on your system.
  - Navigate to the pre-registration-ui directory inside the cloned repository. Then run the following command in that directory
  - ng build --prod --base-href . (to build the code)
- Build Docker Image – Follow the following steps to build docker image on your system.
  - docker build -t \&lt;name\&gt; . (to build the docker image, replace \&lt;name\&gt; with the name of the image you want, &quot;.&quot; Signifies the current directory from where the docker file has to be read.
- Run the docker image – Follow the following steps to build docker image on your system.
  - docker run –d –p 80:80 --name \&lt;container name\&gt; \&lt;image name\&gt; (to run the docker image created with the previous step,-d signifies to run the container in detached mode, -p signifies the port mapping left side of the&quot;:&quot; is the external port that will be exposed to the outside world and right side is the internal port of the container that is mapped with the external port. Replace \&lt;container name\&gt; with the name of your choice for the container, replace \&lt;image name\&gt; with the name of the image specified in .the previous step)

Now you can access the user interface over the internet by accessing the public IP of VM via browser.
