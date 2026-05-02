CREATE DATABASE IF NOT EXISTS YouTubeEthiopia;
USE YouTubeEthiopia;

CREATE TABLE IF NOT EXISTS videos (
    id INT  PRIMARY KEY IDENTITY(1,1),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    node_id VARCHAR(50) NOT NULL,
    node_port INT NOT NULL,
    upload_date DATETIME DEFAULT GETDATE()
);
