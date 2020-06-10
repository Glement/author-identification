.. How to run


How to run?
===========

If you want to run it you can use either IDEA of your choise, or maven.
Run this comman from root directory of this project if you want to use this project with supplied database ( recommended only for testing)

Generates jar with default database

.. code-block:: none

	mvn clean install

If you want to use your own database, fill the neccesary fields in application-production.properties in **author-identification/src/main/resources/application-production.properties** and run with this command

.. code-block:: none

	mvn clean package -P production
	
Generates jar with the database specified in application-production.properties
