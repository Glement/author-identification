.. How to use

How to use?
===========

This application accepts http request. As default it is running at **localhost:8080**.


Find possible author
--------------------

To find possible author of given project (text), send **POST** request to **localhost:8080/find**, where body should consist of json object with this parameters:

| **nameEn** - Name of the project in english.
| **descEn** - Description of the project in english.
| **keywords** - Keywords specific to this project.

As a response you would get **Long** id of author that algorithm thinks has wrote given text.
Maximum number of connections or requests per connection is limited to 4. You can change it in application properties


Test algorithm
--------------

To test algorithm on your dataset you can send **GET** request to **localhost:8080/testAlgorithm**.
