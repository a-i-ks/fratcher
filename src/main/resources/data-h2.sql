-- Initialize database on startup. See
-- https://docs.spring.io/spring-boot/docs/current/reference/html/howto-database-initialization.html#howto-initialize-a-database-using-spring-jdbc
-- for explanation. This is a cool spring feature :-).


-- Remove everything.
DELETE FROM USER_;
DELETE FROM MATCH_;

-- Insert new users.
INSERT INTO USER_ (id, username, email, PASSWORD) VALUES
  (1, 'admin', 'admin@bla.de',
   '4cca4c0e9fd213f4bd3c9f8aa997fdc8ea346944a607087ec1e5dd176b54c8abdb3fa4d1bd44c9835460d52f4fc5ca5b57ef906fab08785981508e525da2c145'),
  (2, 'user', 'bla@bla.de',
   '313dee0b8b9b98060fbf183249811ef45fbef0a5843c5f4a97bb65d678bc42cb0251a8753f3b735b0e8e795cf6472cac10b3a59629e7cb87a2282c14953dffb6'); -- bar


