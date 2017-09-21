-- Initialize database on startup. See
-- https://docs.spring.io/spring-boot/docs/current/reference/html/howto-database-initialization.html#howto-initialize-a-database-using-spring-jdbc
-- for explanation. This is a cool spring feature :-).


-- Remove everything.
DELETE FROM MATCH_;
DELETE FROM USER_;


-- Insert new users.
INSERT INTO USER_ (id, username, name, email, PASSWORD, USER_TYPE, STATUS) VALUES
  (1, 'admin', 'Andre', 'andre.iske@mailbox.org',
   '5d2fbe6da3587a2c4b0914b07b822c26088266e96780c49d10efe87aef763285f6e4407054b0022cb8006e5ac60dd4d084a465ca76d2d63390d1c1dab1239956',
   'ADMIN', 0),
  (2, 'user', 'Max Muster', 'bla@bla.de',
   '313dee0b8b9b98060fbf183249811ef45fbef0a5843c5f4a97bb65d678bc42cb0251a8753f3b735b0e8e795cf6472cac10b3a59629e7cb87a2282c14953dffb6',
   'MODERATOR', 0); -- bar


