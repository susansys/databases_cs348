import java.io.*;

public class Project3{
  private static final String _alphabetLowerCase = "abcdefghijklmnopqrstuvwxyz";
  private static final String _alphabetUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  private static FileWriter fw;

  private static void initializeFileWriter(String outputFileName){
    try{
      fw = new FileWriter(outputFileName);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  private static void closeFileWriter(){
    try{
      if(fw != null){
        fw.flush();
        fw.close();
      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public static void writeToOutputFile(String toWrite){
    if(fw == null){
      System.out.println("FileWriter hasn't been initialized.");
    }
    else{
      try{
        fw.write(toWrite);
        fw.flush();
      }
      catch(Exception e){
        e.printStackTrace();
      }

    }
  }

  /** Carry out encryption using autokey cipher.
    */
  public static String AKEncrypt(String plainText, String key){
    String cipherText = "";

    //extract just the alphabets from the argument plain text
    String plainTextWithJustAlphabets = "";
    for(int i = 0; i < plainText.length(); i++){
      if(_alphabetLowerCase.indexOf(plainText.charAt(i))== -1 && _alphabetUpperCase.indexOf(plainText.charAt(i)) == -1){
        continue; //if not upper case alphabet or lower case alphabet, ignore character
      }
      else{
        plainTextWithJustAlphabets += plainText.charAt(i);
      }
    }

    //Generating string by prepending "key" to plainText - limited by length of plainText
    String keyPlainText = key;
    for(int i = 0 ; i < plainTextWithJustAlphabets.length() - key.length(); i++){
      keyPlainText += plainTextWithJustAlphabets.charAt(i);
    }

    int plainTextWithJustAlphabetsIndex = 0;      //index used to track element iteration in plainTextWithJustAlphabets
    for(int i = 0 ; i < plainText.length(); i++){ //iterate through each character in plainText
      if(_alphabetLowerCase.indexOf(plainText.charAt(i)) == -1 && _alphabetUpperCase.indexOf(plainText.charAt(i)) == -1){
        cipherText += plainText.charAt(i);        //non-alphabetic character copied as is without encryption
        continue;
      }
      else{                                       //in this case the alphabet character in plaintText corresponds to character in plaintTextWithJustAlphabets
        char plainChar = plainTextWithJustAlphabets.charAt(plainTextWithJustAlphabetsIndex);
        char keyPlainChar = keyPlainText.charAt(plainTextWithJustAlphabetsIndex);
        int cipherCharIndex = 0;

        if(Character.isUpperCase(plainChar)){
            cipherCharIndex += _alphabetUpperCase.indexOf(plainChar);
        }
        else if(Character.isLowerCase(plainChar)){
            cipherCharIndex += _alphabetLowerCase.indexOf(plainChar);
        }

        if(Character.isUpperCase(keyPlainChar)){
          cipherCharIndex += _alphabetUpperCase.indexOf(keyPlainChar);
        }
        else if(Character.isLowerCase(keyPlainChar)){
          cipherCharIndex += _alphabetLowerCase.indexOf(keyPlainChar);
        }
        cipherCharIndex %= 26;

        if(Character.isUpperCase(plainChar)){
          cipherText += _alphabetUpperCase.charAt(cipherCharIndex);
        }
        else if(Character.isLowerCase(plainChar)){
          cipherText += _alphabetLowerCase.charAt(cipherCharIndex);
        }

        plainTextWithJustAlphabetsIndex++;
      }
    }
    //System.out.println("Key: "+key);
    //System.out.println("Plain Text: "+plainText);
    //System.out.println("Key Plain Text: "+keyPlainText);
    //System.out.println("---- IN ENCRYPT ---- Encrypted "+plainText +" TO "+cipherText+"\twith key "+key);
    return cipherText;
  }

  public static void testAKEncrypt(){
    System.out.println("------ BEGIN TEST AKEncrypt ------");
    System.out.println("Encrypted: " + AKEncrypt("DATABASES", "KEY"));
    System.out.println("Encrypted: " + AKEncrypt("DataBases", "KEY"));
    System.out.println("Encrypted: " + AKEncrypt("DataBases", "KeY"));
    System.out.println("Encrypted: " + AKEncrypt("DATA @ BASES 1", "KEY"));
    System.out.println("Encrypted: " + AKEncrypt("Data @ Bases 1", "KEY"));
    System.out.println("Encrypted: " + AKEncrypt("D", "KEY"));
    System.out.println("Encrypted: " + AKEncrypt("Da", "KEY"));
    System.out.println("Encrypted: " + AKEncrypt("D", "KEY"));
    System.out.println("Encrypted: " + AKEncrypt("address 1", "SK"));
    System.out.println("------ END TEST AKEncrypt ------\n");
  }

  public static String AKDecrypt(String cipherText, String key){
    String plainText = "";
    String cipherTextWithJustAlphabet = "";
    for(int i = 0; i < cipherText.length(); i++){
      if(_alphabetLowerCase.indexOf(cipherText.charAt(i))== -1 && _alphabetUpperCase.indexOf(cipherText.charAt(i)) == -1){
        continue;
      }
      else{
        cipherTextWithJustAlphabet += cipherText.charAt(i);
      }
    }

    int cipherTextWithJustAlphabetIndex = 0;
    for(int i = 0; i < cipherText.length(); i++){
      if(_alphabetLowerCase.indexOf(cipherText.charAt(i)) == -1 && _alphabetUpperCase.indexOf(cipherText.charAt(i)) == -1){
          plainText += cipherText.charAt(i);
          continue;
      }
      else{
        char cipherChar = cipherTextWithJustAlphabet.charAt(cipherTextWithJustAlphabetIndex);
        char keyChar = key.charAt(cipherTextWithJustAlphabetIndex);
        int plainTextCharIndex = 0;

        if(Character.isUpperCase(cipherChar)){
            plainTextCharIndex += _alphabetUpperCase.indexOf(cipherChar);
        }
        else if(Character.isLowerCase(cipherChar)){
          plainTextCharIndex += _alphabetLowerCase.indexOf(cipherChar);
        }

        if(Character.isUpperCase(keyChar)){
          plainTextCharIndex -= _alphabetUpperCase.indexOf(keyChar);
        }
        else if(Character.isLowerCase(keyChar)){
          plainTextCharIndex -= _alphabetLowerCase.indexOf(keyChar);
        }
        plainTextCharIndex %= 26;
        if(plainTextCharIndex < 0){
          plainTextCharIndex += 26;
        }

        if(Character.isUpperCase(cipherChar)){
          plainText += _alphabetUpperCase.charAt(plainTextCharIndex);
          key += _alphabetUpperCase.charAt(plainTextCharIndex);
        }
        else if(Character.isLowerCase(cipherChar)){
          plainText += _alphabetLowerCase.charAt(plainTextCharIndex);
          key += _alphabetLowerCase.charAt(plainTextCharIndex);
        }
        cipherTextWithJustAlphabetIndex++;
      }
    }
    //System.out.println("---- IN DECRYPT ---- Decrypted "+cipherText +" TO "+plainText+"\twith key "+key);
    return plainText;
  }

  public static void testAKDecrypt(){
    System.out.println("------ BEGIN TEST AKDecrypt ------");
    System.out.println("Decrypted: " + AKDecrypt("NERDBTSFS", "KEY"));
    System.out.println("Decrypted: " + AKDecrypt("NerdBtsfs", "KEY"));
    System.out.println("Decrypted: " + AKDecrypt("NerdBtsfs", "KeY"));
    System.out.println("Decrypted: " + AKDecrypt("NERD @ BTSFS 1", "KEY"));
    System.out.println("Decrypted: " + AKDecrypt("Nerd @ Btsfs 1", "KEY"));
    System.out.println("Decrypted: " + AKDecrypt("Ner", "KEY"));
    System.out.println("Decrypted: " + AKDecrypt("Ne", "KEY"));
    System.out.println("Decrypted: " + AKDecrypt("N", "KEY"));
    System.out.println("Decrypted: " + AKDecrypt("snduhjw 1", "SK"));
    System.out.println("------ END TEST AKDecrypt ------\n");
  }


  public static void main(String [] args){
    //testAKEncrypt();
    //testAKDecrypt();

    String inputFile = args[0];
    String outputFile = args[1];
    initializeFileWriter(outputFile);
    try{
      User.initializeDatabaseConnection();
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));
      String strLine = "";
      User currentUser = null;
      int index = 1;
      while((strLine = reader.readLine()) != null){
        writeToOutputFile(index+": "+strLine+"\n");
        String[] lineTokens = strLine.split(" ");
        //the expected format: LOGIN user_name password
        if(lineTokens[0].equals("LOGIN")){
          //System.out.println("LOGIN COMMAND READ");
          String user = lineTokens[1];
          String pass = lineTokens[2];
          //System.out.println("User :"+lineTokens[1]);
          //System.out.println("Pass :"+lineTokens[2]);
          currentUser = User.attemptLogin(user, pass);
        }

        //the expected format: CREATE ROLE role_name encryptionKey
        else if(lineTokens[0].equals("CREATE") && lineTokens[1].equals("ROLE")){
            if(currentUser == null || !currentUser.isAdmin()){
              writeToOutputFile("Authorization failure\n");
            }
            else{
              currentUser.createRole(lineTokens[2], lineTokens[3]);
            }
        }

        //the expected format: CREATE USER username password
        else if(lineTokens[0].equals("CREATE") && lineTokens[1].equals("USER")){
          if(currentUser == null || !currentUser.isAdmin()){
            writeToOutputFile("Authorization failure\n");
          }
          else{
            currentUser.createUser(lineTokens[2], lineTokens[3]);
          }
        }

        //the expected format: GRANT ROLE username roleName
        else if(lineTokens[0].equals("GRANT") && lineTokens[1].equals("ROLE")){
          if(currentUser == null || !currentUser.isAdmin()){
            writeToOutputFile("Authorization failure\n");
          }
          else{
            currentUser.grantRole(lineTokens[2], lineTokens[3]);
          }
        }

        //the expected format: GRANT PRIVILEGE privName TO roleName ON tableName
        else if(lineTokens[0].equals("GRANT") && lineTokens[1].equals("PRIVILEGE")){
          if(currentUser == null || !currentUser.isAdmin()){
            writeToOutputFile("Authorization failure\n");
          }
          else{
            currentUser.grantPrivilege(lineTokens[2], lineTokens[4], lineTokens[6]);
          }
        }

        //the expected format: REVOKE PRIVILEGE privName FROM roleName ON tableName
        else if(lineTokens[0].equals("REVOKE") && lineTokens[1].equals("PRIVILEGE")){
          if(currentUser == null || !currentUser.isAdmin()){
            writeToOutputFile("Authorization failure\n");
          }
          else{
            currentUser.revokePrivilege(lineTokens[2], lineTokens[4], lineTokens[6]);
          }
        }

        //the expected format: INSERT INTO tableName Values(value_list) ENCRYPT columnNo ownerRole
        else if(lineTokens[0].equals("INSERT") && lineTokens[1].equals("INTO") && lineTokens[3].startsWith("VALUES") && lineTokens[lineTokens.length - 3].equals("ENCRYPT")){
          if(currentUser == null || !currentUser.hasPrivilege(lineTokens[2], "INSERT")){
            writeToOutputFile("Authorization failure\n");
          }
          else{
            String valuesString = strLine.substring(strLine.indexOf("(") + 2, strLine.indexOf(")") - 1);
            String[] valueTokens = valuesString.split("'\\s*,\\s*'");
            currentUser.insertInto(lineTokens[2], valueTokens, lineTokens[lineTokens.length-2], lineTokens[lineTokens.length-1]);
          }
        }

        //expected format: SELECT * FROM tableName
        else if(lineTokens[0].equals("SELECT") && lineTokens[1].equals("*") && lineTokens[2].equals("FROM")){
          if(currentUser == null || !currentUser.hasPrivilege(lineTokens[3], "SELECT")){
            writeToOutputFile("Authorization failure\n");
          }
          else{
            //System.out.println("User has authority (Select)");
            currentUser.printTableContents(lineTokens[3]);
          }
        }

        else if(lineTokens[0].equals("QUIT")){
          writeToOutputFile("\n");
          break;
        }
        writeToOutputFile("\n");
        index++;
      }
      User.closeDatabaseConnection();
      closeFileWriter();
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
}
