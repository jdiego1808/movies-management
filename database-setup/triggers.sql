---- delete all instaces from other tables invole to the movie that we're about to remove.
IF EXISTS (
SELECT *
    FROM INFORMATION_SCHEMA.ROUTINES
WHERE SPECIFIC_SCHEMA = N'dbo'
    AND SPECIFIC_NAME = N'sp_deleteMovie'
    AND ROUTINE_TYPE = N'PROCEDURE'
)
DROP PROCEDURE dbo.sp_deleteMovie
GO
-- Create the stored procedure in the specified schema
ALTER PROCEDURE dbo.sp_deleteMovie
    @movie_id integer
AS
BEGIN
    
    DECLARE @table_name VARCHAR(50), @fk_column VARCHAR(50)
    
    DECLARE cs_tables CURSOR FOR
        SELECT c.name AS foreign_table, fc.name AS foreign_column
            FROM  sysobjects f
            INNER JOIN sysobjects c ON f.parent_obj = c.id
            INNER JOIN sysreferences r ON f.id = r.constid
            INNER JOIN sysobjects p ON r.rkeyid = p.id
            INNER JOIN syscolumns fc ON r.fkeyid = fc.id AND r.fkey1 = fc.colid
        WHERE f.type = 'F' AND p.name='movies'
    
    OPEN cs_tables
    FETCH NEXT FROM cs_tables INTO @table_name, @fk_column
    
    WHILE @@FETCH_STATUS = 0
    BEGIN
        DECLARE @sql nvarchar(255) = N'delete from [dbo].['+ @table_name + '] where '+@fk_column +'='+cast(@movie_id as varchar)
        EXEC(@sql)

        FETCH NEXT FROM cs_tables INTO @table_name, @fk_column
    END
    
    CLOSE cs_tables
    DEALLOCATE cs_tables
    
END
GO

CREATE TRIGGER tg_delMovie ON movies
FOR DELETE
AS
BEGIN
    DECLARE @movie_id int = (SELECT id from deleted)
    EXEC sp_deleteMovie @movie_id
    DELETE FROM [dbo].[movies] where id=@movie_id
END
GO


-- I was about to create a trigger on inserting into [credits] which would check person_id and movie_id existed or not, 
-- but I realized that foreign-key constraints had done that for me. 
-- CREATE TRIGGER tg_insertCredits ON credits
-- for insert
-- AS
-- BEGIN
--     DECLARE @person_id int = (select person_id from inserted)
--     DECLARE @movie_id int = (select movie_id from inserted)
--     IF NOT EXISTS (SELECT * FROM movies where id=@movie_id) OR NOT EXISTS (SELECT * FROM people WHERE id=@person_id)
--         ROLLBACK TRAN
--     ELSE COMMIT TRAN
-- END
-- GO

-- Because I forgot to set attribute IDENTITY(1,1) while creating table PEOPLE and MOVIES. So I wrote these two triggers to automically generate id.
CREATE TRIGGER tg_insertPeople ON people
INSTEAD OF INSERT
AS
BEGIN
    DECLARE @latest_id int = (select max(id) from people)
    INSERT INTO people
    SELECT @latest_id+1, [name], [gender] FROM inserted
END
GO

CREATE TRIGGER tg_insertMovies ON movies
INSTEAD OF INSERT
AS
BEGIN
    DECLARE @latest_id int = (select max(id) from movies)
    INSERT INTO movies
    SELECT @latest_id+1, [title], [year] FROM inserted
END
GO

CREATE TRIGGER tg_insertCredits ON credits
INSTEAD OF INSERT
AS
BEGIN
    DECLARE @pos INT = (SELECT position FROM inserted)
    DECLARE @note VARCHAR(255) = '[null]'
    IF @pos IS NULL
        SET @note = '(uncredited)'
    
    INSERT INTO credits([movie_id], [person_id], [type], [note], [character], [position])
    SELECT movie_id, person_id, type, @note, [character], [position] from inserted

END
GO

-- drop trigger tg_insertCredits

-- insert into credits([movie_id], [person_id], [type], [character], [position])
-- values(2544956, 4881753, 'writer', '[null]', null)
-- select * from credits where movie_id=2544956 and person_id=4881753

-- delete from credits where movie_id=2544956 and person_id=4881753

-- exec dbo.sp_getCredits 2544956
-- GO