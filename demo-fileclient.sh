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
#file='/Users/dragisa/STS-WORKSPACE/demo-fileservice/demo/collection.json'
required_file='/Users/dragisa/STS-WORKSPACE/demo-fileservice/storage/collection.json'
filename=${file##*/}
#filetype="plain/text"
callbackUrl='http://localhost:8999/files'


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
echo "(I) sending $file ..."
send_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=multipart/form-data" -H "Authorization: Bearer $token" -F "file=@$file" -X PUT 'http://localhost:8999/files')
echo "status_code="$send_status
echo "done"

#
# scenario 2. send file
# 
#
echo "(II) sending $file ..."
send_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=multipart/form-data" -H "Authorization: Bearer $token" -F "file=@$file" -X POST 'http://localhost:8999/files')
echo "status_code="$send_status
echo "done"

#
# scenario 3. receive and save file (data streaming)
# 
#
echo "(III) receiving $filename ..."
curl -s -X GET "http://localhost:8999/files/download/$filename" -H "Authorization: Bearer $token" > $filename
echo "saved $filename"
echo --- 
cat $filename
echo --- 
echo "processing $filename ..."
echo "processed $filename"

#
# scenario 4. receive file (data streaming)
# 
#
echo "(IV) receive and process $filename ..."
result=$(curl -s -X GET "http://localhost:8999/files/download/$filename" -H "Authorization: Bearer $token")
echo "process $result"
echo "processing done"

#
# scenario 5. request a file with callback url
# 
#
echo "(V) request $required_file ..."
request_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=multipart/form-data" -H "Authorization: Bearer $token" -F "file=@$required_file" -F "callbackUrl=$callbackUrl" -X POST 'http://localhost:8999/files')
echo "received status_code="$request_status
echo "send $required_file on callback URL"
send_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=multipart/form-data" -H "Authorization: Bearer $token" -F "file=@$file" -X PUT $callbackUrl)
echo "received status_code="$send_status" from callback URL"
echo "done"

#
# scenario 6. request a file with callback url
# 
#
echo "(VI) request $required_file ..."
request_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=multipart/form-data" -H "Authorization: Bearer $token" -F "file=@$required_file" -F "callbackUrl=$callbackUrl" -X POST 'http://localhost:8999/files')
echo "received status_code="$request_status
echo "send $required_file on callback URL"
send_status=$(curl -o /dev/null -s -w "%{http_code}" -H "Content-Type=multipart/form-data" -H "Authorization: Bearer $token" -F "file=@$file" -X POST $callbackUrl)
echo "received status_code="$send_status" from callback URL"
echo "done"


