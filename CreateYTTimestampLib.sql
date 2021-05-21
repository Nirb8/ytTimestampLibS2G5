USE [master]
GO
/****** Object:  Database [ytTimestampLib_S2G5_copy_3]    Script Date: 5/20/2021 2:50:55 PM ******/
CREATE DATABASE [ytTimestampLib_S2G5_copy_3]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'ytTimestampLib_S2G5_copy_3', FILENAME = N'D:\Database\MSSQL15.MSSQLSERVER\MSSQL\DATA\ytTimestampLib_S2G5_copy_3.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 10%)
 LOG ON 
( NAME = N'ytTimestampLib_S2G5_copy_3_log', FILENAME = N'D:\Database\MSSQL15.MSSQLSERVER\MSSQL\DATA\ytTimestampLib_S2G5_copy_3_log.ldf' , SIZE = 1024KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
 WITH CATALOG_COLLATION = DATABASE_DEFAULT
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET COMPATIBILITY_LEVEL = 150
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [ytTimestampLib_S2G5_copy_3].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET ARITHABORT OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET  DISABLE_BROKER 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
--ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET TRUSTWORTHY OFF 
--GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET READ_COMMITTED_SNAPSHOT OFF 
GO
--ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET HONOR_BROKER_PRIORITY OFF 
--GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET  MULTI_USER 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET PAGE_VERIFY CHECKSUM  
GO
--ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET DB_CHAINING OFF 
--GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
EXEC sys.sp_db_vardecimal_storage_format N'ytTimestampLib_S2G5_copy_3', N'ON'
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET QUERY_STORE = OFF
GO
USE [ytTimestampLib_S2G5_copy_3]
GO
/****** Object:  User [kleinsv]    Script Date: 5/20/2021 2:50:55 PM ******/
CREATE USER [kleinsv] FOR LOGIN [kleinsv] WITH DEFAULT_SCHEMA=[dbo]
GO
/****** Object:  User [chanb]    Script Date: 5/20/2021 2:50:55 PM ******/
--CREATE USER [chanb] FOR LOGIN [chanb] WITH DEFAULT_SCHEMA=[dbo]
--GO
ALTER ROLE [db_owner] ADD MEMBER [kleinsv]
GO
--ALTER ROLE [db_owner] ADD MEMBER [chanb]
--GO
/****** Object:  UserDefinedFunction [dbo].[GetProfile]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE FUNCTION [dbo].[GetProfile]
(
)
RETURNS @table TABLE(
[UserID] nvarchar(50),
[UserName] varchar(255),
[DOB] date,
[FavoriteContentType] int)
AS
BEGIN
		INSERT @table
		Select UserID, UserName, DOB,FavoriteContentType
		From Users
	RETURN
END
GO
/****** Object:  UserDefinedFunction [dbo].[GetVideos]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE FUNCTION [dbo].[GetVideos] ()
RETURNS @table TABLE (
	[VideoID] varchar(255),
	[VideoTitle] varchar(255),
	[UploadDate] date,
	[Duration] time(7),
	[ContentTypeTitle] varchar(255),
	[ContentID] int
)
BEGIN
		INSERT @table
		Select v.YTVideoID,v.VideoTitle, v.UploadDate,v.Duration,CT.ContentTypeTitle,CT.ContentTypeID
		FROM VideoGenres VG
		JOIN Videos v ON VG.YTVideoID=V.YTVideoID
		JOIN ContentTypes CT on CT.ContentTypeID=VG.ContentTypeID
	RETURN
END
GO
/****** Object:  Table [dbo].[Users]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users](
	[UserID] [nvarchar](50) NOT NULL,
	[UserName] [varchar](255) NOT NULL,
	[DOB] [date] NULL,
	[FavoriteContentType] [int] NULL,
	[PasswordHash] [nvarchar](255) NOT NULL,
	[PasswordSalt] [nvarchar](255) NOT NULL,
 CONSTRAINT [PK__Users__1788CCAC772B50F3] PRIMARY KEY CLUSTERED 
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Videos]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Videos](
	[YTVideoID] [nvarchar](50) NOT NULL,
	[VideoTitle] [varchar](255) NOT NULL,
	[Duration] [time](7) NOT NULL,
	[UploadDate] [date] NULL,
 CONSTRAINT [PK__Video__5F7ED54CF9C55EE1] PRIMARY KEY CLUSTERED 
(
	[YTVideoID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[VideoGenres]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[VideoGenres](
	[YTVideoID] [nvarchar](50) NOT NULL,
	[ContentTypeID] [int] NOT NULL,
 CONSTRAINT [PK__VideoGen__ED7CB52AD160C3B5] PRIMARY KEY CLUSTERED 
(
	[YTVideoID] ASC,
	[ContentTypeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Timestamps]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Timestamps](
	[TimestampID] [nvarchar](50) NOT NULL,
	[DatePosted] [date] NOT NULL,
	[YTVideoID] [nvarchar](50) NOT NULL,
	[TimestampTitle] [varchar](255) NULL,
	[AuthorID] [nvarchar](50) NULL,
	[VideoTime] [time](7) NOT NULL,
 CONSTRAINT [PK__Timestam__C54284C34CA4B8B2] PRIMARY KEY CLUSTERED 
(
	[TimestampID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ContentTypes]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ContentTypes](
	[ContentTypeID] [int] NOT NULL,
	[ContentTypeTitle] [varchar](255) NOT NULL,
	[ContentTypeDesc] [varchar](5000) NULL,
 CONSTRAINT [PK__ContentT__2026066A29D303FC] PRIMARY KEY CLUSTERED 
(
	[ContentTypeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  View [dbo].[UserView]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE View [dbo].[UserView]
AS
Select V.YTVideoID AS [YouTube ID], V.VideoTitle AS [Video Name], T.TimestampTitle AS [Description], T.VideoTime AS [Timestamp Time], CT.ContentTypeTitle AS [Content Type], T.DatePosted AS [Created Time], U.UserName AS [Creator], T.TimestampID AS [TimestampID]
FROM Timestamps T
JOIN Videos V on V.YTVideoID=T.YTVideoID
JOIN VideoGenres VG ON VG.YTVideoID=V.YTVideoID
JOIN ContentTypes CT ON CT.ContentTypeID=VG.ContentTypeID
Join Users U on U.UserID=T.AuthorID
GO
/****** Object:  Table [dbo].[UserHistory]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[UserHistory](
	[UserID] [nvarchar](50) NOT NULL,
	[TimestampID] [nvarchar](50) NOT NULL,
	[TimeAccessed] [datetime] NULL,
 CONSTRAINT [PK__UserHist__3BDCE4E099E37709] PRIMARY KEY CLUSTERED 
(
	[UserID] ASC,
	[TimestampID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  View [dbo].[UserHistoryView]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[UserHistoryView]
AS
SELECT dbo.Timestamps.YTVideoID, dbo.Videos.VideoTitle, dbo.Timestamps.TimestampTitle, dbo.Timestamps.VideoTime, dbo.UserHistory.TimeAccessed, dbo.UserHistory.UserID
FROM     dbo.Timestamps INNER JOIN
                  dbo.UserHistory ON dbo.Timestamps.TimestampID = dbo.UserHistory.TimestampID AND dbo.Timestamps.TimestampID = dbo.UserHistory.TimestampID INNER JOIN
                  dbo.Videos ON dbo.Timestamps.YTVideoID = dbo.Videos.YTVideoID
GO
/****** Object:  View [dbo].[Profile]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE View [dbo].[Profile]
AS
SELECT UserID, UserName AS [Username], DOB AS [Date of Birth], FavoriteContentType AS [Favorite Content Type]
FROM Users
GO
/****** Object:  Table [dbo].[FavoriteTimestamps]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FavoriteTimestamps](
	[UserID] [nvarchar](50) NULL,
	[TimestampID] [nvarchar](50) NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[TimestampTags]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[TimestampTags](
	[TimestampID] [nvarchar](50) NOT NULL,
	[ContentTypeID] [int] NOT NULL,
 CONSTRAINT [PK__Timestam__7740E4A5490163E7] PRIMARY KEY CLUSTERED 
(
	[TimestampID] ASC,
	[ContentTypeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[FavoriteTimestamps]  WITH CHECK ADD FOREIGN KEY([TimestampID])
REFERENCES [dbo].[Timestamps] ([TimestampID])
GO
ALTER TABLE [dbo].[FavoriteTimestamps]  WITH CHECK ADD FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Timestamps]  WITH CHECK ADD  CONSTRAINT [FK__Timestamp__Autho__49C3F6B7] FOREIGN KEY([AuthorID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Timestamps] CHECK CONSTRAINT [FK__Timestamp__Autho__49C3F6B7]
GO
ALTER TABLE [dbo].[Timestamps]  WITH CHECK ADD  CONSTRAINT [FK__Timestamp__YTVid__48CFD27E] FOREIGN KEY([YTVideoID])
REFERENCES [dbo].[Videos] ([YTVideoID])
GO
ALTER TABLE [dbo].[Timestamps] CHECK CONSTRAINT [FK__Timestamp__YTVid__48CFD27E]
GO
ALTER TABLE [dbo].[TimestampTags]  WITH CHECK ADD  CONSTRAINT [FK__Timestamp__Conte__59063A47] FOREIGN KEY([ContentTypeID])
REFERENCES [dbo].[ContentTypes] ([ContentTypeID])
GO
ALTER TABLE [dbo].[TimestampTags] CHECK CONSTRAINT [FK__Timestamp__Conte__59063A47]
GO
ALTER TABLE [dbo].[TimestampTags]  WITH CHECK ADD  CONSTRAINT [FK__Timestamp__Times__5812160E] FOREIGN KEY([TimestampID])
REFERENCES [dbo].[Timestamps] ([TimestampID])
GO
ALTER TABLE [dbo].[TimestampTags] CHECK CONSTRAINT [FK__Timestamp__Times__5812160E]
GO
ALTER TABLE [dbo].[TimestampTags]  WITH CHECK ADD  CONSTRAINT [FK_TimestampID] FOREIGN KEY([TimestampID])
REFERENCES [dbo].[Timestamps] ([TimestampID])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[TimestampTags] CHECK CONSTRAINT [FK_TimestampID]
GO
ALTER TABLE [dbo].[UserHistory]  WITH CHECK ADD  CONSTRAINT [FK__UserHisto__Times__5535A963] FOREIGN KEY([TimestampID])
REFERENCES [dbo].[Timestamps] ([TimestampID])
GO
ALTER TABLE [dbo].[UserHistory] CHECK CONSTRAINT [FK__UserHisto__Times__5535A963]
GO
ALTER TABLE [dbo].[UserHistory]  WITH CHECK ADD  CONSTRAINT [FK__UserHisto__UserI__5441852A] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[UserHistory] CHECK CONSTRAINT [FK__UserHisto__UserI__5441852A]
GO
ALTER TABLE [dbo].[UserHistory]  WITH CHECK ADD  CONSTRAINT [fk_id] FOREIGN KEY([TimestampID])
REFERENCES [dbo].[Timestamps] ([TimestampID])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[UserHistory] CHECK CONSTRAINT [fk_id]
GO
ALTER TABLE [dbo].[Users]  WITH CHECK ADD  CONSTRAINT [FK__Users__FavoriteC__35BCFE0A] FOREIGN KEY([FavoriteContentType])
REFERENCES [dbo].[ContentTypes] ([ContentTypeID])
GO
ALTER TABLE [dbo].[Users] CHECK CONSTRAINT [FK__Users__FavoriteC__35BCFE0A]
GO
ALTER TABLE [dbo].[VideoGenres]  WITH CHECK ADD  CONSTRAINT [FK__VideoGenr__Conte__32E0915F] FOREIGN KEY([ContentTypeID])
REFERENCES [dbo].[ContentTypes] ([ContentTypeID])
GO
ALTER TABLE [dbo].[VideoGenres] CHECK CONSTRAINT [FK__VideoGenr__Conte__32E0915F]
GO
ALTER TABLE [dbo].[VideoGenres]  WITH CHECK ADD  CONSTRAINT [FK__VideoGenr__YTVid__2E1BDC42] FOREIGN KEY([YTVideoID])
REFERENCES [dbo].[Videos] ([YTVideoID])
GO
ALTER TABLE [dbo].[VideoGenres] CHECK CONSTRAINT [FK__VideoGenr__YTVid__2E1BDC42]
GO
ALTER TABLE [dbo].[Users]  WITH CHECK ADD  CONSTRAINT [DOBCheck] CHECK  (([DOB]<getdate()))
GO
ALTER TABLE [dbo].[Users] CHECK CONSTRAINT [DOBCheck]
GO
ALTER TABLE [dbo].[Videos]  WITH CHECK ADD  CONSTRAINT [CK__Video__UploadDat__24927208] CHECK  (([UploadDate]<getdate()))
GO
ALTER TABLE [dbo].[Videos] CHECK CONSTRAINT [CK__Video__UploadDat__24927208]
GO
/****** Object:  StoredProcedure [dbo].[AddContentType]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--Creates a new Content Type
CREATE PROCEDURE [dbo].[AddContentType] @ContentTypeID int, @ContentTypeTitle varchar(255)
AS
BEGIN
	--check for nulls
	IF @ContentTypeID is NULL
		BEGIN
			PRINT 'ERROR: ContentTypeID cannot be null'
			RETURN (1)
		END
	IF @ContentTypeTitle is NULL
		BEGIN
			PRINT 'ERROR: ContentTypeTitle cannot be null'
			RETURN (2)
		END
INSERT INTO ContentTypes (ContentTypeID, ContentTypeTitle)
VALUES(@ContentTypeID, @ContentTypeTitle)
Return 0;
END
GO
/****** Object:  StoredProcedure [dbo].[AddFavorite]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE Procedure [dbo].[AddFavorite] @UserID nvarchar(50), @DatePosted date, @YTVideoID nvarchar(50),@TimestampTitle varchar(255),
@VideoTime time
AS
BEGIN
if @UserID is null
BEGIN
PRINT 'ERROR: UserID cannot be null'
return 1
END
DECLARE @TimestampID nvarchar(50)

if (Select Count(*) FROM Timestamps WHERE DatePosted=@DatePosted AND YTVideoID=@YTVideoID AND TimestampTitle=@TimestampTitle AND VideoTime=@VideoTime)!=1
BEGIN
PRINT 'ERROR: invalid inputs'
return 2
END
Select @TimestampID=TimestampID FROM Timestamps WHERE DatePosted=@DatePosted AND YTVideoID=@YTVideoID AND TimestampTitle=@TimestampTitle AND VideoTime=@VideoTime
Insert INTO FavoriteTimestamps (UserID, TimestampID)
VALUES(@UserID,@TimestampID)
return 0
end
GO
/****** Object:  StoredProcedure [dbo].[AddTimestamp]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--create timestamps and creates timestamp tag entries
CREATE PROCEDURE [dbo].[AddTimestamp] @TimestampID nvarchar(50), @VideoTime time, @TimePosted date, @YTVideoID nvarchar(50), @TimestampTitle varchar(255), @AuthorID nvarchar(50)
AS
BEGIN
	--check for null
	IF @YTVideoID is NULL OR (SELECT COUNT(*) FROM Videos WHERE YTVideoID =@YTVideoID )=0
		BEGIN
		PRINT 'ERROR: Invalid VideoID'
		Return (2)
		END
	IF @VideoTime is NULL
		BEGIN
		PRINT 'ERROR: Time cannot be null'
		RETURN (3)
		END
	
--entry into timestamps
INSERT INTO Timestamps (TimestampID, DatePosted, VideoTime, YTVideoID, TimestampTitle, AuthorID)
VALUES(@TimestampID, @TimePosted, @VideoTime, @YTVideoID, @TimestampTitle, @AuthorID)

Return 0
END
GO
/****** Object:  StoredProcedure [dbo].[AddVideo]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--creates a new video entry in video also places the video in a video genre
CREATE PROCEDURE [dbo].[AddVideo] @VideoID nchar(11), @VideoTitle varchar(255), @Duration time(7), @UploadDate date
AS
BEGIN
	--checking null conditions
	IF @VideoID is NULL
		BEGIN
			PRINT 'ERROR: VideoID cannot be null'
			RETURN(1)
		END
	IF @VideoTitle is NULL
		BEGIN
			PRINT 'ERROR: Video Title cannot be null';
			RETURN(2)
		END
	IF @Duration is NULL
		BEGIN
			PRINT 'ERROR: Duration time cannot be null';
			RETURN(3)
		END
	IF @UploadDate is NULL
		BEGIN
			PRINT 'ERROR: Upload Date cannot be null';
			RETURN (4)
		END
	--check valid upload date
	IF @UploadDate > GETDATE()
		BEGIN
			PRINT 'ERROR: Invalid Upload Date';
			RETURN (5)
		END
	--check to see if video is already uploaded
	IF (SELECT COUNT(*) FROM Videos WHERE YTVideoID =@VideoID)=1
		BEGIN
			PRINT 'ERROR: Video already added';
			RETURN (6)
		END
	
--Insertion into video table
INSERT INTO Videos (YTVideoID ,VideoTitle ,Duration ,UploadDate )
Values(@VideoID ,@VideoTitle ,@Duration ,@UploadDate )

Return 0;
END
GO
/****** Object:  StoredProcedure [dbo].[ChangeDOB]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--Allow for birthdate to be modified
CREATE PROCEDURE [dbo].[ChangeDOB] @UserID nvarchar(50) , @DOB date
AS
BEGIN
	IF @UserID is null or @UserID =' '
	BEGIN
			Print 'ERROR: UserID cannot be null or empty';
			Return (1);
	END
	IF @DOB > GETDATE()
	BEGIN
		Print 'ERROR: Invalid DOB';
		Return (2);
	END
Update Users
SET DOB =@DOB
WHERE UserID = @UserID
Return 0;
END
GO
/****** Object:  StoredProcedure [dbo].[ChangePassword]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--Allow for password change
CREATE PROCEDURE [dbo].[ChangePassword] @UserID nvarchar(50), @PasswordSalt nvarchar(255), @PasswordHash nvarchar(255)
AS
BEGIN
	IF @UserID is null or @UserID =' '
	BEGIN
			Print 'ERROR: UserID cannot be null or empty';
			Return (1);
	END
	if (Select Count(*) From USers where UserID=@UserID)!=1
	BEGIN
	Print 'ERROR: User does not exist'
	Return (2);
	END
	if @PasswordHash is null
	BEGIN
	Print 'ERROR: Password cannot be null';
	return (3);
	END
	if @PasswordSalt is null
	BEGIN
	Print 'ERROR: Password salt cannot be null';
	return (4)
	END
UPDATE Users
SET PasswordSalt=@PasswordSalt,PasswordHash=@PasswordHash
WHERE UserID=@UserID
Return 0;
END
GO
/****** Object:  StoredProcedure [dbo].[GetAllTimestamps]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[GetAllTimestamps]
AS
Select V.YTVideoID AS [YouTube ID], V.VideoTitle AS [Video Name], 
	T.TimestampTitle AS [Description], T.VideoTime AS [Timestamp Time], 
	CT.ContentTypeTitle AS [Content Type], T.DatePosted AS [Created Time],
	U.UserName AS [Creator], T.TimestampID AS [TimestampID]
	FROM Timestamps T
	JOIN Videos V on V.YTVideoID=T.YTVideoID
	JOIN VideoGenres VG ON VG.YTVideoID=V.YTVideoID
	JOIN ContentTypes CT ON CT.ContentTypeID=VG.ContentTypeID
	Join Users U on U.UserID=T.AuthorID
	ORDER BY [YouTube ID] asc, [Timestamp Time] asc
GO
/****** Object:  StoredProcedure [dbo].[GetFavoriteTimestamps]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[GetFavoriteTimestamps] @userID nvarchar(50)
AS
IF((SELECT COUNT(*) FROM Users WHERE UserID = @userID )=0 OR @userID IS NULL)
BEGIN
	print('Invalid UserID')
	RETURN 1
END

SELECT V.YTVideoID AS [YouTube ID], V.VideoTitle AS [Video Name], 
	T.TimestampTitle AS [Description], T.VideoTime AS [Timestamp Time], 
	CT.ContentTypeTitle AS [Content Type], T.DatePosted AS [Created Time],
	U.UserName AS [Creator], T.TimestampID AS [TimestampID]
	FROM [dbo].[FavoriteTimestamps] FT
	JOIN Timestamps T on T.TimestampID=FT.TimestampID
	JOIN Videos V on V.YTVideoID=T.YTVideoID
	JOIN VideoGenres VG ON VG.YTVideoID=V.YTVideoID
	JOIN ContentTypes CT ON CT.ContentTypeID=VG.ContentTypeID
	JOIN Users U on U.UserID=T.AuthorID 
	WHERE FT.UserID = @userID
GO
/****** Object:  StoredProcedure [dbo].[GetTimestampsByContentType]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[GetTimestampsByContentType] @ContentTypeID int
AS
Select V.YTVideoID AS [YouTube ID], V.VideoTitle AS [Video Name], 
	T.TimestampTitle AS [Description], T.VideoTime AS [Timestamp Time], 
	CT.ContentTypeTitle AS [Content Type], T.DatePosted AS [Created Time],
	U.UserName AS [Creator], T.TimestampID AS [TimestampID]
	FROM Timestamps T
	JOIN Videos V on V.YTVideoID=T.YTVideoID
	JOIN VideoGenres VG ON VG.YTVideoID=V.YTVideoID
	JOIN ContentTypes CT ON CT.ContentTypeID=VG.ContentTypeID
	Join Users U on U.UserID=T.AuthorID
	WHERE CT.ContentTypeID=@ContentTypeID
	ORDER BY [YouTube ID] asc ,[Timestamp Time] asc
GO
/****** Object:  StoredProcedure [dbo].[GetTimestampsByYouTubeID]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[GetTimestampsByYouTubeID] @YTVideoID nvarchar(50)
as
IF(@YTVideoID IS NULL OR (SELECT COUNT(*) FROM Videos WHERE YTVideoID = @YTVideoID) = 0)
BEGIN
	RETURN 1
END

Select V.YTVideoID AS [YouTube ID], V.VideoTitle AS [Video Name], 
	T.TimestampTitle AS [Description], T.VideoTime AS [Timestamp Time], 
	CT.ContentTypeTitle AS [Content Type], T.DatePosted AS [Created Time],
	U.UserName AS [Creator], T.TimestampID AS [TimestampID]
	FROM Timestamps T
	JOIN Videos V on V.YTVideoID=T.YTVideoID
	JOIN VideoGenres VG ON VG.YTVideoID=V.YTVideoID
	JOIN ContentTypes CT ON CT.ContentTypeID=VG.ContentTypeID
	Join Users U on U.UserID=T.AuthorID
	WHERE V.YTVideoID = @YTVideoID
	ORDER BY [YouTube ID] asc ,[Timestamp Time] asc
GO
/****** Object:  StoredProcedure [dbo].[GetTimestampsCreatedByUser]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[GetTimestampsCreatedByUser] @UserID nvarchar(50)
AS
Select V.YTVideoID AS [YouTube ID], V.VideoTitle AS [Video Name], T.TimestampTitle AS [Description], T.VideoTime AS [Timestamp Time], CT.ContentTypeTitle AS [Content Type], T.DatePosted AS [Created Time], U.UserName AS [Creator], T.TimestampID AS [TimestampID]
FROM Timestamps T
JOIN Videos V on V.YTVideoID=T.YTVideoID
JOIN VideoGenres VG ON VG.YTVideoID=V.YTVideoID
JOIN ContentTypes CT ON CT.ContentTypeID=VG.ContentTypeID
Join Users U on U.UserID=T.AuthorID
WHERE T.AuthorID = @UserID
GO
/****** Object:  StoredProcedure [dbo].[GetUserHistory]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[GetUserHistory] @UserID nvarchar(50)
AS
IF((SELECT COUNT(*) FROM Users WHERE UserID = @userID )=0 OR @userID IS NULL)
BEGIN
	print('Invalid UserID')
	RETURN 1
END

	Select V.YTVideoID AS [YouTube ID], V.VideoTitle AS [Video Name], 
	T.TimestampTitle AS [Description], T.VideoTime AS [Timestamp Time], 
	UH.TimeAccessed AS [Time Accessed]
	FROM Timestamps T
	JOIN UserHistory UH on T.TimestampID = UH.TimestampID
	JOIN Videos V on V.YTVideoID=T.YTVideoID
	WHERE UH.UserID = @UserID
	ORDER BY [YouTube ID] asc, [Timestamp Time] asc
GO
/****** Object:  StoredProcedure [dbo].[InsertIntoTimestampTags]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE procedure [dbo].[InsertIntoTimestampTags] @ContentTypeID int, @TimestampID nvarchar(50)
AS
Begin
	 IF @ContentTypeID is NULL
		BEGIN
		PRINT 'ERROR: ContentTypeID cannot be null'
		RETURN (1)
		END
	IF (Select Count(*) FROM ContentTypes WHERE ContentTypeID =@ContentTypeID)=0
	BEGIN
			Print 'ERROR: ContentType value does not exist'
			Return (2)
	END
	--check for null
	IF @TimestampID is NULL
		BEGIN
		PRINT 'ERROR: TimestampID cannot be null'
		RETURN (3)
		END
--entry into timestamp tags
INSERT INTO TimestampTags (TimestampID, ContentTypeID )
VALUES (@TimestampID ,@ContentTypeID )
Return 0;
end
GO
/****** Object:  StoredProcedure [dbo].[InsertIntoVideoGenres]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE Procedure [dbo].[InsertIntoVideoGenres] @VideoID nchar(11), @ContentTypeID int, @TitleType varchar(255)
AS
BEGIN
--Check if contentType is null
	IF @ContentTypeID is NULL
		BEGIN
			PRINT 'ERROR: Content ID cannot be null';
			RETURN(1)
		END
	IF @VideoID is NULL
		BEGIN
			Print 'ERROR: VideoID cannot be null';
			Return(3)
		End

	IF (Select Count(*) FROM ContentTypes WHERE ContentTypeID =@ContentTypeID)=0
	BEGIN
		EXEC dbo.AddContentType @ContentTypeTitle = @TitleType, @ContentTypeID=@ContentTypeID
			--Print 'ERROR: ContentType value does not exist'
			--Return (2)
	END
	
	--Insertion into videoGenres table
INSERT INTO VideoGenres(YTVideoID, ContentTypeID)
Values(@VideoID,@ContentTypeID)
Return 0
End
GO
/****** Object:  StoredProcedure [dbo].[RegisterUser]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[RegisterUser] @UserID nvarchar(255), @UserName varchar(255), @PasswordHash nvarchar(255),@PasswordSalt nvarchar(255)
AS
BEGIN
	--Empty ID Check
	IF @UserID is null or @UserID =' '
	BEGIN
			Print 'ERROR: UserID cannot be null or empty';
			Return (1);
	END
	--Empty Username Check
	IF @UserName is null
	BEGIN
		Print 'ERROR: Username cannot be null';
		Return(2);
	END
	--Empty Password Check
	IF @PasswordHash is null
	BEGIN
		Print 'ERROR: PasswordHash cannot be null';
		Return(3);
	END
	--Empty Password Check
	IF @PasswordSalt is null
	BEGIN
		Print 'ERROR: PasswordSalt cannot be null';
		Return(4);
	END
	--Date of Birth check
	--IF @DOB is NOT NULL AND @DOB > GETDATE()
	--BEGIN
	--	Print 'ERROR: Invalid DOB';
	--	Return (5);
	--END
	--Duplicate ID (shouldn't necessarily need this but just in case)
	IF (SELECT COUNT(*) FROM [Users]
          WHERE UserID = @UserID) = 1
	BEGIN
      PRINT 'ERROR: UserID already in use.';
	  RETURN(6)
	END
	--Duplicate Username
	IF (SELECT COUNT(*) FROM [Users]
          WHERE Username = @Username) = 1
	BEGIN
      PRINT 'ERROR: Username already exists.';
	  RETURN(7)
	END
	--IF (Select Count(*) FROM ContentTypes WHERE ContentTypeID =@FavoriteContentType)=0
	--BEGIN
	--		Print 'ERROR: FavoriteContentType value does not exist'
	--		Return (8)
	--END

INSERT INTO Users(UserID, UserName, PasswordHash, PasswordSalt)
Values(@UserID, @UserName, @PasswordHash, @PasswordSalt) 
Return 0;
END
GO
/****** Object:  StoredProcedure [dbo].[UpdateFavContentType]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--Allow for Favorite Content type to be updated
CREATE PROCEDURE [dbo].[UpdateFavContentType] @UserID nvarchar(50), @FavoriteContentType int
AS
BEGIN
	IF @UserID is null or @UserID =' '
	BEGIN
			Print 'ERROR: UserID cannot be null or empty';
			Return (1);
	END
	IF (Select Count(*) FROM ContentTypes WHERE ContentTypeID =@FavoriteContentType)=0
	BEGIN
			Print 'ERROR: FavoriteContentType value does not exist'
			Return (2)
	END
UPDATE Users
SET FavoriteContentType=@FavoriteContentType
WHERE UserID=@UserID
Return 0;
END
GO
/****** Object:  StoredProcedure [dbo].[UpdateTimestamp]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE Procedure [dbo].[UpdateTimestamp] @TimestampID nvarchar(50), @Title varchar(255), @UserID nvarchar(50)
AS
BEGIN
IF @TimestampID is NULL
BEGIN
Print 'ERROR: Invalid TimestampID'
return 1;
END
IF @UserID!=(SELECT AuthorID FROM Timestamps Where TimestampID=@TimestampID)
BEGIN
Print 'ERROR: User given is not the author'
return 2;
END
Update Timestamps
Set TimestampTitle = @Title
WHERE TimestampID=@TimestampID
END
GO
/****** Object:  StoredProcedure [dbo].[UpdateUserHistory]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--updates the userHistory
CREATE PROCEDURE [dbo].[UpdateUserHistory] @UserID nvarchar(50), @TimestampID nvarchar(50)
AS
BEGIN
	--check null conditions
	IF (@UserID is NULL)
		BEGIN
			PRINT 'ERROR: UserID cannot be null'
			RETURN (1)
		END
	IF (@TimestampID is NULL)
		BEGIN
			PRINT 'ERROR: TimestampID cannot be null'
			RETURN (2)
		END
	-- check validity of UserID/TimestampID
	IF ((SELECT COUNT(UserID) from Users where UserID = @UserID) = 0)
	BEGIN
		PRINT 'ERROR: Invalid UserID'
		RETURN 3
	END
	IF ((SELECT COUNT(TimestampID) from Timestamps where TimestampID = @TimestampID) = 0)
	BEGIN
		PRINT 'ERROR: Invalid TimestampID'
		RETURN 4
	END
	
	IF ((SELECT COUNT(TimestampID) from UserHistory where UserID = @UserID AND TimestampID = @TimestampID) = 0)
	BEGIN
		-- The specified tag doesn't exist in the user's history yet
		INSERT INTO UserHistory (UserID ,TimestampID ,TimeAccessed )
		VALUES(@UserID ,@TimestampID , GETDATE() )
		RETURN 0
	END
	ELSE
	BEGIN
		-- The specified tag already exists in the user's history, just update the timeAccessed
		UPDATE UserHistory
		SET TimeAccessed = GETDATE()
		WHERE UserID = @UserID AND TimestampID = @TimestampID
		RETURN 0
	END
	

Return 0;
END
GO
/****** Object:  StoredProcedure [dbo].[UpdateUsername]    Script Date: 5/20/2021 2:50:55 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[UpdateUsername] @UserID nvarchar(50), @UserName varchar(255)
AS
BEGIN
--Empty ID Check
	IF @UserID is null or @UserID =' '
	BEGIN
			Print 'ERROR: UserID cannot be null or empty';
			Return (1);
	END
	--Empty Username Check
	IF @UserName is null
	BEGIN
		Print 'ERROR: Username cannot be null';
		Return(2);
	END
	--Duplicate Username
	IF (SELECT COUNT(*) FROM [Users]
          WHERE Username = @Username) = 1
	BEGIN
      PRINT 'ERROR: Username already exists.';
	  RETURN(3)
	END
	if (select Count(*) from Users Where UserID=@UserID)!=1
	BEGIN
	Print 'ERROR: User does not exist';
	Return (4)
	END
UPDATE Users 
SET UserName=@UserName
WHERE UserID =@UserID;
Return 0;
END
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPane1', @value=N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[40] 4[20] 2[20] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
         Begin Table = "Timestamps"
            Begin Extent = 
               Top = 7
               Left = 48
               Bottom = 170
               Right = 246
            End
            DisplayFlags = 280
            TopColumn = 2
         End
         Begin Table = "UserHistory"
            Begin Extent = 
               Top = 7
               Left = 294
               Bottom = 148
               Right = 488
            End
            DisplayFlags = 280
            TopColumn = 0
         End
         Begin Table = "Videos"
            Begin Extent = 
               Top = 159
               Left = 294
               Bottom = 322
               Right = 488
            End
            DisplayFlags = 280
            TopColumn = 0
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
      Begin ColumnWidths = 9
         Width = 284
         Width = 1200
         Width = 1200
         Width = 1200
         Width = 1200
         Width = 1200
         Width = 1200
         Width = 1200
         Width = 1200
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'UserHistoryView'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPaneCount', @value=1 , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'UserHistoryView'
GO
USE [master]
GO
ALTER DATABASE [ytTimestampLib_S2G5_copy_3] SET  READ_WRITE 
GO

USE [ytTimestampLib_S2G5_copy_3]
GO
INSERT INTO Users (UserID, UserName, DOB, FavoriteContentType, PasswordHash, PasswordSalt)
VALUES ('fa71a859-0977-482d-87d1-363a342ba099', '___IMPORT_DUMMY', null, null, 'x3NSTAfo8Yf2IQ5LTob+rw==', 'ø禼紲䀻舁�')