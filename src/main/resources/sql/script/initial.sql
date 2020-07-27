DROP TABLE IF EXISTS records CASCADE;

CREATE TABLE image
(
    ID         SERIAL,
    IMAGE      BYTEA NOT NULL,
    URL        VARCHAR(256),
    CONSTRAINT PK_IMAGE_ID PRIMARY KEY (ID)
);

CREATE TABLE tag
(
    ID         SERIAL,
    TAG        VARCHAR(100) NOT NULL UNIQUE,
    CONSTRAINT PK_TAG_ID PRIMARY KEY (ID)
);

CREATE TABLE image_tag
(
    image_id   INTEGER NOT NULL,
    tag_id    INTEGER NOT NULL,
    CONSTRAINT FK_IMAGE_ID FOREIGN KEY (image_id) REFERENCES image (id),
    CONSTRAINT FK_TAG_ID FOREIGN KEY (tag_id) REFERENCES tag (id)
);

--INSERT INTO records (FILE_NAME, TAGS, URL) VALUES ('zero-image1', 'tag1 [0.8734], tag2 [0.7546], tag3 [0.9800]', 'http://yandex.ru/images/img006.jpg');
--INSERT INTO records (FILE_NAME, TAGS, URL) VALUES ('zero-image2', 'tag1 [0.6345], tag2 [0.7657], tag3 [0.6774]', 'http://google.com/images/img12.jpg');
--INSERT INTO records (FILE_NAME, TAGS, URL) VALUES ('zero-image3', 'tag1 [0.6545], tag2 [0.8767], tag3 [0.7786]', 'http://vk.com/api/pic/picture89.jpg');
