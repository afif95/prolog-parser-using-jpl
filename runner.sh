javac -cp ".:/usr/lib/swi-prolog/lib/jpl.jar" \
src/com/comp6591/lib/*.java \
-d ./build/
mkdir -p ./build/META-INF/
echo -e "Manifest-Version: 1.0\nClass-Path: .\nMain-Class: JPLTestApp" > \
./build/META-INF/MANIFEST.MF
jar cvf ./build/JPLTestApp.jar -C ./build/ com
 rm -R ./build/com ./build/META-INF
 jar tf ./build/JPLTestApp.jar
 java \
-cp ./build/JPLTestApp.jar:/usr/lib/swi-prolog/lib/jpl.jar \
com.comp6591.lib.ParseKB
