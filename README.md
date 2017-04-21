image-similarity
================

Canny edges + color histograms + KD-tree indexing

```
mvn clean compile assembly:single
java -jar target/image-similarity-1.0-SNAPSHOT-jar-with-dependencies.jar  src/test/resources/image/artists_03.jpeg 

#TODO accept time offset and file_id as args
#java -cp target/image-similarity-1.0-SNAPSHOT-jar-with-dependencies.jar com.allenday.image.runnable.FrameshiftInsert <x.jpg>

java -cp target/image-similarity-1.0-SNAPSHOT-jar-with-dependencies.jar com.allenday.image.runnable.FrameshiftSearch <file or directory>
```
