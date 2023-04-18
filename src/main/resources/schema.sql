DROP TABLE IF EXISTS Users CASCADE;
DROP TABLE IF EXISTS Friendship CASCADE;
DROP TABLE IF EXISTS Genre CASCADE;
DROP TABLE IF EXISTS Age_rating CASCADE;
DROP TABLE IF EXISTS Film CASCADE;
DROP TABLE IF EXISTS Film_like CASCADE;
DROP TABLE IF EXISTS FilmGenre CASCADE;

CREATE TABLE IF NOT EXISTS Users (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  login VARCHAR(255),
  name VARCHAR(255),
  email VARCHAR(255),
  birthday DATE
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