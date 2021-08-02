---- Export data
IF EXISTS (
SELECT *
    FROM INFORMATION_SCHEMA.ROUTINES
WHERE SPECIFIC_SCHEMA = N'dbo'
    AND SPECIFIC_NAME = N'exportData'
    AND ROUTINE_TYPE = N'PROCEDURE'
)
DROP PROCEDURE dbo.sp_exportData
GO
CREATE PROCEDURE dbo.sp_exportData
    @tableName /*parameter name*/ NVARCHAR(50) /*store name of table or view*/,
    @filename /*parameter name*/  NVARCHAR(200) /*file path to save the export file*/
AS
BEGIN
    DECLARE @sql VARCHAR (8000)
    SELECT @sql = 'bcp Movies.dbo.' + @tableName + ' out ' + @filename + ' -c -t"|" -T -S' + @@SERVERNAME
    EXEC master..xp_cmdshell @sql
END
GO
EXECUTE dbo.sp_exportData 'movies', '..\database-setup\movies.csv'
GO

-- import data
IF EXISTS (
SELECT *
    FROM INFORMATION_SCHEMA.ROUTINES
WHERE SPECIFIC_SCHEMA = N'dbo'
    AND SPECIFIC_NAME = N'sp_importData'
    AND ROUTINE_TYPE = N'PROCEDURE'
)
DROP PROCEDURE dbo.sp_importData
GO
CREATE PROCEDURE dbo.sp_importData
    @tableName /*parameter name*/ NVARCHAR(50) /*store name of table or view*/,
    @filename /*parameter name*/  NVARCHAR(200) /*file path to save the export file*/
AS
BEGIN
    DECLARE @sql VARCHAR (8000)

    SELECT @sql = 'bcp Movies.dbo.' + @tableName + ' in ' + @filename + ' -c -t"|" -T -S' + @@SERVERNAME

    EXEC master..xp_cmdshell @sql
END
GO
EXECUTE dbo.sp_importData 'movies', '..\database-setup\movies.txt'
GO


---- Get movie info
IF EXISTS (
SELECT *
    FROM INFORMATION_SCHEMA.ROUTINES
WHERE SPECIFIC_SCHEMA = N'dbo'
    AND SPECIFIC_NAME = N'getMovie'
    AND ROUTINE_TYPE = N'PROCEDURE'
)
DROP PROCEDURE dbo.sp_getMovie
GO
-- Create the stored procedure in the specified schema
CREATE PROC dbo.sp_getMovie @id integer
AS
    SELECT title, year FROM movies WHERE id=@id
GO
EXECUTE dbo.sp_getMovie 2544956
GO

---- get keyswords base on movie_id
IF EXISTS (
SELECT *
    FROM INFORMATION_SCHEMA.ROUTINES
WHERE SPECIFIC_SCHEMA = N'dbo'
    AND SPECIFIC_NAME = N'sp_getKeywords'
    AND ROUTINE_TYPE = N'PROCEDURE'
)
DROP PROCEDURE dbo.sp_getKeywords
GO
-- Create the stored procedure in the specified schema
CREATE PROCEDURE dbo.sp_getKeywords
    @movie_id /*parameter name*/ int /*movie id*/
AS
BEGIN
    SELECT TOP 20 keyword 
        FROM keywords 
    WHERE movie_id=@movie_id
        ORDER by newId() -- random
END
GO

---- Get genres base on movie_id
-- Create a new stored procedure called 'getGenres' in schema 'dbo'
-- Drop the stored procedure if it already exists
IF EXISTS (
SELECT *
    FROM INFORMATION_SCHEMA.ROUTINES
WHERE SPECIFIC_SCHEMA = N'dbo'
    AND SPECIFIC_NAME = N'sp_getGenres'
    AND ROUTINE_TYPE = N'PROCEDURE'
)
DROP PROCEDURE dbo.sp_getGenres
GO
CREATE PROC dbo.sp_getGenres
    @movie_id /*parameter name*/ int
AS
BEGIN
    SELECT genre from genres where movie_id=@movie_id
END
GO

---- Get locations base on movie_id
-- Create a new stored procedure called 'getShottingLocations' in schema 'dbo'
-- Drop the stored procedure if it already exists
IF EXISTS (
SELECT *
    FROM INFORMATION_SCHEMA.ROUTINES
WHERE SPECIFIC_SCHEMA = N'dbo'
    AND SPECIFIC_NAME = N'sp_getShottingLocations'
    AND ROUTINE_TYPE = N'PROCEDURE'
)
DROP PROCEDURE dbo.sp_getShottingLocations
GO
-- Create the stored procedure in the specified schema
CREATE PROCEDURE dbo.sp_getShottingLocations
    @movie_id /*parameter name*/ int
AS
BEGIN
    SELECT * 
    FROM locations 
    WHERE movie_id=@movie_id
END
GO
-- example to execute the stored procedure we just created
EXECUTE dbo.sp_getShottingLocations 2544956
GO

---- Get all people who stared in the movie base on movie_id
-- Create a new stored procedure called 'getCredits' in schema 'dbo'
-- Drop the stored procedure if it already exists
IF OBJECT_ID(N'dbo.getCredits', N'TF') IS NOT NULL
    DROP PROC dbo.sp_getCredits
GO
-- Create the stored procedure in the specified schema
CREATE PROC dbo.sp_getCredits @movie_id int
AS
    SELECT p.[id], p.[name], p.[gender], c.[type], c.[character], c.[note], c.[position] FROM credits c
    JOIN people p ON p.id=c.person_id
    WHERE movie_id=@movie_id

GO
-- example to execute the stored procedure we just created
exec dbo.sp_getCredits 2544956
GO

---- Optional. I didn't use this procedure in the application. This might be a hepful reference.
---- The method returns people who starred in a specificed movie based on name and type.
-- Create a new stored procedure called 'getCreditsWithConditions' in schema 'dbo'
-- Drop the stored procedure if it already exists
IF EXISTS (
SELECT *
    FROM INFORMATION_SCHEMA.ROUTINES
WHERE SPECIFIC_SCHEMA = N'dbo'
    AND SPECIFIC_NAME = N'getCreditsWithConditions'
    AND ROUTINE_TYPE = N'PROCEDURE'
)
DROP PROCEDURE dbo.sp_getCreditsWithConditions
GO
-- Create the stored procedure in the specified schema
CREATE PROCEDURE dbo.sp_getCreditsWithConditions  @movie_id int, @name VARCHAR(255), @type VARCHAR(20)
AS
BEGIN
    IF @name IS NULL
        BEGIN
            SELECT p.[id], p.[name], c.[type], c.[character], c.[note], c.[position] FROM credits c
            JOIN people p ON p.id=c.person_id
            WHERE movie_id=@movie_id AND c.[type]=@type
        END
    ELSE IF @type IS NULL
        BEGIN
            SELECT p.[id], p.[name], c.[type], c.[character], c.[note], c.[position] FROM credits c
            JOIN people p ON p.id=c.person_id
            WHERE movie_id=@movie_id AND p.[name]=@name
        END
    ELSE
        BEGIN
            SELECT p.[id], p.[name], c.[type], c.[character], c.[note], c.[position] FROM credits c
            JOIN people p ON p.id=c.person_id
            WHERE movie_id=@movie_id AND p.[name]=@name AND c.[type]=@type
        END
END
GO
-- example to execute the stored procedure we just created
EXECUTE dbo.sp_getCreditsWithConditions 2607939, 'Eklund, Sean', 'actor'
GO

---- Get languages use in a specificed movie.
-- Create a new stored procedure called 'getLanguages' in schema 'dbo'
-- Drop the stored procedure if it already exists
IF OBJECT_ID(N'dbo.getLanguages', N'TF') IS NOT NULL
    DROP PROCEDURE dbo.sp_getLanguages
GO
-- Create the stored procedure in the specified schema
CREATE PROCEDURE dbo.sp_getLanguages
    @movie_id /*parameter name*/ int 
AS
BEGIN
    SELECT [language] FROM [dbo].[languages]
    WHERE movie_id=@movie_id
END
GO

---- There may be multiple dates for the same country due to a premiere or film festival 
---- being listed separately from the main release date, or differences between regions.
---- Get release dates of a movie. Return the earliest dates of each country.
IF EXISTS (
SELECT *
    FROM INFORMATION_SCHEMA.ROUTINES
WHERE SPECIFIC_SCHEMA = N'dbo'
    AND SPECIFIC_NAME = N'sp_getReleaseDate'
    AND ROUTINE_TYPE = N'PROCEDURE'
)
DROP PROCEDURE dbo.sp_getReleaseDate
GO
CREATE PROCEDURE dbo.sp_getReleaseDate
    @movie_id int
AS
BEGIN
    SELECT country, min(release_date) as 'release_date'
    from release_dates where movie_id=@movie_id
    GROUP BY country    
END
GO


---- There might be multiple running times depend on some countries. I combine them all into a string.
IF OBJECT_ID(N'dbo.getRunningTimes') IS NOT NULL
    DROP FUNCTION dbo.fc_getRunningTimes
GO
CREATE FUNCTION dbo.fc_getRunningTimes(@movie_id int)
RETURNS VARCHAR(80)
AS
BEGIN    
    DECLARE @runningTime NVARCHAR(40), @tmp VARCHAR(80) = ''
    
    DECLARE db_cursor CURSOR FOR
    SELECT running_time
        FROM dbo.running_times
        WHERE movie_id=@movie_id
    
    OPEN db_cursor
    FETCH NEXT FROM db_cursor INTO @runningTime
    
    WHILE @@FETCH_STATUS = 0
    BEGIN
        -- add instructions to be executed for every row
        SET @tmp= concat(@tmp, @runningTime,', ')

        FETCH NEXT FROM db_cursor INTO @runningTime

        IF @@FETCH_STATUS != 0
            SET @tmp = STUFF(@tmp, LEN(@tmp), 1, '')
    END
    
    CLOSE db_cursor
    DEALLOCATE db_cursor
    
    RETURN @tmp
END
GO



-- Create a new stored procedure called 'sp_getUserWishlist' in schema 'dbo'
-- Drop the stored procedure if it already exists
IF EXISTS (
SELECT *
    FROM INFORMATION_SCHEMA.ROUTINES
WHERE SPECIFIC_SCHEMA = N'dbo'
    AND SPECIFIC_NAME = N'sp_getUserWishlist'
    AND ROUTINE_TYPE = N'PROCEDURE'
)
DROP PROCEDURE dbo.sp_getUserWishlist
GO
-- Create the stored procedure in the specified schema
CREATE PROCEDURE dbo.sp_getUserWishlist
    @user_id INT
AS
BEGIN
    SELECT movie_id, title, year  FROM favorites
    JOIN movies ON movies.id = favorites.movie_id
    WHERE user_id=@user_id
END
GO
-- example to execute the stored procedure we just created
EXECUTE dbo.sp_getUserWishlist 1
GO

-- Create a new stored procedure called 'sp_getUserWishlist' in schema 'dbo'
-- Drop the stored procedure if it already exists
IF EXISTS (
SELECT *
    FROM INFORMATION_SCHEMA.ROUTINES
WHERE SPECIFIC_SCHEMA = N'dbo'
    AND SPECIFIC_NAME = N'sp_getUserWatchedMovies'
    AND ROUTINE_TYPE = N'PROCEDURE'
)
DROP PROCEDURE dbo.sp_getUserWatchedMovies
GO
-- Create the stored procedure in the specified schema
CREATE PROCEDURE dbo.sp_getUserWatchedMovies
    @user_id INT
AS
BEGIN
    SELECT [movie_id], [title], [year], [time]  FROM watched_movies
    JOIN movies ON movies.id = watched_movies.movie_id
    WHERE user_id=@user_id
END
GO
-- example to execute the stored procedure we just created
EXECUTE dbo.sp_getUserWatchedMovies 1
GO
