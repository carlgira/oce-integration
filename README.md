# oce-integration

Sample content and Experience (OCE), integration with OSB and OIC.

* **oce-file-manager:** Java project with utils methods to download and upload files to Content and Experience. (used by OSB in JavaCallout)
* **oce-integration-app**: OSB project with sample integration of the documents API of Content and Experience.
    * oce-documents-api/proxy/files: Three operations of OCE files API (get file information, download file, upload file).
    * oce-documents-api/proxy/folder: Two operation of OCE folder API (get folder info, create folder)
* **OCE_INTEGRATION_01.00.0000.iar**: OIC app with OCE upload operation.
* **oce.postman_collection.json**: Postman file to test operations.

## Requirements 

* maven
* JDK 8
* Soa Suite 12.2.x (jdeveloper)
* Postman

## Configure and Build

1. Create the jar file of the java APP.

Go to **oce-file-manager** compile and copy file to OSB project
```
cd oce-file-manager
mvn clean package
cp target/oce-file-manager-1.0.0.jar ../oce-integration-app/oce-documents-api/resources/
```

2. Open the OSB project and change:
   * User and password in service account oce-documents-api/resources/sa-oce-documents
   * Url of OCE instance in the business service oce-documents-api/business/ref-oce-documents.bix
   * Package and deploy project to OSB

3. Import file to OIC
    * Open your OIC instance and import the IAR file OCE_INTEGRATION_01.00.0000.iar.
    * In the connections sections configure the REST adapter "
oce-rest" with the URL, user and password of the OCE instance.

## Test

Open postman and import the file oce.postman_collection.json. Configure all the variables:
* OCE_URL, OIC_URL, OSB_URL
* OCE_USER, OIC_USER, OSB_USER
* OCE_PASSWORD, OIC_PASSWORD, OSB_PASSWORD

