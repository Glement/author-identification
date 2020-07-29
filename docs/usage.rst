.. How to use

How to use?
===========

This application accepts http request. As default it is running at **localhost:8080/author-identification/**.

Every response that has ACCEPTED code would either start classifier initializing or it means that classifier already initializing.

Find possible author
--------------------

To find possible author of given project (text), send **POST** request to **localhost:8080/author-identification/find**, where body should consist of json object with this parameters:

ProjectDto
| **ID** - author ID, not used in updates and deletes since the ID is defined by the address
| **nameEn** - Name of the project in english.
| **descEn** - Description of the project in english.
| **keywords** - Keywords specific to this project.

AuthorDto
| **id** - author ID

As a response if classifier is not initialized you would get HTTP ACCEPTED code. If it is initialized you would get HTTP OK response and List of
SearchResult which have AuthorDto and Double score, sorted in descending order, higher the score the more likely the author is the real author.
Max size of the list is 10, if there is less Authors in database then the same number would be in the list.
Maximum number of connections or requests per connection is limited to 4. You can change it in application properties.


Test algorithm
--------------

To test algorithm on your dataset you can send **GET** request to **localhost:8080/author-identification/testAlgorithm**.
Response is either the Double with the accuracy in percent with HTTP OK response or Accepted HTTP code if classifier is not yet initialized.

Refresh algorithm
--------------

To refresh algorithm you can send **GET** request to **localhost:8080/author-identification/refresh-classifier**.
Response is HTTP ACCEPTED.


CRUD Operations
---------------

| **GET** to **localhost:8080/author-identification/author** to get List of AuthorDto
| **GET** to **localhost:8080/author-identification/project** to get List of ProjectDto
| **GET** to **localhost:8080/author-identification/project/#/authors** to get project AuthorDto list where # equals id of desired project with HTTP code OK, NOT_FOUND code returned when project not found
| **GET** to **localhost:8080/author-identification/authors/#/projects** to get author ProjectDto list where # equals id of desired author with HTTP code OK, NOT_FOUND code returned when author not found
| **DELETE** to **localhost:8080/author-identification/author/#** to delete author where # equals id of desired author, response is OK if author deleted, NOT_FOUND if author not found
| **DELETE** to **localhost:8080/author-identification/project/#** to delete project where # equals id of desired project,  response is OK if project deleted, NOT_FOUND if project not found
| **PUT** to **localhost:8080/author-identification/project/#** to update project where # equals id of desired project, request body as json must be as follows: **nameEn** - Name of the project in english. **descEn** - Description of the project in english. **keywords** - Keywords specific to this project. Response with ProjectDto and status OK if updated. NOT_FOUND if project not found.
| **PUT** to **localhost:8080/author-identification/author/#/project/$** to add project $ to author #, response contains ProjectDto list of projects that belong to author with status OK, NOT_FOUND if author or project aren't found.
| **DELETE** to **localhost:8080/author-identification/author/#/project/$** to remove project $ from author #, response contains ProjectDto list of projects that belong to author with status OK, NOT_FOUND if author or project aren't found.
| **PUT** to **localhost:8080/author-identification/project/#/author/$** to add author $ to project #, response contains AuthorDto list of authors that belong to project with status OK, NOT_FOUND if author or project aren't found.
| **DELETE** to **localhost:8080/author-identification/project/#/author/$** to remove author $ from project #, response contains AuthorDto list of authors that belong to author with status OK, NOT_FOUND if author or project aren't found.
