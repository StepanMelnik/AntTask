# AntTask
A bunch of useful Ant tasks.

## HTTP client with RedirectStrategy 
HTTP client uses RedirectStrategy implementation that automatically redirects all HEAD, GET, POST and DELETE requests.

### Usage

Specify a task definition in ant script:

	<taskdef name="http" classname="com.sme.ant.http.HttpClientTask" classpathref="cp.all" />

Perform HTTP request and print response properties:

```ant
	<http url="http:localhost:8080/report/reports/1000" method="GET"
		printRequest="true" printResponse="false" 
		printRequestHeaders="true" printResponseHeaders="true" 
		failOnUnexpected="true" expectedCode="200"
		followRedirects="true"
		statusProperty="edit.report.status.property"
		entityProperty="edit.report.response.entity">
		<headers>
			<header name="Accept" value="text/html" />
			<header name="Accept-Language" value="en" />
			<header name="Content-Type" value="text/html;charset=UTF-8" />
			<header name="Connection" value="keep-alive" />
		</headers>
		<queries>
			<query name="auth" value="${auth.token}" />
			<query name="page" value="0" />
		</queries>	
	</http>
	
	<echo message="Status = ${edit.report.status.property}, entity = ${edit.report.response.entity}" />
```

Perform HTTP request and save response as a file:

```ant
	<http url="http:localhost:8080/report/reports/1000" method="GET"
		printRequest="true" printResponse="false" 
		printRequestHeaders="true" printResponseHeaders="true" 
		failOnUnexpected="true" expectedCode="200" followRedirects="true"
		statusProperty="edit.report.status.property"
		outFile="Report.pdf">
		<headers>
			<header name="Accept" value="text/html" />
			<header name="Accept-Language" value="en" />
			<header name="Content-Type" value="text/html;charset=UTF-8" />
			<header name="Connection" value="keep-alive" />
		</headers>
		<queries>
			<query name="auth" value="${auth.token}" />
			<query name="documentType" value="pdf" />
			<query name="downloadFlag" value="yes" />
		</queries>	
	</http>
	
	<echo message="Status = ${edit.report.status.property}" />
	<echo message="The response is saved as Report.pdf file." />
```

### TODO
Add POST and other methods to redirect a request.

## Xml2Sql
Ant target that allows to parse a gigantic xml file with millions of rows and save the rows into database

### TODO (close to commit)
