DROP TABLE IF EXISTS Users CASCADE;
DROP TABLE IF EXISTS Friendship CASCADE;
DROP TABLE IF EXISTS Genre CASCADE;
DROP TABLE IF EXISTS Age_rating CASCADE;
DROP TABLE IF EXISTS Film CASCADE;
DROP TABLE IF EXISTS Film_like CASCADE;
DROP TABLE IF EXISTS FilmGenre CASCADE;
DROP TABLE IF EXISTS USER_FEED CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS review_likes CASCADE;
DROP TABLE IF EXISTS directors CASCADE;
DROP TABLE IF EXISTS director_films CASCADE;


CREATE TABLE IF NOT EXISTS Users (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  login VARCHAR(255),
  name VARCHAR(255),
  email VARCHAR(255),
  birthday DATE
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id     int PRIMARY KEY auto_increment,
    name   varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS Friendship (
  user_id INT,
  friend_id INT,
  status VARCHAR(255),
  PRIMARY KEY(user_id, friend_id),
  FOREIGN KEY(user_id) REFERENCES Users(user_id),
  FOREIGN KEY(friend_id) REFERENCES Users(user_id)
);

CREATE TABLE IF NOT EXISTS Genre (
  genre_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Age_rating (
  age_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Film (
  film_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255),
  description VARCHAR(255),
  release_date DATE,
  duration BIGINT,
  rate INT,
  age_id INT,
  FOREIGN KEY(age_id) REFERENCES Age_rating(age_id)
);

CREATE TABLE IF NOT EXISTS FilmGenre (
  film_id INT,
  genre_id INT,
  PRIMARY KEY(film_id, genre_id),
  FOREIGN KEY(film_id) REFERENCES Film(film_id),
  FOREIGN KEY(genre_id) REFERENCES Genre(genre_id)
);

CREATE TABLE IF NOT EXISTS Film_like (
  user_id INT,
  film_id INT,
  PRIMARY KEY(user_id, film_id),
  FOREIGN KEY(user_id) REFERENCES Users(user_id),
  FOREIGN KEY(film_id) REFERENCES Film(film_id)
);

CREATE TABLE USER_FEED (
	EVENT_ID BIGINT PRIMARY KEY AUTO_INCREMENT,
	USER_ID INTEGER NOT NULL,
	EVENT_TYPE VARCHAR(64) NOT NULL,
	OPERATION VARCHAR(64) NOT NULL,
	ENTITY_ID INTEGER NOT NULL,
	TIME_STAMP BIGINT NOT NULL,
	FOREIGN KEY(USER_ID) REFERENCES Users(user_id)
);


CREATE TABLE IF NOT EXISTS reviews
(
    review_id    int PRIMARY KEY auto_increment,
    content      varchar(1000),
    user_id      int REFERENCES users(user_id),
    film_id      int REFERENCES Film(film_id),
    useful       int,
    is_positive  BOOLEAN
);

CREATE TABLE IF NOT EXISTS review_likes
(
    review_id    INTEGER,
    user_id      INTEGER,
    is_positive  BOOLEAN,
    CONSTRAINT IF NOT EXISTS REVIEW_LIKES_PK
        PRIMARY KEY (review_id, user_id),
    CONSTRAINT IF NOT EXISTS REVIEW_LIKES_FK_USER_ID
        FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE,
    constraint IF NOT EXISTS REVIEW_LIKES_FK_REVIEW_ID
        foreign key (REVIEW_ID) references REVIEWS on delete cascade
);

CREATE TABLE IF NOT EXISTS director_films
(
    film_id     int REFERENCES Film(film_id),
    director_id int REFERENCES directors(director_id),
    PRIMARY KEY (film_id, director_id)
);