#
# Dragisa Dragisic, 2022
#
Feature: fileservice test suite

Background:
    * def tokenUrl = 'http://localhost:8181/auth/realms/zurich/protocol/openid-connect/token'
	* url tokenUrl
	* form field grant_type = 'client_credentials'
	* form field client_id = 'demo-fileservice'
	* form field client_secret = 'dc1eaead-214d-47fc-a30c-6f85f44b23ef'
	* method post
	* status 200
	* def accessToken = response.access_token
	* header Authorization = 'Bearer ' + accessToken
	* url 'http://localhost:8999'
	* header Accept = 'application/json'

Scenario: download csv 

Given path 'scenario-3/files/data.csv'
When method get
Then status 200 

Scenario: download json

Given path 'scenario-3/files/collection.json'
When method get
Then status 200 

Scenario: download file 

Given path 'scenario-3/files/asana.dmg'
When method get
Then status 200 

Scenario: download large file 
Given path 'scenario-3/files/5GiB.bin'
When method get
Then status 200 

Scenario: download very large file 

Given path 'scenario-3/files/10GB.bin'
When method get
Then status 200 


Scenario: upload csv

Given path 'scenario-1/files'
And multipart file file = { read: '../demo/data.csv' }
When method post
Then status 200 

Scenario: upload file

Given path 'scenario-1/files'
And multipart file file = { read: '../demo/asana.dmg' }
When method post
Then status 200 


