package common;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by SMSD on 2/15/2017.
 */
public class FileHandler {

    private File file;
    private Scanner scanner;
    private ArrayList <String> table = new ArrayList<String>();

    public FileHandler(String fileName){
        file = new File(fileName);
    }

    public ArrayList<String> getLines(){
        readFromFile(file);
        return table;
    }

    public void overWriteFile(ArrayList<String> lines, String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter fileWriter = new FileWriter(file, false);
        String writeData = "";
        for(int counter = 0; counter < lines.size(); counter ++)
            writeData += lines.get(counter) + '\n';
        fileWriter.write(writeData);
        fileWriter.close();
    }

    private void readFromFile(File textFile) {
        try {
            scanner = new Scanner(textFile);
            while (scanner.hasNextLine()) {
                table.add(scanner.nextLine());
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addLineToFile(String line) {
        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(line + "\n");
            fw.close();

        } catch(IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }


    public void findAndReplaceWord(String oldWord, String newWord, String time) {
        String line;
        String finalFile = "";
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String word = null;
            while ((line = br.readLine()) != null) {
                System.out.println("line "  + line) ;
                List<String> parsedLine = StringHandler.parseStringByDiv(line, " ");
                System.out.println("In FileHandler :" + parsedLine.get(1));
                System.out.println("Parsed Line");
                word = parsedLine.get(1);
                if(parsedLine.get(1).equals(oldWord) && parsedLine.get(2).equals(time)) {
                    word = newWord;
                }
                finalFile += parsedLine.get(0) +  " ";
                finalFile += word + " ";
                for(int counter = 2; counter < parsedLine.size(); counter ++) {
                    if(counter == parsedLine.size() - 1)
                        finalFile += parsedLine.get(counter) + '\n';
                    else
                        finalFile += parsedLine.get(counter) + " ";
                }
            }
            FileWriter fw = new FileWriter(file);
            fw.write(finalFile);
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void updateCacheFile(String oldWord, String newWord, String time) {
        String line;
        String finalFile = "";
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String word = null;
            while ((line = br.readLine()) != null) {
                System.out.println("line "  + line) ;
                List<String> parsedLine = StringHandler.parseStringByDiv(line, " ");
                System.out.println("In FileHandler :" + parsedLine.get(1));
                System.out.println("Parsed Line");
                word = parsedLine.get(2);
                if(parsedLine.get(0).equals(oldWord) && parsedLine.get(1).equals(time)) {
                    finalFile += parsedLine.get(0) +  " ";
                    finalFile += parsedLine.get(1) + " ";
                    finalFile += newWord + " ";
                    finalFile += parsedLine.get(3) + " ";
                    finalFile += "0" + '\n';
                }else {
                    for (int counter = 0; counter < parsedLine.size(); counter++) {
                        if (counter == parsedLine.size() - 1)
                            finalFile += parsedLine.get(counter) + '\n';
                        else
                            finalFile += parsedLine.get(counter) + " ";
                    }
                }
            }
            FileWriter fw = new FileWriter(file);
            fw.write(finalFile);
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
