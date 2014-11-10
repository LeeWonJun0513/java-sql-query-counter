all:
	rm -rf build
	mkdir -p build
	javac -source 1.5 -target 1.5 -cp 'lib/*' -d build src/java/net/dishevelled/sqlcounter/*.java
	cp -a lib build
	jar cvfm java-sql-query-counter.jar src/MANIFEST.mf -C build .

