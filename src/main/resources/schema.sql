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
  user_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
  login         VARCHAR(255) NOT NULL,
  name          VARCHAR(255),
  email         VARCHAR(255) NOT NULL,
  birthday      DATE
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id BIGINT PRIMARY KEY auto_increment,
    name        varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS Friendship (
  user_id       BIGINT,
  friend_id     BIGINT,
  status        VARCHAR(255),
  PRIMARY KEY(user_id, friend_id),
  CONSTRAINT fk_friendship_user_id
        FOREIGN KEY(user_id)      REFERENCES Users(user_id) ON DELETE CASCADE,
  CONSTRAINT fk_friendship_friend_id
        FOREIGN KEY(friend_id)    REFERENCES Users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Genre (
  genre_id      INT PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Age_rating (
  age_id        INT         PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Film (
  film_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(255),
  description   VARCHAR(255),
  release_date  DATE,
  duration      BIGINT,
  rate          INT,
  age_id        INT,
  FOREIGN KEY(age_id) REFERENCES Age_rating(age_id)
);

CREATE TABLE IF NOT EXISTS FilmGenre (
  film_id       BIGINT,
  genre_id      BIGINT,
  PRIMARY KEY(film_id, genre_id),
  CONSTRAINT fk_film_genre_film_id
        FOREIGN KEY(film_id) REFERENCES Film(film_id) ON DELETE CASCADE,
  CONSTRAINT fk_film_genre_genre_id
        FOREIGN KEY(genre_id) REFERENCES Genre(genre_id)  ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Film_like (
  user_id       BIGINT,
  film_id       BIGINT,
  PRIMARY KEY(user_id, film_id),
  CONSTRAINT fk_likes_film_id
        FOREIGN KEY(user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
  CONSTRAINT fk_likes_user_id
        FOREIGN KEY(film_id) REFERENCES Film(film_id) ON DELETE CASCADE
);

CREATE TABLE USER_FEED (
	EVENT_ID    BIGINT PRIMARY KEY AUTO_INCREMENT,
	USER_ID     BIGINT NOT NULL,
	EVENT_TYPE  VARCHAR(64) NOT NULL,
	OPERATION   VARCHAR(64) NOT NULL,
	ENTITY_ID   INT NOT NULL,
	TIME_STAMP  BIGINT NOT NULL,
	FOREIGN KEY(USER_ID) REFERENCES Users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id    INT PRIMARY KEY auto_increment,
    content      VARCHAR(1000),
    user_id      BIGINT REFERENCES users(user_id),
    film_id      BIGINT REFERENCES Film(film_id),
    useful       int,
    is_positive  BOOLEAN
);

CREATE TABLE IF NOT EXISTS review_likes
(
    review_id    INT,
    user_id      BIGINT,
    is_positive  BOOLEAN,
    CONSTRAINT IF NOT EXISTS REVIEW_LIKES_PK PRIMARY KEY (review_id, user_id),
    CONSTRAINT IF NOT EXISTS REVIEW_LIKES_FK_USER_ID
        FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE,
    constraint IF NOT EXISTS REVIEW_LIKES_FK_REVIEW_ID
        FOREIGN KEY (REVIEW_ID) REFERENCES REVIEWS ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS director_films
(
    film_id     INT REFERENCES Film(film_id),
    director_id INT REFERENCES directors(director_id),
    UNIQUE (film_id, director_id),
    CONSTRAINT fk_film_director_films_id
        FOREIGN KEY (film_id) REFERENCES Film (film_id) ON DELETE CASCADE,
    CONSTRAINT fk_film_director_director_id
        FOREIGN KEY (director_id) REFERENCES Directors (director_id) ON DELETE CASCADE
);