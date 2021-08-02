CREATE DATABASE Movies;
USE Movies;
GO


IF OBJECT_ID('[dbo].[movies]', 'U') IS NOT NULL
DROP TABLE [dbo].[movies]
GO
CREATE TABLE [dbo].[movies](
   [id]        integer      NOT NULL PRIMARY KEY,
   [title]     varchar(255) NOT NULL UNIQUE,
   [year]      integer
)
GO


IF OBJECT_ID('[dbo].[people]', 'U') IS NOT NULL
DROP TABLE [dbo].[people]
GO
CREATE TABLE [dbo].[people](
   [id]        integer      NOT NULL PRIMARY KEY,
   [name]      varchar(255) NOT NULL UNIQUE,
   [gender]    varchar(10)
)
GO

Exec sp_help 'dbo.credits'

IF OBJECT_ID('[dbo].[credits]', 'U') IS NOT NULL
DROP TABLE [dbo].[credits]
GO
CREATE TABLE [dbo].[credits](
   [person_id] integer      NOT NULL REFERENCES people(id),
   [movie_id]  integer      NOT NULL REFERENCES movies(id),
   [type]      varchar(20)  NOT NULL,
   [note]      varchar(255),
   [character] varchar(255),
   [position]  integer,
   PRIMARY KEY (person_id, movie_id, type)
)
GO

IF OBJECT_ID('[dbo].[genres]', 'U') IS NOT NULL
DROP TABLE [dbo].[genres]
GO
CREATE TABLE [dbo].[genres](
   [movie_id]  integer      NOT NULL REFERENCES movies(id),
   [genre]     varchar(25)  NOT NULL
);
GO

IF OBJECT_ID('[dbo].[keywords]', 'U') IS NOT NULL
DROP TABLE [dbo].[keywords]
GO
CREATE TABLE [dbo].[keywords](
   [movie_id]  integer      NOT NULL REFERENCES movies(id),
   [keyword]   varchar(127) NOT NULL   
);
GO

IF OBJECT_ID('[dbo].[languages]', 'U') IS NOT NULL
DROP TABLE [dbo].[languages]
GO
CREATE TABLE [dbo].[languages](
   [movie_id]  integer      NOT NULL  REFERENCES movies (id),
   [language]  varchar(35)  NOT NULL,
   [note]      varchar(255)
);
GO

IF OBJECT_ID('[dbo].[locations]', 'U') IS NOT NULL
DROP TABLE [dbo].[locations]
GO
CREATE TABLE [dbo].[locations](
   [movie_id]  integer      NOT NULL REFERENCES movies (id),
   [location]  varchar(255) NOT NULL,
   [note]      varchar(511)
);
GO

IF OBJECT_ID('[dbo].[release_dates]', 'U') IS NOT NULL
DROP TABLE [dbo].[release_dates]
GO
CREATE TABLE [dbo].[release_dates](
   [movie_id]     integer      NOT NULL REFERENCES movies (id),
   [country]      varchar(40)  NOT NULL,
   [release_date] varchar(10)  NOT NULL,
   [note]         varchar(255)
);
GO

IF OBJECT_ID('[dbo].[running_times]', 'U') IS NOT NULL
DROP TABLE [dbo].[running_times]
GO
CREATE TABLE [dbo].[running_times](
   [movie_id]     integer      NOT NULL REFERENCES movies (id),
   [running_time] varchar(40)  NOT NULL,
   [note]         varchar(255)
);
GO

IF OBJECT_ID('[dbo].[user]', 'U') IS NOT NULL
DROP TABLE [dbo].[users]
GO 
CREATE TABLE [dbo].[users] (
   [id] integer IDENTITY(1,1) PRIMARY KEY,
   [username] varchar(255) not null unique,
   [hashed_password] varchar(255) not null,
   [role] varchar(10) check([role] in ('admin', 'client', 'root'))
);
GO
insert into [users]
VALUES ('root', '$31$16$2hDKRgha_wtqP1Oihcy7Wqnuu3LKYe2fChLXqUBXa3o', 'root') --password is rootuser123 
select * from users


IF OBJECT_ID('[dbo].[favorites]', 'U') IS NOT NULL
DROP TABLE [dbo].[favorites]
GO
CREATE TABLE [dbo].[favorites]
(
   [user_id] integer NOT NULL REFERENCES [users](id),
   [movie_id] integer NOT NULL REFERENCES movies(id),  
   [note] nvarchar(255),
   PRIMARY KEY([user_id], [movie_id])
);
GO



IF OBJECT_ID('[dbo].[watched_movies]', 'U') IS NOT NULL
DROP TABLE [dbo].[watched_movies]
GO
CREATE TABLE [dbo].[watched_movies]
(
   [user_id] integer NOT NULL REFERENCES [users](id),
   [movie_id] integer NOT NULL REFERENCES [movies](id),
   [time] DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   [note] NVARCHAR(255)
   PRIMARY KEY ([user_id], [movie_id])
);
GO


BULK INSERT movies FROM '..\database-setup\movies.csv'
   WITH (  
      DATAFILETYPE = 'char',  
      FIELDTERMINATOR = '|',  
      ROWTERMINATOR = '\n'  
);  
GO
select * from movies

BULK INSERT people FROM '..\database-setup\people.txt'  
   WITH (  
      DATAFILETYPE = 'char',  
      FIELDTERMINATOR = '|',  
      ROWTERMINATOR = '\n'  
);  
GO  
select * from people

BULK INSERT credits FROM '..\database-setup\credits.txt'  
   WITH (   
      FIELDTERMINATOR = '|',  
      ROWTERMINATOR = '\n'  
);  
GO  

select * from credits

BULK INSERT genres FROM '..\database-setup\genres.txt'  
   WITH (   
      FIELDTERMINATOR = '|',  
      ROWTERMINATOR = '\n'  
);  
GO  
select * from genres

BULK INSERT keywords FROM '..\database-setup\keywords.txt'  
   WITH (   
      FIELDTERMINATOR = '|',  
      ROWTERMINATOR = '\n'  
);  
GO  
select * from keywords

BULK INSERT languages FROM '..\database-setup\languages.txt'  
   WITH (   
      FIELDTERMINATOR = '|',  
      ROWTERMINATOR = '\n'  
);  
GO  
select * from languages

BULK INSERT locations FROM '..\database-setup\locations.txt'  
   WITH (   
      FIELDTERMINATOR = '|',  
      ROWTERMINATOR = '\n'  
);  
GO  
select * from locations


BULK INSERT release_dates FROM '..\database-setup\release_dates.txt'  
   WITH (   
      FIELDTERMINATOR = '|',  
      ROWTERMINATOR = '\n'  
);  
GO  
select * from release_dates

BULK INSERT running_times FROM '..\database-setup\running_times.txt'  
   WITH (   
      FIELDTERMINATOR = '|',  
      ROWTERMINATOR = '\n'  
);  
GO  
