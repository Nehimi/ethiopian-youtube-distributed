
CREATE DATABASE YouTubeEthiopia;
GO

USE YouTubeEthiopia;
GO


CREATE TABLE videos (
    id INT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    file_name NVARCHAR(255) NOT NULL,
    file_path NVARCHAR(500) NOT NULL,
    node_id NVARCHAR(50),
    node_port INT
    upload_date DATETIME NOT NULL DEFAULT GETDATE(),  
    
);
GO
