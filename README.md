# RTO Backend

> Backend application for the RTO service.

## Run in local

### Prerequisites

#### [Postgres](https://www.postgresql.org/)
1. Download and install postgres by following the steps [here](https://www.postgresql.org/download/) corresponding to your operating system
2. Create a DB cluster following this [guide](https://www.postgresql.org/docs/current/creating-cluster.html)
3. Start the postgres server following this [guide](https://www.postgresql.org/docs/current/server-start.html)
4. Create a user ```rtoadmin``` using the utility [here](https://www.postgresql.org/docs/current/app-createuser.html) the user must be able to login with username and password
5. Create a database called ```rto_db``` using utility [here](https://www.postgresql.org/docs/current/app-createdb.html) the owner of the database must be the user created in above step

#### [JDK](https://bell-sw.com/pages/downloads/#jdk-25-lts)
1. Download JDK 25 from any vendor of your preference provided one [here](https://bell-sw.com/pages/downloads/#jdk-25-lts)
2. Follow the installation guide to install jdk provided the guide [here](https://docs.bell-sw.com/liberica-jdk/25b37/general/install-guide/) for the jdk above

#### Intellij IDE
1. Download Intellij IDE from [here](https://www.jetbrains.com/idea/download/?section=windows)

### Clone code and execute in local
1. Clone the code from repository using command ```git clone https://github.com/ElephaSolutions/rtoappbyourself.git```
2. switch to branch ```spring-backend```
3. Open class ```VehicleManagementApplication.java``` in ```src/main/java/com/elepha/solutions/rto```
4. Execute the application by clicking on the play button displayed in left gutter when the above class is open in editor.