import java.sql.*;
import java.util.HashSet;
import java.util.LinkedList;

class User{
  String _userName;
  String _password;

  private static Connection conn = null;
  private static Statement stmt = null;

  private static final String ORACLE_DRIVER= "oracle.jdbc.OracleDriver";
  private static final String DB_URL = "jdbc:oracle:thin:@claros.cs.purdue.edu:1524:strep";
  private static final String USER_ID = "apydi";
  private static final String PASSWORD = "VVDcCZP2";

  public User(String userName, String password){
    this._userName = userName;
    this._password = password;
  }

  //we check for ADMIN BY CHECKING THE USERNAME.
  public boolean isAdmin(){
    return (this._userName.equals("admin"));
  }

  public boolean hasPrivilege(String tableName, String privilegeName){  //privilegeName such as INSERT, SELECT
    if(conn == null || stmt == null){
      System.out.println("Database not initialized.");
      return false;
    }
    else{
      try{
        //first we find the required USER_ID.
        int user_id = Integer.MIN_VALUE;
        String sql = "SELECT UserId, Username, Password FROM USERS";
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
          int id = rs.getInt("UserId");
          String username = rs.getString("Username");
          String password = rs.getString("Password");
          if(this._userName.equals(username) && this._password.equals(password)){
            user_id = id;
          }
        }
        if(user_id == Integer.MIN_VALUE){
          System.out.println("User Not Initialized.");
        }

        //next we find the required PRIV_ID
        int priv_id = Integer.MIN_VALUE;
        sql = "SELECT PrivId, Privname FROM Privileges";
        rs = stmt.executeQuery(sql);
        while(rs.next()){
          int id = rs.getInt("PrivId");
          String priv_name = rs.getString("PrivName");

          if(priv_name.equalsIgnoreCase(privilegeName)){
            priv_id = id;
          }
        }
        if(priv_id == Integer.MIN_VALUE){
          System.out.println("Privilege not Initialized.");
        }

        //check if user has PRIVILEGE
        sql = "SELECT * FROM UsersRoles, RolesPrivileges WHERE UsersRoles.RoleId = RolesPrivileges.RoleId AND UsersRoles.UserId = " + user_id + " AND RolesPrivileges.PrivId = " + priv_id + " AND UPPER('"+tableName+"') = UPPER(RolesPrivileges.tableName)";
        //System.out.println(sql);
        rs = stmt.executeQuery(sql);
        if(rs.next() == false){
          rs.close();
          return false;   //no privilege
        }
        else{
          rs.close();
          return true;
        }
      }
      catch(Exception e){
        e.printStackTrace();
        return false;
      }
    }
  }

  public void createRole(String roleName, String encryptionKey){
    if(conn == null || stmt == null){
      System.out.println("Database not initialized.");
      return;
    }
    else{
      try{
        //first we find a UNIQUE ROLEID dynamically
        HashSet<Integer> roleIDSet = new HashSet<Integer>();
        String sql = "SELECT RoleId FROM Roles";
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
          int id = rs.getInt("RoleId");
          roleIDSet.add(id);
          //System.out.println("Role IDs present:\t"+id+"\t");
        }
        rs.close();
        int newValidRoleID = 1;
        boolean newValidRoleIDIsInitialized = false;
        while(!newValidRoleIDIsInitialized){
          if(roleIDSet.contains(newValidRoleID)){
            newValidRoleID++;
          }
          else{
            //System.out.println("Valid Role ID found: "+newValidRoleID);
            newValidRoleIDIsInitialized = true;
          }
        }
        String sql_update = "INSERT INTO Roles VALUES ("+newValidRoleID+","+"\'"+roleName+"\',\'"+encryptionKey+"\')";
        stmt.execute(sql_update);
        Project3.writeToOutputFile("Role created successfully\n");
        //System.out.println(sql_update);
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
  }

  public void createUser(String username, String password){
    if(conn == null || stmt == null){
      System.out.println("Database not initialized.");
      return;
    }
    else{
      try{
        //first we find a UNIQUE ROLEID dynamically
        HashSet<Integer> userIDSet = new HashSet<Integer>();
        String sql = "SELECT UserId FROM Users";
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
          int id = rs.getInt("UserId");
          userIDSet.add(id);
          //System.out.println("USER IDs present:\t"+id+"\t");
        }
        rs.close();
        int newValidUserID = 1;
        boolean newValidUserIDIsInitialized = false;
        while(!newValidUserIDIsInitialized){
          if(userIDSet.contains(newValidUserID)){
            newValidUserID++;
          }
          else{
            //System.out.println("Valid Role ID found: "+newValidUserID);
            newValidUserIDIsInitialized = true;
          }
        }
        String sql_update = "INSERT INTO Users VALUES ("+newValidUserID+","+"\'"+username+"\',\'"+password+"\')";
        stmt.execute(sql_update);
        Project3.writeToOutputFile("User created successfully\n");
        //System.out.println(sql_update);
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
  }

  public void grantRole(String username, String roleName){
    if(conn == null || stmt == null){
      System.out.println("Database not initialized.");
      return;
    }
    else{
      try{
        //find the corresponding USER_ID in the Users table
        int user_id = Integer.MIN_VALUE;
        String sql = "SELECT UserId, Username FROM Users";
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
          int id = rs.getInt("UserId");
          String user = rs.getString("Username");
          if(username.equals(user)){
            user_id = id;
            break;
          }
        }
        if(user_id == Integer.MIN_VALUE){
          System.out.println("Username not initialized:"+username);
          return;
        }
        else{
          //System.out.println("Found User ID: "+user_id +"\tfor "+username);
        }

        //find the corresponding ROLE_ID in the Roles table
        int role_id = Integer.MIN_VALUE;
        sql = "SELECT RoleId, RoleName FROM Roles";
        rs = stmt.executeQuery(sql);
        while(rs.next()){
          int id = rs.getInt("RoleId");
          String role = rs.getString("RoleName");
          if(roleName.equals(role)){
            role_id = id;
          }
        }
        if(role_id == Integer.MIN_VALUE){
          System.out.println("Role not initialized: "+roleName);
          return;
        }
        else{
          //System.out.println("Found Role Id: "+role_id+"\tfor "+roleName);
        }

        //Now insert the relevant values into UsersRoles
        String sql_update = "INSERT INTO UsersRoles VALUES ("+user_id+","+"\'"+role_id+"\')";
        stmt.execute(sql_update);
        Project3.writeToOutputFile("Role assigned successfully\n");
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
  }

  public void grantPrivilege(String privName, String roleName, String tableName){
    if(conn == null || stmt == null){
      System.out.println("Database not initialized.");
      return;
    }
    else{
      try{
        //find corresponding privId in privileges table
        int priv_id = Integer.MIN_VALUE;
        String sql = "SELECT PrivId, PrivName FROM Privileges";
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
          int id = rs.getInt("PrivId");
          String priv = rs.getString("PrivName");
          if(privName.equals(priv)){
            priv_id = id;
            break;
          }
        }
        if(priv_id == Integer.MIN_VALUE){
          System.out.println("Privilege not initialized:"+privName);
          return;
        }
        else{
          //System.out.println("Found Priv ID: "+priv_id +"\tfor "+privName);
        }

        //find the corresponding ROLE_ID in the Roles table
        int role_id = Integer.MIN_VALUE;
        sql = "SELECT RoleId, RoleName FROM Roles";
        rs = stmt.executeQuery(sql);
        while(rs.next()){
          int id = rs.getInt("RoleId");
          String role = rs.getString("RoleName");
          if(roleName.equals(role)){
            role_id = id;
          }
        }
        if(role_id == Integer.MIN_VALUE){
          System.out.println("Role not initialized: "+roleName);
          return;
        }
        else{
          //System.out.println("Found Role Id: "+role_id+"\tfor "+roleName);
        }

        //Now insert the relevant values into RolesPrivileges
        String sql_update = "INSERT INTO RolesPrivileges VALUES ("+role_id+","+"\'"+priv_id+"\'," + "\'"+tableName+"\')";
        stmt.execute(sql_update);
        Project3.writeToOutputFile("Privilege granted successfully\n");
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
  }

  public void revokePrivilege(String privName, String roleName, String tableName){
    if(conn == null || stmt == null){
      System.out.println("Database not initialized.");
      return;
    }
    else{
      try{
        //find corresponding privId in privileges table
        int priv_id = Integer.MIN_VALUE;
        String sql = "SELECT PrivId, PrivName FROM Privileges";
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
          int id = rs.getInt("PrivId");
          String priv = rs.getString("PrivName");
          if(privName.equals(priv)){
            priv_id = id;
            break;
          }
        }
        if(priv_id == Integer.MIN_VALUE){
          System.out.println("Privilege not initialized:"+privName);
          return;
        }
        else{
          //System.out.println("Found Priv ID: "+priv_id +"\tfor "+privName);
        }

        //find the corresponding ROLE_ID in the Roles table
        int role_id = Integer.MIN_VALUE;
        sql = "SELECT RoleId, RoleName FROM Roles";
        rs = stmt.executeQuery(sql);
        while(rs.next()){
          int id = rs.getInt("RoleId");
          String role = rs.getString("RoleName");
          if(roleName.equals(role)){
            role_id = id;
          }
        }
        if(role_id == Integer.MIN_VALUE){
          System.out.println("Role not initialized: "+roleName);
          return;
        }
        else{
          //System.out.println("Found Role Id: "+role_id+"\tfor "+roleName);
        }

        //find the tablename as it is stored in RolesPrivileges
        sql = "SELECT TableName FROM RolesPrivileges";
        rs = stmt.executeQuery(sql);
        while(rs.next()){
          String table = rs.getString("TableName");
          if(tableName.equalsIgnoreCase(table)){
            tableName = table;    //implements case insensitivity for table names
          }
        }

        //Now DELETE the relevant values from RolesPrivileges
        String sql_update = "DELETE FROM RolesPrivileges WHERE ROLEID = " +role_id +" AND PRIVID = "+priv_id+" AND TABLENAME = \'"+tableName+"\'";
        stmt.execute(sql_update);
        //System.out.println(sql_update);
        Project3.writeToOutputFile("Privilege revoked successfully\n");
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
  }

  public void insertInto(String tableName, String[] tableValues, String encryptColumn, String ownerRole){
    if(conn == null || stmt == null){
      System.out.println("Database not initialized.");
      return;
    }
    else{
      try{
        //first find the corresponding role_id
        int ownerRoleID = findRoleID(ownerRole);
        if(ownerRoleID == Integer.MIN_VALUE){
          System.out.println("Owner Role Id Not Found");
        }
        String encryption_key = getEncryptionKey(ownerRoleID);
        if(encryption_key == null){
          System.out.println("encryption_key not found");
        }
        int columnToEncrypt = Integer.parseInt(encryptColumn);
        columnToEncrypt -= 1;
        if(columnToEncrypt >= 0 && columnToEncrypt < tableValues.length){
          tableValues[columnToEncrypt] = Project3.AKEncrypt(tableValues[columnToEncrypt], encryption_key);
        }

        //we need to ADD SINGLE quotes to values corresponding to VARCHAR columns
        ResultSet test = stmt.executeQuery("Select * from "+tableName);
        ResultSetMetaData rsmd = test.getMetaData();
        int columnCount = rsmd.getColumnCount();
        for(int i = 1; i <= columnCount  - 2; i++){   //guaranteed last two columns will be numbers
          //System.out.println(rsmd.getColumnName(i)+" "+rsmd.getColumnType(i));
          if(rsmd.getColumnType(i) == Types.VARCHAR || rsmd.getColumnType(i) == Types.DATE || rsmd.getColumnType(i) == Types.TIMESTAMP || rsmd.getColumnType(i) == Types.TIME){
            tableValues[i-1] = "'"+tableValues[i-1]+"'";  //add single quotes
          }
        }

        String sql = "INSERT INTO "+ tableName + " VALUES(";
        for(int i =0; i < tableValues.length; i++){
          sql+= tableValues[i]+", ";
        }
        columnToEncrypt += 1;
        sql += (columnToEncrypt + ", ");
        sql += (ownerRoleID +")");
        //System.out.println(sql);
        stmt.execute(sql);
        Project3.writeToOutputFile("Row inserted successfully\n");
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
  }

  public void printTableContents(String tableName){
    if(conn == null || stmt == null){
      System.out.println("Database not initialized.");
      return;
    }
    else{
      try{
        //find the corresponding USER_ID in the Users table
        int user_id = Integer.MIN_VALUE;
        String sql = "SELECT UserId, Username, Password FROM Users";
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
          int id = rs.getInt("UserId");
          String user = rs.getString("Username");
          String pass = rs.getString("Password");
          if(this._userName.equals(user) && this._password.equals(pass)){
            user_id = id;
            break;
          }
        }
        if(user_id == Integer.MIN_VALUE){
          System.out.println("Username not initialized:"+this._userName);
          return;
        }
        else{
          //System.out.println("Found User ID: "+user_id +"\tfor "+this._userName);
        }
        rs.close();

        ResultSet test = stmt.executeQuery("Select * from "+tableName);
        ResultSetMetaData rsmd = test.getMetaData();
        int columnCount = rsmd.getColumnCount();
        for(int i = 1; i < columnCount-2; i++){
          Project3.writeToOutputFile(rsmd.getColumnName(i).toUpperCase() + ", ");
        }
        Project3.writeToOutputFile(rsmd.getColumnName(columnCount-2).toUpperCase()+"\n");

        while(test.next()){
          LinkedList<String> tupleValues = new LinkedList<String>();
          for(int i = 1; i <= columnCount; i++){
            tupleValues.add(test.getString(i));
            //System.out.print(test.getString(i) + "\t");
          }
          //System.out.println();
          //assume last element is owner_role_id
          int owner_role_id = Integer.parseInt(tupleValues.get(tupleValues.size() - 1));
          //second last element is encrypted column
          int encrypt_column = Integer.parseInt(tupleValues.get(tupleValues.size() -2));
          //System.out.println(owner_role_id+"\t"+encrypt_column);
          //System.out.println(doesUserRolePairExist(user_id , owner_role_id));
          if((encrypt_column-1) >= 0 && (encrypt_column-1) < tupleValues.size()-2 && doesUserRolePairExist(user_id , owner_role_id)){
            tupleValues.set(encrypt_column-1, Project3.AKDecrypt(tupleValues.get(encrypt_column-1), getEncryptionKey(owner_role_id)));
          }
          for(int i=0; i < tupleValues.size()-3; i++){
            Project3.writeToOutputFile(tupleValues.get(i)+", ");
          }
          Project3.writeToOutputFile(tupleValues.get(tupleValues.size()-3)+"\n");
        }
        test.close();
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
  }

  public static User attemptLogin(String user_name, String pass_word){
    User toReturn = null;
    if(conn == null || stmt == null){
      System.out.println("Database not initialized.");
      return toReturn;
    }
    try{
      String sql = "SELECT UserId, Username, Password FROM Users";
      ResultSet rs = stmt.executeQuery(sql);
      while(rs.next()){
        int id = rs.getInt("UserId");
        String username = rs.getString("Username");
        String password = rs.getString("Password");
        if(username.equals(user_name) && password.equals(pass_word)){
          Project3.writeToOutputFile("Login successful\n");
          toReturn = new User(user_name, pass_word);
          break;
        }
        //System.out.println(id+"\t"+username+"\t"+password);
      }
      if(toReturn == null){
        Project3.writeToOutputFile("Invalid login\n");
      }
      rs.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return toReturn;
  }

public static boolean doesUserRolePairExist(int user_id, int role_id){
  try{
    String sql = "SELECT * FROM UsersRoles WHERE UsersRoles.UserId = "+user_id+" AND UsersRoles.RoleId = "+role_id;
    Statement stmt_2 = conn.createStatement();
    ResultSet rs = stmt_2.executeQuery(sql);
    boolean result = rs.next();
    rs.close();
    stmt_2.close();
    return result; //FALSE if empty. TRUE if something exists
  }
  catch(Exception e){
    e.printStackTrace();
    return false;
  }
}

public static String getEncryptionKey(int role_id){
  try{
    Statement stmt_2 = conn.createStatement();
    String sql = "SELECT RoleId, EncryptionKey FROM Roles";
    ResultSet rs = stmt_2.executeQuery(sql);
    while(rs.next()){
      int id = rs.getInt("RoleId");
      String encryption_key = rs.getString("EncryptionKey");
      if(id == role_id){
        return encryption_key;
      }
    }
    rs.close();
    stmt_2.close();
    return null;
  }
  catch(Exception e){
    e.printStackTrace();
    return null;
  }
}

public static int findRoleID(String roleName){
  try{
    int role_id = Integer.MIN_VALUE;
    String sql = "SELECT RoleId, RoleName FROM Roles";
    ResultSet rs = stmt.executeQuery(sql);
    while(rs.next()){
      int id = rs.getInt("RoleId");
      String role = rs.getString("RoleName");
      if(roleName.equals(role)){
        role_id = id;
        break;
      }
    }
    rs.close();
    if(role_id == Integer.MIN_VALUE){
      System.out.println("Role not initialized: "+roleName);
      return role_id;
    }
    else{
      return role_id;
    }
  }
  catch(Exception e){
    e.printStackTrace();
    return Integer.MIN_VALUE;
  }
}

  public static void initializeDatabaseConnection(){
    try{
      Class.forName(ORACLE_DRIVER);
      conn = DriverManager.getConnection(DB_URL, USER_ID, PASSWORD);
      stmt = conn.createStatement();
    }
    catch(Exception e){
      e.printStackTrace();
      closeDatabaseConnection();
    }
  }

  public static void closeDatabaseConnection(){
    try{
      if(stmt != null)
        stmt.close();
    }
    catch(SQLException se2){
      se2.printStackTrace();
    }
    try{
      if(conn != null)
        conn.close();
    }
    catch(SQLException se){
      se.printStackTrace();
    }
  }
}
