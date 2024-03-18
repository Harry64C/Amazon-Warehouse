/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


 import java.sql.DriverManager;
 import java.sql.Connection;
 import java.sql.Statement;
 import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.io.File;
 import java.io.FileReader;
 import java.io.BufferedReader;
 import java.io.InputStreamReader;
 import java.util.List;
 import java.util.ArrayList;
 import java.lang.Math;
 import java.sql.Timestamp;
 import java.time.Instant;  

import java.awt.*;
import java.awt.event.*;
 /**
  * This class defines a simple embedded SQL utility class that is designed to
  * work with PostgreSQL JDBC drivers.
  *
  */
 public class Amazon {
 
    // reference to physical database connection.
    private Connection _connection = null;
 
    // handling the keyboard inputs through a BufferedReader
    // This variable can be global for convenience.
    static BufferedReader in = new BufferedReader(
                                 new InputStreamReader(System.in));
 
    /**
     * Creates a new instance of Amazon store
     *
     * @param hostname the MySQL or PostgreSQL server hostname
     * @param database the name of the database
     * @param username the user name used to login to the database
     * @param password the user login password
     * @throws java.sql.SQLException when failed to make a connection.
     */
    public Amazon(String dbname, String dbport, String user, String passwd) throws SQLException {
 
       System.out.print("Connecting to database...");
       try{
          // constructs the connection URL
          String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
          System.out.println ("Connection URL: " + url + "\n");
 
          // obtain a physical connection
          this._connection = DriverManager.getConnection(url, user, passwd);
          System.out.println("Done");
       }catch (Exception e){
          System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
          System.out.println("Make sure you started postgres on this machine");
          System.exit(-1);
       }//end catch
    }//end Amazon
 
    // Method to calculate euclidean distance between two latitude, longitude pairs. 
    public double calculateDistance (double lat1, double long1, double lat2, double long2){
       double t1 = (lat1 - lat2) * (lat1 - lat2);
       double t2 = (long1 - long2) * (long1 - long2);
       return Math.sqrt(t1 + t2); 
    }
    /**
     * Method to execute an update SQL statement.  Update SQL instructions
     * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
     *
     * @param sql the input SQL string
     * @throws java.sql.SQLException when update failed
     */
    public void executeUpdate (String sql) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the update instruction
       stmt.executeUpdate (sql);
 
       // close the instruction
       stmt.close ();
    }//end executeUpdate
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and outputs the results to
     * standard out.
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQueryAndPrintResult (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);
 
       /*
        ** obtains the metadata object for the returned result set.  The metadata
        ** contains row and column info.
        */
       ResultSetMetaData rsmd = rs.getMetaData ();
       int numCol = rsmd.getColumnCount ();
       int rowCount = 0;
 
       // iterates through the result set and output them to standard out.
       boolean outputHeader = true;
       while (rs.next()){
        if(outputHeader){
          for(int i = 1; i <= numCol; i++){
          System.out.print(rsmd.getColumnName(i) + "\t");
          }
          System.out.println();
          outputHeader = false;
        }
          for (int i=1; i<=numCol; ++i)
             System.out.print (rs.getString (i) + "\t");
          System.out.println();
          ++rowCount;
       }//end while
       stmt.close ();
       return rowCount;
    }//end executeQuery
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the results as
     * a list of records. Each record in turn is a list of attribute values
     *
     * @param query the input query string
     * @return the query result as a list of records
     * @throws java.sql.SQLException when failed to execute the query
     */
    public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);
 
       /*
        ** obtains the metadata object for the returned result set.  The metadata
        ** contains row and column info.
        */
       ResultSetMetaData rsmd = rs.getMetaData ();
       int numCol = rsmd.getColumnCount ();
       int rowCount = 0;
 
       // iterates through the result set and saves the data returned by the query.
       boolean outputHeader = false;
       List<List<String>> result  = new ArrayList<List<String>>();
       while (rs.next()){
         List<String> record = new ArrayList<String>();
       for (int i=1; i<=numCol; ++i)
          record.add(rs.getString (i));
         result.add(record);
       }//end while
       stmt.close ();
       return result;
    }//end executeQueryAndReturnResult
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the number of results
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQuery (String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement ();
 
        // issues the query instruction
        ResultSet rs = stmt.executeQuery (query);
 
        int rowCount = 0;
 
        // iterates through the result set and count nuber of results.
        while (rs.next()){
           rowCount++;
        }//end while
        stmt.close ();
        return rowCount;
    }
 
    /**
     * Method to fetch the last value from sequence. This
     * method issues the query to the DBMS and returns the current
     * value of sequence used for autogenerated keys
     *
     * @param sequence name of the DB sequence
     * @return current value of a sequence
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int getCurrSeqVal(String sequence) throws SQLException {
    Statement stmt = this._connection.createStatement ();
 
    ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
    if (rs.next())
       return rs.getInt(1);
    return -1;
    }
 
    /**
     * Method to close the physical connection if it is open.
     */
    public void cleanup(){
       try{
          if (this._connection != null){
             this._connection.close ();
          }//end if
       }catch (SQLException e){
          // ignored.
       }//end try
    }//end cleanup
 
    /**
     * The main execution method
     *
     * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
     */
    static boolean isManager = false;;
    public static void main (String[] args) {
       if (args.length != 3) {
          System.err.println (
             "Usage: " +
             "java [-classpath <classpath>] " +
             Amazon.class.getName () +
             " <dbname> <port> <user>");
          return;
       }//end if
 
       Greeting();
       Amazon esql = null;
       try{
          // use postgres JDBC driver.
          Class.forName ("org.postgresql.Driver").newInstance ();
          // instantiate the Amazon object and creates a physical
          // connection.
          String dbname = args[0];
          String dbport = args[1];
          String user = args[2];
          esql = new Amazon (dbname, dbport, user, "");
 
          boolean keepon = true;
          while(keepon) {
             // These are sample SQL statements
             System.out.println("MAIN MENU");
             System.out.println("---------");
             System.out.println("1. Create user");
             System.out.println("2. Log in");
             System.out.println("9. < EXIT");
             String authorisedUser = null;
             switch (readChoice()){
                case 1: CreateUser(esql); break;
                case 2: authorisedUser = LogIn(esql); break;
                case 9: keepon = false; break;
                default : System.out.println("Unrecognized choice!"); break;
             }//end switch
             if (authorisedUser != null) {
               boolean usermenu = true;
               isManagerFunc(esql, authorisedUser);
               while(usermenu) {
                 System.out.println("MAIN MENU");
                 System.out.println("---------");
                 System.out.println("1. View Stores within 30 miles");
                 System.out.println("2. View Product List");
                 System.out.println("3. Place a Order");
                 System.out.println("4. View 5 recent orders");
 
                 //the following functionalities basically used by managers
                 if(isManager){
                  System.out.println("5. Update Product");
                  System.out.println("6. View 5 recent Product Updates Info");
                  System.out.println("7. View 5 Popular Items");
                  System.out.println("8. View 5 Popular Customers");
                  System.out.println("9. Place Product Supply Request to Warehouse");
                 }
                 System.out.println(".........................");
                 System.out.println("20. Log out");
                 switch (readChoice()){
                    case 1: viewStores(esql,authorisedUser); break;
                    case 2: viewProducts(esql); break;
                    case 3: placeOrder(esql, authorisedUser); break;
                    case 4: viewRecentOrders(esql,authorisedUser); break;
                    case 5: updateProduct(esql,authorisedUser); break;
                    case 6: viewRecentUpdates(esql, authorisedUser); break;
                    case 7: viewPopularProducts(esql, authorisedUser); break;
                    case 8: viewPopularCustomers(esql, authorisedUser); break;
                    case 9: placeProductSupplyRequests(esql, authorisedUser); break;
 
                    case 20: usermenu = false; break;
                    default : System.out.println("Unrecognized choice!"); break;
                 }
               }
             }
          }//end while
       }catch(Exception e) {
          System.err.println (e.getMessage ());
       }finally{
          // make sure to cleanup the created table and close the connection.
          try{
             if(esql != null) {
                System.out.print("Disconnecting from database...");
                esql.cleanup ();
                System.out.println("Done\n\nBye !");
             }//end if
          }catch (Exception e) {
             // ignored.
          }//end try
       }//end try
    }//end main
 
    public static void Greeting(){
       System.out.println(
          "\n\n*******************************************************\n" +
          "              Welcome to the Amazon WareHouse              \n" +
          "*******************************************************\n");
    }//end Greeting
 
    /*
     * Reads the users choice given from the keyboard
     * @int
     **/
    public static int readChoice() {
       int input;
       // returns only if a correct value is given.
       do {
          System.out.print("Please make your choice: ");
          try { // read the integer, parse it and break.
             input = Integer.parseInt(in.readLine());
             break;
          }catch (Exception e) {
             System.out.println("Your input is invalid!");
             continue;
          }//end try
       }while (true);
       return input;
    }//end readChoice
 
    /*
     * Creates a new user
     **/
    public static void CreateUser(Amazon esql){
       try{
          System.out.print("\tEnter name: ");
          String name = in.readLine();
          System.out.print("\tEnter password: ");
          String password = in.readLine();
          System.out.print("\tEnter latitude: ");   
          String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
          System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
          String longitude = in.readLine();
          
          String type="Customer";
 
          String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);
 
          esql.executeUpdate(query);
          System.out.println ("User successfully created!");
       }catch(Exception e){
          System.err.println (e.getMessage ());
       }
    }//end CreateUser
 
    public static Void isManagerFunc(Amazon esql, String user){
      List<List<String>> result;
       try{
          String query = String.format("SELECT userID, type FROM Users WHERE name = '%s'",user);
          result = esql.executeQueryAndReturnResult(query);
          List<String> usr = result.get(0);
          String type = usr.get(1).trim();
          if(!type.equalsIgnoreCase("manager") && !type.equalsIgnoreCase("admin")){
             isManager = false;
             return null;
          }
          else{
            isManager = true;
            return null;
          }
         }catch(Exception e){
            System.err.println(e);
         }
         return null;
    }
    /*
     * Check log in credentials for an existing user
     * @return User login or null is the user does not exist
     **/
    public static String LogIn(Amazon esql){
       try{
          System.out.print("\tEnter name: ");
          String name = in.readLine();
          System.out.print("\tEnter password: ");
          String password = in.readLine();
 
          String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
          int userNum = esql.executeQuery(query);
     if (userNum > 0)
       return name;
          return null;
       }catch(Exception e){
          System.err.println (e.getMessage ());
          return null;
       }
    }//end
 
 // Rest of the functions definition go in here
 
    public static void viewStores(Amazon esql, String user) {
       // get the user location here;
       List<List<String>> result;
       try{
          String query = String.format("SELECT latitude, longitude FROM Users WHERE name = '%s'",user);
          result = esql.executeQueryAndReturnResult(query);
          List<String> coords = result.get(0);
          try{
            query = String.format("SELECT * FROM Store " + 
            "WHERE storeID IN "+
            "(SELECT s2.storeID FROM Store s2 " + 
            "GROUP BY s2.storeID HAVING" + 
            "  SQRT(POW((s2.latitude - %s), 2) + POW((s2.longitude - %s), 2)) < 30)",Double.parseDouble(coords.get(0)), Double.parseDouble(coords.get(1)));
            result = esql.executeQueryAndReturnResult(query);

            System.out.println("Store Id \t latitude \t longitude \t  manager id \t date established");
             for(int i = 0; i < result.size(); i++){
                for(int j = 0; j < result.get(i).size(); j++){
                   System.out.print(result.get(i).get(j) + "\t\t");
                }
                System.out.println();
             }
          }catch(Exception e){
             System.err.println(e.getMessage());
          }
       }catch(Exception e){
          System.err.println(e.getMessage());
       }
 
 
    }
    public static void viewProducts(Amazon esql) {
       System.out.println("Enter StoreID: ");
       int input = -1;
       do {
          try { // read the integer, parse it and break.
             input = Integer.parseInt(in.readLine());
             break;
          }catch (Exception e) {
             System.out.println("Your input is invalid!");
             continue;
          }
       } while(input == -1);
       String query = String.format("SELECT * FROM Product WHERE storeID = '%s'", input);
       try{
          esql.executeQueryAndPrintResult(query);
       } catch(Exception e){
          System.err.println(e.getMessage());
       }
    }


    public static void placeOrder(Amazon esql, String user) {
      List<List<String>> result;
      String query;

      try{
         query = String.format("SELECT latitude, longitude FROM Users WHERE name = '%s'",user);
         result = esql.executeQueryAndReturnResult(query);
         List<String> coords = result.get(0);
         try{
            System.out.print("\tEnter storeId: ");
            String storeID = in.readLine();
            
            // make sure store is within 30 miles
            query = String.format("SELECT latitude, longitude FROM Store WHERE storeID = %s", storeID);
            result = esql.executeQueryAndReturnResult(query);

            if(result.isEmpty()){
               System.out.println ("Store does not exist!");
               return;
            }
            List<String> storeCoords = result.get(0);

            Double distance = esql.calculateDistance(Double.parseDouble(coords.get(0)), Double.parseDouble(coords.get(1)),Double.parseDouble(storeCoords.get(0)),Double.parseDouble(storeCoords.get(1)));
            if(distance > 30){
               System.out.println ("Store is too far to order from!");
               return;
            }

            // get productName and unitsOrdered from User
            System.out.print("\tEnter the name of the product you wish to purchase: ");
            String productName = in.readLine();
            System.out.print("\tEnter number of items: ");   
            String unitsOrdered = in.readLine();   

            // make sure the number of units ordered is possible
            query = String.format("SELECT numberOfUnits FROM Product WHERE storeID = '%s' AND productName = '%s'", storeID, productName);
            result = esql.executeQueryAndReturnResult(query);
            int inStock = Integer.parseInt(result.get(0).get(0));
            while ((Integer.parseInt(unitsOrdered) > inStock) ||  (Integer.parseInt(unitsOrdered) < 1)) {
               System.out.print("\tError! Enter a reasonable number of items: ");   
               unitsOrdered = in.readLine();   
            }

            // get the timestamp and customerID
            Timestamp orderTime = new Timestamp(System.currentTimeMillis());
            query = String.format("SELECT userID FROM Users WHERE name = '%s'",user);
            result = esql.executeQueryAndReturnResult(query);
            String customerID = result.get(0).get(0);

            // get the order number (increments each time)
            query = String.format("SELECT COUNT(*)+1 FROM Orders");
            result = esql.executeQueryAndReturnResult(query);
            String orderNumber = result.get(0).get(0);
            System.out.print("Your order number is " + orderNumber + "\n");

            // put it all together
            query = String.format("INSERT INTO ORDERS (orderNumber, customerID, storeID, productName, unitsOrdered, orderTime) VALUES ('%s','%s', '%s', '%s','%s', '%s')", orderNumber, customerID, storeID, productName, unitsOrdered, orderTime);
            esql.executeUpdate(query);
            System.out.println ("Order successfully created!");

            // now update the product table 
            // try{
            //    query = String.format("UPDATE Product SET numberOfUnits = numberOfUnits - '%s' WHERE storeID = '%s' AND productName = '%s'", unitsOrdered, storeID, productName);
            //    esql.executeUpdate(query);
            //    System.out.println ("Store offerings updated");
            // }catch(Exception e){
            //    System.err.println (e.getMessage ());
            // }
         }catch(Exception e){
            System.err.println (e.getMessage ());
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
    }


    public static void viewRecentOrders(Amazon esql, String usr) {
       List<List<String>> result;
       try{
          String query = String.format("SELECT userID, type FROM Users WHERE name = '%s'",usr);
          result = esql.executeQueryAndReturnResult(query);
          List<String> user = result.get(0);
          if(user.get(1).trim().equalsIgnoreCase("manager") ){
             query = String.format("SELECT o.orderNumber, u.name, s.storeID, o.productName, o.orderTime FROM Orders o, Store s, Users u WHERE s.storeID = o.storeID AND s.managerID = %s AND o.customerID = u.userID ORDER BY o.orderTime DESC",user.get(0));
             int rows = esql.executeQueryAndPrintResult(query);
          }
          else if (user.get(1).trim().equalsIgnoreCase("admin")){
            query = "SELECT o.orderNumber, u.name, s.storeID, o.productName, o.orderTime FROM Orders o, Store s, Users u WHERE s.storeID = o.storeID AND o.customerID = u.userID ORDER BY o.orderTime DESC";
             int rows = esql.executeQueryAndPrintResult(query);
          }
          else{
             query = String.format("SELECT * FROM Orders WHERE customerID = %s ORDER BY orderTime DESC LIMIT 5",user.get(0));
             int rows = esql.executeQueryAndPrintResult(query);
          }
       }catch(Exception e){
          System.err.println(e.getMessage());
       }
    }
    public static void updateProduct(Amazon esql, String usr) {
       List<List<String>> result;
       try{
          String query = String.format("SELECT userID, type FROM Users WHERE name = '%s'",usr);
          result = esql.executeQueryAndReturnResult(query);
          List<String> user = result.get(0);
          String type = user.get(1).trim();
          if(!type.equalsIgnoreCase("manager") && !type.equalsIgnoreCase("admin")){
             System.out.println("You are not a manager or an admin");
             return;
          }
          int storeid = 0;
          do{
             try{
                System.out.println("What is the store Id you are trying to update for:");
                storeid = Integer.parseInt(in.readLine());
                query = String.format("SELECT managerID from Store WHERE storeID = %s",storeid);
                result = esql.executeQueryAndReturnResult(query);
                if(!type.equalsIgnoreCase("admin") && Integer.parseInt(result.get(0).get(0)) != Integer.parseInt(user.get(0))){
                   System.out.println("You are not the Manager");
                   continue;
                }
                else{
                   break;
                }
             } catch(Exception e){
                System.err.println(e.getMessage());
             }
          } while(true);
          String pname = "";
          do{
             try{
                System.out.println("What is the Product Name you are tying to update for:");
                pname = in.readLine();
                query = String.format("Select productName FROM Product WHERE productName = '%s' AND storeID = %s", pname, storeid);
                int rows = esql.executeQuery(query);
                if(rows == 0){
                   System.out.println("Product does not exist");
                   continue;
                }
                else{
                   break;
                }
                
             } catch(Exception e){
                System.err.println(e.getMessage());
             }
          } while(true);
          try{
             System.out.println("new number of units:");
             int numofUnits = Integer.parseInt(in.readLine());
             System.out.println("new Price:");
             float newPrice = Float.parseFloat(in.readLine());
             query = String.format("UPDATE Product SET numberOfUnits = %s, pricePerUnit = %s WHERE storeID = %s AND productName = '%s'", numofUnits, newPrice, storeid, pname);
             esql.executeUpdate(query);
          } catch(Exception e){
             System.err.println(e.getMessage());
          }
       }catch(Exception e){
          System.err.println(e.getMessage());
       }
    }


    public static void viewRecentUpdates(Amazon esql, String user) {
      List<List<String>> result;
      String query;

      try{
         query = String.format("SELECT userID, type FROM Users WHERE name = '%s'",user);
         result = esql.executeQueryAndReturnResult(query);
         int managerID = Integer.parseInt(result.get(0).get(0));
         String type = result.get(0).get(1).trim();
         if(!type.equalsIgnoreCase("admin")){
               if(!type.equalsIgnoreCase("manager")){
               System.out.println(result.get(0).get(1));
               System.out.println("You are not the Manager");
               return;
            }
         }
         System.out.println("Enter storeID:");
         int storeID = Integer.parseInt(in.readLine());
         query = String.format("SELECT managerID from Store WHERE storeID = %s",storeID);
         result = esql.executeQueryAndReturnResult(query);
         // if (managerID != Integer.parseInt(result.get(0).get(0)) && !result.get(0).get(1).equalsIgnoreCase("admin")){
         //    System.out.println("You are not the Manager");
         //    return;
         // }
         if(!type.equalsIgnoreCase("admin")){
            if( managerID != Integer.parseInt(result.get(0).get(0)) ){
               System.out.println("You are not the Manager");
               return;
            }
         }
         // now select latest 5 updates
         query = String.format("SELECT * FROM ProductUpdates WHERE storeID = '%s' ORDER BY updateNumber DESC LIMIT 5", storeID);
         esql.executeQueryAndPrintResult(query);
      } catch(Exception e){
         System.err.println(e.getMessage());
      }
    }


    
    public static void viewPopularProducts(Amazon esql, String user) {
      List<List<String>> result;
      String query;
      try{
         System.out.println ("------------------------------------");
         System.out.println("\tMost Popular Items");
         System.out.println ("------------------------------------");

         query = String.format("SELECT userID FROM Users WHERE name = '%s'",user);
         result = esql.executeQueryAndReturnResult(query);
         String managerID = result.get(0).get(0);

         query = String.format("Select COUNT(storeID) FROM Store WHERE managerID = '%s'", managerID);
         result = esql.executeQueryAndReturnResult(query);
         int numOfStores = Integer.parseInt(result.get(0).get(0));
         //System.out.println(numOfStores);

         query = String.format("Select storeID FROM Store WHERE managerID = '%s'", managerID);
         result = esql.executeQueryAndReturnResult(query);

         for(int i = 0; i < numOfStores; i++) {
            int storeID = Integer.parseInt(result.get(i).get(0));
            System.out.println("\tFor store " + storeID + ":");

            query = String.format("SELECT productName FROM Orders WHERE storeID = '%s' ORDER BY unitsOrdered DESC LIMIT 5", storeID);
            esql.executeQueryAndPrintResult(query);
         }
         System.out.println ("------------------------------------");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
    }

    public static void viewPopularCustomers(Amazon esql, String user) {
      List<List<String>> result;
      String query;
      try{
         System.out.println ("------------------------------------");
         System.out.println("\tTop 5 Customers");
         System.out.println ("------------------------------------");

         query = String.format("SELECT userID FROM Users WHERE name = '%s'",user);
         result = esql.executeQueryAndReturnResult(query);
         String managerID = result.get(0).get(0);

         query = String.format("Select COUNT(storeID) FROM Store WHERE managerID = '%s'", managerID);
         result = esql.executeQueryAndReturnResult(query);
         int numOfStores = Integer.parseInt(result.get(0).get(0));
         //System.out.println(numOfStores);

         query = String.format("Select storeID FROM Store WHERE managerID = '%s'", managerID);
         result = esql.executeQueryAndReturnResult(query);

         for(int i = 0; i < numOfStores; i++) {
            int storeID = Integer.parseInt(result.get(i).get(0));
            System.out.println("\tFor store " + storeID + ":");
            
            query = String.format("SELECT name FROM users U, (SELECT customerID, SUM(unitsOrdered) AS x FROM Orders WHERE storeID = '%s' GROUP BY customerID ORDER BY x DESC LIMIT 5) AS top5 WHERE U.userID = top5.customerID", storeID);
            esql.executeQueryAndPrintResult(query);
         }
         System.out.println ("------------------------------------");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
    }


    public static void placeProductSupplyRequests(Amazon esql, String user) {
      List<List<String>> result;
      String query;

      try{
         query = String.format("SELECT userID,type FROM Users WHERE name = '%s'",user);
         result = esql.executeQueryAndReturnResult(query);
         int managerID = Integer.parseInt(result.get(0).get(0));
         String type = result.get(0).get(1).trim();
         System.out.println("Enter storeID:");
         int storeID = Integer.parseInt(in.readLine());
         query = String.format("SELECT managerID from Store WHERE storeID = %s",storeID);
         result = esql.executeQueryAndReturnResult(query);
         if(!type.equalsIgnoreCase("admin")){
            if (managerID != Integer.parseInt(result.get(0).get(0))){
               System.out.println("You are not the Manager");
               return;
            }
         }
         System.out.print("\tEnter product name: ");
         String productName = in.readLine();
         System.out.print("\tEnter number of units needed: ");
         String unitsRequested = in.readLine();
         System.out.print("\tEnter warehouse ID: ");
         String warehouseID = in.readLine();

         // get the order number (increments each time)
         query = String.format("SELECT COUNT(*)+1 FROM ProductSupplyRequests");
         result = esql.executeQueryAndReturnResult(query);
         String requestNumber = result.get(0).get(0);
         System.out.print("Your request number is " + requestNumber + "\n");

         // create a new product request
         try{
            query = String.format("INSERT INTO ProductSupplyRequests (requestNumber, managerID, warehouseID, storeID, productName, unitsRequested) VALUES ('%s','%s','%s','%s','%s','%s')", requestNumber, managerID, warehouseID, storeID, productName, unitsRequested);
            esql.executeUpdate(query);
            System.out.println ("Supply request placed!");
         }catch(Exception e){
            System.err.println (e.getMessage ());
         }
         // now update the product table 
         // try{
         //    query = String.format("UPDATE Product SET numberOfUnits = numberOfUnits + '%s' WHERE storeID = '%s' AND productName = '%s'", unitsRequested, storeID, productName);
         //    esql.executeUpdate(query);
         //    System.out.println ("Store offerings updated");
         // }catch(Exception e){
         //    System.err.println (e.getMessage ());
         // }
      } catch(Exception e){
         System.err.println(e.getMessage());
      }
    }
 
 }//end Amazon
 