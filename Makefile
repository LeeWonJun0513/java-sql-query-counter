all:
	rm -rf build
	mkdir -p build
	javac -cp 'lib/*' -d build src/java/net/dishevelled/sqlcounter/*.java
	cp -a lib build
	jar cvfm java-sql-query-counter.jar src/MANIFEST.mf -C build .

