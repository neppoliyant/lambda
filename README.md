
***REMOVED***

***REMOVED***

***REMOVED***
***REMOVED***
***REMOVED***

***REMOVED***

LAMBDA_JAR - path to the lambda jar.
LAMBDA_HANDLER - the lambda class which implements RequestHandler<I,O>
LAMBDA_HTTP_METHOD - only GET or POST is currently supported. default is POST
LAMBDA_RESOURCE - the http path to map to the handler (for example "/hello")
LAMBDA_TIMEOUT - the maximum amount of time to wait for a call to finish



***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***

***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***


***REMOVED***
***REMOVED***
***REMOVED***


curl -X POST -d 'shane' 'http://localhost:8080/hello'


docker run -d -e "LAMBDA_TIMEOUT=3" -e "LAMBDA_HANDLER=com.digitalsanctum.lambda.samples.HelloPojo" --name api -p 8080:8080 digitalsanctum/lambda-api

curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Shane", "lastName":"Witbeck"}' 'http://localhost:8080/hello'
curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Shane", "lastName":"Witbeck"}' 'http://162.243.187.105:8080/hello'
curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Shane", "lastName":"Witbeck"}' 'http://lambda-api-4f03eb87.digitalsanctum.svc.tutum.io:8080/hello'


***REMOVED***

***REMOVED***

***REMOVED***

***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***


***REMOVED***

***REMOVED***

***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***
***REMOVED***



