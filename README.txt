Oracle DB config: src/main/resources/config.properties

Compile
./gradlew jfxJar

Populate Data
- Class location: src/main/java/coen280/data/Populate.java
- command
java -jar build/populate.jar 'hetrec2011-movielens-2k-v2/movies.dat' 'hetrec2011-movielens-2k-v2/tags.dat' 'hetrec2011-movielens-2k-v2/movie_actors.dat' 'hetrec2011-movielens-2k-v2/movie_countries.dat' 'hetrec2011-movielens-2k-v2/movie_directors.dat' 'hetrec2011-movielens-2k-v2/movie_genres.dat' 'hetrec2011-movielens-2k-v2/movie_tags.dat' 'hetrec2011-movielens-2k-v2/user_taggedmovies-timestamps.dat'

Movie Search GUI
- Class location: src/main/java/coen280/gui/*
- command
java -jar build/hw3.jar

