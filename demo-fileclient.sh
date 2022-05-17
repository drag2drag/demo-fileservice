#/bin/bash

#
# demo content
#
#
#file='/Users/dragisa/STS-WORKSPACE/demo-fileservice/demo/bild.jpg'
#file='/Users/dragisa/STS-WORKSPACE/demo-fileservice/demo/login.pdf'
#file='/Users/dragisa/STS-WORKSPACE/demo-fileservice/demo/webex.dmg'
#file='/Users/dragisa/STS-WORKSPACE/demo-fileservice/demo/openapi.yaml'
file='/Users/dragisa/STS-WORKSPACE/demo-fileservice/demo/data.csv'
requestedMovedFile='/Users/dragisa/STS-WORKSPACE/demo-fileservice/demo/login.xxx'
movedFile='/Users/dragisa/STS-WORKSPACE/demo-fileservice/demo/login.pdf'
largeFile='/Users/dragisa/STS-WORKSPACE/demo-fileservice/demo/largefile.wmv'
veryLargeFile='/Users/dragisa/STS-WORKSPACE/demo-fileservice/demo/10GB.bin'
requestedFile='/Users/dragisa/STS-WORKSPACE/demo-fileservice/demo/data.csv'
filename=${file##*/}
requestedMovedFilename=${requestedMovedFile##*/}
movedFilename=${movedFile##*/}
largeFilename=${largeFile##*/}
veryLargeFilename=${veryLargeFile##*/}
requestedFilename=${requestedFile##*/}
callbackUrl='http://localhost:8999/scenario-5/files/callback'
callbackProcessUrl='http://localhost:8999/scenario-5/files/callback-process'


#
# get token from keycloak
#
#
echo "retrieving token ..."
token=$(curl -s -L -X POST 'http://localhost:8181/auth/realms/zurich/protocol/openid-connect/token' -H 'Content-Type: application/x-www-form-urlencoded' -d 'grant_type=client_credentials&client_id=demo-fileservice&client_secret=dc1eaead-214d-47fc-a30c-6f85f44b23ef' | jq -r .access_token)

echo "retrieved token"
echo ---
echo $token
echo ---


#
# scenario 1. send file
# 
#
echo "(I) sending $filename ..."
send_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=multipart/form-data" -H "Authorization: Bearer $token" -F "file=@$filename" -X POST 'http://localhost:8999/scenario-1/files')
echo "status_code="$send_status
echo "done"

#
# scenario 2. send file
# 
#
echo "(II) sending $filename ..."
send_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=multipart/form-data" -H "Authorization: Bearer $token" -F "file=@$filename" -X POST 'http://localhost:8999/scenario-2/files')
echo "status_code="$send_status
echo "done"

#
# scenario 3. receive and save file (data)
# 
#
echo "(III) receiving $filename ..."
curl -s -X GET "http://localhost:8999/scenario-3/files?fileName=$filename" -H "Authorization: Bearer $token" > $filename
echo "saved $filename"
echo --- 
cat $filename
echo --- 
echo "processing $filename ..."
echo "processed $filename"

#
# scenario 4. receive file (data)
# 
#
echo "(IV) receive and process $filename ..."
result=$(curl -s -X GET "http://localhost:8999/scenario-3/files/$filename" -H "Authorization: Bearer $token")
echo "processing "$result
echo "processing done"

# very large file
echo "(IV) receive and process $veryLargeFilename ..."
result=$(curl -s -X GET "http://localhost:8999/scenario-3/files/$veryLargeFilename" -H "Authorization: Bearer $token")
echo "processing "$result
echo "processing done"


#
# scenario 5. request a file with callback url
# 
#
echo "(V) request $requestedFilename..."
request_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=application/x-www-form-urlencoded" -H "Authorization: Bearer $token" -d "file=$requestedFilename" -d "callbackUrl=$callbackUrl" -X POST 'http://localhost:8999/scenario-5/files/request')
echo "received status_code="$request_status
echo "send $requestedFilename on callback URL"
send_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=multipart/form-data" -H "Authorization: Bearer $token" -F "file=@$requestedFilename" -X POST $callbackUrl)
echo "received status_code="$send_status" from callback URL"
echo "done"

#
# scenario 6. request a file with callback url
# 
#
echo "(VI) request $requestedFilename ..."
request_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=application/x-www-form-urlencoded" -H "Authorization: Bearer $token" -d "file=$requestedFilename" -d "callbackUrl=$callbackUrl" -X POST 'http://localhost:8999/scenario-5/files/request')
echo "received status_code="$request_status
echo "send $requestedFilename on callback URL"
send_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=multipart/form-data" -H "Authorization: Bearer $token" -F "file=@$requestedFilename" -X POST $callbackProcessUrl)
echo "received status_code="$send_status" from callback-process URL"
echo "done"

#
# scenario 7. read file transfer jobs
# 
#
echo "(VII) show requested files ..."
curl -s -X GET "http://localhost:8999/scenario-3/files/metadata" -H "Authorization: Bearer $token" | jq .

echo "(VII) scheduled upload"
curl -s -X POST "http://localhost:8999/scenario-7/files/scheduled-upload" -H "Authorization: Bearer $token" | jq .

#
# scenario 8. file moved (303 - see other)
# 
#
echo "(VIII-1) sending $movedFilename ..."
send_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=multipart/form-data" -H "Authorization: Bearer $token" -F "file=@$movedFilename" -X POST 'http://localhost:8999/scenario-1/files')
echo "status_code="$send_status
echo "done"

echo "(VIII-2.1) receiving $requestedMovedFilename ..."
curl -s -X GET "http://localhost:8999/scenario-3/files?fileName=$requestedMovedFilename" -H "Authorization: Bearer $token" | jq .

echo "(VIII-2.2) receiving $requestedFilename (with redirect) ..."
curl -s -L -X GET "http://localhost:8999/scenario-3/files?fileName=$requestedFilename" -H "Authorization: Bearer $token" > $movedFilename
echo "received $movedFilename"


#
# scenario 9. chunked large file upload 
# 
#
echo "(IX) sending large file $largeFilename ..."
# split -b 10m file_name segment_prefix
send_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=multipart/form-data" -H "Transfer-Encoding: chunked" -H "Authorization: Bearer $token" -F "file=@$largeFilename" -X POST 'http://localhost:8999/scenario-1/files')
echo "status_code="$send_status
echo "done"


