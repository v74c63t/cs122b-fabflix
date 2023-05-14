USE moviedb;

ALTER TABLE sales
ADD quantity INT,
ADD price DECIMAL(10,2),
ADD total DECIMAL(10,2);