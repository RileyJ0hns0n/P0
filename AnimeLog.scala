import java.sql.Connection
import java.sql.DriverManager


object AnimeLog {
  //CONNECT TO MY SQL DATABASE
  val url = "jdbc:mysql://localhost:3306/AnimeLog"
  val driver = "com.mysql.jdbc.Driver"
  val username = "root"
  val password = "Wojtek_1312"
  var connection:Connection = _

  connection = DriverManager.getConnection(url, username, password)

  //main
  def main(args: Array[String]): Unit={

    val statement = connection.createStatement

    //GET USER
    println("Enter your user: ")
    var user = scala.io.StdIn.readLine()


    //Variables
    var LogAnime = true
    var option = ""


    //User Logging an Anime
    while (LogAnime == true){
      println("Press\n" +
        " 1 to log an Anime, \n" +
        " 2 to check user anime logs,\n" +
        " 3 to change user,\n" +
        " 4 to see all Anime logs of all users,\n" +
        " 5 if you are finished,\n" +
        " 6 if you want to delete logs")
      option = scala.io.StdIn.readLine()
      while(option =="1") {
        println("What genre do you want to log? comedy, action, slice-of-life, slice-of-life, romance")
        var animeGenre = scala.io.StdIn.readLine()
        var animeTitle = checkCategory(animeGenre)
        createanimeRecord( animeTitle, connection, user)
        var continueLog = scala.io.StdIn.readLine()
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
        user=""
      }
      else if (option == "4"){
        printAllAnimeLog(connection)
        println("Print complete!\n")
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

    println("Thank you for logging your Anime! Enjoy!")
  } //END OF MAIN

  //OPTION 1 step 1: Checks for Genre and Anime title
  def checkCategory(x: String): String = {
    var animeGenre = x
    var animeTitle = ""
    val animeGenreList = List("comedy", "action","slice-of-life","romance")
    val animeList = List("seven deadly sins", "no game no life","sword art online", "haikyu","free", "hinomaru sumo",
      "black clover", "your lie in april", "march comes in like a lion", "food wars", "saiki k", "nozaki kun")
    while(!animeGenreList.contains(animeGenre)){
      println("Invalid category. Please select a valid category: 'comedy' 'action' 'slice-of-life' 'romance' 'slice-of-life'")
      animeGenre = scala.io.StdIn.readLine()
    }
    do {
      if (animeGenre == "comedy") {
        println("Which Anime? 'food wars' 'saiki k' 'no game no life'")
      } else if (animeGenre == "action")
        println("Which Anime? 'seven deadly sins' 'sword art online' 'black clover' 'hinomaru sumo'")
      else if (animeGenre == "slice-of-life")
        println("Which Anime? 'nozaki kun' 'free' 'haikyu'")
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
  def createanimeRecord(animeTitle: String, connection: Connection, user: String): Unit = {
    println("How good was this Anime 1-10?")
    var AnimeRating = scala.io.StdIn.readLine()
    println("How many episodes did you watch?")
    var AnimeEpisodes = scala.io.StdIn.readLine()
    println("How likely are you to recommend 1-10?")
    var AnimeRecom = scala.io.StdIn.readLine()
    LogAnimeDB(animeTitle, AnimeRating, AnimeEpisodes, AnimeRecom, connection, user)
  }

  //OPTION 1 step 3: Insert record into Anime table
  def LogAnimeDB(name : String, rating : String, episode: String, recom : String, connection: Connection, user : String): Unit ={
    val pstmt = connection.prepareStatement("INSERT INTO Anime(animeTitle,AnimeRating,AnimeRecom,AnimeEpisodes,user) VALUES(?,?,?,?,?)")
    try{
      pstmt.setString(1, name)
      pstmt.setInt(2, rating.toInt)
      pstmt.setInt(3, episode.toInt)
      pstmt.setInt(4, recom.toInt)
      pstmt.setString(5, user)
      pstmt.executeUpdate
      println("Your Anime has been successfully logged! Do you want to log more Anime? y or n")
    }
    catch {
      case e: IllegalArgumentException => println("Invalid, Input wasn't added.\n" +
        "Do you want to add a Anime? y or n")
    }
  }

  //OPTION 2: Print a users log
  def checkUser(x : String, connection: Connection): Unit = {
    var pstmt = connection.prepareStatement("SELECT * FROM Anime WHERE user= ?")
    pstmt.setString(1,x)
    var rs = pstmt.executeQuery()
    println("  Anime        Rating       episode        recom           User")
    while (rs.next) {
      val animeTitle= rs.getString("animeTitle")
      val AnimeRating = rs.getInt("AnimeRating")
      val AnimeEpisodes = rs.getInt("AnimeEpisodes")
      val AnimeRecom = rs.getInt("AnimeRecom")
      val user = rs.getString("user")
      println(s"$animeTitle,      $AnimeRating,      $AnimeEpisodes,      $AnimeRecom,      $user")
    }
  }

  //OPTION 4 METHOD THAT PRINTS OUT ALL THE LOGS OF ALL USER
  def printAllAnimeLog(connection: Connection): Unit = {
    val statement = connection.createStatement
    val rs = statement.executeQuery("SELECT * FROM Anime ORDER BY user")
    println("Anime      Rating      episode      recom      User")
    while (rs.next) {
      val animeTitle= rs.getString("animeTitle")
      val AnimeRating = rs.getInt("AnimeRating")
      val AnimeEpisodes = rs.getInt("AnimeEpisodes")
      val AnimeRecom = rs.getInt("AnimeRecom")
      val user = rs.getString("user")
      println(s"$animeTitle,       $AnimeRating,      $AnimeEpisodes,      $AnimeRecom,      $user")
    }
  }

  def deleteUserLog(user : String, connection: Connection): Unit = {
    println("Are you sure you wanna delete your logs? y or n")
    var yesOrNo = scala.io.StdIn.readLine()
    if (yesOrNo=="y"){
      var pstmt = connection.prepareStatement("DELETE FROM Anime WHERE user=?")
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
