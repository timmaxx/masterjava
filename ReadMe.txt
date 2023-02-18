4. Подключаем логирование с общими настройкам
0615


После 4_10
При попытке выполнить UserDaoTest
Выскакивало:
Unable to make protected final java.lang.Class java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain) throws java.lang.ClassFormatError accessible: module java.base does not "opens java.lang" to unnamed module @9e89d68

На StackOver нашёл:
https://stackoverflow.com/questions/41265266/how-to-solve-inaccessibleobjectexception-unable-to-make-member-accessible-m
How to solve InaccessibleObjectException ("Unable to make {member} accessible: module {A} does not 'opens {package}' to {B}") on Java 9?

В моём случае:
{member} = {protected final java.lang.Class java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain) throws java.lang.ClassFormatError}
{A} = {java.base}
{package} = {java.lang}
{B} = {unnamed module @9e89d68}

Дополнительная статья:
https://nipafx.dev/java-modules-reflection-vs-encapsulation/

В IDEA в EditConfiguration в поле "VM options..." дописал:
--add-opens java.base/java.lang=ALL-UNNAMED

Эта ошибка ушла.


Но появилась такая:
The authentication type 10 is not supported. Check that you have configured the pg_hba.conf file to include the client's IP address or subnet, and that it is using an authentication scheme supported by the driver.

Погуглил, нашёл:
https://stackoverflow.com/questions/64210167/unable-to-connect-to-postgres-db-due-to-the-authentication-type-10-is-not-suppor

В файле
C:\Program Files\PostgreSQL\13\data\pg_hba.conf
нашёл строку:
host    all             all             127.0.0.1/32            scram-sha-256
поменял на:
host    all             all             127.0.0.1/32            trust

Ошибка ушла.
