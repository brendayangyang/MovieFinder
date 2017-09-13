CREATE TABLE movie(
	movie_id INTEGER,
	title varchar(200),
	year INTEGER,
	rt_audience_rating number(5, 2),
	rt_audience_num_ratings INTEGER,
	PRIMARY KEY(movie_id)
);

CREATE TABLE tag(
	tag_id INTEGER,
	value varchar(100),
	PRIMARY KEY(tag_id)
);

CREATE TABLE movie_actor(
	movie_id INTEGER,
	actor_id varchar(256),
	actor_name varchar(256),
	rating INTEGER,
	FOREIGN KEY(movie_id) REFERENCES movie
);


CREATE TABLE movie_countries(
	movie_id INTEGER,
	country varchar(256),
	FOREIGN KEY(movie_id) REFERENCES movie
);


CREATE TABLE movie_director(
	movie_id INTEGER,
	director_id varchar(100),
	director_name varchar(100),
	FOREIGN KEY(movie_id) REFERENCES movie
);


CREATE TABLE movie_genres(
	movie_id INTEGER,
	genre varchar(256),
	PRIMARY KEY(movie_id, genre),
	FOREIGN KEY(movie_id) REFERENCES movie
);


CREATE TABLE movie_tags(
	movie_id INTEGER,
	tag_id INTEGER,
	tag_weight INTEGER,
	FOREIGN KEY(tag_id) REFERENCES tag
);


CREATE TABLE user_taggedmovies_timestamps(
	user_id INTEGER,
	movie_id INTEGER,
	tag_id INTEGER,
	FOREIGN KEY(tag_id) REFERENCES tag,
	FOREIGN KEY(movie_id) REFERENCES movie
);


CREATE INDEX movie_actor_idx ON movie_actor(movie_id, actor_name);
CREATE INDEX movie_country_idx ON movie_countries(country);
CREATE INDEX movie_director_idx ON movie_director(movie_id, director_name);
CREATE INDEX movie_genre_idx ON movie_genres(genre);
CREATE INDEX movie_tag_idx ON movie_tags(movie_id);
CREATE INDEX utt_idx ON user_taggedmovies_timestamps(movie_id, tag_id);

