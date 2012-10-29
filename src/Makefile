default: Test.class

Test.class: Test.java cache/*.java classifier/*.java database/*.java hierarchy/*.java query/*.java searcher/*.java summarizer/*.java util/*.java
	javac -cp ".:lib/*" Test.java

# make run ARG1=api-key ARG2=min-specificty ARG3=min-coverage ARG4=host
run: Test.class
	java -cp ".:lib/*" RelevanceFeedback "${ARG1}" "${ARG2}" "$(ARG3)" "${ARG4}"

# Or you can run
# ./run.sh <ApiKey> <min_speciality> <min_coverage> <hostname> 

# Or you can directly run the java command
#java -cp ".:lib/*" Test <ApiKey> <min_speciality> <min_coverage> <hostname>

clean:
	rm *.class cache/*.class classifier/*.class database/*.class hierarchy/*.class query/*.class searcher/*.class summarizer/*.class util/*.class