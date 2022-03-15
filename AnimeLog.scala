import java.sql.{Connection,DriverManager}

object AnimeLog {
  def main(args: Array[String]): Unit={

    //CONNECT TO MY SQL DATABASE
    val url = "jdbc:mysql://localhost:3306/AnimeLog"
    val driver = "com.mysql.jdbc.Driver"
    val username = "root"
    val password = "Wojtek_1312"
    var connection:Connection = null

    connection = DriverManager.getConnection(url, username, password)
    val statement = connection.createStatement
    statement.executeQuery("")

    //GET USER
    println("Enter your user: ")
    var user = scala.io.StdIn.readLine()


    //Variables
    var LogAnime = true
    var viewDate = ""
    var option = ""


    //User Logging an Anime
    while (LogAnime == true){
      println("Press 1 to log an Anime, \n" +
        " 2 to check user anime logs,\n" +
        " 3 to change user,\n" +
        " 4 to see all Anime logs of all users,\n" +
        " 5 if you are finished,\n" +
        " 6 if you want to delete logs")
      option = scala.io.StdIn.readLine()
      while(option =="1") {
        if (viewDate == "") {
          println("What is the view date? (yyyy-mm-dd format)")
          viewDate = scala.io.StdIn.readLine()
        }
        println("What genre do you want to log? comedy, action, slice-of-life, slice-of-life, romance")
        val animeGenre = scala.io.StdIn.readLine()
        val animeTitle = checkCategory(animeGenre)
        createanimeRecord(viewDate, animeTitle, connection, user)
        val continueLog = scala.io.StdIn.readLine()
        viewDate = ""
        if (continueLog == "n")
          option = "done"
        else if (continueLog == "y")
          print("Logging more Anime. ")
        else {
          print("Invalid input!")
          option = "done"
        }
      }
      if (option == "2"){
        checkUser(user, connection)
      }
      else if (option =="3") {
        println("Enter a new user or an existing one: ")
        user = scala.io.StdIn.readLine()
        viewDate=""
      }
      else if (option == "4"){
        printAllAnimeLog(connection)
        println("Print complete!")
      }
      else if (option == "5")
        LogAnime=false
      else if (option == "6")
        deleteUserLog(user,connection)
      else if (option =="done")
        println("")
      else
        println("Invalid option!")

    }

    println("Thank you for logging your Anime!!")
  }

  //END OF MAIN

  //OPTION 1 step 1: Checks for Genre and Anime title
  def checkCategory(x: String): String = {
    var animeGenre = x
    var animeTitle = ""
    val animeGenreList = List("comedy", "action","slice-of-life","romance")
    val animeList = List("Seven Deadly Sins", "No Game No Life","Sword Art Online", "Haikyu","Free", "Hinomaru Sumo",
      "Black Clover", "Your lie in April", "March Comes in Like a Lion", "Food Wars", "Saiki-K", "Nozaki-Kun")
    while(!animeGenreList.contains(animeGenre)){
      println("Invalid category. Please select a valid category: 'comedy' 'action' 'slice-of-life' 'romance' 'slice-of-life'")
      animeGenre = scala.io.StdIn.readLine()
    }
    do {
      if (animeGenre == "comedy") 
        println("Which Anime? 'Food Wars' 'Saiki-K' 'No Game No Life'")
      else if (animeGenre == "action")
        println("Which Anime? 'Seven Deadly Sins' 'Sword Art Online' 'Black Clover' 'Hinomaru Sumo'")
      else if (animeGenre == "slice-of-life")
        println("Which Anime? 'Nozaki-Kun' 'Free' 'Haikyu'")
      else if (animeGenre == "romance")
        println("Which Anime? 'March Comes in Like a Lion' 'Your Lie in April'")
      else
        print("Invalid input!")
      animeTitle = scala.io.StdIn.readLine()
    }
    while(!animeList.contains(animeTitle))
    animeTitle
  }

  //OPTION 1 step 2: Takes the values for episode/recom/rating
  def createanimeRecord(viewDate : String, animeTitle: String, connection: Connection, user: String): Unit = {
    println("How good was this Anime 1-10?")
    val AnimeRating = scala.io.StdIn.readLine()
    println("How many episodes did you watch?")
    val AnimeEpisodes = scala.io.StdIn.readLine()
    println("How likely are you to recommend 1-10?")
    val AnimeRecom = scala.io.StdIn.readLine()
    LogAnimeDB(viewDate, animeTitle, AnimeRating, AnimeEpisodes, AnimeRecom, connection, user)
  }

  //OPTION 1 step 3: Insert record into Anime table
  def LogAnimeDB(date : String, name : String, rating : String, recom : String, episode: String, connection: Connection, user : String): Unit ={
    val pstmt = connection.prepareStatement("INSERT INTO Anime(viewDate,animeTitle,animeGenre,AnimeRating,AnimeEpisodes,AnimeRecom, user) VALUES(?,?,?,?,?,?)")
    try{
      pstmt.setDate(1, java.sql.Date.valueOf(date))
      pstmt.setString(2, name)
      pstmt.setInt(3, rating.toInt)
      pstmt.setInt(4, recom.toInt)
      pstmt.setInt(5, episode.toInt)
      pstmt.setString(6, user)
      pstmt.executeUpdate
      println("Your Anime has been successfully logged! Do you want to log more Anime? y or n")
    }
    catch {
      case e: IllegalArgumentException => println("Invalid Input!! Record wasn't added. Do you want to add a Anime? y or n")
    }
  }

  //OPTION 2: PRINT LOG OF THAT USER
  def checkUser(x : String, connection: Connection): Unit = {
    val pstmt = connection.prepareStatement("SELECT * FROM Anime WHERE user=?")
    pstmt.setString(1,x)
    val rs = pstmt.executeQuery()
    println("Date Anime Rating episode recom User")
    while (rs.next) {
      val viewDate = rs.getDate("viewDate")
      val animeTitle= rs.getString("animeTitle")
      val AnimeRating = rs.getInt("AnimeRating")
      val AnimeEpisodes = rs.getInt("AnimeEpisodes")
      val AnimeRecom = rs.getInt("AnimeRecom")
      val user = rs.getString("user")
      println("%10s %-14s %-6s  %-5s %-5s %s".format(viewDate, animeTitle, AnimeRating, AnimeEpisodes, AnimeRecom, user))
    }
  }

  //OPTION 4 METHOD THAT PRINTS OUT ALL THE LOGS OF ALL USER
  def printAllAnimeLog(connection: Connection): Unit = {
    val statement = connection.createStatement
    val rs = statement.executeQuery("SELECT * FROM Anime ORDER BY user")
    println("Date Anime Rating episode recom User")
    while (rs.next) {
      val viewDate = rs.getDate("viewDate")
      val animeTitle= rs.getString("animeTitle")
      val AnimeRating = rs.getInt("AnimeRating")
      val AnimeEpisodes = rs.getInt("AnimeEpisodes")
      val AnimeRecom = rs.getInt("AnimeRecom")
      val user = rs.getString("user")
      println("%10s %-14s %-6s  %-5s %-5s %s".format(viewDate, animeTitle, AnimeRating, AnimeEpisodes, AnimeRecom, user))
    }
  }

  def deleteUserLog(user : String, connection: Connection): Unit = {
    println("Are you sure you wanna delete your logs? y or n")
    val yesOrNo = scala.io.StdIn.readLine()
    if (yesOrNo=="y"){
      val pstmt = connection.prepareStatement("DELETE FROM Anime WHERE user=?")
      pstmt.setString(1,user)
      pstmt.execute()
      println("Logs deleted!")

    }
    else if (yesOrNo=="n")
      println("Logs not deleted")
    else
      println("Invalid input!")
  }
}